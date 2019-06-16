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
package us.hyperpvp.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.GameMap;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.map.team.TeamColor;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.listeners.MiscListener;
import us.hyperpvp.thread.CycleThread;
import us.hyperpvp.thread.FightThread;
import us.hyperpvp.thread.misc.IThread;
import us.hyperpvp.thread.misc.ThreadType;

public class CycleUtil {

	@SuppressWarnings("deprecation")
	public static void resetSpectatorInventory(Player player) {

		player.getInventory().clear();

		/*ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle(ChatColor.RESET + "" + ChatColor.AQUA + "" + ChatColor.BOLD + "Welcome to Hyper PVP");
		bookMeta.setAuthor("Quackdot");
		List<String> pages = new ArrayList<String>();
		pages.add("Welcome to HyperPVP.\n\nRight you are in the spawn hub.\n\nTo join a game type /join\n\nTo watch others type /spectate");
		pages.add("Please note you can see your kills at\n\nhttp://hyperpvp.us/profile/" + player.getName());
		pages.add("Hope you have fun! :)");
		bookMeta.setPages(pages);
		book.setItemMeta(bookMeta);
		player.getInventory().addItem(book);*/

		/*ItemStack clock = new ItemStack(Material.WATCH);
		ItemMeta clockMeta = clock.getItemMeta();
		clockMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "The Magic Clock");//
		clockMeta.setLore(Arrays.asList(new String[] {ChatColor.RESET + "" + ChatColor.YELLOW + "Hides all other spectators."}));
		clock.setItemMeta(clockMeta);

		player.getInventory().addItem(clock);*/

		player.getInventory().setItem(0, new ItemStack(Material.COMPASS, 1));

		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "" + ChatColor.BOLD + "Team Selection");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Use this star to easily select your team!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().setItem(1, item);

