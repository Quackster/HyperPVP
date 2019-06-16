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
package us.hyperpvp.thread;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.server.v1_13_R2.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.thread.misc.IThread;

public class FightThread extends IThread {

	public FightThread() {

		int mapTime = HyperPVP.getTime();
		int left = HyperPVP.getMinutesLeft();

		int third = mapTime / 3;

		int one = mapTime;
		int two = mapTime - third;
		int three = mapTime - third - third;

		ChatColor status = ChatColor.GREEN;

		if (left <= one && left > two) {
			status = ChatColor.GREEN;
		}
		else if (left <= two && left > three) {
			status = ChatColor.GOLD;
		}
		else if (left <= three) {
			status = ChatColor.RED;
		}

		//String motd = status + HyperPVP.getMap().getMapName();
		String motd = status + "<< " + ChatColor.AQUA + "[" + HyperPVP.getMap().getType().name() + "] " + HyperPVP.getMap().getMapName() + status + " >>";

		try {
			CraftServer craftServer = (CraftServer) Bukkit.getServer();
			MinecraftServer server = craftServer.getServer();
			server.setMotd(motd);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		HyperPVP.setMinutesLeft(HyperPVP.getMap().getTime());
		HyperPVP.setTimeString(HyperPVP.getTime() + ":00");

		HyperPVP.setCycling(false);
		HyperPVP.setWinningTeam(null);

		Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Now playing " + ChatColor.GOLD + HyperPVP.getMap().getMapName() + ChatColor.DARK_PURPLE + " by " + ChatColor.RED + HyperPVP.getMap().getAuthor().replace(", ", ChatColor.DARK_PURPLE + ", " + ChatColor.RED));

		HyperPVP.getMap().startWorld();

		try {
			PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE servers SET current_type = ?, current_name = ?, status = ?, mins_left = ?, map_status = ? WHERE bungee_name = ?"); {
				statement.setString(1, HyperPVP.getMap().getType().getType());
				statement.setString(2, HyperPVP.getMap().getMapName());
				statement.setInt(3, 3);
				statement.setInt(4, HyperPVP.getMinutesLeft());
				statement.setString(5, "STARTING");
				statement.setString(6, HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase());

				statement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HyperPVP.getMap().shuffleTeams();
	}

	@Override
	public void run() {

		try {

			HyperPVP.setTimeString(HyperPVP.getTime() + ":00");

			/*for (int i = 0; i < 80; i++) {

				if (HyperPVP.getGameSessions().size() == 0) {
					Thread.sleep(1000);
				} else {
					break;
				}

			}*/

			while (true) {

				if (this.isCancelled() || this.isInterrupted() || HyperPVP.getPlayers().size() != 0) {
					break;
				} else {

					if (HyperPVP.getPlayers().size() == 0) {
						//Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Need more players to start!");
						Thread.sleep(1000);
					} else {
						break;
					}
				}
			}

			if (this.isCancelled() || this.isInterrupted()) {
				return;
			}

			Bukkit.broadcastMessage(ChatColor.GREEN + "Starting game!");
			this.matchAnnounce(30, 15000);
			this.matchAnnounce(15, 5000);
			this.matchAnnounce(10, 1000);
			Thread.sleep(1000);
			Thread.sleep(1000);
			Thread.sleep(1000);
			Thread.sleep(1000);
			this.matchAnnounce(5, 1000);
			this.matchAnnounce(4, 1000);
			this.matchAnnounce(3, 1000);
			this.matchAnnounce(2, 1000);
			this.matchAnnounce(1, 1000);

			if (this.isCancelled() || this.isInterrupted()) {
				return;
			}

			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + 
					"########################" +
					"\n##" + ChatColor.GOLD + "The match has started!" + ChatColor.DARK_PURPLE + " ##\n" +
					"########################");
			try {
				PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE servers SET map_status = ? WHERE bungee_name = ?"); {
					statement.setString(1, "PLAYING");
					statement.setString(2, HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase());

					statement.execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			HyperPVP.setMatchBeenAnnounced(true);
			HyperPVP.setNeedsMatchCheck(true);

			int timeMinutes = HyperPVP.getTime();
			HyperPVP.setMinutesLeft(timeMinutes);

			for (int i = timeMinutes; i >= 0; i--)
			{
				if (i == 0) {
					continue;
				}

				if (HyperPVP.isCycling()) {
					break;
				}

				if (i != timeMinutes) {

					HyperPVP.setMinutesLeft(i);

					int minutes = timeMinutes;
					int left = HyperPVP.getMinutesLeft();

					int third = minutes / 3;

					int one = minutes;
					int two = minutes - third;
					int three = minutes - third - third;

					PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE servers SET status = ? WHERE bungee_name = ?"); {

						if (left <= one && left > two) {
							statement.setInt(1, 3);

						} else if (left <= two && left > three) {
							statement.setInt(1, 2);

						} else if (left <= three) {
							statement.setInt(1, 1);
						}

						statement.setString(2, HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase());
						statement.execute();
					}
				}

				this.update(i, 00);

				if (i == 1 ) {
					this.update(0, 30);
					this.update(0, 15, 5);
					this.update(0, 10, 5);
					this.update(0, 5, 1);
					this.update(0, 4, 1);
					this.update(0, 3, 1);
					this.update(0, 2, 1);
					this.update(0, 1, 1);

					if (this.isCancelled() || this.isInterrupted()) {
						return;
					}
				}

			}

			if (!HyperPVP.isCycling()) {
				HyperPVP.setNeedsCycleThread(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void matchAnnounce(int seconds, int milliseconds) throws InterruptedException {

		if (this.isCancelled() || this.isInterrupted()) {
			return;
		}

		Bukkit.broadcastMessage(ChatColor.GREEN + "Match starting in " + ChatColor.DARK_RED + seconds + ChatColor.GREEN + " seconds");
		Thread.sleep(milliseconds);
	}

	public void update(int minutes, int seconds) {

		if (this.isCancelled() || this.isInterrupted()) {
			return;
		}

		String secs = (String) (seconds == 0 ? "00" : String.valueOf(seconds));
		this.broadcast(minutes, secs);

		int dminutes = HyperPVP.getTime();
		int left = HyperPVP.getMinutesLeft();

		int third = dminutes / 3;

		int one = dminutes;
		int two = dminutes - third;
		int three = dminutes - third - third;

		ChatColor status = ChatColor.GREEN;

		if (left <= one && left > two) {
			status = ChatColor.GREEN;
		}
		else if (left <= two && left > three) {
			status = ChatColor.GOLD;
		}
		else if (left <= three) {
			status = ChatColor.RED;
		}


		String motd = status + "<< " + ChatColor.AQUA + "[" + HyperPVP.getMap().getType().name() + "] " + HyperPVP.getMap().getMapName() + status + " >>";

		//String motd = status + HyperPVP.getMap().getMapName();

		try {

			MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
			server.setMotd(motd);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void update(int minutes, int seconds, int countseconds) {

		if (this.isCancelled() || this.isInterrupted()) {
			return;
		}

		try {
			Thread.sleep(countseconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String secs = (String) (seconds == 0 ? "00" : String.valueOf(seconds));
		this.broadcast(minutes, secs);


		/*if (announceMapCreator   == 3) {
	    			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Currently playing " + ChatColor.GOLD + HyperPVP.getMap().getMapName() + ChatColor.DARK_PURPLE + " by " + ChatColor.RED + HyperPVP.getMap().getAuthor().replace(", ", ChatColor.DARK_PURPLE + ", " + ChatColor.RED));
	    			announceMapCreator = 0;
	    		} else {
	    			announceMapCreator = announceMapCreator + 1;
	    		}
		 */
	}

	public void broadcast(int minutes, String seconds) {

		if (this.isCancelled() || this.isInterrupted()) {
			return;
		}

		try {

			String message = "time left"; 

			if (HyperPVP.getMap().getType() == GameType.TDM ||
					HyperPVP.getMap().getType() == GameType.DTM ||
					HyperPVP.getMap().getType() == GameType.DTC) {

				TeamMap one = HyperPVP.getMap().getTeams().get(0);
				TeamMap two = HyperPVP.getMap().getTeams().get(1);

				message += " - team kills " + one.getColor() + HyperPVP.getMap().getTeamStats(0) + ChatColor.WHITE + ":" + two.getColor() + HyperPVP.getMap().getTeamStats(1);

			} else if (HyperPVP.getMap().getType() == GameType.FFA) {

				List<Session> topPlayers = HyperPVP.getMap().getTop();

				if (topPlayers.size() != 0 && topPlayers.get(0).getKills() != 0) {

					if (topPlayers.size() > 1) {

						Session inLead = topPlayers.get(0);
						Session inLeadTwo = topPlayers.get(1);

						if (inLead.getKills() == inLeadTwo.getKills()) {

							message += "!";

						} else {

							message += ". " + ChatColor.GOLD + inLead.getPlayer().getName() + ChatColor.WHITE + " is in lead with " + ChatColor.GOLD + inLead.getKills() + ChatColor.WHITE + " kills!";

						}
					} else {

						Session inLead = topPlayers.get(0);
						message += ". " + ChatColor.GOLD + inLead.getPlayer().getName() + ChatColor.WHITE + " is in lead with " + ChatColor.GOLD + inLead.getKills() + ChatColor.WHITE + " kills!";
					}
				} else {
					message += "!";
				}
			}

			if (!seconds.equals("30")) {

				PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE servers SET mins_left = ? WHERE bungee_name = ?"); {
					statement.setInt(1, minutes);
					statement.setString(2, HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase());
					statement.execute();
				}

				//WebRequest.sendRequest("update_servers", HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase() + ",mins_left," + minutes);
			}


			Bukkit.getServer().broadcastMessage("[" + ChatColor.DARK_AQUA + minutes + ChatColor.WHITE + ":" + ChatColor.DARK_AQUA + seconds + ChatColor.WHITE + "] " + message);

			//HyperPVP.initalizeMySQL(false);
			HyperPVP.setTimeString(minutes + ":" + seconds);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
