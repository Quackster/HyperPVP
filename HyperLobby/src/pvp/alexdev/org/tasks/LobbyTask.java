package pvp.alexdev.org.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class LobbyTask extends BukkitRunnable {

	private int taskId = 0;

	@SuppressWarnings("deprecation")
	public LobbyTask(Plugin pl, int secondInterval) {
		this.taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pl, this, 0, secondInterval * 20);
	}

	/**
	 * @return the task id
	 */
	public int getTaskId() {
		return taskId;
	}

}
