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
package us.hyperpvp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import us.hyperpvp.commands.AuthCommand;
import us.hyperpvp.commands.CycleCommand;
import us.hyperpvp.commands.JoinCommand;
import us.hyperpvp.commands.MatchInfoCommand;
import us.hyperpvp.commands.PinCommand;
import us.hyperpvp.commands.RegisterCommand;
import us.hyperpvp.commands.ReportCommand;
import us.hyperpvp.commands.ScoreCommand;
import us.hyperpvp.commands.SpectateCommand;
import us.hyperpvp.commands.TeamChatCommand;
import us.hyperpvp.game.Game;
import us.hyperpvp.game.map.GameMap;
import us.hyperpvp.game.map.team.Detonator;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.listeners.BlockListener;
import us.hyperpvp.listeners.CreatureListener;
import us.hyperpvp.listeners.EntityListener;
import us.hyperpvp.listeners.InventoryListener;
import us.hyperpvp.listeners.MiscListener;
import us.hyperpvp.listeners.PlayerListener;
import us.hyperpvp.misc.Configuration;
import us.hyperpvp.misc.CycleUtil;
import us.hyperpvp.storage.Storage;
import us.hyperpvp.thread.Announcer;
import us.hyperpvp.thread.FightThread;
import us.hyperpvp.thread.misc.IThread;
import us.hyperpvp.thread.misc.ThreadType;

public class HyperPVP extends JavaPlugin {

	private static HyperPVP plugin;
	private static Storage storage;
	private static Configuration configuration;
	private static Boolean isCycling;
	private static TeamMap winningTeam;
	private static Integer minutesLeft;
	private static String timeString = "";
	private static Game game;
	private static Random random;
	private static Boolean cannotJoin;
	private static GameMap previousWorld;
	private static Boolean needsCycleThread;
	private static Boolean needsGameThread;
	private static Boolean needsMatchCheck;
	private static Boolean hasMatchBeenAnnounced;
	private static Boolean checkFirework;
	private static Map<Location, Color> fireworkLocation;
	private static int games = 0;
	private static Scoreboard scoreboard;
	private static HashMap<String, Team> scoreboardTeams = new HashMap<String, Team>();
	
	private static Map<Detonator, Session> detonators;
	private static Map<Player, ChatColor> teamCycle;
	private static ConcurrentMap<ThreadType, IThread> threads;
	private static ConcurrentHashMap<String, Session> players;
	private static Player winningPlayer;
	private static boolean needsRestart;
	private static int callId;

