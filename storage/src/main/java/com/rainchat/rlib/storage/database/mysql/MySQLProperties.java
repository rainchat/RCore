package com.rainchat.rlib.storage.database.mysql;

public class MySQLProperties {

	private String url = "jdbc:mysql://{1}:{2}/{3}";
	public String user, password;
	
	public void setHost(String host)
	{
		url = url.replace("{1}", host);
	}
	
	public void setPort(int port)
	{
		url = url.replace("{2}", Integer.toString(port));
	}
	
	public void setDatabase(String db)
	{
		url = url.replace("{3}", db);
	}
	
	public String getUrl()
	{
		return (url);
	}
}
