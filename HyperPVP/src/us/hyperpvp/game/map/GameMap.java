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
package us.hyperpvp.game.map;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameSpawns;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.region.Region;
import us.hyperpvp.game.map.region.RegionType;
import us.hyperpvp.game.map.team.TeamMap;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.listeners.MiscListener;
import us.hyperpvp.misc.CycleUtil;

public class GameMap {

	private World world;
	private String worldName;
	private String mapName;
	private GameType type;
	private List<GameSpawns> spawnCoords;
	private List<Region> mapRegions;
	private List<TeamMap> teams;
	private Map<String, TeamMap> playerTeams;
	
	private double X;
	private double Y;
	private double Z;

	private List<ItemStack> items;
	private long mapid;
	private int time;
	private String author;
	private List<String> features;
	private int maxPlayers;
	private boolean storm;
	private boolean thundering;

	public GameMap(FileConfiguration conf, int mapId, GameType type, int time, World world, String mapName, String author, String worldName, Location spawn, List<GameSpawns> spawnCoords, List<TeamMap> teams, int maxPerTeam, List<ItemStack> items, List<String> specialfeatures, boolean storm, boolean thundering) throws IOException {

		this.mapid = mapId;
		this.world = world;
		this.time = time;
		this.worldName = worldName;
		this.type = type;
		this.mapName = mapName;
		this.author = author;
		this.X = spawn.getX();
		this.Y = spawn.getY();
		this.Z = spawn.getZ();
		this.spawnCoords = spawnCoords;
		this.mapRegions = new ArrayList<Region>();
		this.playerTeams = new HashMap<String, TeamMap>();
		this.teams = teams;
		this.items = items;
		this.maxPlayers = maxPerTeam;
		this.features = specialfeatures;
		
		this.storm = storm;
		this.setThundering(thundering);

		try {

			List<Integer> regions = new ArrayList<Integer>();

			for (int i = 0; i < 20; i++) {

				if (conf.contains("Settings.Regions." + i)) {
					regions.add(i);
				}

			}

			for (int id : regions) {

				List<ChatColor> whitelist = new ArrayList<ChatColor>();

				for (String color : conf.getStringList("Settings.Regions." + id + ".TeamWhitelist")) {

					if (color.length() == 0 || color == null) {
						continue;
					}

					whitelist.add(ChatColor.valueOf(color));
				}

				List<Material> blocks = new ArrayList<Material>();

				for (String material : conf.getStringList("Settings.Regions." + id + ".Blocks")) {

					if (material.length() == 0 || material == null) {
						continue;
					}

					blocks.add(Material.valueOf(material));
				}

				int maxX = conf.getInt("Settings.Regions." + id + ".MaxX");
				int maxY = conf.getInt("Settings.Regions." + id + ".MaxY");
				int maxZ = conf.getInt("Settings.Regions." + id + ".MaxZ");

				int minX = conf.getInt("Settings.Regions." + id + ".MinX");
				int minY = conf.getInt("Settings.Regions." + id + ".MinY");
				int minZ = conf.getInt("Settings.Regions." + id + ".MinZ");

				String regionType = conf.getString("Settings.Regions." + id + ".Type");
				String regionAlert = conf.getString("Settings.Regions." + id + ".Alert");

				Region region = new Region(this.world, RegionType.toValue(regionType), whitelist, blocks, regionAlert, maxX, maxY, maxZ, minX, minY, minZ);
				this.mapRegions.add(region);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startWorld() {
		
		this.world.setTime(0);
		this.world.setStorm(this.storm);
		this.world.setThundering(this.thundering);
		this.world.setWeatherDuration(999999);
		
		for (Animals animal : this.world.getEntitiesByClass(Animals.class)) {
			animal.remove();
		}
		
		for (Monster entity : HyperPVP.getMap().getWorld().getEntitiesByClass(Monster.class)) {
			entity.remove();
		}
	}
	
	
	public void shuffleTeams() {
		
		List<TeamMap> tempTeams = new ArrayList<TeamMap>();
		
		for (TeamMap team : this.teams) {
			tempTeams.add(team);
		}
		
		Collections.shuffle(tempTeams);
		this.teams = tempTeams;

	}

	public void dispose(boolean unload) {

		this.playerTeams.clear();

		for (TeamMap team : this.teams) {
			team.reset();
		}

		if (unload) {
			this.unload();
		}
	}

	public boolean isLoaded() {
		for (World w: Bukkit.getServer().getWorlds()) {
			if (w.getName().equals(this.worldName)) {
				return w.getPlayers().size() == 0;
			}
		}
		return false;
	}

	public void load() {
		WorldCreator creator = WorldCreator.name(this.worldName);
		creator.environment(Environment.NORMAL);
		creator.generator("CleanroomGenerator:.");
		this.world = creator.createWorld();
		world.setAutoSave(false);

		//new Location(this.world, this.X, this.Y, this.Z);

	}

	public void unload() {

		/*this.objective.unregister();
		this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);*/

		try {
			Bukkit.unloadWorld(this.worldName, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Bukkit.unloadWorld(this.world, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.world = null;
	}

	@SuppressWarnings("deprecation")
	public void joinGame(Player player, String name) {

		int rank = 1;

		try {
			rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + player.getName() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}


		if (this.type == GameType.FFA) {

			List<Session> first = this.getTeamMembers(this.teams.get(0).getColor());

			if (first.size() >= this.maxPlayers && rank == 1) {

				System.out.println(this.maxPlayers);
				player.sendMessage(ChatColor.RED + "Teams full - " + ChatColor.GOLD + "hyperpvp.us/shop" + ChatColor.GREEN + " to join full teams or view other servers at " + ChatColor.GREEN + "hyperpvp.us/servers");
				return;
			}

		} else {

			List<Session> first = this.getTeamMembers(this.teams.get(0).getColor());
			List<Session> second = this.getTeamMembers(this.teams.get(1).getColor());

			if ((first.size() + second.size()) >= this.maxPlayers && rank == 1) {

				System.out.println(this.maxPlayers);
				player.sendMessage(ChatColor.RED + "Teams full - " + ChatColor.GOLD + "hyperpvp.us/shop" + ChatColor.GREEN + " to join full teams or view other servers at " + ChatColor.GREEN + "hyperpvp.us/servers");
				return;
			}
		}

		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		if (HyperPVP.hasMatchBeenAnnounced()) {
			player.setGameMode(GameMode.SURVIVAL);
		} else {
			player.setGameMode(GameMode.CREATIVE);
		}

		player.getInventory().clear();
		player.updateInventory();

		CycleUtil.addGameSession(player);

		TeamMap team = null;
		
		if (this.type == GameType.FFA) {
			team = this.teams.get(0);
			
			
		} else {

			if (name != null) {

				team = this.getTeamByName(name);

			} else {

				List<Session> first = this.getTeamMembers(this.teams.get(0).getColor());
				List<Session> second = this.getTeamMembers(this.teams.get(1).getColor());

				if (first.size() == second.size()) {
					team = this.teams.get(HyperPVP.getRandom().nextInt(1));
				} else {

					if (first.size() > second.size()) {
						team = this.teams.get(1);

					} else
						team = this.teams.get(0);
				}
			}
		}
		
		Session session = HyperPVP.getSession(player);
		session.setTeam(team);
		
		player.sendMessage(ChatColor.GRAY + "You joined the " + session.getTeam().getColor() + HyperPVP.capitalize(session.getTeam().getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + " Team");

		HyperPVP.setListName(session.getTeam().getColor(), player);
		MiscListener.refreshTag(player);

		player.setFallDistance(0F);

		List<GameSpawns> tempSpawns = new ArrayList<GameSpawns>();
		
		for (GameSpawns spawn : this.spawnCoords) {
			tempSpawns.add(spawn);
		}
		
		Collections.shuffle(tempSpawns);
		
		int i = 0;
		for (GameSpawns spawn : tempSpawns) {
			
			if (spawn.getColor() == team.getColor()) {
				session.getSpawns().put(i++, spawn);
			}
		}
		
		if (HyperPVP.hasMatchBeenAnnounced()) {

			CycleUtil.startMatch(player);
			CycleUtil.refreshShowHidePlayer();
			player.teleport(session.getGameSpawn());
		}
	

		if (!HyperPVP.hasMatchBeenAnnounced()) {
			player.getInventory().clear();
			player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			player.updateInventory();
			player.closeInventory();
		}

		try {
			HyperPVP.getStorage().executeQuery("UPDATE servers SET team_one = '" + this.getTeamMembers(this.teams.get(0).getColor()).size() + "' WHERE bungee_name = '" + HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase() + "'");

			if (this.type != GameType.FFA) {
				HyperPVP.getStorage().executeQuery("UPDATE servers SET team_two = '" + this.getTeamMembers(this.teams.get(1).getColor()).size() + "' WHERE bungee_name = '" + HyperPVP.getConfiguration().getConfig().getString("Server").toLowerCase() + "'");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String matchInfoToString(CommandSender player) {

		StringBuilder builder = new StringBuilder();

		player.sendMessage(ChatColor.RED + "------------------ " + ChatColor.AQUA + "Match Info" + ChatColor.RED + " ------------------");

		if (HyperPVP.getMap().getType() == GameType.TDM ||
				HyperPVP.getMap().getType() == GameType.DTM ||
				HyperPVP.getMap().getType() == GameType.DTC ||
				HyperPVP.getMap().getType() == GameType.RTC ||
				HyperPVP.getMap().getType() == GameType.CONQUEST) {

			player.sendMessage(ChatColor.DARK_AQUA + "Time: " + ChatColor.AQUA + HyperPVP.getTimeString());

			TeamMap one = this.teams.get(0);
			TeamMap two = this.teams.get(1);

			player.sendMessage(one.getColor() + HyperPVP.capitalize(one.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + ChatColor.GRAY + " kills: " + ChatColor.WHITE + one.getKills() + " | " + two.getColor() + HyperPVP.capitalize(two.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "")) + ChatColor.GRAY + " kills: " + ChatColor.WHITE + two.getKills() + " | " + ChatColor.AQUA + "Observers" + ChatColor.GRAY + ": " + ChatColor.WHITE + HyperPVP.getSpectators().size());

			player.sendMessage("");

			String goal = "";

			if (HyperPVP.getMap().getType() == GameType.TDM) {
				goal = ChatColor.AQUA + "(Team Death Match) Get your team the most kills.";
			}

			if (HyperPVP.getMap().getType() == GameType.DTM) {
				goal = ChatColor.AQUA + "(Destroy The Monument) Destory all of other teams obsidian.";
			}

			if (HyperPVP.getMap().getType() == GameType.DTC) {
				goal = ChatColor.AQUA + "(Destroy The Core) Leak their obisdian core.";
			}

			if (HyperPVP.getMap().getType() == GameType.RTC) {
				goal = ChatColor.AQUA + "(Race To Core) Be the first team to destroy the core in the middle.";
			}
			
			if (HyperPVP.getMap().getType() == GameType.CONQUEST) {
				goal = ChatColor.AQUA + "(Conquest) The first team to run out of tickets loses, or when the timer runs out the team with the most tickets wins.";
			}

			player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Goal: " + ChatColor.RESET + goal);

		}

		if (HyperPVP.getMap().getType() == GameType.FFA) {

			player.sendMessage(ChatColor.DARK_AQUA + "Time: " + ChatColor.AQUA + HyperPVP.getTimeString());
			player.sendMessage("");
			player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Goal: " + ChatColor.RESET + ChatColor.AQUA + "Kill everyone, get the highest kill score.");
			player.sendMessage("");

			List<Session> topPlayers = HyperPVP.getMap().getTop();

			if (topPlayers.size() != 0) {

				int i = 1;

				for (Session set : topPlayers) {

					if (i > 10 && set.getKills() != 0) {
						continue;
					}

					player.sendMessage(i + ". " + ChatColor.GOLD + set.getPlayer().getName() + ChatColor.WHITE + " with " + set.getKills() + "!");

					i++;
				}
			}

		}


		return builder.toString();
	}

	public Location getRandomSpawn(Player player) {
		ChatColor color = HyperPVP.getSession(player).getTeam().getColor();
		return this.getRandomSpawn(color);
	}

	public Location getRandomSpawn(ChatColor color) {

		try {
			List<GameSpawns> coords = this.getTeamCoords(color);

			GameSpawns coordSpawn = null;

			if (coords.size() == 1) {
				coordSpawn = coords.get(0);
			} else {
				coordSpawn = coords.get(HyperPVP.getRandom().nextInt(coords.size()));
			}

			Location gameSpawn = new Location(this.world, coordSpawn.getX(), coordSpawn.getY(), coordSpawn.getZ());
			return gameSpawn;
		} catch (Exception e) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reload");
		}
		return null;
	}	

	public List<Region> getRegions(RegionType type) {

		List<Region> regions = new ArrayList<Region>();
		for (Region region : this.mapRegions) {
			if (region.getType() == type) {
				regions.add(region);
			}
		}

		return regions;

	}

	public List<GameSpawns> getTeamCoords(ChatColor color) {

		List<GameSpawns> teams = new ArrayList<GameSpawns>();
		for (GameSpawns team : this.spawnCoords) {
			if (team.getColor() == color) {
				teams.add(team);
			}
		}
		return teams;
	}
	
	public boolean isInsideBase(Player player) {
		for (Region region : this.getRegions(RegionType.TEAM)) {
			if (region.hasLocation(player.getLocation())) {
				return true;
			}
		}

		return false;
	}

	public List<Session> getTeamMembers(ChatColor color) {

		List<Session> sessions = new ArrayList<Session>();

		for (Session session : HyperPVP.getPlayers()) {
			if (session.getTeam() != null && session.getTeam().getColor() == color) {
				sessions.add(session);
			}
		}

		return sessions;
	}

	private TeamMap teamSorted(int first, int second) {
		if (first == 0 && second == 0) {
			return null;
		}

		if (first > second) {
			return this.teams.get(0);
		} 

		if (first < second) {
			return this.teams.get(1);
		}
		
		return null;
	}

	public TeamMap getTeamTicketsWinning() {

		int first = this.getTeamTickets(0);
		int second = this.getTeamTickets(1);
		
		return teamSorted(first, second);
	}
	

	public TeamMap getTeamTicketsLosing() {

		int first = this.getTeamTickets(1);
		int second = this.getTeamTickets(0);

		return teamSorted(first, second);
	}
	
	public TeamMap getTeamWinning() {

		int first = this.getTeamStats(0);
		int second = this.getTeamStats(1);

		return teamSorted(first, second);
	}
	
	public TeamMap getTeamLosing() {

		int first = this.getTeamStats(1);
		int second = this.getTeamStats(0);

		return teamSorted(first, second);
	}

	public List<Session> getTop() {

		List<Session> map = new ArrayList<Session>();

		for (Session session : HyperPVP.getPlayers()) {

			map.add(session);
		}

		Comparator<Session> comparator = new Comparator<Session>() {

			public int compare(Session c1, Session c2) {
				if (c1.getKills() < c2.getKills()) {
					return 1;
				}
				if (c1.getKills() > c2.getKills()) {
					return -1;
				}
				return -1;
			}
		};

		Collections.sort(map, comparator);

		return map;
	}

	public TeamMap getTeamByName(String name) {

		for (TeamMap team : teams) {
			if (team.getColor().name().toLowerCase().replace("_", " ").replace("dark ", "").startsWith(name)) {
				return team;
			}
		}

		return null;

	}
	
	public void resetWhoBroke() {

		for (Session member : HyperPVP.getPlayers()) {
			member.setDestroyer(false);
		}
	}

	public int countTeamBlocksDestroyed(TeamMap team) {

		int blocks = 0;

		for (Session member : HyperPVP.getPlayers()) {
			if (member.getTeam() == team) {
				blocks += member.getMonumentBlocks().size();
			}
		}
		return blocks;
	}
	
	public TeamMap getTeam(ChatColor color) {
		
		for (TeamMap team : HyperPVP.getMap().getTeams()) {
			if (team.getColor() == color) {
				return team;
			}
		}
		
		return null;
	}

	public int getTeamStats(int index) {
		return this.teams.get(index).getKills();
	}

	public int getTeamTickets(int index) {
		return this.teams.get(index).getTickets();
	}
	
	public int getCounter() {
		return HyperPVP.getSpectators().size();
	}

	public World getWorld() {

		return world;
	}

	public String getMapName() {
		return mapName;
	}

	public List<GameSpawns> getCoords() {
		return spawnCoords;
	}

	public Location getSpawn() {
		return new Location(this.world, this.X, this.Y, this.Z);
	}

	public void setSpawn(Location spawn) {
	}

	public GameType getType() {
		return type;
	}

	public List<TeamMap> getTeams() {
		return teams;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public long getMap() {
		return this.mapid;
	}

	public int getTime() {
		return time;
	}

	public String getAuthor() {
		return author;
	}

	public List<String> getFeatures() {
		return features;
	}

	public int getMaxPerTeam() {
		return maxPlayers;
	}

	/**
	 * @return the thundering
	 */
	public boolean isThundering() {
		return thundering;
	}

	/**
	 * @param thundering the thundering to set
	 */
	public void setThundering(boolean thundering) {
		this.thundering = thundering;
	}


	/*public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}*/
}
