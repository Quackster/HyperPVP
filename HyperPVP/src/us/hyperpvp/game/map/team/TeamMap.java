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
package us.hyperpvp.game.map.team;

import org.bukkit.ChatColor;

import us.hyperpvp.misc.CycleUtil;

public class TeamMap {
	
	private ChatColor color;
	private int kills;
	private String format;
	
	private int tickets;
	private int resetTickets;
	
	public TeamMap(ChatColor color) {
		this.color = color;
		this.resetTickets = 0;
		this.reset();
	}
	
	public TeamMap(ChatColor color, int tickets) {
		this.color = color;
		this.resetTickets = tickets;
		this.reset();
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public void killIncrease() {
		this.kills = this.kills + 1;
	}
	
	public void decreaseTickets() {
		if (this.resetTickets > 0) {
			this.tickets = this.tickets - 1;
			
			if (this.tickets == 0) {
				CycleUtil.cycleNext(true, null, null);
			}
		}
	}
	
	public void killDecrease() {
		//this.kills = this.kills - 1;
	}
	
	public void reset() {
		this.kills = 0;
		this.tickets = this.resetTickets;
	}
	
	public Integer getKills() {
		return this.kills;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getTickets() {
		return tickets;
	}

	public void removeTicket() {
		this.tickets = this.tickets - 1;
	}


}
