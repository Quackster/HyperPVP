package pvp.alexdev.org.tasks;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import pvp.alexdev.org.HyperPVP;

public class RconNetwork implements Runnable {

	private ServerSocket socket;
	private boolean closing;

	public RconNetwork() {

		try {
			HyperPVP.getJavaPlugin().getLogger().info("RCON listening on port: " + 56222);
			this.socket = new ServerSocket(56222);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {

		while (true) {

			Socket client = null;

			try {
				client = this.socket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (client == null) {
				continue;
			}

			DataInputStream inputStream = null;

			try {

				inputStream = new DataInputStream(client.getInputStream());
				inputStream.readShort();

				byte[] headerPacket = new byte[inputStream.readShort()];
				inputStream.read(headerPacket);
				String header = new String(headerPacket);

				byte[] messagePacket = new byte[inputStream.readShort()];
				inputStream.read(messagePacket);
				String message = new String(messagePacket);
				
				Bukkit.getLogger().info("[RconNetwork] Packet received: " + header + " - " + message);

				if (header.equalsIgnoreCase("restart")) {

					if (!this.closing) {
						
						Bukkit.broadcastMessage("<" + ChatColor.RED + "*Console" + ChatColor.WHITE + "> A scheduled reboot has occurred.");
						this.closing = true;
					}

					Bukkit.broadcastMessage("<" + ChatColor.RED + "*Console" + ChatColor.WHITE + "> " + message);
				}
				
				
				
				if (header.equalsIgnoreCase("messageNoFormat")) {
					Bukkit.broadcastMessage(message);
				}
				
				if (header.equalsIgnoreCase("restartwarning")) {

					TimeUnit.SECONDS.sleep(15);
					announce("45");

					TimeUnit.SECONDS.sleep(15);
					announce("30");

					TimeUnit.SECONDS.sleep(15);
					announce("15");

					TimeUnit.SECONDS.sleep(5);
					announce("10");

					TimeUnit.SECONDS.sleep(5);
					announce("5");

					TimeUnit.SECONDS.sleep(1);
					announce("4");

					TimeUnit.SECONDS.sleep(1);
					announce("3");

					TimeUnit.SECONDS.sleep(1);
					announce("2");

					TimeUnit.SECONDS.sleep(1);
					announce("1");
					
					HyperPVP.setNeedsKick(true);
					HyperPVP.setKickMessage(ChatColor.RED + "Disconnected: " + ChatColor.WHITE + "Server is restarting!");
				}

				inputStream.close();
				client.close();


			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	
	public static void announce(String seconds) {
		Bukkit.broadcastMessage("<" + ChatColor.RED + "*Console" + ChatColor.WHITE + "> Server is restarting in " + seconds + " " + (seconds.equals("1") ? "second" : "seconds!"));
	}

	public ServerSocket getSocket() {
		return socket;
	}

}
