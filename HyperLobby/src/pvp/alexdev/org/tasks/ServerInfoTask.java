package pvp.alexdev.org.tasks;

import org.bukkit.plugin.Plugin;

import pvp.alexdev.org.util.ServerReadThread;


public class ServerInfoTask extends LobbyTask {

	public ServerInfoTask(Plugin pl, int secondInterval) {
		super(pl, secondInterval);
	}

	@Override
	public void run() {
		
		Thread thread = new Thread(new ServerReadThread());
		thread.start();

	}

}
