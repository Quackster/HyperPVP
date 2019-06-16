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
package us.hyperpvp.commands;

import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hyperpvp.HyperPVP;

public class AuthCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {

			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You can only register if you're on the server!");
				return true;
			}

			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "You have entered invalid arguments!");
				return false;
			}

			if (!this.isValidEmailAddress(args[0])) {
				sender.sendMessage(ChatColor.RED + "You have entered an invalid email!");
				return true;
			}

			if (args[1].length() < 3) {
				sender.sendMessage(ChatColor.RED + "Your password is smaller than 3 characters!");
				return true;
			}
		
			Player player = (Player)sender;

			if (HyperPVP.getStorage().readString("SELECT password FROM users WHERE username = '" + player.getName() + "'").length() != 0) {
				sender.sendMessage(ChatColor.GREEN + "Your account is already registered!");
				return true;
			}
			
			if (!HyperPVP.getStorage().entryExists("SELECT * FROM pincodes WHERE name = '" + player.getName() + "'")) {

				String pin = random();
				
				PreparedStatement statement = HyperPVP.getStorage().queryParams("INSERT INTO pincodes (id, name, email, password) VALUES (?, ?, ?, ?)"); {
					statement.setString(1, pin);
					statement.setString(2, player.getName());
					statement.setString(3, args[0]);
					statement.setString(4, args[1]);
					statement.execute();
				}
				
				player.sendMessage(ChatColor.GOLD + "Please go to " + ChatColor.YELLOW + "http://paintballpvp.com/register" + ChatColor.GOLD + " and enter your pincode " + ChatColor.YELLOW + pin);
			} else {
				sender.sendMessage(ChatColor.RED + "Your account is already registered, type /pin if you forgot your PIN code.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;

	}

	public String random() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder password = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			password.append(chars.toCharArray()[HyperPVP.getRandom().nextInt(chars.length())]);
		}

		return password.toString();
	}

	public boolean isValidEmailAddress(String email) {
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();
		return matchFound;
	}
}
