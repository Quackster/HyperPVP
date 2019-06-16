package pvp.alexdev.org.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import pvp.alexdev.org.HyperPVP;
import pvp.alexdev.org.util.MiscUtils;

public class Session {

	private Player player;
	private ResultSet row;
	private int rank;
	private String rankLabel;
	private int id;
	private boolean newUser = false;

	public Session(int id) {
		//this.player = player;
		//this.pet = null;
		
		this.id = id;
		try {
			this.row = HyperPVP.getStorage().getRow("SELECT * FROM users WHERE id = '" + this.id + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Session(Player player) {
		this.player = player;
		
		
		try {
			if (!HyperPVP.getStorage().exists("SELECT * FROM users WHERE uuid = '" + this.player.getUniqueId().toString() + "'")) {

				this.newUser = true;
				
				PreparedStatement statement = HyperPVP.getStorage().prepare("INSERT INTO users (uuid, username, last_online, email) VALUES (?, ?, ?, ?)"); {
					statement.setString(1, this.player.getUniqueId().toString());
					statement.setString(2, this.player.getName());
					statement.setLong(3, (System.currentTimeMillis() / 1000L));
					statement.setString(4, "");
					statement.execute();
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		if (player != null) {
			this.refresh();
		}
	}
	
	private void refresh() {
		try {
			
			row = HyperPVP.getStorage().getRow("SELECT * FROM users WHERE uuid = '" + player.getUniqueId().toString() + "'");
			this.rank = row.getInt("rank");
			this.id = row.getInt("id");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.rankLabel = this.rank == 1 ? "" : HyperPVP.getRankManager().getRanks().get(this.rank);

	}
	

	public void sendMembershipStatus() {

		if (this.isDonator()) {
			try {
				UserDonator userDonator = this.getDonator();

				String format = userDonator.getDays();

				player.sendMessage(ChatColor.AQUA + "You have a total " + ChatColor.WHITE + format.substring(1) + ChatColor.AQUA + " of " + ChatColor.WHITE + ChatColor.ITALIC + userDonator.getName() + ChatColor.RESET + ChatColor.AQUA + " left");
			} catch (Exception e) {
				// user not donator
			}
		}

		//p.sendMessage(ChatColor.DARK_GREEN + "You have " + ChatColor.WHITE + session.getCurrency() + ChatColor.DARK_GREEN + " scrap metal");
	}

	public UserDonator getDonator() {

		try {

			ResultSet set = HyperPVP.getStorage().getRow("SELECT * FROM users_subscription WHERE user_id = '" + this.id + "' AND expired = 0");

			long expire = set.getLong("expire") - HyperPVP.getTimestamp();
			String date = expire == 0 ? "forever" : getFormat(expire);
			String name = HyperPVP.getRankManager().getRanks().get(set.getInt("sub_id")).substring(2);

			return new UserDonator(date, name);


		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}
	
	public void clearTitles() {
		this.sendTitle("", "", 20);
	}
	
	public void sendTitle(String title, String subTitle, int duration) {
		
		PacketPlayOutTitle packet = null;
		
		packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), 0, duration, 0);
		((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);

		packet = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subTitle + "\"}"), 0, duration, 0);
		((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
		
	}

	public String getFormat(long seconds) {

		String date = "";

		long[] args = HyperPVP.calculateTime(seconds);

		if (args[0] != 0 && date.length() == 0) {
			date = date + " " + args[0] + " " + (args[0] == 1 ? "day" : "days") + ",";
		}

		if (args[1] != 0) {
			date = date + " " + args[1] + " " + (args[1] == 1 ? "hour" : "hours") + ",";
		}

		if (date.length() == 0) {

			if (args[2] != 0) {
				date = date + " " + args[2] + " " + (args[2] == 1 ? "minute" : "minutes") + ",";
			}

			if (args[3] != 0) {
				date = date + " " + args[3] + " " + (args[2] == 1 ? "second" : "seconds") + ",";
			}
		}

		return MiscUtils.removeLastChar(date);
	}


	public boolean isDonator() {

		/*if (this.rank > 7) {
			return true;
		}

		try {

			if (!Outbreak.getStorage().exists("SELECT * FROM users_subscription WHERE user_id = '" + this.id + "' AND expired = 0")) {
				return false;
			}

			ResultSet set = Outbreak.getStorage().getRow("SELECT * FROM users_subscription WHERE user_id = '" + this.id + "' AND expired = 0");

			long expire = set.getLong("expire");

			if (expire == 0) {

				if (this.id != set.getInt("sub_id")) {
					Outbreak.getStorage().execute("UPDATE users SET rank = '" + set.getInt("sub_id") + "' WHERE user_id = '" + this.id + "'");
				}

				return true;
			} else {
				if (expire > Outbreak.getTimestamp()) {

					if (this.id != set.getInt("sub_id")) {
						Outbreak.getStorage().execute("UPDATE users SET rank = '" + set.getInt("sub_id") + "' WHERE id = '" + this.id + "'");
					}

					return true;

				} else {

					Outbreak.getStorage().execute("DELETE FROM users_subscription WHERE user_id = '" + this.id + "' AND expired = 0");
					Outbreak.getStorage().execute("UPDATE users SET rank = 1 WHERE id = '" + this.id + "'");

					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		return false;
	}

	public Player getPlayer() {
		return player;
	}

	public int getRank() {
		return this.rank;
	}

	public String getRankLabel() {
		this.refresh();
		return this.rankLabel;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * @return the newUser
	 */
	public boolean isNewUser() {
		return newUser;
	}

	/**
	 * @param newUser the newUser to set
	 */
	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}
}
