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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameSpawns;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.team.TeamMap;

public class GameMapManager {

	private HashMap<Integer, GameMap> maps;
	private GameMap currentMap;
	private int currentIndex = 0;

	@SuppressWarnings("unchecked")
	public GameMapManager(HyperPVP plugin) {
		this.maps = new HashMap<Integer, GameMap>();
		
		HashMap<Integer, GameMap> tempMaps = new HashMap<Integer, GameMap>();

		int i = 0;

		for (File file : new File(HyperPVP.getJavaPlugin().getDataFolder().getAbsolutePath() + File.separator + "maps").listFiles()) {

			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

			List<GameSpawns> coords = new ArrayList<GameSpawns>();

			for (String line : (List<String>)conf.getList("Settings.Spawns")) {

				String[] str = line.split(",");

				ChatColor color = ChatColor.valueOf(str[0]);
				double x = Double.parseDouble(str[1]);
				double y = Double.parseDouble(str[2]);
				double z = Double.parseDouble(str[3]);

				coords.add(new GameSpawns(color, x, y, z));

			}
			
			boolean thundering = false;
			boolean storm = false;
			
			if (conf.contains("Settings.Weather.Thundering")) {
				thundering = conf.getBoolean("Settings.Weather.Thundering");
			}
			
			if (conf.contains("Settings.Weather.Storm")) {
				storm = conf.getBoolean("Settings.Weather.Storm");
			}
			
			int index = 0;
			int[] tickets = null;
			
			if (conf.contains("Settings.Tickets")) {
				tickets = new int[conf.getList("Settings.Tickets").size()];
				for (Integer line : (List<Integer>)conf.getList("Settings.Tickets")) {
					tickets[index] = line;
					index++;
				}
			}

			index = 0;
			
			List<TeamMap> teams = new ArrayList<TeamMap>();
			for (String line : (List<String>)conf.getList("Settings.Teams")) {
				if (tickets != null) {
					teams.add(new TeamMap(ChatColor.valueOf(line), tickets[index]));
					index++;
				}
				else {
					teams.add(new TeamMap(ChatColor.valueOf(line)));
				}
			}

			String author = "";
			for (String line : (List<String>)conf.getList("Settings.Authors")) {
				author += line + ", ";
			}

			List<String> specialfeatures = new ArrayList<String>();

			for(String feature : (List<String>)conf.getList("Settings.Features")) {
				specialfeatures.add(feature);
			}


			List<ItemStack> items = new ArrayList<ItemStack>();

			for (String line : (List<String>)conf.getList("Settings.Items")) {

				String[] str = null;//line.split(",");

				
				if (line.contains("|")) {
					str = line.split("\\|")[0].split(",");
					
				} else {
					str = line.split(",");
				}

				ItemStack stack = new ItemStack(value(str[0]));
				stack.setAmount(Integer.parseInt(str[1]));

				if (line.contains("|")) {

					for (String e : line.replace(line.split("\\|")[0] + "|", "").split("\\|")) {

						ItemMeta meta = stack.getItemMeta();

						int enchantmentLevel = Integer.parseInt(e.split(",")[1]);
						meta.addEnchant(Enchantment.getByName(e.split(",")[0]), enchantmentLevel, true);

						stack.setItemMeta(meta);
					}
				}
				
				items.add(stack);
			}

			WorldCreator creator = WorldCreator.name(conf.getString("Settings.World"));
			creator.environment(Environment.NORMAL);
			creator.generator("CleanroomGenerator:.");
			World world = creator.createWorld();
			world.setAutoSave(false);

			double x = conf.getDouble("Settings.X");
			double y = conf.getDouble("Settings.Y");
			double z = conf.getDouble("Settings.Z");

			Location spawn = new Location(world, x, y, z);

			i = i + 1;
			
			int max = Integer.parseInt(conf.getString("Settings.MaxPerTeam"));
		
			
			try {
				tempMaps.put(i, new GameMap(conf, i, GameType.get(conf.getString("Settings.Mode")), conf.getInt("Settings.Minutes"), world, conf.getString("Settings.Name"), author.substring(0, author.length() - 2), conf.getString("Settings.World"), spawn, coords, teams, max, items, specialfeatures, storm, thundering));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		List<GameMap> gamemaps = new ArrayList<GameMap>();

		for (GameMap map : tempMaps.values()) {
			gamemaps.add(map);
		}
		
		Collections.shuffle(gamemaps);

		this.maps = new HashMap<Integer, GameMap>();

		for (int j = 0; j < gamemaps.size(); j++) {
			this.maps.put(j, gamemaps.get(j));
		}

		this.changeWorld();
		
	}
	
	public Material value(String str) {
		
		for (Material m : Material.values()) {
			
			if (m.name().contains(str)) {
				return m;
			}
		}
		
		return Material.AIR;
	}

	public GameMap changeWorld() {

		if (this.currentMap == null) {
			this.currentMap = this.maps.get(0);
			this.currentIndex = 0;

		} else {

			HyperPVP.setPreviousWorld(this.getGameMap());

			if (this.maps.containsKey(this.currentIndex + 1)) {
				this.currentMap = this.maps.get(this.currentIndex + 1);
				this.currentIndex = this.currentIndex + 1;
			} else {
				this.currentMap = this.maps.get(0);
				this.currentIndex = 0;
			}

		}

		if (!this.getGameMap().isLoaded()) {
			this.getGameMap().load();
		}
		
		GameMap map = this.getGameMap();
		return map;
	}

	public GameMap changeWorld(String mapName) {

		GameMap map = null;

		for (Entry<Integer, GameMap> set : this.maps.entrySet()) {

			map = set.getValue();

			if (map.getWorldName().equals(mapName)) {
				this.currentMap = map;
				this.currentIndex = set.getKey();
			}
		}

		if (map == null) {
			this.changeWorld();
		}

		if (!this.getGameMap().isLoaded()) {
			this.getGameMap().load();
		}

		return this.getGameMap();
	}

	public World getCurrentWorld() {
		return currentMap.getWorld();
	}

	public GameMap getGameMap() {
		return this.maps.get(this.currentIndex);
	}

	public GameMap getGameMap(World world) {

		for (GameMap map : this.maps.values()) {
			if (map.getWorld() == world) {
				return map;
			}
		}

		return null;
	}

}
