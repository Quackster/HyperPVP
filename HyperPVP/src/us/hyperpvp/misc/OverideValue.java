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

import java.lang.reflect.Field;

public abstract class OverideValue {

	public String toValue() {
		
		StringBuilder b = new StringBuilder("{" + this.getClass().getName() + " ");
		
		String fields = "";
		for (Field f : this.getClass().getFields()) {
			
			try {
				fields += ", " + f.getName() + "=" + f.get(this.getClass()).toString();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		b.append(fields);
		
		
		b.append("}");
		
		return b.toString();
	}
}
