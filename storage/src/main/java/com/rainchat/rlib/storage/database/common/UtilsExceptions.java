package com.rainchat.rlib.storage.database.common;

public abstract class UtilsExceptions {

	public static class ConnectionException extends Exception
	{
		private static final long serialVersionUID = 8528273750888299126L;
	}
	
	public static class TableCreationException extends Exception
	{
		private static final long serialVersionUID = -9181237485320757667L;	
	}
	
	public static class WrongBaseClassException extends Exception
	{
		private static final long serialVersionUID = 4628610289787775841L;
	}
	
	public static class TableDropException extends Exception
	{
		private static final long serialVersionUID = 4678243439607058494L;
	}
	
	public static class DataInsertionException extends Exception
	{
		private static final long serialVersionUID = -4228831363934452798L;	
	}
	
	public static class DataUpdateException extends Exception
	{
		private static final long serialVersionUID = -7293916309834496952L;	
	}
	
	public static class DataRetrieveException extends Exception
	{
		private static final long serialVersionUID = -5706035183964371288L;	
	}
	
	public static class DataDeleteException extends Exception
	{
		private static final long serialVersionUID = -3925653474272909058L;	
	}
}
