package pvp.alexdev.org.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {

	public ChatListener(JavaPlugin p) {
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

		Player player = event.getPlayer();

		if (event.getPlayer().isOp()) {
			event.setFormat(ChatColor.DARK_RED + player.getName() + ": " + ChatColor.WHITE + event.getMessage());
			//event.setFormat("<" + ChatColor.GRAY  + player.getName() + ChatColor.WHITE + ">" + ChatColor.GRAY + ": " + event.getMessage());
		} else {
			event.setFormat(player.getName() + ": " + event.getMessage());
		}

	}

	
}
