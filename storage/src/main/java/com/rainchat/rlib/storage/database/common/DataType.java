package com.rainchat.rlib.storage.database.common;

public enum DataType {

	INTEGER("INTEGER"),
	DOUBLE("DOUBLE"),
	VARCHAR("VARCHAR"),
	TEXT("TEXT"),
	BLOB("BLOB");
	
	private final String sqlType;
	
	private DataType(String sqlType)
	{
		this.sqlType = sqlType;
	}

	public String getSqlType() {
		return sqlType;
	}
	
	public static String getSqlType(DataType[] types)
	{
		String ret = "";
		int idx = 0;
		for (DataType t : types)
		{
			ret += t.sqlType;
			if (idx < (types.length - 1))
				ret += " ";
		}
		return (ret.trim());
	}
}
