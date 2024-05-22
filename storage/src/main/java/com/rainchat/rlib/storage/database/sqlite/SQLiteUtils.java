package com.rainchat.rlib.storage.database.sqlite;

import com.rainchat.rlib.storage.database.common.DatabaseUtils;
import com.rainchat.rlib.storage.database.common.DynamicPreparedStatement;
import com.rainchat.rlib.storage.database.common.KeyInfo;
import com.rainchat.rlib.storage.database.common.UtilsExceptions;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteUtils extends DatabaseUtils {

	private final String dbPath;
	
	public SQLiteUtils(String dbPath) throws ClassNotFoundException
	{
		this(dbPath, new DynamicPreparedStatement());
	}
	
	/**
	 * 
	 * @param dbPath Database path, relative to the server.jar
	 * @throws ClassNotFoundException 
	 */
	public SQLiteUtils(String dbPath, DynamicPreparedStatement builder) throws ClassNotFoundException
	{
		super(builder);
		Class.forName("org.sqlite.JDBC");
		this.dbPath = dbPath;
	}
	
	@Override
	protected void connectToDb() throws UtilsExceptions.ConnectionException {
		try {
			this.con = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
			execStatement = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UtilsExceptions.ConnectionException();
		}
	}

	@Override
	public String getSqlType(KeyInfo info) {
		switch (info)
		{
		case AUTO_INCREMENT:
			return ("AUTOINCREMENT");
		case NOT_NULL:
			return ("NOT NULL");
		case PRIMARY_KEY:
			return ("PRIMARY KEY");
		}
		return (null);
	}

}
