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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.listeners.MiscListener;

public class SpectateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		try {
			
			if (!(sender instanceof Player)) {
				return true;
			}
			
			Player player = (Player)sender;
			
			if (HyperPVP.getSession(player).isPlaying() == false) {
				player.teleport(HyperPVP.getMap().getSpawn());
				//sender.sendMessage(ChatColor.RED + "You aren't playing a game!");
				return true;
			}
			
			if (!HyperPVP.isCycling()) {
				HyperPVP.getSession(player).leaveGame(true, false);
			}
			
			MiscListener.refreshTag(player);
			
			//TagAPI.refreshPlayer(player);
			//HyperTag.changePlayer(player);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
