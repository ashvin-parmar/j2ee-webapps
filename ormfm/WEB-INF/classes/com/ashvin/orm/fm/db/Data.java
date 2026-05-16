import java.util.*;
import java.lang.reflect.*;
import com.ashvin.orm.fm.annotations.*;

public class Data
{
private static Map<Class<?>,TableSchema> cache=new HashMap<>();
private Data()
{
}
public static TableSchema getInfo(Class<?> objClass) throws DataException
{
if(cache.containsKey(objClass))
{
return cache.get(objClass);
}
if(!objClass.isAnnotationPresent(Table.class))
{
throw new DataException("Class "+objClass.getName()+" has no @Table annotation"); 
}
Table tableAnnotation=objClass.getAnnotation(Table.class);
String tableName=tableAnnotation.name();
TableSchema tableSchema=new TableSchema(tableName);
Field[] javaFields=objClass.getDeclaredFields();
for(Field javaField:javaFields)
{
if(!javaField.isAnnotationPresent(Column.class)) continue;
Column columnAnnotation=javaField.getAnnotation(Column.class);
String columnName=columnAnnotation.name();
String fieldName=javaField.getName();
Class<?> fieldType=javaField.getType();

FieldSchema fieldSchema=new FieldSchema(fieldName,columnName,fieldType);

if(javaField.isAnnotationPresent(PrimaryKey.class))
{
fieldSchema.setPrimaryKey(true);
}
if(javaField.isAnnotationPresent(AutoIncrement.class))
{
boolean trueValue=true;
fieldSchema.setAutoIncrement(trueValue);
}
if(javaField.isAnnotationPresent(Unique.class))
{
boolean trueValue=true;
fieldSchema.setUnique(trueValue);
}
if(javaField.isAnnotationPresent(ForeignKey.class))
{
ForeignKey fkAnnotation=javaField.getAnnotation(ForeignKey.class);
String fkParentClass=fkAnnotation.parent();
String fkParentColumn=fkAnnotation.column();
fieldSchema.setForeignKey(fkParentClass,fkParentColumn);
}
int mods=javaField.getModifiers();
if(javaField.isAnnotationPresent(SetterGetter.class))
{
fieldSchema.setSetterAllowed(true);
fieldSchema.setGetterAllowed(true);
}
else if(Modifier.isPublic(mods))
{
fieldSchema.setPublicAllowed(true);
}
else
{
continue;   //Private properties with no setter getter are not included in this scenario
}
tableSchema.addField(fieldSchema);
}
cache.put(objClass,tableSchema);
return tableSchema;
}
}
