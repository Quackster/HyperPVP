package pvp.alexdev.org.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pvp.alexdev.org.HyperPVP;

public class ServerReadThread implements Runnable {

	public static List<ItemStack> items = new ArrayList<ItemStack>();

	@SuppressWarnings("deprecation")
	@Override
	public void run() {

		items.clear();
		
		try {
			
			ResultSet set = HyperPVP.getStorage().prepare("SELECT * FROM servers").executeQuery();

			while (set.next()) {

				int players = HyperPVP.getStorage().count("SELECT * FROM servers_users WHERE server_id = '" + set.getString("bungee_name") + "'");
				int maxplayers = 30;
				
				ItemStack server = null;
				
				String mapStatus = set.getString("map_status");
				int gameStatus = set.getInt("status");
				
				if (mapStatus.equals("STARTING")) {
					server = new ItemStack (35);
				} else {
					
					if (gameStatus == 3) {
						server = new ItemStack (35, 0, (short) 5);
					}
				
					if (gameStatus == 2) {
						server = new ItemStack (35, 0, (short) 4);
					}
					
					if (gameStatus == 3) {
						server = new ItemStack (35, 0, (short) 1);
					}
				}
				
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.WHITE + "" + players + "/" + maxplayers);
				
				ItemMeta meta = server.getItemMeta();
				meta.setLore(lore);
				meta.setDisplayName(set.getString("name"));
				server.setItemMeta(meta);
				server.setAmount(players);
				
				items.add(server);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		HyperPVP.getInfoInventory().clear();
		
		for (ItemStack item : items) {
			HyperPVP.getInfoInventory().addItem(item);
		}
		
		items.clear();

	}
}
