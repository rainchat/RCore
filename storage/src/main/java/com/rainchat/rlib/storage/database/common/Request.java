package com.rainchat.rlib.storage.database.common;

import java.io.Serializable;

public abstract class Request implements Serializable {

	/**
	 * Called after the data was inserted in database </br>
	 * Can be used to retrieve a auto incremented variable set by the database
	 * @param utils
	 * @throws UtilsExceptions.WrongBaseClassException
	 * @throws UtilsExceptions.DataRetrieveException
	 */
	public void postInsert(DatabaseUtils utils) throws UtilsExceptions.WrongBaseClassException, UtilsExceptions.DataRetrieveException {}

	/**
	 * Called after an update has been done </br>
	 * Should be used to update where clauses if they are not set as Value field
	 * @param utils The database utils
	 * @throws UtilsExceptions.WrongBaseClassException
	 * @throws UtilsExceptions.DataRetrieveException
	 */
	public void postUpdate(DatabaseUtils utils) throws UtilsExceptions.WrongBaseClassException, UtilsExceptions.DataRetrieveException {}

	/**
	 * Called after a retrieve of data </br>
	 * Should update where clauses that are not set as Value
	 * @param utils
	 * @throws UtilsExceptions.WrongBaseClassException
	 * @throws UtilsExceptions.DataRetrieveException
	 */
	public void postRetrieve(DatabaseUtils utils) throws UtilsExceptions.WrongBaseClassException, UtilsExceptions.DataRetrieveException {}
}
