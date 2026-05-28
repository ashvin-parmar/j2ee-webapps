package com.ashvin.orm.fm.utils;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.text.*;
public class JDBCMethodExtractor {
private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
private static final Map<Integer, Method> resultSetGetters          = new HashMap<>();
private static final Map<Integer, Method> preparedStatementSetters  = new HashMap<>();
private static final Map<Integer, Class<?>> typeConverters          = new HashMap<>();
static {
try {
Class<ResultSet>          RS = ResultSet.class;
Class<PreparedStatement>  PS = PreparedStatement.class;
resultSetGetters         .put(Types.INTEGER,   RS.getMethod("getInt",       int.class));
preparedStatementSetters .put(Types.INTEGER,   PS.getMethod("setInt",       int.class, int.class));
typeConverters           .put(Types.INTEGER,   int.class);
resultSetGetters         .put(Types.SMALLINT,  RS.getMethod("getInt",       int.class));
preparedStatementSetters .put(Types.SMALLINT,  PS.getMethod("setInt",       int.class, int.class));
typeConverters           .put(Types.SMALLINT,  int.class);
resultSetGetters         .put(Types.TINYINT,   RS.getMethod("getInt",       int.class));
preparedStatementSetters .put(Types.TINYINT,   PS.getMethod("setInt",       int.class, int.class));
typeConverters           .put(Types.TINYINT,   int.class);
resultSetGetters         .put(Types.BIGINT,    RS.getMethod("getLong",      int.class));
preparedStatementSetters .put(Types.BIGINT,    PS.getMethod("setLong",      int.class, long.class));
typeConverters           .put(Types.BIGINT,    long.class);
resultSetGetters         .put(Types.FLOAT,     RS.getMethod("getFloat",     int.class));
preparedStatementSetters .put(Types.FLOAT,     PS.getMethod("setFloat",     int.class, float.class));
typeConverters           .put(Types.FLOAT,     float.class);
resultSetGetters         .put(Types.REAL,      RS.getMethod("getFloat",     int.class));
preparedStatementSetters .put(Types.REAL,      PS.getMethod("setFloat",     int.class, float.class));
typeConverters           .put(Types.REAL,      float.class);
resultSetGetters         .put(Types.DOUBLE,    RS.getMethod("getDouble",    int.class));
preparedStatementSetters .put(Types.DOUBLE,    PS.getMethod("setDouble",    int.class, double.class));
typeConverters           .put(Types.DOUBLE,    double.class);
resultSetGetters         .put(Types.NUMERIC,   RS.getMethod("getDouble",    int.class));
preparedStatementSetters .put(Types.NUMERIC,   PS.getMethod("setDouble",    int.class, double.class));
typeConverters           .put(Types.NUMERIC,   double.class);
resultSetGetters         .put(Types.DECIMAL,   RS.getMethod("getDouble",    int.class));
preparedStatementSetters .put(Types.DECIMAL,   PS.getMethod("setDouble",    int.class, double.class));
typeConverters           .put(Types.DECIMAL,   double.class);
resultSetGetters         .put(Types.BOOLEAN,   RS.getMethod("getBoolean",   int.class));
preparedStatementSetters .put(Types.BOOLEAN,   PS.getMethod("setBoolean",   int.class, boolean.class));
typeConverters           .put(Types.BOOLEAN,   boolean.class);
resultSetGetters         .put(Types.BIT,       RS.getMethod("getBoolean",   int.class));
preparedStatementSetters .put(Types.BIT,       PS.getMethod("setBoolean",   int.class, boolean.class));
typeConverters           .put(Types.BIT,       boolean.class);
resultSetGetters         .put(Types.DATE,      RS.getMethod("getDate",      int.class));
preparedStatementSetters .put(Types.DATE,      PS.getMethod("setDate",      int.class, Date.class));
typeConverters           .put(Types.DATE,      Date.class);
resultSetGetters         .put(Types.TIME,      RS.getMethod("getTime",      int.class));
preparedStatementSetters .put(Types.TIME,      PS.getMethod("setTime",      int.class, Time.class));
typeConverters           .put(Types.TIME,      Time.class);
resultSetGetters         .put(Types.TIMESTAMP, RS.getMethod("getTimestamp", int.class));
preparedStatementSetters .put(Types.TIMESTAMP, PS.getMethod("setTimestamp", int.class, Timestamp.class));
typeConverters           .put(Types.TIMESTAMP, Timestamp.class);
resultSetGetters         .put(Types.VARCHAR,     RS.getMethod("getString",   int.class));
preparedStatementSetters .put(Types.VARCHAR,     PS.getMethod("setString",   int.class, String.class));
typeConverters           .put(Types.VARCHAR,     String.class);
resultSetGetters         .put(Types.CHAR,        RS.getMethod("getString",   int.class));
preparedStatementSetters .put(Types.CHAR,        PS.getMethod("setString",   int.class, String.class));
typeConverters           .put(Types.CHAR,        String.class);
resultSetGetters         .put(Types.LONGVARCHAR,  RS.getMethod("getString",  int.class));
preparedStatementSetters .put(Types.LONGVARCHAR,  PS.getMethod("setString",  int.class, String.class));
typeConverters           .put(Types.LONGVARCHAR,  String.class);
resultSetGetters         .put(Types.OTHER, RS.getMethod("getObject", int.class));
preparedStatementSetters .put(Types.OTHER, PS.getMethod("setObject", int.class, Object.class));
typeConverters           .put(Types.OTHER, Object.class);
} catch (NoSuchMethodException e) {
throw new ExceptionInInitializerError(e);
}
}
public static Method getJDBCGetter(int sqlType) 
{
return resultSetGetters.getOrDefault(sqlType, resultSetGetters.get(Types.OTHER));
}
public static Method getJDBCSetter(int sqlType) 
{
return preparedStatementSetters.getOrDefault(sqlType, preparedStatementSetters.get(Types.OTHER));
}
public static Object convertToJDBC(int sqlType, Object value) 
{
if (value == null) return null;
String s = value.toString();
Class<?> target = typeConverters.getOrDefault(sqlType, Object.class);
if (target == int.class)        return Integer.parseInt(s);
if (target == long.class)       return Long.parseLong(s);
if (target == float.class)      return Float.parseFloat(s);
if (target == double.class)     return Double.parseDouble(s);
if (target == boolean.class)    return Boolean.parseBoolean(s);
if (target == Date.class)       return Date.valueOf(sdf.format(value));
if (target == Time.class)       return Time.valueOf(s);
if (target == Timestamp.class)  return Timestamp.valueOf(s);
if (target == String.class)     return s;
return value;
}
public static Object convertToJava(int sqlType, Object value) 
{
if (value == null) return null;
if ((sqlType == Types.INTEGER || sqlType == Types.SMALLINT || sqlType == Types.TINYINT) && value instanceof Integer) return value;
if (sqlType == Types.BIGINT && value instanceof Long) return value;
if ((sqlType == Types.FLOAT || sqlType == Types.REAL) && value instanceof Float) return value;
if ((sqlType == Types.DOUBLE || sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) && value instanceof Double) return value;
if ((sqlType == Types.BOOLEAN || sqlType == Types.BIT) && value instanceof Boolean) return value;
if (sqlType == Types.DATE && value instanceof Date) return value;
if (sqlType == Types.TIME && value instanceof Time) return value;
if (sqlType == Types.TIMESTAMP && value instanceof Timestamp) return value;
if ((sqlType == Types.VARCHAR || sqlType == Types.CHAR || sqlType == Types.LONGVARCHAR) && value instanceof String) return value;
String s = value.toString();
switch (sqlType) {
case Types.INTEGER:
case Types.SMALLINT:
case Types.TINYINT: return Integer.parseInt(s);
case Types.BIGINT: return Long.parseLong(s);
case Types.FLOAT:
case Types.REAL: return Float.parseFloat(s);
case Types.DOUBLE:
case Types.NUMERIC:
case Types.DECIMAL: return Double.parseDouble(s);
case Types.BOOLEAN:
case Types.BIT: return Boolean.parseBoolean(s);
case Types.DATE: return Date.valueOf(s);
case Types.TIME: return Time.valueOf(s);
case Types.TIMESTAMP: return Timestamp.valueOf(s);
default: return s;
}
}
}
