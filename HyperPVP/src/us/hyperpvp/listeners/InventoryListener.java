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

import java.sql.SQLException;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();

		Inventory inv = event.getInventory();
		InventoryView inventoryView = event.getView();

		if (inventoryView.getTitle().contains("Team Selection") && event.getCurrentItem() != null) {
			if (event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) {
				event.setCancelled(true);
				return;
			}

			int rank = 1;

			try {
				rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + player.getName() + "'");
			} catch (SQLException e) {

			}

			if (rank >= 3 && event.getCurrentItem().hasItemMeta() && 
					event.getCurrentItem().getItemMeta().getDisplayName().endsWith(" Team") && HyperPVP.getMap().getType() != GameType.FFA) {

				String team = event.getCurrentItem().getItemMeta().getDisplayName().substring(6).split(" ")[0];
				HyperPVP.getMap().joinGame(player, team.toLowerCase());

			} else if (event.getCurrentItem().hasItemMeta() && 
					event.getCurrentItem().getItemMeta().getDisplayName().endsWith(" Team") && HyperPVP.getMap().getType() == GameType.FFA) {

				String team = event.getCurrentItem().getItemMeta().getDisplayName().substring(6).split(" ")[0];
				HyperPVP.getMap().joinGame(player, team.toLowerCase());

			} else if ( event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().endsWith("Auto Join")) {

				HyperPVP.getMap().joinGame(player, null);
			}

			event.setCancelled(true);
			return;

		} else if (inventoryView.getTitle().contains("What is HyperPVP?") && event.getCurrentItem() != null) {


		} else {
			if (!HyperPVP.hasMatchBeenAnnounced()) {
				event.setCancelled(true);
				return;
			}

			if (HyperPVP.getSpectators().contains(event.getWhoClicked()) && !event.getWhoClicked().isOp()) {
				event.setCancelled(true);
				return;

			}
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {

		if (HyperPVP.isCycling()) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		Player player = (Player)event.getWhoClicked();

		ItemStack item = event.getCurrentItem();
		ItemMeta meta = item.getItemMeta();
		
		if (item.getType().name().contains("SWORD") 
				|| item.getType().name().contains("SPADE") 
				|| item.getType().name().contains("AXE") 
				|| item.getType().name().contains("PICKAXE")
				|| item.getType().name().contains("HELMET")
				|| item.getType().name().contains("BOOTS")
				|| item.getType().name().contains("LEGGINGS")
				|| item.getType().name().contains("CHESTPLATE") 
				|| item.getType().name().contains("AXE")) 
		{

			if (meta.getLore() == null || meta.getLore().size() == 0) {
				meta.setLore(Arrays.asList(new String[] { "Crafted by: " + player.getName() }));
			} else {
				meta.getLore().add("Crafted by: " + player.getName());
			}

			event.getCurrentItem().setItemMeta(meta);
		}
	}
}
