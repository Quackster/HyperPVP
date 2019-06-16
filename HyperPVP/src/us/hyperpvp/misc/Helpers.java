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
package us.hyperpvp.misc;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class Helpers {

	public static int getSecondByTick(int seconds) {
		return seconds * 20;
	}
	
	public static int INVINCIBLE_TIMER = 10;
	public static int COMBAT_LOG_TIMER = 5;
	
	public static DyeColor getDye(Color colour) {
		
		if (colour == Color.AQUA) {
			return DyeColor.CYAN;
		}
		
		if (colour == Color.BLUE) {
			return DyeColor.BLUE;
		}
		
		if (colour == Color.BLACK) {
			return DyeColor.BLACK;
		}
		
		if (colour == Color.FUCHSIA) {
			return DyeColor.PINK;
		}
		
		if (colour == Color.GRAY) {
			return DyeColor.GRAY;
		}
		
		if (colour == Color.LIME) {
			return DyeColor.LIME;
		}
		
		if (colour == Color.MAROON) {
			return DyeColor.RED;
		}
		
		if (colour == Color.NAVY) {
			return DyeColor.BLUE;
		}
		
		if (colour == Color.OLIVE) {
			return DyeColor.GREEN;
		}
		
		if (colour == Color.ORANGE) {
			return DyeColor.ORANGE;
		}
		
		if (colour == Color.PURPLE) {
			return DyeColor.PURPLE;
		}
		
		if (colour == Color.RED) {
			return DyeColor.RED;
		}
		
		if (colour == Color.SILVER) {
			return DyeColor.GRAY;
		}
		
		if (colour == Color.TEAL) {
			return DyeColor.CYAN;
		}
		
		if (colour == Color.WHITE) {
			return DyeColor.WHITE;
		}
		
		return DyeColor.YELLOW;
		
	}
}
