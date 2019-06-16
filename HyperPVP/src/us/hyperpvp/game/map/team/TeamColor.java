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
import org.bukkit.Color;

public enum TeamColor {
	/**
	 * Represents black
	 */
	BLACK('0', 0x00, Color.BLACK),
	/**
	 * Represents dark blue
	 */
	DARK_BLUE('1', 0x1, Color.BLUE),
	/**
	 * Represents dark green
	 */
	DARK_GREEN('2', 0x2, Color.GREEN),
	/**
	 * Represents dark blue (aqua)
	 */
	DARK_AQUA('3', 0x3, Color.TEAL),
	/**
	 * Represents dark red
	 */
	DARK_RED('4', 0x4, Color.RED),
	/**
	 * Represents dark purple
	 */
	DARK_PURPLE('5', 0x5, Color.PURPLE),
	/**
	 * Represents gold
	 */
	GOLD('6', 0x6, Color.ORANGE),
	/**
	 * Represents gray
	 */
	GRAY('7', 0x7, Color.GRAY),
	/**
	 * Represents dark gray
	 */
	DARK_GRAY('8', 0x8, Color.GRAY),
	/**
	 * Represents blue
	 */
	BLUE('9', 0x9, Color.BLUE),
	/**
	 * Represents green
	 */
	GREEN('a', 0xA, Color.LIME),
	/**
	 * Represents aqua
	 */
	AQUA('b', 0xB, Color.AQUA),
	/**
	 * Represents red
	 */
	RED('c', 0xC, Color.RED),
	/**
	 * Represents light purple
	 */
	LIGHT_PURPLE('d', 0xD, Color.FUCHSIA),
	/**
	 * Represents yellow
	 */
	YELLOW('e', 0xE, Color.YELLOW),
	/**
	 * Represents white
	 */
	WHITE('f', 0xF, Color.WHITE);


	/**
	 * The special character which prefixes all chat colour codes. Use this if you need to dynamically
	 * convert colour codes from your custom format.
	 */
	public char COLOR_CHAR = '\u00A7';
	private String toString;
	private Color color;

	private TeamColor(char code, int intCode, Color color) {
		this(code, intCode, false, color);
	}

	private TeamColor(char code, int intCode, boolean isFormat, Color color) {
		this.toString = new String(new char[] {COLOR_CHAR, code});
		this.color = color;
	}

	public static Color get(ChatColor color) {

		for (TeamColor enumm : TeamColor.class.getEnumConstants()) {
			if (enumm.toString().equals(color.toString())) {
				return enumm.getColor();
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return toString;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
