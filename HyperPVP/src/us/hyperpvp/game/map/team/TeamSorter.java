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

import java.util.List;

import us.hyperpvp.HyperPVP;
import us.hyperpvp.game.GameType;
import us.hyperpvp.game.map.GameMap;
import us.hyperpvp.game.session.Session;

public class TeamSorter {

	public static void sort(Session session, GameMap map) {

		if (map.getTeams().size() == 1 || map.getType() == GameType.FFA) {
			session.setTeam(map.getTeams().get(0));
			return;
		}

		if (map.getTeams().size() == 2) {
			List<Session> first = map.getTeamMembers(map.getTeams().get(0).getColor());
			List<Session> second = map.getTeamMembers(map.getTeams().get(1).getColor());

			if (first.size() > second.size()) {
				session.setTeam(map.getTeams().get(1));

			} else if (first.size() < second.size()) {
				session.setTeam(map.getTeams().get(0));
			}

		}

		if (map.getTeams().size() == 3) {
			List<Session> first = map.getTeamMembers(map.getTeams().get(0).getColor());
			List<Session> second = map.getTeamMembers(map.getTeams().get(1).getColor());
			List<Session> third = map.getTeamMembers(map.getTeams().get(2).getColor());

			if (third.size() < first.size() 
					&& third.size() < second.size()) {

				session.setTeam(map.getTeams().get(2));
				return;
			}

			if (second.size() < first.size() 
					&& second.size() < third.size()) {

 				session.setTeam(map.getTeams().get(1));
				return;
			}

			if (first.size() < second.size() 
					&& first.size() < third.size()) {

				session.setTeam(map.getTeams().get(0));
				return;
			}
		}

		if (map.getTeams().size() == 4) {
			List<Session> first = map.getTeamMembers(map.getTeams().get(0).getColor());
			List<Session> second = map.getTeamMembers(map.getTeams().get(1).getColor());
			List<Session> third = map.getTeamMembers(map.getTeams().get(2).getColor());
			List<Session> four = map.getTeamMembers(map.getTeams().get(3).getColor());

			if (four.size() < third.size() 
					&& four.size() < second.size() 
					&& four.size() < first.size()) {

				session.setTeam(map.getTeams().get(3));
				return;
			}

			if (third.size() < four.size() 
					&& third.size() < second.size() 
					&& third.size() < first.size()) {

				session.setTeam(map.getTeams().get(2));
				return;
			}

			if (second.size() < four.size() 
					&& second.size() < third.size() 
					&& second.size() < first.size()) {

				session.setTeam(map.getTeams().get(1));
				return;
			}

			if (first.size() < four.size() 
					&& second.size() < third.size() 
					&& second.size() < second.size()) {

				session.setTeam(map.getTeams().get(0));
				return;
			}
		}

		
		//if all else fails.
		
		session.setTeam(map.getTeams().get(HyperPVP.getRandom().nextInt(map.getTeams().size())));
	}

}
