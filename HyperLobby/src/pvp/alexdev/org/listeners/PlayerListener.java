package pvp.alexdev.org.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

import pvp.alexdev.org.HyperPVP;
import pvp.alexdev.org.rank.bans.Ban;
import pvp.alexdev.org.rank.bans.BanManager;
import pvp.alexdev.org.users.Session;

import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener  implements Listener {

	private BanManager banManager;

	public PlayerListener() {
		this.banManager = new BanManager();
	}
	
	@EventHandler
	public void onPlayerLogin(final PlayerLoginEvent e) {

		Player p = e.getPlayer();
		
		Session session = new Session(p);

		if (banManager.hasBanUUID(session.getId())) {
			Ban ban = banManager.getCurrentBan(p);
			e.disallow(Result.KICK_BANNED, banManager.kickMessage(p, ban.getReason(), ban.getType(), ban.getDate()));
			return;
		}
		
		HyperPVP.getUsers().put(p.getUniqueId(), session);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		final Player player = event.getPlayer();
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HyperPVP.getJavaPlugin(), new Runnable() {

			@Override
			public void run() {
				player.teleport(HyperPVP.getWorldSpawn());

			}
		}, 0L);

		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

			event.getPlayer().showPlayer(otherPlayer);
			otherPlayer.showPlayer(event.getPlayer());

		}	
		
		Session session = HyperPVP.getUsers().get(player.getUniqueId());
		
		if (session.isNewUser()) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "Welcome " + ChatColor.DARK_GREEN + event.getPlayer().getName() + ChatColor.GREEN +" to Minecraft PVP!");
		}
		
		player.setGameMode(GameMode.ADVENTURE);
		player.sendMessage(ChatColor.DARK_AQUA + "Welcome to the Minecraft PVP lobby!");
		player.sendMessage(ChatColor.AQUA + "This is a gamemode rotation server!");
		player.sendMessage(ChatColor.AQUA + "Craft, destroy cores, monuments and conquer the opposing team!");
		//player.sendMessage(ChatColor.GOLD + "Learn how to play at " + ChatColor.GRAY + "wiki.outbreakmc.net");

		/*if (!ValueManager.getBooleanValue("BUNGEE_COORD")) {
			this.player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Welcome to Outbreak");
			this.player.sendMessage(ChatColor.DARK_GREEN + "This is a survival zombie server with lootable objects, guns.");
			this.player.sendMessage(ChatColor.DARK_GREEN + "bandits and zombies.");
			this.player.sendMessage(ChatColor.GREEN + "If you want to private message use @name <message>");
			this.player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Type /survive to start playing!");
		}*/

		player.getInventory().clear();
		player.getInventory().addItem(HyperPVP.getServers());
		player.updateInventory(); 

		player.setFoodLevel(20);
		player.setHealth(20F);
		player.setExp(0);
		player.setFireTicks(0);
		player.setLevel(0);

		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		event.setJoinMessage("");
		

		session.sendMembershipStatus();
		
		//session.sendTitle(ChatColor.GOLD + "Welcome to Alex's PVP server", ChatColor.GRAY + "+10 EXP for logging in", 20 * 3);
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		event.setQuitMessage("");
		HyperPVP.getUsers().remove(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		
		if (event.getFrom().getY() < 25) {
			player.setFallDistance(0f);
			player.teleport(HyperPVP.getWorldSpawn());
			player.setFallDistance(0f);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.ENCHANTED_BOOK) {
				event.getPlayer().openInventory(HyperPVP.getInfoInventory());
			}

			if (!event.getPlayer().isOp()) {
				event.setCancelled(true);
			}

		}

		if (!event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

		Player player = event.getPlayer();
		event.getPlayer().setDisplayName(event.getPlayer().getName());

		Session session = HyperPVP.getUsers().get(player.getUniqueId());

		if (session.getRank() != 1) {
			event.setFormat("[" + session.getRankLabel() + ChatColor.WHITE + "] " + player.getName() + ": " + event.getMessage());
		}

		if (session.getRank() == 1) {
			event.setFormat(player.getName() + ": " + event.getMessage());
		}
	}
}
