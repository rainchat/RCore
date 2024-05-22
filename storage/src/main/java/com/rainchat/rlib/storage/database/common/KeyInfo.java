package com.rainchat.rlib.storage.database.common;


import com.rainchat.rlib.storage.database.annotations.Value;
public enum KeyInfo {

	PRIMARY_KEY,
	NOT_NULL,
	AUTO_INCREMENT;
	
	public static boolean containKeyInfo(Value field, KeyInfo info)
	{
		for (KeyInfo f : field.infos())
		{
			if (f == info)
				return (true);
		}
		return (false);
	}
}