		item = new ItemStack(Material.ENCHANTED_BOOK);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.RED + "" + ChatColor.BOLD + "What is HyperPVP?");
		lore.clear();
		lore.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Right click this book to further explain HyperPVP!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		player.getInventory().setItem(2, item);


		player.updateInventory(); 


	}

	@SuppressWarnings("deprecation")
	public static void resetInventory(Player player, boolean firstSpawn) {

		Session session = HyperPVP.getSession(player);

		player.getInventory().clear();
		player.updateInventory();

		for (ItemStack stack : HyperPVP.getMap().getItems()) {

			if (!firstSpawn) {
				if (stack.getType() == Material.ARROW) {
					if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {

						if (player.getLastDamageCause() != null) {
							if (player.getLastDamageCause().getCause() != null) {
								if (player.getLastDamageCause().getCause() == DamageCause.VOID) {
									continue;
								}
							}
						}
					}
				}
			}

			player.getInventory().addItem(stack);
		}


		ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
		LeatherArmorMeta lam = (LeatherArmorMeta)lhelmet.getItemMeta();
		lam.setColor(TeamColor.get(session.getTeam().getColor()));
		lhelmet.setItemMeta(lam);
		player.getInventory().setHelmet(lhelmet);

		lhelmet = new ItemStack(Material.LEATHER_BOOTS, 1);
		lam = (LeatherArmorMeta)lhelmet.getItemMeta();
		lam.setColor(TeamColor.get(session.getTeam().getColor()));
		lhelmet.setItemMeta(lam);
		player.getInventory().setBoots(lhelmet);

		lhelmet = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		lam = (LeatherArmorMeta)lhelmet.getItemMeta();
		lam.setColor(TeamColor.get(session.getTeam().getColor()));
		lhelmet.setItemMeta(lam);
		player.getInventory().setChestplate(lhelmet);

		lhelmet = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		lam = (LeatherArmorMeta)lhelmet.getItemMeta();
		lam.setColor(TeamColor.get(session.getTeam().getColor()));
		lhelmet.setItemMeta(lam);
		player.getInventory().setLeggings(lhelmet);



		player.updateInventory();
	}

	public static void checkWorlds() {

		if (HyperPVP.getCheckFirework()) {

			for (Entry<Location, Color> set : HyperPVP.getFireworkLocation().entrySet()) {

				Firework firework = set.getKey().getWorld().spawn(set.getKey(), Firework.class);
				FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();

				int type = HyperPVP.getRandom().nextInt(3);

				if (type == 0) {
					data.addEffects(FireworkEffect.builder().withColor(set.getValue()).with(Type.BALL_LARGE).build());
				}

				if (type == 1) {
					data.addEffects(FireworkEffect.builder().withColor(set.getValue()).with(Type.CREEPER).build());
				}

				if (type == 2) {
					data.addEffects(FireworkEffect.builder().withColor(set.getValue()).with(Type.STAR).build());
				}

				data.setPower(2);
				firework.setFireworkMeta(data);
			}

			HyperPVP.setCheckFirework(false);
			HyperPVP.getFireworkLocation().clear();
		}

		if (!HyperPVP.isCycling()) {
			for (Session session : HyperPVP.getSessions().values()) {

				Player player = session.getPlayer();
				
				if (player.getWorld() != HyperPVP.getMap().getWorld()/* && player.getWorld() != HyperPVP.getDefaultWorld()*/)  {

					HyperPVP.setListName(ChatColor.AQUA, player);
					resetSpectatorInventory(player);
					player.teleport(HyperPVP.getMap().getSpawn());
				}
			}

		}

		if (HyperPVP.isCycling()) {
			for (Item entity : HyperPVP.getMap().getWorld().getEntitiesByClass(Item.class)) {
				entity.remove();
			}
		}

		if (HyperPVP.getNeedsCycleThread()) {
			HyperPVP.setNeedsCycleThread(false);
			cycleNext(true, null, null);
		}

		if (HyperPVP.getNeedsGameThread()) {
			HyperPVP.setNeedsGameThread(false);

			HyperPVP.resetScoreboard();
			HyperPVP.getTeamCycle().clear();

			for (Player player : Bukkit.getOnlinePlayers()) {
				MiscListener.refreshTag(player);
			}

			if (HyperPVP.getPreviousWorld() != null ) {
				if (HyperPVP.getPreviousWorld().isLoaded()) {
					HyperPVP.getPreviousWorld().dispose(true);
				}
			}

			HyperPVP.getThreads().put(ThreadType.FIGHT, new FightThread());
			HyperPVP.getThreads().get(ThreadType.FIGHT).start();
		}

		if (HyperPVP.getNeedsMatchCheck()) {
			HyperPVP.setNeedsMatchCheck(false);


			for (Session player : HyperPVP.getPlayers()) {
				player.setPlaying(true);
				startMatch(player.getPlayer());
			}

			CycleUtil.refreshShowHidePlayer();
			
		}

	}
	
	public static void cycleNext(boolean thread, TeamMap team, String mapName) {

		if (thread) {

			HyperPVP.setCycling(true);
			
			for (Entry<ThreadType, IThread> set : HyperPVP.getThreads().entrySet()) {

				if (set.getKey() != ThreadType.ANNOUNCE) {
					set.getValue().setCancelled(true);
				}
			}


			if (HyperPVP.getMap().getType() == GameType.CONQUEST) {
				
				HyperPVP.setWinningTeam(HyperPVP.getMap().getTeamTicketsWinning());
				HyperPVP.setWinningPlayer(null);
				
				TeamMap losing = HyperPVP.getMap().getTeamTicketsLosing();

				if (team != null) {
					HyperPVP.setWinningTeam(team);
				}

				if (HyperPVP.getWinningTeam() != null) {
					
					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " * " + losing.getColor() + 
							HyperPVP.capitalize(losing.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + 
							ChatColor.GOLD + " has ran out of tickets" + ChatColor.GOLD + "!" + ChatColor.DARK_PURPLE + " * ");

					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " # # # # # # # # # # # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "Game Over!" + ChatColor.DARK_PURPLE + "    # #\n" +
							ChatColor.DARK_PURPLE + "# # " + HyperPVP.getWinningTeam().getColor() + HyperPVP.capitalize(HyperPVP.getWinningTeam().getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team wins! " + ChatColor.DARK_PURPLE + "# #\n" +
							ChatColor.DARK_PURPLE + "# # # # # # # # # # # #\n");
				} else {

					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " # # # # # # # # # # # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "Game Over!" + ChatColor.DARK_PURPLE + "    # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "No one won!" + ChatColor.DARK_PURPLE + "  # #\n" +
							ChatColor.DARK_PURPLE + "# # # # # # # # # # # #\n");
				}
				
			} else if (HyperPVP.getMap().getType() == GameType.FFA) {
				

				List<Session> topPlayers = HyperPVP.getMap().getTop();

				int i = 1;

				if (topPlayers.size() > 0) {
					Bukkit.broadcastMessage(ChatColor.GRAY + "The top 10 so far players are..");
				}

				for (Session set : topPlayers) {

					if (i > 10 && set.getKills() != 0) {
						continue;
					}

					Bukkit.broadcastMessage(i + ". " + ChatColor.GOLD + set.getPlayer().getName() + ChatColor.WHITE + " with " + set.getKills() + "!");

					i++;
				}

				if (topPlayers.size() != 0) {

					if (topPlayers.size() > 1) {

						Session inLead = topPlayers.get(0);
						Session inLeadTwo = topPlayers.get(1);

						if (inLead.getKills() == inLeadTwo.getKills()) {
							HyperPVP.setWinningPlayer(null);
						} else {

							HyperPVP.setWinningPlayer(topPlayers.get(0).getPlayer());

						}
					} else {
						HyperPVP.setWinningPlayer(topPlayers.get(0).getPlayer());
					}
				}
			} else {
				HyperPVP.setWinningTeam(HyperPVP.getMap().getTeamWinning());
				HyperPVP.setWinningPlayer(null);

				if (team != null) {
					HyperPVP.setWinningTeam(team);
				}

				if (HyperPVP.getWinningTeam() != null) {

					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " # # # # # # # # # # # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "Game Over!" + ChatColor.DARK_PURPLE + "    # #\n" +
							ChatColor.DARK_PURPLE + "# # " + HyperPVP.getWinningTeam().getColor() + HyperPVP.capitalize(HyperPVP.getWinningTeam().getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team wins! " + ChatColor.DARK_PURPLE + "# #\n" +
							ChatColor.DARK_PURPLE + "# # # # # # # # # # # #\n");
				} else {

					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " # # # # # # # # # # # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "Game Over!" + ChatColor.DARK_PURPLE + "    # #\n" +
							ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "No one won!" + ChatColor.DARK_PURPLE + "  # #\n" +
							ChatColor.DARK_PURPLE + "# # # # # # # # # # # #\n");
				}
			}


			GameMap newMap = null;
			if (mapName == null) {
				newMap = HyperPVP.getGame().getMapManager().changeWorld();
			} else {
				newMap = HyperPVP.getGame().getMapManager().changeWorld(mapName);
			}

			List<Session> sessions = HyperPVP.getPlayers();

			for (Session game : sessions) {

				HyperPVP.getTeamCycle().put(game.getPlayer(), game.getTeam().getColor());
				game.getPlayer().closeInventory();
				game.leaveGame(false, true);
				MiscListener.refreshTag(game.getPlayer());
			}
			
			HyperPVP.updateScoreboards();
			
			Bukkit.getScheduler().cancelTask(HyperPVP.getCallId());
			HyperPVP.getThreads().put(ThreadType.CYCLE, new CycleThread(HyperPVP.getWinningTeam(), HyperPVP.getWinningPlayer(), newMap));
			HyperPVP.getThreads().get(ThreadType.CYCLE).start();

			HyperPVP.setCallId(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HyperPVP.getPlugin(), getCheckTask(), 0, 10));

		} else {

			for (Entry<ThreadType, IThread> set : HyperPVP.getThreads().entrySet()) {
				set.getValue().setCancelled(true);
			}

			HyperPVP.getThreads().clear();
		}
	}

	public static void visibilityStatus(boolean canSee, Player toSee) {
		Plugin plugin = HyperPVP.getPlugin();

		for (Session session : HyperPVP.getSessions().values()) {
			for (Session otherSession : HyperPVP.getSessions().values()) {

				Player player = session.getPlayer();
				Player otherPlayer = otherSession.getPlayer();
				
				if (canSee) {
					player.showPlayer(plugin, otherPlayer);
					otherPlayer.showPlayer(plugin, player);
				} else {
					player.hidePlayer(plugin, otherPlayer);
					otherPlayer.hidePlayer(plugin, player);
				}
			}
		}
	}

	public static void refreshShowHidePlayer() {
		Plugin plugin = HyperPVP.getPlugin();

		for (Session session : HyperPVP.getSessions().values()) {
			for (Session otherSession : HyperPVP.getSessions().values()) {

				Player player = session.getPlayer();
				Player otherPlayer = otherSession.getPlayer();

					player.showPlayer(plugin, otherPlayer);
					otherPlayer.showPlayer(plugin, player);
			}
		}
		
		for (Session session : HyperPVP.getSessions().values()) {
			for (Session otherSession : HyperPVP.getSessions().values()) {
				
				Player player = session.getPlayer();
				Player otherPlayer = otherSession.getPlayer();
				
				if (otherSession.isDead()) {
					player.hidePlayer(plugin, otherPlayer);
					otherPlayer.hidePlayer(plugin, player);
				}
				
				if (HyperPVP.isSpectator(player)) {
					if (HyperPVP.isGamePlayer(otherPlayer)) {
						otherPlayer.hidePlayer(plugin, player);
						player.showPlayer(plugin, otherPlayer);
					}
				}
				
				if (HyperPVP.isGamePlayer(player)) {
					if (HyperPVP.isSpectator(otherPlayer)) {
						player.hidePlayer(plugin, otherPlayer);
						otherPlayer.showPlayer(plugin, player);
					}
				}

				if (HyperPVP.isGamePlayer(player)) {
					if (HyperPVP.isGamePlayer(otherPlayer)) {
						otherPlayer.showPlayer(plugin, player);
						player.showPlayer(plugin, otherPlayer);
					}
				}

				if (HyperPVP.isSpectator(player)) {
					if (HyperPVP.isSpectator(otherPlayer)) {

						otherPlayer.showPlayer(plugin, player);
						player.showPlayer(plugin, otherPlayer);
					}
				}
			}
		}
	}

	public static void startMatch(Player client) {
		if (!HyperPVP.isGamePlayer(client)) {
			return;
		}
			
		resetInventory(client, true);
		HyperPVP.getSession(client).resetKillTimer();
		client.teleport(HyperPVP.getSession(client).getGameSpawn());
		client.setGameMode(GameMode.SURVIVAL);
		client.sendMessage(HyperPVP.getMap().matchInfoToString(client));
	}

	public static void addGameSession(Player player) {
		HyperPVP.getSession(player).setPlaying(true);
	}

	public static Runnable getCheckTask() {
		return new Runnable() {
			public void run() {
				CycleUtil.checkWorlds();
			}
		};
	}
}
