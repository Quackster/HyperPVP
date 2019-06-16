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
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.session.Session;

public class ScoreCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
	
		if (sender instanceof Player) {
			Player player = (Player)sender;

			if (!player.isOp()) {
				return true;
			}
			
			
			Session session = HyperPVP.getSession(player);
			
			if (HyperPVP.getMap().getType() != GameType.FFA) {
				session.getTeam().killIncrease();
			} else {
				session.killIncrease();
			}
			
			for (Session s : HyperPVP.getSessions().values()) {
				s.updateScoreboard(true);
			}
		}
		
		return true;
	}

}
