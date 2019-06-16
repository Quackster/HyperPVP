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
package us.hyperpvp.game;

import org.bukkit.ChatColor;

public class GameSpawns {

	private double X;
	private double Y;
	private double Z;
	private ChatColor color;

	public GameSpawns(ChatColor color, double x2, double y2, double z2) {
		this.color = color;
		this.X = x2;
		this.Y = y2;
		this.Z = z2;
	}
	
	public double getX() {
		return this.X;
	}
	
	public double getY() {
		return this.Y;
	}
	
	public double getZ() {
		return this.Z;
	}

	public ChatColor getColor() {
		return color;
	}
	
}
