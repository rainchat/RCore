package com.rainchat.rlib.storage.database.common;

import com.google.common.collect.Lists;
import com.rainchat.rlib.storage.database.annotations.Where;
import com.rainchat.rlib.storage.database.annotations.Row;
import com.rainchat.rlib.storage.database.annotations.Value;


import java.io.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is dedicated to creating prepared statement based on instance class annotation </br>
 * Method in this class should not be called outside of the API
 *
 */
public class DynamicPreparedStatement {
	
	private Connection con = null;
	
	public void setConnection(Connection con)
	{
		this.con = con;
	}

	// Source : https://stackoverflow.com/questions/16966629/what-is-the-difference-between-getfields-and-getdeclaredfields-in-java-reflectio
	public static List<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {

		List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && 
				(exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
			List<Field> parentClassFields = 
					(List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	/*
	 * Used to fill in the {args} value of createPreparedStatement
	 */
	private String buildArgField(Value field, DatabaseUtils utils)
	{
		String values = "";
		String arg = "{field} {type} {info}";
		arg = arg.replace("{field}", field.fieldName());
		if (field.length() > 0)
			arg = arg.replace("{type}", String.format("%s(%d)", field.type().getSqlType(), field.length()));
		else
			arg = arg.replace("{type}", field.type().getSqlType());
		for (KeyInfo info : field.infos())
			values += utils.getSqlType(info) + " ";
		arg = arg.replace("{info}", values.trim());
		return (arg.trim());
	}

	/**
	 * Build the create table prepared statement based on the descriptor class
	 * @param descriptor The descriptor class
	 * @param utils The database utils used
	 * @return The prepared statement with value set
	 * @throws SQLException
	 */
	PreparedStatement createPreparedStatement(Class<? extends Request> descriptor, DatabaseUtils utils) throws SQLException
	{
		Row row = descriptor.getAnnotation(Row.class);
		String sql = "CREATE TABLE IF NOT EXISTS {table_name}({args});";
		String statement = sql.replace("{table_name}", row.tableName());
		String args = "";
		List<Field> fields = getFieldsUpTo(descriptor, Request.class);
		for (Field f : fields)
		{
			Value db = null;
			if ((db = f.getAnnotation(Value.class)) == null)
				continue;
			if (!args.isEmpty())
				args += ", ";
			args += buildArgField(db, utils);
		}
		statement = statement.replace("{args}", args.trim());
		return (con.prepareStatement(statement.trim()));
	}

	/**
	 * Fill the PreparedStatement values, ignore primary key
	 * @param statement The PreparedStatement
	 * @param instance The request instance to use
	 * @return The PreparedStatement ready to use
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IOException 
	 */
	private PreparedStatement fillPreparedStatementValues(PreparedStatement statement, Request instance, boolean insert) throws IllegalArgumentException, IllegalAccessException, SQLException, IOException
	{
		int idx = 1;
		List<Field> fields = getFieldsUpTo(instance.getClass(), Request.class);
		for (Field f : fields)
		{
			Value db = null;
			if ((db = f.getAnnotation(Value.class)) == null
					|| KeyInfo.containKeyInfo(db, KeyInfo.AUTO_INCREMENT)
					|| (!insert && KeyInfo.containKeyInfo(db, KeyInfo.PRIMARY_KEY)))
				continue;
			statement = setStatementValue(f, idx, instance, db.type(), statement);
			idx += 1;
		}
		return (statement);
	}
	
	/**
	 * Set the statement value at idx, depending on value type
	 * @param f
	 * @param idx
	 * @param instance
	 * @param type
	 * @param statement
	 * @return The PreparedStatement filled with values
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IOException 
	 */
	private PreparedStatement setStatementValue(Field f, int idx, Request instance, DataType type, PreparedStatement statement) throws IllegalArgumentException, IllegalAccessException, SQLException, IOException
	{
		f.setAccessible(true);

		switch (type)
		{
		case DOUBLE:
			statement.setDouble(idx, f.getDouble(instance));
			break;
		case INTEGER:
			statement.setInt(idx, f.getInt(instance));
			break;
		case TEXT:
			statement.setString(idx, (String) f.get(instance));
			break;
		case VARCHAR:
			statement.setString(idx, (String) f.get(instance));
			break;
		case BLOB:
			ByteArrayOutputStream array = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(array);
			out.writeObject(f.get(instance));
			out.flush();
			statement.setBytes(idx, array.toByteArray());
			break;
		}
		return (statement);
	}

	/**
	 * Fill prepared statement where clause
	 * @param statement The prepared statement
	 * @param instance The Request instance used to fill values
	 * @param idx The idx the first where clause is
	 * @return The PreparedStatement ready to use
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IOException 
	 */
	private PreparedStatement fillPreparedStatementWhere(PreparedStatement statement, Request instance,
			int idx) throws IllegalArgumentException, IllegalAccessException, SQLException, IOException
	{
		List<Field> fields = getFieldsUpTo(instance.getClass(), Request.class);
		for (Field f : fields)
		{
			Where where = null;
			if ((where = f.getAnnotation(Where.class)) == null)
				continue;
			statement = setStatementValue(f, idx, instance, where.type(), statement);
			idx += 1;
		}
		return (statement);
	}

	/**
	 * Create a insert PreparedStatement
	 * @param con The connection
	 * @param instance The request instance to insert
	 * @return The PreparedStatement ready to use
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	PreparedStatement insertPreparedStatement(Connection con, Request instance) throws SQLException, IllegalArgumentException, IllegalAccessException, IOException
	{
		Row row = instance.getClass().getAnnotation(Row.class);
		String sql = "INSERT INTO {table_name}({fields}) VALUES({args});";
		String statement = sql.replace("{table_name}", row.tableName());
		String args = "";
		String field = "";
		List<Field> fields = getFieldsUpTo(instance.getClass(), Request.class);
		for (Field f : fields)
		{
			Value db = null;
			if ((db = f.getAnnotation(Value.class)) == null
					|| KeyInfo.containKeyInfo(db, KeyInfo.AUTO_INCREMENT))
				continue;
			if (!field.isEmpty())
			{
				field += ", ";
				args += ", ";
			}
			field += db.fieldName();
			args += "?";
		}
		statement = statement.replace("{args}", args.trim());
		statement = statement.replace("{fields}", field.trim());
		PreparedStatement ret = con.prepareStatement(statement.trim());
		return (fillPreparedStatementValues(ret, instance, true));
	}

	private static String getConditionString(Object instance)
	{
		String conditionStr = "";
		List<Field> fields = getFieldsUpTo(instance.getClass(), Request.class);
		for (Field f : fields)
		{
			Where where = null;
			if ((where = f.getAnnotation(Where.class)) == null)
				continue;
			if (!conditionStr.isEmpty())
				conditionStr += " AND ";
			conditionStr += where.fieldName() + " = ?";
		}
		return (conditionStr.trim());
	}

	/**
	 * Create a update PreparedStatement for the request
	 * @param instance The request instance
	 * @return The PreparedStatement ready to use
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	PreparedStatement updatePreparedStatement(Request instance) throws SQLException, IllegalArgumentException, IllegalAccessException, IOException
	{
		Row row = instance.getClass().getAnnotation(Row.class);
		String sql = "UPDATE {table_name} SET {fields} WHERE {condition};";
		String statement = sql.replace("{table_name}", row.tableName());
		String fieldStr = "";
		int whereIdx = 1;
		List<Field> fields = getFieldsUpTo(instance.getClass(), Request.class);
		for (Field f : fields)
		{
			Value info = f.getAnnotation(Value.class);
			if (info != null && !KeyInfo.containKeyInfo(info, KeyInfo.PRIMARY_KEY))
			{
				if (!fieldStr.isEmpty())
					fieldStr += ", ";
				fieldStr += info.fieldName() + " = ?";
				whereIdx += 1;
			}
		}
		statement = statement.replace("{fields}", fieldStr.trim());
		statement = statement.replace("{condition}", getConditionString(instance));
		System.out.println(statement);
		PreparedStatement ret = con.prepareStatement(statement);
		ret = fillPreparedStatementValues(ret, instance, false);
		ret = fillPreparedStatementWhere(ret, instance, whereIdx);
		return (ret);
	}

	/**
	 * Create a PreparedStatement used to retrieve data
	 * @param retriever The request definition, containing where clauses filled
	 * @param useWhere Does the where clause has to be added ?
	 * @return The PreparedStatement ready to use
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	PreparedStatement retrievePreparedStatement(Request retriever, boolean useWhere) throws SQLException, IllegalArgumentException, IllegalAccessException, IOException
	{
		Row row = retriever.getClass().getAnnotation(Row.class);
		String sql = "SELECT * FROM {table_name}";
		if (useWhere)
			sql += " WHERE {condition};";
		String statement = sql.replace("{table_name}", row.tableName());
		if (useWhere)
			statement = statement.replace("{condition}", getConditionString(retriever));

		PreparedStatement ret = con.prepareStatement(statement);
		if (useWhere)
			ret = fillPreparedStatementWhere(ret, retriever, 1);
		System.out.println(statement);
		return (ret);
	}

	/**
	 * Create a delete PreparedStatement
	 * @param instance Instance containing where clauses
	 * @return The PreparedStatement
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	PreparedStatement deletePreparedStatement(Request instance) throws SQLException, IllegalArgumentException, IllegalAccessException, IOException
	{
		Row row = instance.getClass().getAnnotation(Row.class);
		String sql = "DELETE FROM {table_name} WHERE {condition};";
		String statement = sql.replace("{table_name}", row.tableName());
		statement = statement.replace("{condition}", getConditionString(instance));
		PreparedStatement ret = con.prepareStatement(statement);
		ret = fillPreparedStatementWhere(ret, instance, 1);
		return (ret);
	}
}
