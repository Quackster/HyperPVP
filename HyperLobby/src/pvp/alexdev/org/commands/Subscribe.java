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
package pvp.alexdev.org.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pvp.alexdev.org.HyperPVP;
import pvp.alexdev.org.rank.bans.BanManager;
import pvp.alexdev.org.users.Session;

public class Subscribe implements CommandExecutor {

	private BanManager banManager;

	public Subscribe() {
		this.banManager = new BanManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {


		if (!sender.isOp()) {
			return false;
		}

		String uuid = args[1];

		ResultSet row = null;

		try {
			row = HyperPVP.getStorage().getRow("SELECT id,nickname FROM users WHERE uuid = '" + uuid + "'");


			int userid = row.getInt("id");
			String name = row.getString("nickname");

			int sub = 0;
			int seconds = 0;

			if (args.length > 2) {
				sub = Integer.parseInt(args[2]);
				seconds = Integer.parseInt(args[3]);	

			}

			final Session session = new Session(userid);

			if (args[0].equals("add")) {

				if (session.isDonator()) {
					return true;
				}

				HyperPVP.getStorage().execute("UPDATE users SET rank = '" + sub + "' WHERE id = '" + userid + "'");

				try {
					PreparedStatement statement = HyperPVP.getStorage().prepare("INSERT INTO users_subscription (sub_id, user_id, expire, seconds) VALUES (?, ?, ?, ?)"); {
						statement.setInt(1,sub);
						statement.setInt(2, userid);
						statement.setLong(3, HyperPVP.getTimestamp() + seconds);
						statement.setLong(4, seconds);
						statement.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Player player : Bukkit.getOnlinePlayers()) {

					if (player.getName().equals(name)) {
						HyperPVP.getUsers().get(player.getUniqueId()).sendMembershipStatus();
						//PlayerListener.sendMembershipStatus(player);
					}
				}
			}

			if (args[0].equals("renew")) {

				HyperPVP.getStorage().execute("DELETE FROM users_subscription WHERE user_id = '" + userid + "' AND expired = 0");

				try {
					PreparedStatement statement = HyperPVP.getStorage().prepare("INSERT INTO users_subscription (sub_id, user_id, expire, seconds) VALUES (?, ?, ?, ?)"); {
						statement.setInt(1,sub);
						statement.setInt(2, userid);
						statement.setLong(3, HyperPVP.getTimestamp() + seconds);
						statement.setLong(4, seconds);
						statement.execute();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				HyperPVP.getStorage().execute("UPDATE users SET rank = '" + sub + "' WHERE id = '" + userid + "'");

				for (Player player : Bukkit.getOnlinePlayers()) {

					if (player.getName().equals(name)) {
						HyperPVP.getUsers().get(player.getUniqueId()).sendMembershipStatus();
					}
				}
			}

			if (args[0].equals("remove")) {

				if (session.isDonator()) {
					HyperPVP.getStorage().execute("DELETE FROM users_subscription WHERE user_id = '" + userid + "' AND expired = 0");
				}

				HyperPVP.getStorage().execute("UPDATE users SET rank = 1 WHERE id = '" + userid + "'");
			}

			if (args[0].equals("chargeback")) {

				if (session.isDonator()) {
					HyperPVP.getStorage().execute("DELETE FROM users_subscription WHERE user_id = '" + userid + "' AND expired = 0");
				}

				banManager.banUser(null, session, "Donator chargeback fraud", false, true);

				HyperPVP.getStorage().execute("UPDATE users SET rank = 0 WHERE id = '" + userid + "'");
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return true;
	}

}
