package com.rainchat.rlib.storage.database.common;

import com.google.common.primitives.Bytes;
import com.rainchat.rlib.storage.database.annotations.Row;
import com.rainchat.rlib.storage.database.annotations.Value;
import com.rainchat.rlib.storage.database.annotations.Where;
import com.rainchat.rlib.storage.database.common.UtilsExceptions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseUtils {

	protected Connection con = null;
	protected Statement execStatement = null;
	protected final DynamicPreparedStatement statementBuilder;
	
	protected DatabaseUtils(DynamicPreparedStatement statementBuilder)
	{
		this.statementBuilder = statementBuilder;
	}

	protected boolean assertClassIsRow(Class<?> c)
	{
		return (c.getAnnotation(Row.class) != null);
	}

	protected boolean assertClassContainRetrieve(Class<?> c)
	{
		List<Field> fields = DynamicPreparedStatement.getFieldsUpTo(c, Request.class);
		for (Field f : fields)
		{
			if (f.getAnnotation(Where.class) != null)
				return (true);
		}
		return (false);
	}

	public void connect() throws ConnectionException
	{
		this.connectToDb();
		this.statementBuilder.setConnection(con);
	}
	
	public String getSqlType(KeyInfo[] infos)
	{
		String ret = "";
		for (KeyInfo t : infos)
			ret += getSqlType(t)+ " ";
		return (ret.trim());
	}
	public abstract String getSqlType(KeyInfo info);
	
	/**
	 * Connect to the database
	 * @throws ConnectionException
	 */
	protected abstract void connectToDb() throws ConnectionException;

	public void close() {
		try {
			if (execStatement != null && !execStatement.isClosed())
				execStatement.close();
			if (con != null && !con.isClosed())
				con.close();
		} catch (SQLException e) {}
	}

	/**
	 * Create the table linked to the descriptor
	 * @param descriptor The descriptor
	 * @throws WrongBaseClassException
	 * @throws TableCreationException
	 */
	public synchronized void createTable(Class<? extends Request> descriptor) throws WrongBaseClassException, TableCreationException
	{
		if (!this.assertClassIsRow(descriptor))
			throw new WrongBaseClassException();
		try {
			PreparedStatement createStatement = this.statementBuilder.createPreparedStatement(descriptor, this);
			createStatement.executeUpdate();
			createStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TableCreationException();
		}
	}

	/**
	 * Insert data in the database
	 * @param instance The instance containing data
	 * @throws WrongBaseClassException
	 * @throws TableCreationException
	 * @throws DataInsertionException
	 */
	public synchronized void insertData(Request instance) throws WrongBaseClassException, TableCreationException, DataInsertionException {
		if (!this.assertClassIsRow(instance.getClass()))
			throw new WrongBaseClassException();
		try {
			PreparedStatement insertStatement = this.statementBuilder.insertPreparedStatement(con, instance);
			insertStatement.executeUpdate();
			insertStatement.close();
			instance.postInsert(this);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException | IOException e) {
			e.printStackTrace();
			throw new DataInsertionException();
		} catch (DataRetrieveException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Update data in the database, using the where clauses of the instance
	 * @param instance The Request instance
	 * @throws WrongBaseClassException
	 * @throws DataUpdateException
	 */
	public synchronized void updateData(Request instance) throws WrongBaseClassException, DataUpdateException {
		if (!this.assertClassIsRow(instance.getClass()) || !this.assertClassContainRetrieve(instance.getClass()))
			throw new WrongBaseClassException();
		try {
			PreparedStatement updateStatement = this.statementBuilder.updatePreparedStatement(instance);
			updateStatement.executeUpdate();
			updateStatement.close();
			instance.postUpdate(this);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException | IOException | DataRetrieveException e) {
			e.printStackTrace();
			throw new DataUpdateException();
		}
	}

	private static void fillFieldValues(Field f, ResultSet rs, Request ret) throws IllegalArgumentException, IllegalAccessException, SQLException, ClassNotFoundException, IOException
	{
		Value db = f.getAnnotation(Value.class);
		if (db == null)
			return;
		f.setAccessible(true);
		switch (db.type())
		{
		case DOUBLE:
			f.setDouble(ret, rs.getDouble(db.fieldName()));
			break;
		case INTEGER:
			f.setInt(ret, rs.getInt(db.fieldName()));
			break;
		case TEXT:
			f.set(ret, rs.getString(db.fieldName()));
			break;
		case VARCHAR:
			f.set(ret, rs.getString(db.fieldName()));
			break;
		case BLOB:
			byte[] array = rs.getBytes(db.fieldName());
			ByteArrayInputStream bytesArray = new ByteArrayInputStream(array);
			ObjectInput in = new ObjectInputStream(bytesArray);
			f.set(ret, in.readObject());
			break;
		}
	}

	/**
	 * Retrieve data from the database
	 * @param instance The instance to use
	 * @return Return an object of instance T, T being the final type of instance
	 * @throws WrongBaseClassException
	 * @throws DataRetrieveException
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T retrieveData(Request instance) throws WrongBaseClassException, DataRetrieveException {
		if (!this.assertClassIsRow(instance.getClass()) || !this.assertClassContainRetrieve(instance.getClass()))
			throw new WrongBaseClassException();
		Request ret = null;
		try {
			PreparedStatement retrieveStatement = this.statementBuilder.retrievePreparedStatement(instance, true);
			ResultSet rs = retrieveStatement.executeQuery();
			if (!rs.next())
			{
				retrieveStatement.close();
				rs.close();
				return (null);
			}
			ret = instance.getClass().newInstance();
			for (Field f : DynamicPreparedStatement.getFieldsUpTo(instance.getClass(), Request.class))
				DatabaseUtils.fillFieldValues(f, rs, ret);
			retrieveStatement.close();
			rs.close();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new DataRetrieveException();
		}
		ret.postRetrieve(this);
		return (T) (ret);
	}

	public synchronized <T extends Request> List<T> retrieveDataList(Class<T> c) throws WrongBaseClassException
	{
		return retrieveDataList(c, false);
	}

	public synchronized <T extends Request> List<T> retrieveDataList(Class<T> c, boolean bool) throws WrongBaseClassException
	{
		List<T> ret = new ArrayList<>();
		if (!this.assertClassIsRow(c) || !this.assertClassContainRetrieve(c))
			throw new WrongBaseClassException();
		T add = null;
		try {
			PreparedStatement retrieveStatement = this.statementBuilder.retrievePreparedStatement(c.newInstance(), bool);
			ResultSet rs = retrieveStatement.executeQuery();
			while (rs.next())
			{
				add = c.newInstance();
				for (Field f : DynamicPreparedStatement.getFieldsUpTo(c, Request.class))
					fillFieldValues(f, rs, add);
				ret.add(add);
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (ret.isEmpty() ? null : ret);
	}

	public synchronized <T extends Request> List<T> retrieveDataList(Request instance) throws WrongBaseClassException, DataRetrieveException {
		List<T> rest = new ArrayList<>();
		if (!this.assertClassIsRow(instance.getClass()) || !this.assertClassContainRetrieve(instance.getClass()))
			throw new WrongBaseClassException();
		Request ret = null;
		try {
			PreparedStatement retrieveStatement = this.statementBuilder.retrievePreparedStatement(instance, true);
			ResultSet rs = retrieveStatement.executeQuery();
			while (rs.next())
			{
				ret = instance.getClass().newInstance();
				for (Field f : DynamicPreparedStatement.getFieldsUpTo(instance.getClass(), Request.class))
					DatabaseUtils.fillFieldValues(f, rs, ret);
				ret.postRetrieve(this);
				rest.add((T) ret);
			}
			retrieveStatement.close();
			rs.close();

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SQLException |
				 IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new DataRetrieveException();
		}

		return rest;
	}

	/**
	 * Delete data from the database where requirement of instance are met
	 * @param instance The instance to use
	 * @throws WrongBaseClassException
	 * @throws DataDeleteException
	 */
	public synchronized void deleteData(Request instance) throws WrongBaseClassException, DataDeleteException {
		if (!this.assertClassIsRow(instance.getClass()) || !this.assertClassContainRetrieve(instance.getClass()))
			throw new WrongBaseClassException();
		try {
			PreparedStatement deleteStatement = this.statementBuilder.deletePreparedStatement(instance);
			deleteStatement.executeUpdate();
		} catch (IllegalArgumentException | IllegalAccessException | SQLException | IOException e) {
			e.printStackTrace();
			throw new DataDeleteException();
		}
	}

	/**
	 * Drop table used by the descriptor
	 * @param decriptor
	 * @throws TableDropException
	 * @throws WrongBaseClassException
	 */
	public synchronized void dropTable(Class<? extends Request> decriptor) throws TableDropException, WrongBaseClassException {
		if (!this.assertClassIsRow(decriptor))
			throw new WrongBaseClassException();
		Row row = decriptor.getAnnotation(Row.class);
		try {
			execStatement.executeUpdate("DROP TABLE IF EXISTS " + row.tableName() + ";");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TableDropException();
		}
	}
}