	@Override
	public void onEnable() {
		
		plugin = this;
		isCycling = false;
		needsCycleThread = false;
		needsGameThread = false;
		needsMatchCheck = false;
		checkFirework = false;
		hasMatchBeenAnnounced = false;
		minutesLeft = 30;
		fireworkLocation = new HashMap<Location, Color>();
		teamCycle = new HashMap<Player, ChatColor>();
		threads = new ConcurrentHashMap<ThreadType, IThread>();
		players = new ConcurrentHashMap<String, Session>();
		detonators = new HashMap<Detonator, Session>();
		random = new Random();

		this.getLogger().info("Loading configuration.");
		this.initalizeConfiguration();

		this.getLogger().info("Loading MySQL database.");

		if (!this.initalizeMySQL(true, false)) {
			this.getLogger().info("Trying SQLite instead!");
			
			if (!this.initalizeMySQL(true, true)) {
				this.getLogger().info("Failed!");
				return;
			} else {
				this.getLogger().info("=========== ATTENTION! ===========");
				this.getLogger().info("===    You are using SQLite    ===");
				this.getLogger().info("=========== ATTENTION! ===========");
			}
			//return;
		}

		this.getLogger().info("Loading the 'Game' instance.");
		game = new Game(this);

		this.getLogger().info("Loading the listeners.");
		this.handlers();
		this.commands();

		this.getLogger().info("Starting fight thread.");

		HyperPVP.threads.put(ThreadType.FIGHT, new FightThread());
		HyperPVP.threads.get(ThreadType.FIGHT).start();

		this.getLogger().info("Starting announce thread");

		HyperPVP.threads.put(ThreadType.ANNOUNCE, new Announcer());
		HyperPVP.threads.get(ThreadType.ANNOUNCE).start();

		this.getLogger().info("Performing task..");
		callId = getServer().getScheduler().scheduleSyncRepeatingTask(this, CycleUtil.getCheckTask(), 0, 10);

		players.clear();

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.CREATIVE);
		
		}

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		HyperPVP.resetScoreboard();
	}
	
	public static void resetScoreboard() {
		scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

		for (Player player : Bukkit.getOnlinePlayers()) {
			MiscListener.refreshTag(player);
		}
	}

	@Override
	public void onDisable() {
		try {

			HyperPVP.getStorage().getConnection().close();
			HyperPVP.getStorage().getStatement().close();
			Bukkit.getScheduler().cancelTask(callId);

			getMap().unload();
			CycleUtil.cycleNext(false, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initalizeConfiguration() {

		try {
			configuration = new Configuration(true, this, "config.yml");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean initalizeMySQL(boolean clear, boolean SQLite) {
		try {
			if (SQLite) {
				File file = new File(this.getDataFolder(), "hyperpvp.db");
				
				if (!file.exists()) {
					file.createNewFile();
				}
				
				storage = new Storage(file);
			} else {
				storage = new Storage(configuration.getConfig().getString("MySQL.Hostname"), configuration.getConfig().getString("MySQL.Username"), configuration.getConfig().getString("MySQL.Password"), configuration.getConfig().getString("MySQL.Database"));
			}
			
			if (clear) {
				storage.executeQuery("DELETE FROM servers_users");
			}

			return true;


		} catch (Exception e) {
			this.getLogger().info("Could not connect, reason; ");
			e.printStackTrace();
			return false;
		}

	}

	private void handlers() {
		this.getServer().getPluginManager().registerEvents(new MiscListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		this.getServer().getPluginManager().registerEvents(new CreatureListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
	}

	public void commands() {
		this.getCommand("join").setExecutor(new JoinCommand());
		this.getCommand("spectate").setExecutor(new SpectateCommand());
		this.getCommand("matchinfo").setExecutor(new MatchInfoCommand());
		this.getCommand("pin").setExecutor(new PinCommand());
		this.getCommand("register").setExecutor(new RegisterCommand());
		this.getCommand("cycle").setExecutor(new CycleCommand());
		this.getCommand("score").setExecutor(new ScoreCommand());
		this.getCommand("t").setExecutor(new TeamChatCommand());
		this.getCommand("report").setExecutor(new ReportCommand());
	}

	public static void updateScoreboards() {

		for (Session session : HyperPVP.getSessions().values()) {
			session.updateScoreboard(true);
		}
	}
	
	public static Session getSession(Player player) {
		return players.get(player.getName());
	}
	
	public static boolean isSpectator(Player player) {
		return !players.get(player.getName()).isPlaying();
	}

	public static boolean isGamePlayer(Player player) {
		return players.get(player.getName()).isPlaying();
	}
	
	public static List<Session> getSpectators() {
		List<Session> users = new ArrayList<Session>();
		
		for (Session session : players.values()) {
			if (!session.isPlaying()) {
				users.add(session);
			}
		}
		
		return users;
	}
	
	public static List<Session> getPlayers() {
		List<Session> users = new ArrayList<Session>();
		
		for (Session session : players.values()) {
			if (session.isPlaying()) {
				users.add(session);
			}
		}
		
		return users;
	}
	
	public static void setListName(ChatColor color, Player player) {

		if (player.getName().length() >= 14) {
			player.setPlayerListName(color + player.getName().substring(0, 14));
		}else {
			player.setPlayerListName(color + player.getName());
		}
	}

	public static Game getGame() {
		return game;
	}

	public static Storage getStorage() {
		return storage;
	}

	/*public static World getDefaultWorld() {
		return Bukkit.getWorld("world");
	}

	public static Location getWorldSpawn() {
		return new Location(getDefaultWorld(), 31, 54, -24);
	}*/

	public static World getGameWorld() {
		return getGame().getMapManager().getCurrentWorld();
	}

	public static GameMap getMap() {
		
		if (getGame()== null) {
			System.out.println("NULL >> ");
		}
		
		if (getGame().getMapManager() == null) {
			System.out.println("NULL >> getGame().getMapManager()");
		}
		
		if (getGame().getMapManager().getGameMap() == null) {
			System.out.println("NULL >> getGame().getMapManager().getGameMap()");
		}
		
		return getGame().getMapManager().getGameMap();
	}

	public static ConcurrentHashMap<String, Session> getSessions() {
		return players;
	}

	public static Random getRandom() {
		return random;
	}

	public static Map<ThreadType, IThread> getThreads() {
		return threads;
	}

	public static Boolean getCannotJoin() {
		return cannotJoin;
	}

	public static void setCannotJoin(Boolean cannotJoin) {
		HyperPVP.cannotJoin = cannotJoin;
	}

	public static Boolean isCycling() {
		return isCycling;
	}

	public static void setCycling(Boolean deny) {
		HyperPVP.isCycling = deny;
	}

	public static TeamMap getWinningTeam() {
		return winningTeam;
	}

	public static void setWinningTeam(TeamMap winningTeam) {
		HyperPVP.winningTeam = winningTeam;
	}

	public static Integer getMinutesLeft() {
		return minutesLeft;
	}

	public static Configuration getConfiguration() {
		return configuration;
	}


	public static void setMinutesLeft(Integer minutesLeft) {
		HyperPVP.minutesLeft = minutesLeft;
	}

	public static String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	public static int getTime() {
		return getMap().getTime();
	}

	public static GameMap getPreviousWorld() {
		return previousWorld;
	}

	public static void setPreviousWorld(GameMap previousWorld) {
		HyperPVP.previousWorld = previousWorld;
	}

	public static Boolean getNeedsCycleThread() {
		return needsCycleThread;
	}

	public static void setNeedsCycleThread(Boolean needsCycleThread) {
		HyperPVP.needsCycleThread = needsCycleThread;
	}

	public static Boolean getNeedsGameThread() {
		return needsGameThread;
	}

	public static void setNeedsGameThread(Boolean needsGameThread) {
		HyperPVP.needsGameThread = needsGameThread;
	}

	public static Map<Player, ChatColor> getTeamCycle() {
		return teamCycle;
	}

	public static Boolean hasMatchBeenAnnounced() {
		return hasMatchBeenAnnounced;
	}

	public static void setMatchBeenAnnounced(Boolean hasMatchBeenAnnounced) {
		HyperPVP.hasMatchBeenAnnounced = hasMatchBeenAnnounced;
	}

	public static Boolean getNeedsMatchCheck() {
		return needsMatchCheck;
	}

	public static void setNeedsMatchCheck(Boolean needsMatchCheck) {
		HyperPVP.needsMatchCheck = needsMatchCheck;
	}

	public static String getTimeString() {
		return timeString;
	}

	public static void setTimeString(String timeString) {
		HyperPVP.timeString = timeString;
	}

	public static HyperPVP getPlugin() {
		return plugin;
	}

	public static void setPlugin(HyperPVP plugin) {
		HyperPVP.plugin = plugin;
	}

	public static Boolean getIsCycling() {
		return isCycling;
	}

	public static void setIsCycling(Boolean isCycling) {
		HyperPVP.isCycling = isCycling;
	}

	public static Boolean getHasMatchBeenAnnounced() {
		return hasMatchBeenAnnounced;
	}

	public static void setHasMatchBeenAnnounced(Boolean hasMatchBeenAnnounced) {
		HyperPVP.hasMatchBeenAnnounced = hasMatchBeenAnnounced;
	}

	public static Boolean getCheckFirework() {
		return checkFirework;
	}

	public static void setCheckFirework(Boolean checkFirework) {
		HyperPVP.checkFirework = checkFirework;
	}

	public static Map<Location, Color> getFireworkLocation() {
		return fireworkLocation;
	}

	public static void setFireworkLocation(Map<Location, Color> fireworkLocation) {
		HyperPVP.fireworkLocation = fireworkLocation;
	}

	public static int getGames() {
		return games;
	}

	public static void setGames(int games) {
		HyperPVP.games = games;
	}

	public static Scoreboard getScoreboard() {
		return scoreboard;
	}

	public static void setScoreboard(Scoreboard scoreboard) {
		HyperPVP.scoreboard = scoreboard;
	}

	public static HashMap<String, Team> getScoreboardTeams() {
		return scoreboardTeams;
	}

	public static void setScoreboardTeams(HashMap<String, Team> scoreboardTeams) {
		HyperPVP.scoreboardTeams = scoreboardTeams;
	}

	public static Map<Detonator, Session> getDetonators() {
		return detonators;
	}

	public static void setDetonators(Map<Detonator, Session> detonators) {
		HyperPVP.detonators = detonators;
	}

	public static Player getWinningPlayer() {
		return winningPlayer;
	}

	public static void setWinningPlayer(Player winningPlayer) {
		HyperPVP.winningPlayer = winningPlayer;
	}

	public static boolean isNeedsRestart() {
		return needsRestart;
	}

	public static void setNeedsRestart(boolean needsRestart) {
		HyperPVP.needsRestart = needsRestart;
	}

	public static int getCallId() {
		return callId;
	}

	public static void setCallId(int callId) {
		HyperPVP.callId = callId;
	}

}
