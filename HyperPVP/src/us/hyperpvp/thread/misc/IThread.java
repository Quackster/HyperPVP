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
package us.hyperpvp.thread.misc;

public abstract class IThread extends Thread {

	private boolean cancelled;

	public IThread() {
		this.cancelled = false;
	}


	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		
		if (this.cancelled) {
			//this.
			//this.interrupt();
		}
	}
}
