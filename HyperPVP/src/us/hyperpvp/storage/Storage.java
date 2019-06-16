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
package us.hyperpvp.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import us.hyperpvp.HyperPVP;

public class Storage {

	private Connection connection;
	private Statement driverStatement;

	public Storage(String host, String username, String password, String db) throws SQLException {

		this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db, username, password);
		//this.connection.setAutoCommit(false);
		this.makeStatement();

	}

	public Storage(File file) throws SQLException, ClassNotFoundException, IOException {

		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		//this.connection.setAutoCommit(false);
		this.makeStatement();
		
		this.executeDBScripts(new File(HyperPVP.getJavaPlugin().getDataFolder(), "hyperdb.sql"), this.driverStatement);
	}

	//http://www.coderanch.com/t/306966/JDBC/databases/Execute-sql-file-java
	public boolean executeDBScripts(File file, Statement stmt) throws IOException,SQLException {
		boolean isScriptExecuted = false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n ");
			}
			in.close();
			stmt.executeUpdate(sb.toString());
			isScriptExecuted = true;
		} catch (Exception e) {
			System.err.println("Failed to execute " + file.getName() +". The error is "+ e.getMessage());
		}
		return isScriptExecuted;
	} 

	public void makeStatement() throws SQLException {

		this.driverStatement = connection.createStatement();

	}

	public String readString(String query) throws SQLException 
	{	
		ResultSet result = driverStatement.executeQuery(query);
		result.first();
		return result.getString(query.split(" ")[1]);
	}

	public Integer readInt32(String query) throws SQLException
	{	

		ResultSet result = driverStatement.executeQuery(query);
		result.first();
		return result.getInt(query.split(" ")[1]);

	}

	public PreparedStatement queryParams(String query) throws SQLException
	{

		return connection.prepareStatement(query);
	}

	public void executeQuery(String query) throws SQLException
	{

		driverStatement.execute(query);
	}

	public boolean entryExists(String query) throws SQLException
	{

		ResultSet result = driverStatement.executeQuery(query);
		return result.next();
	}

	public Integer entryCount(String q) throws SQLException
	{
		int i = 0;


		ResultSet resSet = driverStatement.executeQuery(q);

		while (resSet.next()) {
			++i;
		}
		return i;
	}

	public Integer entryCount(PreparedStatement pStmt) throws SQLException
	{
		int i = 0;

		ResultSet resSet = pStmt.executeQuery();

		while (resSet.next()) {
			++i;
		}

		return i;
	}

	public ResultSet readRow(String Query) throws SQLException
	{
		ResultSet resSet = driverStatement.executeQuery(Query);

		while (resSet.next()) {
			return resSet;
		}

		return null;
	}

	public Connection getConnection() {
		return this.connection;
	}
	
	public Statement getStatement() {
		return this.driverStatement;
	}

}
