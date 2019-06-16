package pvp.alexdev.org.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import pvp.alexdev.org.HyperPVP;

public class Storage {

	private static Logger log = HyperPVP.getInstance().getLogger();
	private BoneCP connections = null;

	public Storage(String host, String username, String password, String db) {
		
		checkDriver();

		boolean isConnectionFailed = false;

		try {
			BoneCPConfig config = new BoneCPConfig();

			config.setJdbcUrl("jdbc:mysql://" + host + "/" + db);
			config.setUsername(username);
			config.setPassword(password);

			config.setMinConnectionsPerPartition(0);
			config.setMaxConnectionsPerPartition(5);
			config.setConnectionTimeout(1000, TimeUnit.SECONDS);
			config.setPartitionCount(1);

			log.info("Connecting to the MySQL server");
			this.connections = new BoneCP(config);

		} catch(Exception e) {
			isConnectionFailed = true;
			e.printStackTrace();
		} finally {
			if(!isConnectionFailed) {
				log.info("Connection to MySQL server was successful");
			}
		}
	}

	public int getConnectionCount() {
		return this.connections.getTotalLeased();
	}

	public BoneCP getConnections() {
		return this.connections;
	}

	public PreparedStatement prepare(String query) throws SQLException {
		return prepare(query, false);
	}

	public PreparedStatement prepare(String query, boolean returnKeys) throws SQLException {
		Connection conn = null;

		try {
			conn = this.connections.getConnection();

			if(returnKeys) {
				return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			} else {
				return conn.prepareStatement(query);
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return null;
	}

	public void execute(String query) {
		try {
			this.prepare(query).execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean exists(String query) throws SQLException {
		Connection conn = null;

		try {
			conn = this.connections.getConnection();
			return conn.createStatement().executeQuery(query).next();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return false;
	}

	public int count(String query) throws SQLException {
		Connection conn = null;

		try {
			conn = this.connections.getConnection();
			PreparedStatement statement = conn.prepareStatement(query);
			return this.count(statement);
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return 0;
	}

	public int count(PreparedStatement statement) {
		int i = 0;

		try {
			
			ResultSet result = statement.executeQuery();
			
			while (result.next()) {
				i++;
			}
			return i;
			
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public ResultSet getRow(String query) throws SQLException {
		Connection conn = null;
		try {
			conn = this.connections.getConnection();
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while(result.next()) {
				return result;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return null;
	}

	public ResultSet getTable(String query) throws SQLException {
		Connection conn = null;
		try {
			conn = this.connections.getConnection();
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			return result;
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return null;
	}

	public String getString(String query) {
		try {
			ResultSet result = this.prepare(query).executeQuery();
			result.first();

			String str = query.split(" ")[1];

			if(str.startsWith("`")) {
				str = str.substring(1, str.length() - 1);
			}

			return result.getString(str);
		} catch(SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public Integer getInt(String query) {
		
		return Integer.valueOf(this.getString(query));
	}

	public void checkDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
