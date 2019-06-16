/*******************************************************************************
 * Copyright 2014 Alex Miller
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package pvp.alexdev.org.rank.bans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pvp.alexdev.org.HyperPVP;
import pvp.alexdev.org.users.Session;
import pvp.alexdev.org.util.MiscUtils;

public class BanManager {

	public boolean hasBanUUID(int userId) {

		try {

			if (!HyperPVP.getStorage().exists("SELECT * FROM users_bans WHERE user_id = '" + userId + "' AND expired = 0 AND type <> 'WARN'")) {
				return false;
			}

			ResultSet set = HyperPVP.getStorage().getRow("SELECT * FROM users_bans WHERE user_id = '" + userId + "'");

			long expire = set.getLong("expire");

			if (expire == 0) {
				return true;
			} else {
				if (expire > HyperPVP.getTimestamp()) {
					return true;
				} else {
					HyperPVP.getStorage().execute("UPDATE users_bans SET expired = 1 WHERE id = '" + set.getInt("id") + "'");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public Ban getCurrentBan(Player player) {

		try {

			int userId = HyperPVP.getStorage().getInt("SELECT id FROM users WHERE uuid = '" + player.getUniqueId().toString() + "'");
			ResultSet set = HyperPVP.getStorage().getRow("SELECT * FROM users_bans WHERE user_id = '" + userId + "' AND expired = 0 AND type <> 'WARN'");

			long expire = set.getLong("expire");
			String date = getFormat(expire - HyperPVP.getTimestamp());

			return new Ban(set.getString("reason"), expire, date, BanType.valueOf(set.getString("type")), BanSubject.valueOf(set.getString("subject")));


		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<Ban> getPreviousBans(Player player) {

		List<Ban> bans = new ArrayList<Ban>();

		try {
			int userId = HyperPVP.getStorage().getInt("SELECT id FROM users WHERE uuid = '" + player.getUniqueId().toString() + "'");
			ResultSet set = HyperPVP.getStorage().prepare("SELECT * FROM users_bans WHERE user_id = '" + userId + "' AND expired = 1").executeQuery();

			while (set.next()) {

				long expire = set.getLong("expire");
				String date = getFormat(expire - HyperPVP.getTimestamp());

				bans.add(new Ban(set.getString("reason"), expire, date, BanType.valueOf(set.getString("type")), BanSubject.valueOf(set.getString("subject"))));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bans;
	}

	public List<Ban> getPreviousBans(int userId, BanType type, BanSubject subject) {

		List<Ban> bans = new ArrayList<Ban>();

		try {
			
			ResultSet set = HyperPVP.getStorage().prepare("SELECT * FROM users_bans WHERE user_id = '" + userId + "' AND expired = 1 AND type = '" + type.name() + "' AND subject = '" + subject.name() + "'").executeQuery();

			while (set.next()) {

				long expire = set.getLong("expire");
				String date = getFormat(expire - HyperPVP.getTimestamp());

				bans.add(new Ban(set.getString("reason"), expire, date, BanType.valueOf(set.getString("type")), BanSubject.valueOf(set.getString("subject"))));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bans;
	}

	public String getFormat(long seconds) {

		String date = "";

		long[] args = HyperPVP.calculateTime(seconds);

		if (args[0] != 0) {
			date = date + " " + args[0] + " " + (args[0] == 1 ? "day" : "days") + ",";
		}

		if (args[1] != 0) {
			date = date + " " + args[1] + " " + (args[1] == 1 ? "hour" : "hours") + ",";
		}

		if (args[2] != 0) {
			date = date + " " + args[2] + " " + (args[2] == 1 ? "minute" : "minutes") + ",";
		}

		if (args[3] != 0) {
			date = date + " " + args[3] + " " + (args[2] == 1 ? "second" : "seconds") + ",";
		}

		if (date.endsWith(",")) {
		return MiscUtils.removeLastChar(date);
		} else {
			return date;
		}
	}

	public void banUser(Player banner, Session session, String reason, boolean isHack, boolean permOverride) {

		if (this.hasBanUUID(session.getId())) {
			if (banner != null) {
				banner.sendMessage(ChatColor.RED + "This user already has a ban");
				return;
			}
		}

		if (session.getRank() >= 6) {
			if (banner != null) {
				banner.sendMessage(ChatColor.RED + "You cannot ban a user that has ban privileges");
			}
			return;
		}

		long expire = 0;
		BanType type = BanType.WEEK;
		BanSubject subject = BanSubject.DISRESPECT;

		try {

			if (permOverride) {

				type = BanType.PERM;
				
			} else {

				if (isHack) {

					subject = BanSubject.HACK;

					int bans = this.getPreviousBans(session.getId(), BanType.WEEK, BanSubject.HACK).size();

					if (bans < 2) {
						expire = (HyperPVP.getTimestamp() + 604800);
					} else {
						type = BanType.PERM;
					}

				} else {

					subject = BanSubject.DISRESPECT;
					type = BanType.WARN;

					int bans = this.getPreviousBans(session.getId(), BanType.WARN, BanSubject.DISRESPECT).size();

					if (bans < 2) {
						expire = (HyperPVP.getTimestamp());
					} else {
						type = BanType.WEEK;
						expire = (HyperPVP.getTimestamp() + 604800);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PreparedStatement statement = HyperPVP.getStorage().prepare("INSERT INTO users_bans (user_id, reason, seconds, type, subject, expire) VALUES (?, ?, ?, ?, ?, ?)"); {
				statement.setInt(1, session.getId());
				statement.setString(2, reason);
				statement.setLong(3, (expire == 0 ? 0 : expire - HyperPVP.getTimestamp()));
				statement.setString(4, type.name());
				statement.setString(5, subject.name());
				statement.setLong(6, expire);
				statement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (type == BanType.WARN) {
			if (banner != null ) {
				banner.sendMessage(ChatColor.RED + "You kicked " + session.getPlayer().getName() + " (" + reason + ")");
			}
		} else {
			if (banner != null) {
				banner.sendMessage(ChatColor.RED + "You banned " + session.getPlayer().getName() + " (" + reason + ") " + (type == BanType.PERM ? "permanently" : "and will last for (" + getFormat(expire) + ")"));
			}
		}
		
		Player player = session.getPlayer();
		
		if (player == null) {
			
			for (Player p : Bukkit.getOnlinePlayers()) {

				if (session.getPlayer().getName() != null) {
					if (p.getName().equals(session.getPlayer().getName())) {
						player = p;
						break;
					}
				}
			}
		}
		
		if (player != null) {
			session.getPlayer().kickPlayer(kickMessage(player, reason, type, getFormat(expire - HyperPVP.getTimestamp())));
		}
	}

	public String kickMessage(Player p, String reason, BanType type, String date) {


		if (type == BanType.PERM) {
			return "" + ChatColor.RED + "Permanently Banned  " + ChatColor.GOLD + ">> " + ChatColor.AQUA + reason + "\n\n\n" + ChatColor.GREEN + "Permanent bans cannot be appealed" + "\n\n" + ChatColor.YELLOW + "More information at " + ChatColor.GOLD + "http://outbreakmc.net/rules";

		}

		if (type == BanType.WEEK) {
			return "" + ChatColor.GREEN + "Temporarily Banned  " + ChatColor.GOLD + ">> " + ChatColor.AQUA + reason + "\n\n\n" + ChatColor.DARK_GREEN + "Your ban will expire in" + ChatColor.GREEN + date + "\n\n" + ChatColor.YELLOW + "More information at " + ChatColor.GOLD + "http://outbreakmc.net/rules";
		}


		return "" + ChatColor.DARK_AQUA + "Warned  " + ChatColor.GOLD + ">> " + ChatColor.AQUA + reason + "\n\n\n" + /*ChatColor.DARK_GREEN + "Your ban will expire in" + ChatColor.GREEN + date + "\n\n" + */ChatColor.YELLOW + "More information at " + ChatColor.GOLD + "http://outbreakmc.net/rules";

	}
}
