package pvp.alexdev.org;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.minecraft.server.v1_10_R1.MinecraftServer;
import pvp.alexdev.org.commands.PinCommand;
import pvp.alexdev.org.commands.RegisterCommand;
import pvp.alexdev.org.commands.Subscribe;
import pvp.alexdev.org.listeners.*;
import pvp.alexdev.org.mysql.Storage;
import pvp.alexdev.org.rank.RankManager;
import pvp.alexdev.org.tasks.DayTask;
import pvp.alexdev.org.tasks.LobbyTask;
import pvp.alexdev.org.tasks.RconNetwork;
import pvp.alexdev.org.tasks.ServerInfoTask;
import pvp.alexdev.org.users.Session;
import pvp.alexdev.org.util.Configuration;
import pvp.alexdev.org.util.EntityHider;
import pvp.alexdev.org.util.JarUtils;
import pvp.alexdev.org.util.EntityHider.Policy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class HyperPVP extends JavaPlugin {

	private static Random r = new Random();
	private static World world;
	private static Scoreboard board;
	private static HyperPVP instance;
	private static Configuration config;
	private static List<String> servers;
	private static List<LobbyTask> tasks;
	private static Map<String, String> labels;
	private static ConcurrentHashMap<UUID, Session> users;
	private static EntityHider hider;
	private static Inventory infoInventory;

	public static int popItem = 20;
	public static int popNotice = 10;
	public static int popDiamondSwordNotice = 30;
	public static int popDiamondSword = 40;
	public static int popChainNotice = 50;
	public static int popChain = 60;

	private static Location villagerLocation;
	private static Zombie zombie;
	private static Storage storage;
	private Logger log;
	private static RankManager rankManager;
	private static boolean needsKick = false;
	private static boolean shutdownRestart = false;
	private static String kickMessage = "";
	
	//Math.ceil(n/9.0) * 9

	@Override
	public void onEnable() {

		labels = new HashMap<String, String>();
		tasks = new ArrayList<LobbyTask>();
		
		instance = this;
		log = HyperPVP.getJavaPlugin().getLogger();
		users = new ConcurrentHashMap<UUID, Session>();
		
		
		log.info("Loading external libraries");


		try { 
			final File[] libs = new File[] { 
					new File(getDataFolder(), "lib" + File.separator + "bonecp-0.7.1.RELEASE.jar"), 
					new File(getDataFolder(), "lib" + File.separator + "mysql-connector-java-5.1.6-bin.jar"), 
					new File(getDataFolder(), "lib" + File.separator + "slf4j-api-1.7.2.jar"), 
					new File(getDataFolder(), "lib" + File.separator + "slf4j-simple-1.7.2.jar"),
					new File(getDataFolder(), File.separator + "lib" + File.separator + "json-simple-1.1.1.jar") }; 

			for (final File lib : libs) { 
				if (!lib.exists()) { 
					JarUtils.extractFromJar(lib.getName(), 
							lib.getAbsolutePath()); 
				} 
			} 
			for (final File lib : libs) { 
				if (!lib.exists()) { 
					getLogger().warning("There was a critical error loading My plugin! Could not find lib: " + lib.getName()); 
				} 
				addClassPath(JarUtils.getJarUrl(lib)); 
			} 
		} catch (final Exception e) { 
			e.printStackTrace(); 
		} 

		config = new Configuration(this, "config.yml");
		this.getLogger().info("Connecting to MySQL with host: " + HyperPVP.getOptions().getString("MySQL.Host"));
		storage = new Storage(HyperPVP.getOptions().getString("MySQL.Host"), HyperPVP.getOptions().getString("MySQL.Username"), HyperPVP.getOptions().getString("MySQL.Password"), HyperPVP.getOptions().getString("MySQL.Database"));
		hider = new EntityHider(this, Policy.BLACKLIST);
		servers = config.getConfig().getStringList("Servers");
		
		rankManager = new RankManager();

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		if (spawnWorldNull()) {
			WorldCreator creator = WorldCreator.name("lobby");
			creator.environment(Environment.NORMAL);
			creator.generator("CleanroomGenerator:.");
			world = creator.createWorld();
		}
			
		infoInventory = new CraftInventoryCustom(null, 9, "Servers");
		
		HyperPVP.tasks.add(new DayTask(this, 0));
		HyperPVP.tasks.add(new ServerInfoTask(this, 60));

		for (String str : servers) {

			String label = str.split(";")[0];

			for (ChatColor color : ChatColor.values()) {
				label = label.replace("<" + color.name() + ">", color.toString());
			}

			labels.put(label, str.split(";")[1]);
		}

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();

		for (LivingEntity m : Bukkit.getWorld("lobby").getEntitiesByClass(LivingEntity.class)) {
			if (m.getType() != EntityType.PLAYER) {
				m.remove();	
			}
		}
		
		this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		this.getServer().getPluginManager().registerEvents(new ItemListener(this), this);
		
		this.getCommand("subscribe").setExecutor(new Subscribe());
		this.getCommand("register").setExecutor(new RegisterCommand());
		this.getCommand("pin").setExecutor(new PinCommand());

		//villagerLocation = new Location(Bukkit.getWorld("lobby"), 90.29209, 76, -33.37092);
		//villagerLocation.setYaw(2 * 90);
		
		//zombie = (Zombie) villagerLocation.getWorld().spawnEntity(villagerLocation, EntityType.ZOMBIE);
		//zombie.setCustomName("Servers");
		//zombie.setCustomNameVisible(true);
		//zombie.setRemoveWhenFarAway(false);
		
		CraftServer craftServer = (CraftServer) Bukkit.getServer();
		MinecraftServer server = craftServer.getServer();
		
		StringBuilder motd = new StringBuilder();
		
		motd.append(ChatColor.GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "US" + ChatColor.RESET + "" + ChatColor.GRAY + "] " + ChatColor.RESET + "" + ChatColor.AQUA + "" + ChatColor.BOLD + "Minecraft " + ChatColor.RESET + ChatColor.DARK_AQUA + ChatColor.BOLD + "PVP" + ChatColor.RESET + " -- " + ChatColor.AQUA + "Gamemode rotation");
		motd.append("\n");
		motd.append(ChatColor.GREEN + "" + ChatColor.ITALIC + "Will you dominate?" + ChatColor.RESET + " " + ChatColor.GOLD + "| " + ChatColor.BLUE + "Updates daily!");
		
		server.setMotd(motd.toString());
		
		try {
			new RconNetwork();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerJoinEvent event = new PlayerJoinEvent(player, "");
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
		
		
		
	}
	
	public static String getDirection(Float yaw)
	{
	    yaw = yaw / 90;
	    yaw = (float)Math.round(yaw);
	 
	    if (yaw == -4 || yaw == 0 || yaw == 4) {return "SOUTH";}
	    if (yaw == -1 || yaw == 3) {return "EAST";}
	    if (yaw == -2 || yaw == 2) {return "NORTH";}
	    if (yaw == -3 || yaw == 1) {return "WEST";}
	    return "";
	}
	
	public static int getOppositeDirection(Float yaw) {
		return getOppositeDirection(getDirection(yaw));
	}
	
	public static int getOppositeDirection(String bearing)
	{
	    if (bearing.equals("NORTH")) {
	    	return 4 * 90;
	    }
	    
	    if (bearing.equals("SOUTH")) {
	    	return 2 * 90;
	    }
	    
	    if (bearing.equals("WEST")) {
	    	return 3 * 90;
	    }
	    
	    if (bearing.equals("EAST")) {
	    	return 1 * 90;
	    }
		
	    return 0;
	}
	
	private void addClassPath(final URL url) throws IOException { 
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader .getSystemClassLoader(); 
		final Class<URLClassLoader> sysclass = URLClassLoader.class; 
		try { 
			final Method method = sysclass.getDeclaredMethod("addURL",  new Class[] { URL.class }); 
			method.setAccessible(true); 
			method.invoke(sysloader, new Object[] { url }); 
		} catch (final Throwable t) { 
			t.printStackTrace(); 
		} 
	} 

	public static ItemStack getServers() {

		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + "" + ChatColor.BOLD + "Teleportation");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "Use this book to connect to other servers!");
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}
	

	@Override
	public void onDisable() {

		for (LobbyTask task : tasks) {
			this.getServer().getScheduler().cancelTask(task.getTaskId());
		}
	}

	public static boolean spawnWorldNull() {

		try {
			world.setTime(7000);
			return false;
		} catch (Exception e) {
			return true;
		}

	}

	public static World getWorld() {
		return world;
	}

	public static Location getWorldSpawn() {
		
		Location loc = new Location(Bukkit.getWorld("lobby"), -16.238264733948174, 48, 1.3821550142013965);
		loc.setPitch(-1.5011308f);
		loc.setYaw(-234.919f);
		
		return loc;
	}

	public static Location getAnimalSpawn() {
		return new Location(world, 10, 65, -10);
	}

	public static Scoreboard getBoard() {
		return board;
	}

	public static void setBoard(Scoreboard board) {
		HyperPVP.board = board;
	}

	public static HyperPVP getInstance() {
		return instance;
	}

	public static FileConfiguration getOptions() {
		return config.getConfig();
	}

	public static EntityHider getHider() {
		return hider;
	}

	public static Random getRandom() {
		return r;
	}

	public static Inventory getInfoInventory() {
		return infoInventory;
	}

	public static void setInfoInventory(Inventory infoInventory) {
		HyperPVP.infoInventory = infoInventory;
	}

	public static Plugin getJavaPlugin() {
		return getInstance();
	}

	public static Location getVillagerLocation() {
		return villagerLocation;
	}

	public static void setVillagerLocation(Location villagerLocation) {
		HyperPVP.villagerLocation = villagerLocation;
	}

	public static Zombie getZombie() {
		return zombie;
	}

	public static void setZombie(Zombie zombie) {
		HyperPVP.zombie = zombie;
	}

	public static Storage getStorage() {
		return storage;
	}
	
	public static Map<UUID, Session> getUsers() {
		return users;
	}

	public static RankManager getRankManager() {
		return rankManager;
	}


	public static long getTimestamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime()).getTime() / 1000L;
	}
	
	public static long[] calculateTime(long seconds) {

		long day = TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hours);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.DAYS.toSeconds(day) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minute);
		return new long[] { day, hours, minute, second };
	}

	public static boolean getNeedsKick() {
		return needsKick;
	}

	public static void setNeedsKick(boolean var) {
		needsKick = var;
	}

	public static String getKickMessage() {
		return kickMessage;
	}

	public static void setKickMessage(String kickMessage) {
		HyperPVP.kickMessage = kickMessage;
	}

	public static boolean isShutdownRestart() {
		return shutdownRestart;
	}

	public static void setShutdownRestart(boolean shutdownRestart) {
		HyperPVP.shutdownRestart = shutdownRestart;
	}
}
