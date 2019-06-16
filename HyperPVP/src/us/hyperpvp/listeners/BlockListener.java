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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.region.Region;
import us.hyperpvp.game.map.region.RegionType;
import us.hyperpvp.game.map.team.Detonator;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.session.ScoreType;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.misc.CycleUtil;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.isCancelled()) {
			return;
		}

		if ((HyperPVP.isSpectator(event.getPlayer()) || HyperPVP.isCycling()) && !event.getPlayer().isOp()) {

			/*if (!HyperPVP.isCycling()) {
				event.getPlayer().sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "Please type " + ChatColor.GOLD + "/join" + ChatColor.ITALIC + "" + ChatColor.GRAY +  " to place blocks!");
			}*/
			event.setCancelled(true);

			return;

		} else {

			if (!HyperPVP.hasMatchBeenAnnounced()) {
				event.setCancelled(true);
				return;
			}

			for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

				if (region.hasLocation(event.getBlock().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to modify the team spawn.");
					event.setCancelled(true);
					return;
				}
			}

			Session session = HyperPVP.getSession(event.getPlayer());

			for (Region region : HyperPVP.getMap().getRegions(RegionType.BLOCK_PLACE_DESTORY)) {

				if (region.hasLocation(event.getBlock().getLocation()) && region.getBlocks().contains(event.getBlock().getType())) {
					if (region.getTeamWhitelist().size() == 0) {
						event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " in here");
						event.setCancelled(true);
						return;
					} else {
						if (region.getTeamWhitelist().contains(session.getTeam().getColor())) {
							event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " on your own teams side.");
							event.setCancelled(true);
							return;
						}
					}
				}
			}

			/*if (region.getTeamWhitelist().size() != 0) {
					Session session = HyperPVP.getSession(event.getPlayer());

					if (region.getTeamWhitelist().contains(session.getTeam().getColor()) && region.hasLocation(event.getBlock().getLocation()) && region.getBlocks().contains(event.getBlock().getType())) {
						if (region.getTeamWhitelist().size() != 0) {
							event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " on your own teams side.");
						} else {
							event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " in here");

						}

						event.setCancelled(true);
						return;
					}
				}

				if (region.hasLocation(event.getBlock().getLocation()) && region.getBlocks().contains(event.getBlock().getType())) {

					if (region.getTeamWhitelist().size() != 0) {
						event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " on your own teams side.");
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " in here");

					}

					event.setCancelled(true);
					return;
				}*/

			if (HyperPVP.getMap().getFeatures().contains("specialtntplace")) {

				if (!(event.getBlock().getType() == Material.TNT)) {
					return;
				}

				Block b = event.getBlock();
				Location location = b.getLocation();

				b.setType(Material.AIR);

				Entity tnt = HyperPVP.getMap().getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
				HyperPVP.getDetonators().put(new Detonator(tnt), HyperPVP.getSession(event.getPlayer()));
				//tnt.setMetadata("detonator", new FixedMetadataValue(HyperPVP.getJavaPlugin(), event.getPlayer()));
				//tnt.
			}


			if (HyperPVP.getMap().getType() == GameType.DTM) {

				if (event.getBlock().getType() != Material.OBSIDIAN) {
					return;
				}

				for (Session s : HyperPVP.getPlayers()) {

					if (s.getMonumentBlocks().contains(event.getBlock())) {
						s.getMonumentBlocks().remove(event.getBlock());
					}
				}
			}

			if (HyperPVP.getMap().getType() == GameType.DTC) {

				if (event.getBlock().getType() != Material.OBSIDIAN) {
					return;
				}

				for (Region region : HyperPVP.getMap().getRegions(RegionType.DTC)) {

					if (region.hasLocation(event.getBlock().getLocation())) {

						event.setCancelled(true);
						return;
					}
				}
			}

			if (HyperPVP.getMap().getType() == GameType.RTC) {

				if (event.getBlock().getType() == Material.OBSIDIAN) {
					return;
				}

				Region region = HyperPVP.getMap().getRegions(RegionType.RTC).get(0); 

				if (region.hasLocation(event.getBlockAgainst().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You cannot place a block against the core while in RTC.");
					event.setCancelled(true);
					return;
				}

				if (region.hasLocation(event.getBlock().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You cannot place a block against the core while in RTC.");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		if ((HyperPVP.isSpectator(event.getPlayer()) || HyperPVP.isCycling()) && !event.getPlayer().isOp()) {

			/*if (!HyperPVP.isCycling()) {
				event.getPlayer().sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "Please type " + ChatColor.GOLD + "/join" + ChatColor.ITALIC + "" + ChatColor.GRAY +  " to break blocks!");
			}*/
			event.setCancelled(true);

			return;
		}


		else  {

			if (HyperPVP.getMap().getFeatures().contains("noblockbreak") && HyperPVP.hasMatchBeenAnnounced() && !HyperPVP.isCycling()) {
				event.getPlayer().sendMessage(ChatColor.RED + "This map does not allow you to break blocks.");
				event.setCancelled(true);
				return;
			}

			if (!HyperPVP.hasMatchBeenAnnounced()) {
				event.setCancelled(true);
				return;
			}


			for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

				if (region.hasLocation(event.getBlock().getLocation())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to modify the team spawn.");
					event.setCancelled(true);
					return;
				}
			}

			Session session = HyperPVP.getSession(event.getPlayer());

			for (Region region : HyperPVP.getMap().getRegions(RegionType.BLOCK_PLACE_DESTORY)) {

				//if (region.getTeamWhitelist().contains(session.getTeam().getColor())) {

				if (region.hasLocation(event.getBlock().getLocation()) && region.getBlocks().contains(event.getBlock().getType())) {
					event.getPlayer().sendMessage(ChatColor.RED + "You can't break " + event.getBlock().getType().name().toLowerCase().replace("_", " ") + " within this arena.");
					event.setCancelled(true);
					return;
				}
				//}
			}

			if (HyperPVP.getMap().getType() == GameType.DTM) {

				if (event.getBlock().getType() != Material.OBSIDIAN) {
					return;
				}

				for (Region region : HyperPVP.getMap().getRegions(RegionType.DTM)) {

					if (!region.hasLocation(event.getBlock().getLocation())) {
						continue;
					}

					if (region.getTeamWhitelist().contains(session.getTeam().getColor())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.WHITE + "You can't break your own monument!");
						return;
					} else {

						HyperPVP.getGame().getMapManager().getGameMap().resetWhoBroke();
						session.setDestroyer(true);

						session.getMonumentBlocks().add(event.getBlock());		

						if (HyperPVP.getGame().getMapManager().getGameMap().countTeamBlocksDestroyed(session.getTeam()) == region.getMonumentBlocks().size()) {

							TeamMap winningTeam = null;
							Session leaker = null;

							for (Session winner : HyperPVP.getPlayers()) {
								if (winner.isDestroyer()) {
									winningTeam = session.getTeam();
									leaker = session;

								}
							}

							ChatColor lost = region.getTeamWhitelist().get(0);

							Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " * " + lost + HyperPVP.capitalize(lost.name().toLowerCase().replace("_", " ").replace("dark ", "")) + ChatColor.GOLD + " teams's monument has been destroyed by " + ChatColor.AQUA + leaker.getPlayer().getName() + ChatColor.GOLD + "!" + ChatColor.DARK_PURPLE + " * ");

							if (HyperPVP.getPlayers().size() >= 2) {
								leaker.updateStats(ScoreType.MONUMENT);
							}

							CycleUtil.cycleNext(true, winningTeam, null);
						}

					}
				}
			}


			if (HyperPVP.getMap().getType() == GameType.DTC) {

				if (event.getBlock().getType() != Material.OBSIDIAN) {
					return;
				}

				for (Region region : HyperPVP.getMap().getRegions(RegionType.DTC)) {

					if (!region.hasLocation(event.getBlock().getLocation())) {
						continue;
					}

					if (region.getTeamWhitelist().contains(session.getTeam().getColor())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.WHITE + "You can't break your own core!");
						return;
					} else {

						HyperPVP.getGame().getMapManager().getGameMap().resetWhoBroke();
						session.setDestroyer(true);
					}
				}
			}

			if (HyperPVP.getMap().getType() == GameType.RTC) {

				if (event.getBlock().getType() != Material.OBSIDIAN) {
					return;
				}

				Region region = HyperPVP.getMap().getRegions(RegionType.RTC).get(0); 

				if (!region.hasLocation(event.getBlock().getLocation())) {
					return;
				}


				HyperPVP.getGame().getMapManager().getGameMap().resetWhoBroke();
				session.setDestroyer(true);
			}

			if (event.getBlock().getType() == Material.OBSIDIAN) {

				if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.DIAMOND_PICKAXE) {
					event.getPlayer().sendMessage("Only diamond pickaxes can be used to break the core");
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		try {
			if (!HyperPVP.hasMatchBeenAnnounced()) {
				event.setCancelled(true);
			}

			if (HyperPVP.getMap().getType() == GameType.RTC) {

				if (event.getBlock().getType() == Material.LAVA) {
					for (Region region : HyperPVP.getMap().getRegions(RegionType.RTC)) {
						if (region.hasLocation(event.getBlock().getLocation()) && !region.hasLocation(event.getToBlock().getLocation())) {

							TeamMap winningTeam = null;
							Session leaker = null;

							for (Session session : HyperPVP.getPlayers()) {
								if (session.isDestroyer()) {
									winningTeam = session.getTeam();
									leaker = session;

								}
							}

							Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " * " + ChatColor.GOLD + " The core has been LEAKED by " + ChatColor.AQUA + leaker.getPlayer().getName() + ChatColor.GOLD + "!" + ChatColor.DARK_PURPLE + " * ");

							if (HyperPVP.getPlayers().size() >= 2) {
								leaker.updateStats(ScoreType.CORE);
							}

							CycleUtil.cycleNext(true, winningTeam, null);
						}
					}
				}
			}

			if (HyperPVP.getMap().getType() == GameType.DTC) {
				if (event.getBlock().getType() == Material.LAVA) {
					for (Region region : HyperPVP.getMap().getRegions(RegionType.DTC)) {
						if (region.hasLocation(event.getBlock().getLocation()) && !region.hasLocation(event.getToBlock().getLocation())) {
							TeamMap winningTeam = null;
							Session leaker = null;

							for (Session session : HyperPVP.getPlayers()) {
								if (session.isDestroyer()) {
									winningTeam = session.getTeam();
									leaker = session;

								}
							}

							ChatColor lost = region.getTeamWhitelist().get(0);

							Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + " * " + lost + HyperPVP.capitalize(lost.name().toLowerCase().replace("_", " ").replace("dark ", "")) + ChatColor.GOLD + " teams's core has been LEAKED by " + ChatColor.AQUA + leaker.getPlayer().getName() + ChatColor.GOLD + "!" + ChatColor.DARK_PURPLE + " * ");

							if (HyperPVP.getPlayers().size() >= 2) {
								leaker.updateStats(ScoreType.CORE);	
							}

							CycleUtil.cycleNext(true, winningTeam, null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

		if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}

		if (!HyperPVP.hasMatchBeenAnnounced()) {
			event.setCancelled(true);
			return;
		}

		for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

			if (region.hasLocation(event.getBlockClicked().getLocation())) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't empty a bucket on a spawn!");
				event.setCancelled(true);
				return;
			}
		}

		if (HyperPVP.getMap().getType() == GameType.DTC || HyperPVP.getMap().getType() == GameType.RTC) {

			for (Region region : HyperPVP.getMap().getRegions(RegionType.DTC)) {

				if (region.hasLocation(event.getBlockClicked().getLocation())) {
					event.getPlayer().sendMessage("You can't empty a bucket on a core!");
					event.setCancelled(true);
				}
			}
		}

	}


	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {

		if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}

		if (!HyperPVP.hasMatchBeenAnnounced()) {
			event.setCancelled(true);
			return;
		}

		for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

			if (region.hasLocation(event.getBlockClicked().getLocation())) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't fill a bucket on a spawn!");
				event.setCancelled(true);
			}
		}

		if (HyperPVP.getMap().getType() == GameType.DTC || HyperPVP.getMap().getType() == GameType.RTC) {

			for (Region region : HyperPVP.getMap().getRegions(RegionType.DTC)) {

				if (region.hasLocation(event.getBlockClicked().getLocation())) {
					event.getPlayer().sendMessage("You can't fill a bucket from a core!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {


		/*if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}*/
	}

	@EventHandler(priority=EventPriority.MONITOR)
	protected void onBlockSpread(BlockGrowEvent event) {
		/*if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}*/
	}

	@EventHandler(priority=EventPriority.MONITOR)
	protected void onBlockIgnite(BlockIgniteEvent event) {


		/*if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}*/
	}
}
