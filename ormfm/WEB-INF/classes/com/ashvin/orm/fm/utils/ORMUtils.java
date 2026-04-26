package com.ashvin.orm.fm.utils;

import java.sql.*;
import java.util.*;

public class ORMUtils
{
private static final Map<JDBCType,Class<?>> sqlToJavaTypes=new HashMap<>();
static
{
sqlToJavaTypes.put(JDBCType.BIGINT,Long.class);
sqlToJavaTypes.put(JDBCType.INTEGER,Integer.class);
sqlToJavaTypes.put(JDBCType.SMALLINT,Short.class);
sqlToJavaTypes.put(JDBCType.TINYINT,Byte.class);
sqlToJavaTypes.put(JDBCType.FLOAT,Double.class);
sqlToJavaTypes.put(JDBCType.DOUBLE,Double.class);
sqlToJavaTypes.put(JDBCType.DECIMAL,java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.NUMERIC,java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.REAL,Float.class);
sqlToJavaTypes.put(JDBCType.BIT,Boolean.class);
sqlToJavaTypes.put(JDBCType.DATE,java.util.Date.class);
sqlToJavaTypes.put(JDBCType.CHAR,java.lang.String.class);
sqlToJavaTypes.put(JDBCType.VARCHAR,java.lang.String.class);
sqlToJavaTypes.put(JDBCType.LONGVARCHAR,java.lang.String.class);

}
public static Class<?> jdbcToJavaMappedType(JDBCType jdbcTypeCode)
{
return sqlToJavaTypes.get(jdbcTypeCode);
}

}
