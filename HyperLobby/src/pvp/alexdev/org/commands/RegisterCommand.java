package pvp.alexdev.org.commands;

import java.sql.ResultSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pvp.alexdev.org.HyperPVP;

public class RegisterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player player = (Player) sender;
		
		ResultSet row = null;

		try {
			row = HyperPVP.getStorage().getRow("SELECT id,password,salt FROM users WHERE uuid = '" + player.getUniqueId().toString() + "'");

			int id = row.getInt("id");
			String password = row.getString("password");
			String salt = row.getString("salt");
			
			if (password.length() == 0 || salt.length() == 0) {
				
				String pin = generatePin(6);
				HyperPVP.getStorage().execute("UPDATE users SET pin = '" + pin + "' WHERE id = '" + id + "'");
				
				player.sendMessage(ChatColor.GREEN + "Your pin code is " + ChatColor.GOLD + pin + ChatColor.GREEN + "  please write this down to register on the website");
				player.sendMessage(ChatColor.GRAY + "Use /pin if you have forgotten your pin code");
				
				
			} else {
				player.sendMessage(ChatColor.RED + "You are already registered");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	public static String generatePin(int length){
		
		String alphabet = new String("0123456789abcdefghijklmnopqrstuvwxyz"); //9
		String result = new String(); 

		for (int i = 0; i < length; i++) {
		    result = result + alphabet.charAt(HyperPVP.getRandom().nextInt(alphabet.length())); //13
		}
		
		return result;
	}

}
