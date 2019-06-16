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
import us.hyperpvp.misc.CycleUtil;


public class CycleCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {

			if (sender instanceof Player) {
				Player player = (Player)sender;

				if (!player.isOp()) {
					sender.sendMessage(ChatColor.RED + "Insufficient permissions to perform this action");
					return true;
				}
			}

			if (HyperPVP.isCycling()) {
				sender.sendMessage(ChatColor.RED + "Can't cycle while another cycle is in progress.");
				return true;
			}

			if (args.length != 1) {
				CycleUtil.cycleNext(true, null, null);

			} else if (args.length == 1) {
				CycleUtil.cycleNext(true, null, args[0]);
			}

			//this.plugin.getThreads().put(ThreadType.FIGHT, new FightThread(this.plugin));
			//this.plugin.getThreads().get(ThreadType.FIGHT).start();


		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

}
