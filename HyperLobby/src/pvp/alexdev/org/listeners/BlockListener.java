package pvp.alexdev.org.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {

		if (event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LAVA) {
			event.setCancelled(true);
		}
	}



	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);

	}

}
