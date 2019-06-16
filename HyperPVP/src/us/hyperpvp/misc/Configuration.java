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
package us.hyperpvp.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {

	private File mFile;
	private FileConfiguration mConfig;

	public Configuration(boolean config, JavaPlugin Plugin, String FileName)
	{
		try {
			
			this.mFile = new File(Plugin.getDataFolder(), FileName);

			if (this.isEmpty()) {
				
				if (!Plugin.getDataFolder().exists()) 
					Plugin.getDataFolder().mkdir();
				
				if (!this.mFile.exists()) {
					this.mFile.createNewFile();
				}
				
				this.mConfig = YamlConfiguration.loadConfiguration(mFile);
				this.setValues(config);
			
			} else {
				this.mConfig = YamlConfiguration.loadConfiguration(mFile);
			}
		
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	
	public Configuration(boolean config, JavaPlugin Plugin, File mFile)
	{
		try {
			
			this.mFile = mFile;

			if (this.isEmpty()) {
				
				if (!Plugin.getDataFolder().exists()) 
					Plugin.getDataFolder().mkdir();
				
				if (!this.mFile.exists()) {
					this.mFile.createNewFile();
				}
				
				this.mConfig = YamlConfiguration.loadConfiguration(mFile);
				this.setValues(config);
			
			} else {
				this.mConfig = YamlConfiguration.loadConfiguration(mFile);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setValues(boolean config) {

		if (!config) {
			return;
		}
		
		try
		{
			mConfig.addDefault("MySQL.Hostname", "localhost");
			mConfig.addDefault("MySQL.Username", "root");
			mConfig.addDefault("MySQL.Password", "password");
			mConfig.addDefault("MySQL.Database", "paintballpvp");
			mConfig.addDefault("Server", "Thor");
			
			List<String> messages = new ArrayList<String>();
			messages.add("Type <AQUA>/join<GRAY> to play a game.");
			messages.add("View your stats at <AQUA>hyperpvp.us/profile/{name}");
			messages.add("Use <AQUA>/report<GRAY> to report a misbehaving player.");
			messages.add("Our website is <AQUA>hyperpvp.us");
			messages.add("Visit our forum <AQUA>hyperpvp.us/forum");
			
			mConfig.addDefault("Broadcast.Prefix", "<GRAY>[<AQUA><BOLD><ITALIC>TIP<RESET><GRAY>] ");
			mConfig.addDefault("Broadcast.Messages", messages);
			
			mConfig.options().copyDefaults(true);
			mConfig.save(this.mFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean isEmpty() {
		try {
			return mFile.length() == 0;
		} catch (Exception e) {
			return true;
		}
	}
	
	public FileConfiguration getConfig() {
		return mConfig;
	}
}
