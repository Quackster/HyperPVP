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
package pvp.alexdev.org.rank.bans;

public class Ban {

	private String reason;
	private long expire;
	private String date;
	private BanType type;
	private BanSubject subject;
	
	public Ban(String reason, long expire, String date, BanType type, BanSubject subject) {
		this.reason = reason;
		this.expire = expire;
		this.date = date;
		this.type = type;
		this.subject = subject;
	}

	public String getReason() {
		return reason;
	}

	public long getExpire() {
		return expire;
	}
	
	public boolean isPermanent() {
		return expire == 0;
	}

	public String getDate() {
		return date;
	}

	public BanType getType() {
		return type;
	}

	public BanSubject getSubject() {
		return subject;
	}
	
	
}
