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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.misc.TextUtilities;

public class TeamChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {

			if (!(sender instanceof Player)) {
				return true;
			}

			Player player = (Player)sender;

			if (!HyperPVP.getSession(player).isPlaying()) {
				sender.sendMessage(ChatColor.RED + "You can't team chat if you're not in a game!");
				return true;
			}

			if (HyperPVP.isCycling()) {
				sender.sendMessage(ChatColor.RED + "You can't team chat while the game is cycling!");
				return true;
			}

			if (args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Not enough arguments!");
				return false;
			}
			
			String message = TextUtilities.getFinalArg(args, 0);
			
			Session session = HyperPVP.getSession(player);
			String teamName = HyperPVP.capitalize(session.getTeam().getColor().name().toLowerCase().replace("_", " "));
			
			for (Session teamMate : HyperPVP.getMap().getTeamMembers(session.getTeam().getColor())) {
				teamMate.getPlayer().sendMessage("<" + session.getTeam().getColor() + teamName + " Team" + ChatColor.WHITE + ">" + ChatColor.GRAY +" " + player.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE + message);
			}
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
