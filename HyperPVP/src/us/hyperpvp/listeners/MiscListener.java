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
package us.hyperpvp.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.session.Session;

public class MiscListener implements Listener {

	public static void refreshTag(Player named) {

		Team team = null;

		if (HyperPVP.getScoreboard().getTeam(named.getName()) == null) {
			team = HyperPVP.getScoreboard().registerNewTeam(named.getName());
			handleColorSet(named, team, ChatColor.AQUA);
			team.addEntry(named.getName());	
			team.setCanSeeFriendlyInvisibles(true);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			named.setScoreboard(HyperPVP.getScoreboard());
		}
		
		if (HyperPVP.getTeamCycle().containsKey(named)) {
			ChatColor mapTeam = HyperPVP.getTeamCycle().get(named);
			team = HyperPVP.getScoreboard().getTeam(named.getName());
			handleColorSet(named, team, mapTeam);
		} else {

			if (HyperPVP.getSpectators().contains(named.getName())) {
				ChatColor mapTeam = ChatColor.AQUA;
				team = HyperPVP.getScoreboard().getTeam(named.getName());
				handleColorSet(named, team, mapTeam);
			}  else if (HyperPVP.isGamePlayer(named)) {

				Session session = HyperPVP.getSession(named);
				ChatColor mapTeam = session.getTeam().getColor();
				team = HyperPVP.getScoreboard().getTeam(named.getName());
				handleColorSet(named, team, mapTeam);
			} else {
				ChatColor mapTeam = ChatColor.AQUA;
				team = HyperPVP.getScoreboard().getTeam(named.getName());
				handleColorSet(named, team, mapTeam);
			}
		}
	}

	public static void handleColorSet(Player named, Team team, ChatColor mapTeam) {
		team.setDisplayName(named.getName());
		team.setPrefix(mapTeam.toString());
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

		event.getPlayer().setDisplayName(event.getPlayer().getName());

		int rank = 0;

		try {
			rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + event.getPlayer().getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();	
		}

		String prefix = "";

		if (rank == 2) {
			prefix = ChatColor.YELLOW + "*";
		}

		if (rank == 3) {
			prefix = ChatColor.DARK_BLUE + "*";
		}

		if (rank == 4) {
			prefix = ChatColor.DARK_GREEN + "*";
		}

		if (rank == 5) {
			prefix = ChatColor.DARK_RED + "*";
		}

		if (HyperPVP.isGamePlayer(event.getPlayer())) {
			Session game = HyperPVP.getSession(event.getPlayer());
			ChatColor team = game.getTeam().getColor();
			event.setFormat(team + "[Team] " + prefix + team + event.getPlayer().getName() + ChatColor.RESET + ": " + event.getMessage());

		} else if (HyperPVP.getTeamCycle().containsKey(event.getPlayer())) {

			ChatColor team = HyperPVP.getTeamCycle().get(event.getPlayer());
			event.setFormat(team + "[Team] " + prefix + team + event.getPlayer().getName() + ChatColor.RESET + ": " + event.getMessage());
		} else {

			ChatColor team = ChatColor.AQUA;
			event.setFormat("<" + prefix + team + event.getPlayer().getName() + ChatColor.RESET + ">: " + event.getMessage());
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String[] split = event.getMessage().split("\\s+");
		String command = split[0].substring(1);

		if ((command.equalsIgnoreCase("plugins")) || (command.equalsIgnoreCase("pl")) && !player.isOp()) {
			event.getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			event.setCancelled(true);
		}

		if ((command.equalsIgnoreCase("help"))) {
			player.sendMessage(ChatColor.DARK_RED  + "List of commands");

			player.sendMessage(ChatColor.GOLD + "/join " + ChatColor.WHITE + "Joins the game");
			player.sendMessage(ChatColor.GOLD  + "/spectate " + ChatColor.WHITE + "Leaves the game and spectate instead.");
			player.sendMessage(ChatColor.GOLD  + "/report " + ChatColor.WHITE + "Reports a user to staff.");
			player.sendMessage(ChatColor.GOLD  + "/server <name>" + ChatColor.WHITE + "Views the list of servers you can connect to.");
			event.setCancelled(true);
		}

		if (command.equalsIgnoreCase("whisper") || command.equalsIgnoreCase("tell") || command.equalsIgnoreCase("me") || command.equalsIgnoreCase("kill")) {
			event.getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			event.setCancelled(true);
		}

		if ((command.equalsIgnoreCase("stop")) && event.getPlayer().isOp()) {
			try {
				for (Player user : Bukkit.getOnlinePlayers()) {
					user.sendMessage(ChatColor.RED + "The lobby will up again shortly!");

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);
					dos.writeUTF("Connect");
					dos.writeUTF("lobby");
					user.sendPluginMessage(HyperPVP.getJavaPlugin(), "BungeeCord", baos.toByteArray());
					baos.close();
					dos.close();
				}

				TimeUnit.SECONDS.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Bukkit.shutdown();
		}

		/*int rank = 1;

		try {
			rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + player.getName() + "'");	
		} catch (SQLException e) {
			e.printStackTrace();	
		}

		try {
			if ((command.equalsIgnoreCase("stop") && rank == 6)) {
				for (Player user : Bukkit.getOnlinePlayers()) {
					user.sendMessage(ChatColor.RED + "Hyper PVP will up again shortly!");

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);
					dos.writeUTF("Connect");
					dos.writeUTF("lobby");
					user.sendPluginMessage(HyperPVP.getJavaPlugin(), "BungeeCord", baos.toByteArray());
					baos.close();
					dos.close();
				}

				TimeUnit.SECONDS.sleep(5);

				Bukkit.shutdown();
			} else if ((command.equalsIgnoreCase("stop") && rank != 6)) {
				player.sendMessage("Unknown command. Type \"help\" for help.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

}
