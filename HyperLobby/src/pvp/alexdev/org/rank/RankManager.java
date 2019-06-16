package pvp.alexdev.org.rank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import pvp.alexdev.org.HyperPVP;

public class RankManager {

	private Map<Integer, String> ranks;
	
	public RankManager() {
		this.ranks = new HashMap<Integer, String>();
		this.load();
	}

	private void load() {
		
		try {
			ResultSet row = HyperPVP.getStorage().prepare("SELECT * FROM users_ranks").executeQuery();
			
			while (row.next()) {
				
				this.ranks.put(row.getInt("id"), row.getString("rank"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, String> getRanks() {
		return ranks;
	}
}
