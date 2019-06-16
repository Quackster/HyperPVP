package pvp.alexdev.org.tasks;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import pvp.alexdev.org.HyperPVP;

public class DayTask extends LobbyTask {

	public DayTask(Plugin pl, int secondInterval) {
		super(pl, secondInterval);
	}

	@Override
	public void run() {

		World world = HyperPVP.getWorld();

		if (HyperPVP.spawnWorldNull()) {
			WorldCreator creator = WorldCreator.name("lobby");
			creator.environment(Environment.NORMAL);
			creator.generator("CleanroomGenerator:.");
			world = creator.createWorld();
		}


		world.setTime(7000);

		if (world.isThundering()) {
			world.setThundering(false);
			world.setStorm(false);
			world.setThunderDuration(0);
		}
		
		if (HyperPVP.getNeedsKick()) {
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.kickPlayer(HyperPVP.getKickMessage());
			}
			
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Bukkit.shutdown();
		}

		//Outbreak.getZombie().teleport(Outbreak.getVillagerLocation());
	}

}
