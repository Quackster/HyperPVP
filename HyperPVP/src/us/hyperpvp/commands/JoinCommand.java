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

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hyperpvp.HyperPVP;

public class JoinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player)sender;

		if (HyperPVP.getSession(player).isPlaying()) {
			sender.sendMessage(ChatColor.RED + "You are already in a game!");
			return true;
		}

		if (HyperPVP.isCycling()) {
			sender.sendMessage(ChatColor.RED + "You can't join while a game is cycling!");
			return true;
		}

		int rank = 1;

		try {
			rank = HyperPVP.getStorage().readInt32("SELECT rank FROM users WHERE username = '" + player.getName() + "'");	
		} catch (SQLException e) {
			rank = 1;
			e.printStackTrace();	
		}

		if (rank >= 6) {
			player.sendMessage("You don't have permission!");
			return true;
		}

		if (rank >= 3) {

			if (args.length == 1) {
				HyperPVP.getMap().joinGame(player, args[0]);
			} else {
				HyperPVP.getMap().joinGame(player, null);
			}

		} else {

			HyperPVP.getMap().joinGame(player, null);
		}

		return true;
	}

}
