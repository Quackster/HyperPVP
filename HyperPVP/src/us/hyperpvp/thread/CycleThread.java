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
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.MinecraftServer;
import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.map.GameMap;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.map.team.TeamColor;
import us.hyperpvp.thread.misc.IThread;

public class CycleThread extends IThread {

	private GameMap rotatedMap;
	private TeamMap team;
	private Player name;

	public CycleThread(TeamMap winningTeam, Player player, GameMap rotatedMap) {
		super();
		this.team = winningTeam;
		this.name = player;
		this.rotatedMap = rotatedMap;

		try {
			PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE servers SET map_status = ? WHERE bungee_name = ?"); {
				statement.setString(1, "FINISHED");
				statement.setString(2, HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase());

				statement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void run() {

		/*if (HyperPVP.games == 19) {
			this.restart(30, 15);
			this.restart(15, 5);
			this.restart(10, 5);
			this.restart(5, 1);
			this.restart(4, 1);
			this.restart(3, 1);
			this.restart(2, 1);
			this.restart(1, 1);
			HyperPVP.needsRestart = true;
			return;
		} else {
			HyperPVP.games = HyperPVP.games + 1;
		}*/


		try {

			CraftServer craftServer = (CraftServer) Bukkit.getServer();
			MinecraftServer server = craftServer.getServer();
			server.setMotd(ChatColor.AQUA + "Cycling...");
		} catch (Exception e1) {
			e1.printStackTrace();
		}


		try {
			this.cycleAnnounce(20, 1000, false);
			this.cycleAnnounce(20, 1000, true); 
			this.cycleAnnounce(20, 1000, true);
			this.cycleAnnounce(20, 1000, true);
			this.cycleAnnounce(20, 1000, true);

			this.cycleAnnounce(15, 1000, false);
			this.cycleAnnounce(15, 1000, true);
			this.cycleAnnounce(15, 1000, true);
			this.cycleAnnounce(15, 1000, true);
			this.cycleAnnounce(15, 1000, true);

			this.cycleAnnounce(10, 1000, false);
			this.cycleAnnounce(10, 1000, true);
			this.cycleAnnounce(10, 1000, true);
			this.cycleAnnounce(10, 1000, true);
			this.cycleAnnounce(10, 1000, true);


			this.cycleAnnounce(5, 1000, false);
			this.cycleAnnounce(4, 1000, false);
			this.cycleAnnounce(3, 1000, false);
			this.cycleAnnounce(2, 1000, false);
			this.cycleAnnounce(1, 1000, false);

			/*for (int i = 10; i >= 0; i--) {

				if (i == 0) {
					continue;
				}

				if (this.isCancelled()) {
					return;
				}

				this.cycleAnnounce(i, 1000);

			}*/

			HyperPVP.setCycling(false);
			HyperPVP.setMatchBeenAnnounced(false);
			HyperPVP.setNeedsGameThread(true);
			
			HyperPVP.setGames(HyperPVP.getGames() + 1);

			HyperPVP.setWinningPlayer(null);
			HyperPVP.setWinningTeam(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void restart(int tiem, int i) {
		Bukkit.broadcastMessage(ChatColor.RED + "Restarting in " + tiem + " seconds!");
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cycleAnnounce(int i, int milli, boolean silent) throws InterruptedException {

		if (this.isCancelled() || this.isInterrupted()) {
			return;
		}

		if (!silent) {
			Bukkit.broadcastMessage(ChatColor.DARK_RED + ">> " + ChatColor.AQUA + "Cycling to " + ChatColor.DARK_AQUA + "[" + rotatedMap.getType().getType() + "] " + rotatedMap.getMapName() + ChatColor.AQUA + " in " + ChatColor.DARK_AQUA + i + ChatColor.AQUA + " seconds" + ChatColor.DARK_RED + " <<");
		}

		if (this.team != null) {

			for (Entry<Player, ChatColor> set : HyperPVP.getTeamCycle().entrySet()) {

				if (set.getValue() != this.team.getColor()) {
					continue;
				}

				Player p = set.getKey();
				ChatColor c = set.getValue();

				HyperPVP.getFireworkLocation().put(p.getLocation(), TeamColor.get(c));
				HyperPVP.setCheckFirework(true);

			}
		}

		if (this.name != null) {

			Player p = name;
			ChatColor c = ChatColor.GOLD;

			HyperPVP.getFireworkLocation().put(p.getLocation(), TeamColor.get(c));
			HyperPVP.setCheckFirework(true);
		}

		Thread.sleep(milli);
		
	}
}
