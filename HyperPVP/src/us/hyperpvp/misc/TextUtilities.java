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

import java.util.regex.Pattern;

public class TextUtilities {

	public static String getFinalArg(String[] args, int start)
	{
		StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++)
		{
			if (i != start)
			{
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}
	
	public static String replaceFormat(String input) {
		if (input == null) {
			return "";
		}
		
		return replaceColor(input, Pattern.compile("(?<!&)&([0-9a-fk-orA-FK-OR])"));
	}
	
	static String replaceColor(String input, Pattern pattern) {
		return Pattern.compile("&&(?=[0-9a-fk-orA-FK-OR])").matcher(pattern.matcher(input).replaceAll("\u00a7$1")).replaceAll("&");
	}
}
