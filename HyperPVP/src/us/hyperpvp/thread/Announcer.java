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
package us.hyperpvp.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.session.Session;
import us.hyperpvp.thread.misc.IThread;

public class Announcer extends IThread {

	private String prefix;
	private List<String> broadcasts;
	private int seconds;

	public Announcer() {
		
		this.seconds = HyperPVP.getConfiguration().getConfig().getInt("Broadcast.Interval");
		this.prefix = this.addColour(HyperPVP.getConfiguration().getConfig().getString("Broadcast.Prefix"));
		this.broadcasts = new ArrayList<String>();
		
		List<String> temp = HyperPVP.getConfiguration().getConfig().getStringList("Broadcast.Messages");
		
		for (String str : temp) {

			String input = str;
			input = this.addColour(input);
			this.broadcasts.add(input);
		}
	}

	private String addColour(String input) {
		for (ChatColor color : ChatColor.values()) {
			input = input.replace("<" + color.name() + ">", color.toString());
		}
		
		return input;
	}

	public void dispose() {
		this.broadcasts.clear();

	}

	@Override
	public void run() {

		while (true) {
			
			for (String msg : this.broadcasts) {

				for (Session session : HyperPVP.getSessions().values()) {
					
					Player player = session.getPlayer();
					String message = msg.replace("{name}", player.getName());
					player.sendMessage(this.prefix + message);

				}

				try {
					TimeUnit.SECONDS.sleep(seconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


			}
		}

	}

}
