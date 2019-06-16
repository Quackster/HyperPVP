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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.region.Region;
import us.hyperpvp.game.map.region.RegionType;
import us.hyperpvp.game.map.team.Detonator;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.misc.Helpers;

public class EntityListener implements Listener {

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntity() instanceof Player) {
			if (HyperPVP.getSpectators().contains(((Player)event.getEntity()).getName())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {

		Projectile projectile = event.getEntity();

		if (!(projectile instanceof Arrow))
			return;

		Arrow arrow = (Arrow)projectile;

		if (!(arrow.getShooter() instanceof Player)) {
			return;
		}

		if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
			arrow.remove();
		}

		BlockIterator bi = new BlockIterator(arrow.getWorld(), arrow.getLocation().toVector(), arrow.getVelocity().normalize(), 0, 4);

		while (bi.hasNext())
		{
			final Block hit = bi.next();

			if (!(arrow.getShooter() instanceof Player)) {
				return;
			}

			//final Player shooter = (Player) arrow.getShooter();

			if (hit.getType() == Material.GLASS || hit.getType() == Material.GLOWSTONE || hit.getType() == Material.GLASS_PANE)
			{
				//this.sendBlockBreakParticles(hit, hit.getLocation());

				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendBlockChange(hit.getLocation(), Material.AIR, (byte) 0);
				}

				arrow.remove();

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HyperPVP.getPlugin(), new Runnable() {

					@Override
					public void run() {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.sendBlockChange(hit.getLocation(), hit.getType(), (byte) 0);
						}
					}
				}, Helpers.getSecondByTick(2));

				break;
			}
		}
	}


	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (HyperPVP.isCycling()) {
			event.getEntity().remove();
			shooter.sendMessage(ChatColor.RED + "You can't fire an " + event.getEntityType().name().toLowerCase().replace("_", " ") + " while cycling!");
			event.setCancelled(true);

		}

		for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {
			if (region.hasLocation(shooter.getLocation())) {
				event.getEntity().remove();
				shooter.sendMessage(ChatColor.RED + "You can't fire an " + event.getEntityType().name().toLowerCase().replace("_", " ") + " inside a team spawn!");
				event.setCancelled(true);
				return;
			}
		}

		if (HyperPVP.getMap().getFeatures().contains("tntarrows")) {
			Entity entity = HyperPVP.getMap().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
			entity.setVelocity(event.getEntity().getVelocity());
			event.getEntity().remove();	
		}

		if (HyperPVP.getMap().getFeatures().contains("highvelocity")) {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(3));
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {

		if (HyperPVP.getMap().getFeatures().contains("tntarrows")) {
			if (event.getEntityType() == EntityType.PRIMED_TNT) {
				event.blockList().clear();
			}
		}

		List<Block> exploded = new ArrayList<Block>();

		if (HyperPVP.getMap().getFeatures().contains("specialtntplace")) {

			if (event.getEntityType() == EntityType.PRIMED_TNT) {

				if (!event.getEntity().hasMetadata("detonator")) {
					return;
				}

				Session session = null;
				Detonator detonator = null;
				for (us.hyperpvp.game.map.team.Detonator d : HyperPVP.getDetonators().keySet()) {
					if (d.getTnt() == event.getEntity()) { 
						detonator = d;
						session = HyperPVP.getDetonators().get(d);
						continue;
					}
				}

				for (Block b : event.blockList()) {
					exploded.add(b);
				}

				event.blockList().clear();

				for (Block b : exploded) {
					for (Region region : HyperPVP.getMap().getRegions(RegionType.BLOCK_PLACE_DESTORY)) {

						if (region.hasLocation(b.getLocation()) && region.getBlocks().contains(b.getType()) && !region.getTeamWhitelist().contains(session.getTeam().getColor())) {

						} else {
							event.blockList().add(b);
						}
					}
				}

				HyperPVP.getDetonators().remove(detonator);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {

		if (event instanceof EntityDamageByEntityEvent) {
			return;
		}

		if (event.getEntity() instanceof Player) {

			Player hurt = (Player) event.getEntity();

			if (HyperPVP.isGamePlayer(hurt)) {
				if (HyperPVP.getSession(hurt).getKillTimer() > (System.currentTimeMillis() / 1000L)) {
					event.setCancelled(true);
					return;
				}
			}

			if (hurt.getGameMode() == GameMode.CREATIVE) {
				
				hurt.setFallDistance(0F);
				
				if (event.getCause() == DamageCause.VOID) {
					
					if (HyperPVP.isCycling() && HyperPVP.getPreviousWorld() != null) {
						hurt.teleport(HyperPVP.getPreviousWorld().getSpawn());
					} else if ((HyperPVP.isGamePlayer(hurt))) {
						hurt.teleport(HyperPVP.getMap().getRandomSpawn(hurt));
					} else {
						hurt.teleport(HyperPVP.getMap().getSpawn());
					}
				}
				
				event.setCancelled(true);
			}

			if (HyperPVP.isGamePlayer(hurt)) {
				if (event.getDamage() >= hurt.getHealth()) {
					HyperPVP.getSession(hurt).handleDeath(event);
					event.setCancelled(true);
				}
			}

		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (event.getEntity() instanceof Monster || event.getEntity() instanceof Animals) {

			Player damager = null;

			if (event.getDamager() instanceof Projectile) {

				if (((Projectile)event.getDamager()).getShooter() instanceof Player) {
					damager = (Player) ((Projectile) event.getDamager()).getShooter();
				}

			} else if (event.getDamager() instanceof Player) {
				damager = (Player) event.getDamager();
			}

			if (damager == null) {
				return;
			}

			if (damager.getGameMode() == GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}

		if (event.getEntity() instanceof Player) {

			Player hurt = (Player) event.getEntity();

			if (HyperPVP.getGame().getMapManager().getGameMap().isInsideBase(hurt)) {
				event.setCancelled(true);
				return;
			}

			Player damager = null;
			Session sessionDamage = null;
			Session sessionHurt = null;

			if (event.getDamager() instanceof Projectile) {

				if (((Projectile)event.getDamager()).getShooter() instanceof Player) {
					damager = (Player) ((Projectile) event.getDamager()).getShooter();
				}

			} else if (event.getDamager() instanceof Player) {
				damager = (Player) event.getDamager();
			}

			if (damager == null) {
				return;
			}

			if (damager.getGameMode() == GameMode.CREATIVE || damager.getGameMode() == GameMode.ADVENTURE) {
				
				if (!HyperPVP.isCycling() || !HyperPVP.hasMatchBeenAnnounced()) {
					damager.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "Please type " + ChatColor.GOLD + "/join" + ChatColor.ITALIC + "" + ChatColor.GRAY +  " to start playing");
				}
				
				event.setCancelled(true);
				return;
			}

			for (Region region : HyperPVP.getMap().getRegions(RegionType.TEAM)) {

				if (region.hasLocation(hurt.getLocation())) {

					if (event.getDamager() instanceof Projectile) {
						event.getDamager().remove();
					}

					damager.sendMessage(ChatColor.RED + "You can't hurt someone inside a team spawn!");
					event.setCancelled(true);
				}
			}

			sessionDamage = HyperPVP.getSession(damager);
			sessionHurt = HyperPVP.getSession(hurt);

			if (sessionDamage.expiredKillTimer()) {
				if (!sessionHurt.expiredKillTimer()) {

					event.setCancelled(true);

					if (event.getDamager() instanceof Arrow && event.getDamager() != event.getEntity()) {

						if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
							damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));
						}
					}

					return;
				}
			} else {

				event.setCancelled(true);

				if (event.getDamager() instanceof Arrow && event.getDamager() != event.getEntity()) {
					if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
						damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));
					}
				}

				return;
			}

			if (HyperPVP.getMap().getType() != GameType.FFA) {
				if (sessionDamage.getTeam().getColor() == sessionHurt.getTeam().getColor()) {
					event.setCancelled(true);
					return;
				}
			}

			if (event.getDamager() instanceof Snowball) {
				if (HyperPVP.getMap().getFeatures().contains("deadlysnowball")) {
					damager.playSound(damager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
					event.setDamage(2.F);
				}
			}

			if (event.getDamager() instanceof Arrow && event.getDamager() != event.getEntity()) {
				if (HyperPVP.getMap().getFeatures().contains("oneshotarrow")) {
					event.setDamage(1000);
				}
			}

			sessionHurt.setLastDamagedBy(sessionDamage.getPlayer());

			if (HyperPVP.isGamePlayer(hurt)) {
				if (event.getDamage() >= hurt.getHealth()) {

					sessionHurt.handleDeath(event);
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onFoodLevelChange (FoodLevelChangeEvent event) {

		if (HyperPVP.getMap().getFeatures().contains("nohunger")) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {

		if (HyperPVP.getMap().getFeatures().contains("fastregen")) {
			if (event.getRegainReason() == RegainReason.SATIATED) {
				event.setAmount(event.getAmount() + 1);
			}
		}

	}
}
