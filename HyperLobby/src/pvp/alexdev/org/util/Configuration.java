package pvp.alexdev.org.util;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {

	private File mFile;
	private FileConfiguration mConfig;

	public Configuration(JavaPlugin Plugin, String FileName)
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
				this.setValues();
			
			} else {
				this.mConfig = YamlConfiguration.loadConfiguration(mFile);
			}
		
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private void setValues() {

		try {
			
			mConfig.addDefault("MySQL.Host", "localhost");
			mConfig.addDefault("MySQL.Username", "root");
			mConfig.addDefault("MySQL.Password", "");
			mConfig.addDefault("MySQL.Database", "outbreak");
			
			mConfig.options().copyDefaults(true);
			mConfig.save(this.mFile);
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		return mConfig;
	}

	public Boolean isEmpty() {
		try
		{
			return mFile.length() == 0;
		}
		catch (Exception e)
		{
			return true;
		}
	}
}
