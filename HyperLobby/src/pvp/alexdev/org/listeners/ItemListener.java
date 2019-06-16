package pvp.alexdev.org.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pvp.alexdev.org.HyperPVP;


public class ItemListener implements Listener {

	private JavaPlugin p;

	public ItemListener(JavaPlugin p) {
		super();
		this.p = p;
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();

		if (inv.getTitle().contains(HyperPVP.getInfoInventory().getTitle()) && event.getCurrentItem() != null) {

			ItemStack current = event.getCurrentItem();

			if (current.hasItemMeta() && current.getItemMeta().getDisplayName().length() != 0) {

				event.setCancelled(true);
				
				try {

					player.sendMessage(ChatColor.AQUA.toString() + ChatColor.ITALIC.toString() + "Connecting to " + ChatColor.RESET + ChatColor.DARK_AQUA.toString() + ChatColor.ITALIC + strip(current.getItemMeta().getDisplayName()));

					String bungee = HyperPVP.getStorage().getString("SELECT bungee_name FROM servers WHERE name = '" + strip(current.getItemMeta().getDisplayName().toLowerCase()) + "'");
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);
					dos.writeUTF("Connect");
					dos.writeUTF(bungee);
					player.sendPluginMessage(p, "BungeeCord", baos.toByteArray());
					
					baos.close();
					dos.close();


				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
			

		}
	}
	
	public String strip(String str) {
		
		String output = str;
		for (ChatColor c : ChatColor.values()) {
			output = output.replace(c.toString(), "");
		}
		return output;
	}
}
