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
package us.hyperpvp.game.map.region;

public enum RegionType {
	
	MAP("MAP"),
	BLOCK_PLACE_DESTORY("BLOCK_PLACE_DESTORY"),
	INTERACT("INTERACT"),
	TEAM("TEAM"),
	DTC("DTC"), 
	DTM("DTM"), 
	RTC("RTC"),
	CTW("CTW");
	
	private String type;
	
	RegionType(String type) {
		this.type = type;
	}

	public static RegionType toValue(String name) {

		RegionType[] enums = RegionType.class.getEnumConstants();

		for (RegionType enumm : enums) {
			if (enumm.getType().equals(name)) {
				return enumm;
			}
		}

		return null;
	}
	
	public String getType() {
		return type;
	}
}
