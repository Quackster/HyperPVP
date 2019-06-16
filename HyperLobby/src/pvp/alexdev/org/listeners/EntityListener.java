package pvp.alexdev.org.listeners;

import pvp.alexdev.org.HyperPVP;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EntityListener  implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {

		if (event.toWeatherState()) {
			event.setCancelled(true); 
		}

	}

	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {

		if (event.toThunderState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLightningStrike(LightningStrikeEvent event) {
		event.setCancelled(true);
	}


	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.getDrops().clear();
		event.setDroppedExp(0);

	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {

		if (e.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);

			}
		}

		if (e.getRightClicked().getType() == EntityType.ZOMBIE && ((Zombie)e.getRightClicked()).getCustomName().contains("Servers")) {
			e.getPlayer().openInventory(HyperPVP.getInfoInventory());

		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent e) {

		if (e.getSpawnReason() == SpawnReason.CUSTOM) {
			return;
		}

		if (e.getSpawnReason() == SpawnReason.SPAWNER) {
			return;
		}

		e.setCancelled(true);
	}

	

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (event.getDamager().getType() == EntityType.PLAYER) {
			/*if (event.getEntity().getType() == EntityType.PLAYER) {
				event.setCancelled(true);
			}
		}

		if (event.getDamager().getType() == EntityType.ZOMBIE) {
			if (event.getEntity().getType() == EntityType.PLAYER) {
				event.setDamage(8F);


			}*/
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			
			Player player = (Player)entity;
			
			if (event.getCause() == DamageCause.ENTITY_ATTACK) {
				return;
			}
			
			if (event.getCause() == DamageCause.VOID) {
				player.setFallDistance(0f);
				player.teleport(HyperPVP.getWorldSpawn());
				player.setFallDistance(0f);
			}
			
			event.setCancelled(true);
			
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		event.setCancelled(true);
	}



}
