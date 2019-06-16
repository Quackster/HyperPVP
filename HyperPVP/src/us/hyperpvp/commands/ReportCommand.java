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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hyperpvp.misc.TextUtilities;

public class ReportCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player)sender;

		if (args.length <= 1) {
			sender.sendMessage(ChatColor.RED + "You haven't supplied enough arguments.");
			return false;
		}

		String userReport = args[0];

		if (userReport.length() == 0) {
			sender.sendMessage(ChatColor.RED + "You didn't enter a valid person to report.");
			return true;
		}

		@SuppressWarnings("deprecation")
		Player reported = Bukkit.getPlayer(userReport);

		if (reported == null) {
			sender.sendMessage(ChatColor.RED + "You didn't enter a valid person to report.");
			return true;
		}
		
		if (reported == player) {
			sender.sendMessage(ChatColor.RED + "You can't report yourself.");
			return true;
		}
		
		
		String reportMessage = TextUtilities.getFinalArg(args, 1);

		if (reportMessage.length() == 0) {
			sender.sendMessage(ChatColor.RED + "You didn't enter a valid reason to report.");
			return false;
		}
		
		String message = ChatColor.GOLD + player.getName() + ChatColor.DARK_AQUA + " reported " + ChatColor.YELLOW + userReport + ChatColor.DARK_AQUA + " for " + ChatColor.YELLOW + reportMessage;
		alert(message);
		
		player.sendMessage(ChatColor.GOLD + "Thank you for reporting, a staff member, if online, will reply shortly.");

		return true;
	}

	public void alert(String msg){
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("hyperpvp.seereports")) {
				p.sendMessage(msg);
			}
		}
	}
}
