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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.region.Region;
import us.hyperpvp.game.map.region.RegionType;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.map.team.TeamColor;
import us.hyperpvp.game.session.Session;

import us.hyperpvp.misc.CycleUtil;
import us.hyperpvp.misc.Helpers;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		//HyperPVP.checkWorlds(event.getPlayer());
		Player player = event.getPlayer();

		try {

			if (!HyperPVP.getStorage().entryExists("SELECT * FROM users WHERE uuid = '" + event.getPlayer().getUniqueId().toString() + "'")) {

				Bukkit.broadcastMessage(ChatColor.AQUA + "Welcome " + ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.AQUA +" to HyperPVP!");

				PreparedStatement statement = HyperPVP.getStorage().queryParams("INSERT INTO users (uuid, username, last_online, email) VALUES (?, ?, ?, ?)"); {
					statement.setString(1, event.getPlayer().getUniqueId().toString());
					statement.setString(2, event.getPlayer().getName());
					statement.setLong(3, (System.currentTimeMillis() / 1000L));
					statement.setString(4, "");
					statement.execute();
				}
			}

			PreparedStatement statement = HyperPVP.getStorage().queryParams("INSERT INTO servers_users (server_id, user) VALUES (?, ?)"); {
				statement.setString(1, HyperPVP.getConfiguration().getConfig().getString("Server"));
				statement.setString(2, event.getPlayer().getName());
				statement.execute();
			}    

			statement = HyperPVP.getStorage().queryParams("UPDATE users SET last_online = ?, username = ? WHERE uuid = ?"); {
				statement.setLong(1, (System.currentTimeMillis() / 1000L));
				statement.setString(2, event.getPlayer().getName());
				statement.setString(3, event.getPlayer().getUniqueId().toString());
				statement.execute();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		if (HyperPVP.isCycling() && HyperPVP.getPreviousWorld() != null) {
			player.teleport(HyperPVP.getPreviousWorld().getSpawn());
		} else {
			player.teleport(HyperPVP.getMap().getSpawn());
		}
		
		//rplayer.setScoreboard(HyperPVP.getMap().getScoreboard());

		//HyperPVP.resetSpectatorInventory(player);
		Session.addSpectator(player, true);
		MiscListener.refreshTag(player);

		player.sendMessage(ChatColor.BLUE + "Welcome to " + ChatColor.GOLD + "Hyper PVP" + ChatColor.BLUE + "!");
		player.sendMessage(ChatColor.BLUE + "Here you can play " + ChatColor.GOLD + "intense PVP matches " + ChatColor.BLUE + "with others.");
		player.sendMessage(ChatColor.BLUE + "To start playing type " + ChatColor.GOLD + "/join");
		player.sendMessage(ChatColor.BLUE + "And to watch others type " + ChatColor.GOLD + "/spectate");

		//player.sendMessage(ChatColor.DARK_RED + "Attention!");
		//player.sendMessage(ChatColor.RED + "All statistics have been reset as of (05/09/2013) due to updates. Won't happen again!");

		event.setJoinMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " joined the game");
		HyperPVP.setListName(ChatColor.AQUA, player);

		for (Player p : Bukkit.getOnlinePlayers()) {
			MiscListener.refreshTag(p);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(HyperPVP.getJavaPlugin(), new Runnable() {
			@Override
			public void run() {
				CycleUtil.refreshShowHidePlayer();
			}
		}, 1);

	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		//event.setLeaveMessage(ChatColor.GRAY + event.getPlayer().getName() + " left the game");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		try {
			PreparedStatement statement = HyperPVP.getStorage().queryParams("UPDATE users SET last_online = ? WHERE uuid = ?"); {
				statement.setLong(1, (System.currentTimeMillis() / 1000L));
				statement.setString(2, event.getPlayer().getUniqueId().toString());
				statement.execute();
			}

			HyperPVP.getStorage().executeQuery("DELETE FROM servers_users WHERE user = '" + event.getPlayer().getName() + "'");

		} catch (Exception e) {
			e.printStackTrace();
		}


		Player player = event.getPlayer();

		if (HyperPVP.getTeamCycle().containsKey(player)) {
			HyperPVP.getTeamCycle().remove(player);
		}
		
		Session session = HyperPVP.getSession(player);
		
		if (session.isPlaying()) {
			HyperPVP.getSession(player).leaveGame(false, false);
		}

		HyperPVP.getSessions().remove(player.getName());
		HyperPVP.getScoreboard().getTeam(player.getName()).unregister();

		event.setQuitMessage(ChatColor.DARK_AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " left the game");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage("");
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String[] split = event.getMessage().split("\\s+");
		String command = split[0].substring(1);


		if (command.equalsIgnoreCase("kill")) {
			event.getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			event.setCancelled(true);
			return;
		}

		if (command.equalsIgnoreCase("plugins") || command.equalsIgnoreCase("pl")) {
			event.getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			event.setCancelled(true);
			return;
		}

		if (command.equalsIgnoreCase("me")) {
			event.getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {

		Player player = event.getPlayer();

		Session session = HyperPVP.getSession(player);
		
		if (HyperPVP.isCycling()) {
			event.setRespawnLocation(HyperPVP.getPreviousWorld().getRandomSpawn(player));
		} else {
			event.setRespawnLocation(session.getGameSpawn());
		}

		CycleUtil.resetInventory(player, false);
		
		session.setLastDamagedBy(null);	
		session.resetKillTimer();

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (event.getItem() == null || event.getItem().getType() == null) {
				return;
			}

			if (event.getClickedBlock().getType() == Material.CHEST ||
					event.getClickedBlock().getType() == Material.WORKBENCH || 
					event.getClickedBlock().getType() == Material.FURNACE ||
					event.getClickedBlock().getType() == Material.ANVIL ||
					event.getClickedBlock().getType() == Material.BURNING_FURNACE) {

				if (HyperPVP.isCycling()) {
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ) {

			if (event.getItem().getType() == Material.ENCHANTED_BOOK && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Team Selection"))
			{

				event.setCancelled(true);

				Inventory inventory = new CraftInventoryCustom(event.getPlayer(), 9, ChatColor.RESET + "Team Selection");

				ItemStack item = new ItemStack(Material.NETHER_STAR);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "Auto Join");

				List<String> lore = new ArrayList<String>();

				if (HyperPVP.getMap().getType() != GameType.FFA) {

					TeamMap one = HyperPVP.getMap().getTeams().get(0);
					TeamMap two = HyperPVP.getMap().getTeams().get(1);

					lore.add(one.getColor() + "" + HyperPVP.getMap().getTeamMembers(one.getColor()).size() + ChatColor.WHITE + " / " + two.getColor() + HyperPVP.getMap().getTeamMembers(two.getColor()).size());
					lore.add(ChatColor.AQUA + "Puts you on the team with the fewest players.");
				} else {

					List<Session> topPlayers = HyperPVP.getMap().getTop();

					if (topPlayers.size() != 0) {

						Session inLead = topPlayers.get(0);
						lore.add(ChatColor.GOLD + inLead.getPlayer().getName() + ChatColor.WHITE + " currently has the most kills.");

					} else {
						lore.add(ChatColor.YELLOW + "No one is playing!");
					}

					lore.add(ChatColor.AQUA + "Puts you with everyone vs everyone.");
				}

				meta.setLore(lore);
				item.setItemMeta(meta);

				inventory.setItem(0, item);

				if (HyperPVP.getMap().getType() != GameType.FFA) {

					TeamMap one = HyperPVP.getMap().getTeams().get(0);
					TeamMap two = HyperPVP.getMap().getTeams().get(1);

					int rank = 0;

					try {
						rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + event.getPlayer().getName() + "'");
					} catch (SQLException e) {
						e.printStackTrace();
					}

					ItemStack teamOne = new ItemStack(Material.WOOL, 1, Helpers.getDye(TeamColor.get(one.getColor())).getData());
					ItemMeta teamOneMeta = teamOne.getItemMeta();
					teamOneMeta.setDisplayName(ChatColor.RESET + "" + one.getColor() + "" + ChatColor.BOLD + HyperPVP.capitalize(one.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team");
					lore.clear();
					//wolf
					if (rank == 2) {
						lore.add(ChatColor.GOLD + "Thank you for buying premium.");
						lore.add(ChatColor.GREEN + "Upgrade to " + ChatColor.AQUA + "Spider" + ChatColor.GREEN + " to join any team.");
					} else if (rank == 3 || rank == 4 || rank == 5) {
						//spyder
						lore.add(ChatColor.GOLD + "Thank you for buying premium!");
						lore.add(ChatColor.GREEN + "You can join any teams!");
					} else { 
						lore.add(ChatColor.GOLD + "Premium members can pick their teams!");
						lore.add(ChatColor.AQUA + "Buy premium at " + ChatColor.GREEN + "hyperpvp.us/shop");
					}
					teamOneMeta.setLore(lore);
					teamOne.setItemMeta(teamOneMeta);
					inventory.setItem(1, teamOne);

					ItemStack teamTwo = new ItemStack(Material.WOOL, 1, Helpers.getDye(TeamColor.get(two.getColor())).getData());
					ItemMeta teamTwoMeta = teamOne.getItemMeta();
					teamOneMeta.setDisplayName(ChatColor.RESET + "" + two.getColor() + "" + ChatColor.BOLD + HyperPVP.capitalize(two.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team");
					lore.clear();

					if (rank == 2) {
						lore.add(ChatColor.GOLD + "Thank you for buying premium.");
						lore.add(ChatColor.GREEN + "Upgrade to " + ChatColor.AQUA + "Spider" + ChatColor.GREEN + " to join any team.");
					} else if (rank == 3) {
						//spyder
						lore.add(ChatColor.GOLD + "Thank you for buying premium!");
						lore.add(ChatColor.GREEN + "You can join any teams!");
					} else { 
						lore.add(ChatColor.GOLD + "Premium members can pick their teams!");
						lore.add(ChatColor.AQUA + "Buy premium at " + ChatColor.GREEN + "hyperpvp.us/shop");
					}

					teamTwoMeta.setLore(lore);
					teamTwo.setItemMeta(teamOneMeta);
					inventory.setItem(2, teamTwo);
				} else {

					TeamMap one = HyperPVP.getMap().getTeams().get(0);
					ItemStack teamOne = new ItemStack(Material.WOOL, 1, Helpers.getDye(TeamColor.get(one.getColor())).getData());
					ItemMeta teamOneMeta = teamOne.getItemMeta();
					teamOneMeta.setDisplayName(ChatColor.RESET + "" + one.getColor() + "" + ChatColor.BOLD + HyperPVP.capitalize(one.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team");
					lore.clear();
					lore.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "There is only team as you are against everyone else!");
					teamOneMeta.setLore(lore);
					teamOne.setItemMeta(teamOneMeta);
					inventory.setItem(1, teamOne);

				}

				player.openInventory(inventory);

			}

			if (event.getItem().getType() == Material.ENCHANTED_BOOK && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("What is HyperPVP?"))
			{
				event.setCancelled(true);

				Inventory inventory = new CraftInventoryCustom(event.getPlayer(), 9, "About HyperPVP");

				List<String> lore = new ArrayList<String>();

				ItemStack rules = new ItemStack(Material.PAPER, 1);
				ItemMeta rulesMeta = rules.getItemMeta();
				rulesMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "The rules of HyperPVP");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "All rules can be found at " + ChatColor.GREEN + "hyperpvp.us/rules");
				rulesMeta.setLore(lore);
				rules.setItemMeta(rulesMeta);
				inventory.setItem(0, rules);

				ItemStack DTC = new ItemStack(Material.LAVA_BUCKET, 1);
				ItemMeta DTCMeta = rules.getItemMeta();
				DTCMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "What is DTC?");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "DTC is where the person must leak the other teams core to win.");
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "It stands for Destroy The Core.");
				DTCMeta.setLore(lore);
				DTC.setItemMeta(DTCMeta);
				inventory.setItem(1, DTC);

				ItemStack DTM = new ItemStack(Material.OBSIDIAN, 1);
				ItemMeta DTMMeta = rules.getItemMeta();
				DTMMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "What is DTM?");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "DTM is where you must fully destroy the other teams monument to win.");
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "It stands for Destroy The Monument.");
				DTMMeta.setLore(lore);
				DTM.setItemMeta(DTMMeta);
				inventory.setItem(2, DTM);

				ItemStack FFA = new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData());
				ItemMeta FFAMeta = rules.getItemMeta();
				FFAMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "What is FFA?");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Free for all forces every player against each other.");
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "No teams, no objectives.");
				FFAMeta.setLore(lore);
				FFA.setItemMeta(FFAMeta);
				inventory.setItem(3, FFA);

				ItemStack TDM = new ItemStack(Material.IRON_SWORD, 1);
				ItemMeta TDMMeta = rules.getItemMeta();
				TDMMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "What is TDM?");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "TDM pitches two teams against each other for an all out war.");
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "It stands for Team Death Match.");
				TDMMeta.setLore(lore);
				TDM.setItemMeta(TDMMeta);
				inventory.setItem(4, TDM);

				ItemStack RTC = new ItemStack(Material.DIAMOND_PICKAXE, 1);
				ItemMeta RTCMeta = rules.getItemMeta();
				RTCMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "" + ChatColor.BOLD + "What is RTC?");
				lore.clear();
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "RTC is when a team has to leak the core in the middle of the map.");
				lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "It stands for Race To Core.");
				RTCMeta.setLore(lore);
				RTC.setItemMeta(RTCMeta);
				inventory.setItem(5, RTC);

				player.openInventory(inventory);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		//HyperPVP.checkWorlds();

		/*if (event.getPlayer().getWorld() == HyperPVP.getDefaultWorld()) {
			int max = 66; // 500
			int least = -65; // -500

			if (event.getTo().getX() > max || event.getTo().getX() < least) {
				moveBack("You can't fly any further.", event);
			}

			if (event.getTo().getZ() > max || event.getTo().getZ() < least) {
				moveBack("You can't fly any further.", event);
			}
		}*/

		if (!HyperPVP.isCycling()) {
			for (Region region : HyperPVP.getMap().getRegions(RegionType.MAP)) {

				if (!region.hasLocation(event.getTo()) && event.getTo().getWorld() == HyperPVP.getMap().getWorld()) {
					moveBack(region.getAlert(), event);
				}
			}
		}

		/*if (HyperPVP.getGameSessions().containsKey(event.getPlayer().getName())) {

			Session session = HyperPVP.getSession(event.getPlayer());

			for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

				if (!region.getTeamWhitelist().contains(session.getTeam().getColor())) {

					if (region.hasLocation(event.getTo())) {
						moveBack(region.getAlert(), event);
					}
				} else {
					if (region.hasLocation(event.getTo())) {
						event.getPlayer().setFireTicks(0);
					}
				}
			}
		}*/
	}

	private void moveBack(String alert, PlayerMoveEvent event) {
		Location newLoc = event.getFrom();
		newLoc.setX(newLoc.getBlockX() + 0.5);
		newLoc.setY(newLoc.getBlockY());
		newLoc.setZ(newLoc.getBlockZ() + 0.5);
		event.getPlayer().teleport(newLoc);

		if (alert.length() != 0) {
			event.getPlayer().sendMessage(ChatColor.DARK_RED + alert);
		}

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (HyperPVP.isSpectator(event.getPlayer()) || HyperPVP.isCycling() && !event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {

		if (HyperPVP.isSpectator(event.getPlayer()) || HyperPVP.isCycling() && !event.getPlayer().isOp()) {
			event.setCancelled(true);
			return;
		}

		Item drop = event.getItemDrop();

		if (drop.getItemStack().getType() == Material.LEATHER_BOOTS || drop.getItemStack().getType() == Material.LEATHER_HELMET || drop.getItemStack().getType() == Material.LEATHER_CHESTPLATE || drop.getItemStack().getType() == Material.LEATHER_LEGGINGS ) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {

		if (!HyperPVP.isCycling()) {

			int minutes = HyperPVP.getTime();
			int left = HyperPVP.getMinutesLeft();

			int third = minutes / 3;

			int one = minutes;
			int two = minutes - third;
			int three = minutes - third - third;

			ChatColor status = null;

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
			event.setMotd(motd);// + "" + ChatColor.COLOR_CHAR + "" + (Bukkit.getOnlinePlayers().length + 2) + "" + ChatColor.COLOR_CHAR + Bukkit.getMaxPlayers());

		} else {

			String motd = ChatColor.AQUA + "Cycling " + ChatColor.RED + "> > >";

			event.setMotd(motd);// + "" + ChatColor.COLOR_CHAR + "" + (Bukkit.getOnlinePlayers().length + 2) + "" + ChatColor.COLOR_CHAR + Bukkit.getMaxPlayers());
		}

		//HyperPVP.checkWorlds(null);

	}

	@EventHandler
	public void onPressurePlateStep(PlayerInteractEvent e)
	{
		if (HyperPVP.isSpectator(e.getPlayer()) || HyperPVP.isCycling() || !HyperPVP.hasMatchBeenAnnounced()) {
			if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock().getType() == Material.STONE_PLATE) {
				e.setCancelled(true);
			}
			return;
		}

		if (!HyperPVP.getMap().getFeatures().contains("jumppreassureplate")) {
			return;
		}

		if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock().getType() == Material.STONE_PLATE) {

			Player p = e.getPlayer();

			double strength = 2.0;
			double up = 2.0;

			Vector v = p.getLocation().getDirection().multiply(strength).setY(up);
			p.setVelocity(v);
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_FLAP, 10.0F, 2.0F);
			e.setCancelled(true);
		}
	}


}
