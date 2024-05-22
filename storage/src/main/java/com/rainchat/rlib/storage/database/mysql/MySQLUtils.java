package com.rainchat.rlib.storage.database.mysql;

import com.rainchat.rlib.storage.database.common.DatabaseUtils;
import com.rainchat.rlib.storage.database.common.DynamicPreparedStatement;
import com.rainchat.rlib.storage.database.common.KeyInfo;
import com.rainchat.rlib.storage.database.common.UtilsExceptions.*;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLUtils extends DatabaseUtils {

	private final MySQLProperties props;

	public MySQLUtils(MySQLProperties props) throws ClassNotFoundException {
		this(props, new DynamicPreparedStatement());
	}

	public MySQLUtils(MySQLProperties props, DynamicPreparedStatement builder) throws ClassNotFoundException {
		super(builder);
		Class.forName("com.mysql.jdbc.Driver");
		this.props = props;
	}

	@Override
	protected void connectToDb() throws ConnectionException {
		try {
			con = DriverManager.getConnection(props.getUrl(), props.user, props.password);
			execStatement = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ConnectionException();
		}
	}

	@Override
	public String getSqlType(KeyInfo info) {
		switch (info)
		{
		case AUTO_INCREMENT:
			return ("AUTO_INCREMENT");
		case NOT_NULL:
			return ("NOT NULL");
		case PRIMARY_KEY:
			return ("PRIMARY KEY");
		}
		return (null);
	}
}
