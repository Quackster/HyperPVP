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
import org.bukkit.Material;

public class Helpers {

	public static int getSecondByTick(int seconds) {
		return seconds * 20;
	}
	
	public static int INVINCIBLE_TIMER = 10;
	public static int COMBAT_LOG_TIMER = 5;
	
	public static Material getDye(Color colour) {
		if (colour == Color.AQUA) {
			return Material.CYAN_WOOL;
		}
		
		if (colour == Color.BLUE) {
			return Material.BLUE_WOOL;
		}
		
		if (colour == Color.BLACK) {
			return Material.BLACK_WOOL;
		}
		
		if (colour == Color.FUCHSIA) {
			return Material.PINK_WOOL;
		}
		
		if (colour == Color.GRAY) {
			return Material.GRAY_WOOL;
		}
		
		if (colour == Color.LIME) {
			return Material.LIME_WOOL;
		}
		
		if (colour == Color.MAROON) {
			return Material.RED_WOOL;
		}
		
		if (colour == Color.NAVY) {
			return Material.BLUE_WOOL;
		}
		
		if (colour == Color.OLIVE) {
			return Material.GREEN_WOOL;
		}
		
		if (colour == Color.ORANGE) {
			return Material.ORANGE_WOOL;
		}
		
		if (colour == Color.PURPLE) {
			return Material.PURPLE_WOOL;
		}
		
		if (colour == Color.RED) {
			return Material.RED_WOOL;
		}
		
		if (colour == Color.SILVER) {
			return Material.GRAY_WOOL;
		}
		
		if (colour == Color.TEAL) {
			return Material.CYAN_WOOL;
		}
		
		if (colour == Color.WHITE) {
			return Material.WHITE_WOOL;
		}
		
		return Material.YELLOW_WOOL;
		
	}
}
