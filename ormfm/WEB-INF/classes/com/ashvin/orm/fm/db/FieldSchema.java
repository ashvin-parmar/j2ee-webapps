public class FieldSchema
{
private String fieldName;
private String columnName;
private Class<?> type;
private boolean isPrimaryKey;
private boolean isAutoIncrement;
private boolean isForeignKey;
private boolean isSetterAllowed;
private boolean isGetterAllowed;
private boolean isPublicAllowed;
private String fkParentClass;
private String fkParentColumn;
public FieldSchema(String fieldName,String columnName,Class<?> type)
{
this.fieldName=fieldName;
this.columnName=columnName;
this.type=type;
this.isPrimaryKey=false;
this.isAutoIncrement=false;
this.isForeignKey=false;
this.isSetterAllowed=false;
this.isGetterAllowed=false;
this.isPublicAllowed=false;
this.fkParentClass=null;
this.fkParentColumn=null;
}
public void setFieldName(String fieldName)
{
this.fieldName=fieldName;
}
public String getFieldName()
{
return this.fieldName;
}
public void setColumnName(String columnName)
{
this.columnName=columnName;
}
public String getColumnName()
{
return this.columnName;
}
public void setType(Class type)
{
this.type=type;
}
public Class getType()
{
return this.type;
}
public void setPrimaryKey(boolean isPrimaryKey)
{
this.isPrimaryKey=isPrimaryKey;
}
public boolean isPrimaryKey()
{
return this.isPrimaryKey;
}
public void setAutoIncrement(boolean isAutoIncrement)
{
this.isAutoIncrement=isAutoIncrement;
}
public boolean isAutoIncrement()
{
return this.isAutoIncrement;
}
public void setForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean isForeignKey()
{
return this.isForeignKey;
}
public void setSetterAllowed(boolean isSetterAllowed)
{
this.isSetterAllowed=isSetterAllowed;
}
public boolean isSetterAllowed()
{
return this.isSetterAllowed;
}
public void setGetterAllowed(boolean isGetterAllowed)
{
this.isGetterAllowed=isGetterAllowed;
}
public boolean isGetterAllowed()
{
return this.isGetterAllowed;
}
public void setPublicAllowed(boolean isPublicAllowed)
{
this.isPublicAllowed=isPublicAllowed;
}
public boolean isPublicAllowed()
{
return this.isPublicAllowed;
}
public void setFKParentClass(String fkParentClass)
{
this.fkParentClass=fkParentClass;
}
public String getFKParentClass()
{
return this.fkParentClass;
}
public void setFKParentColumn(String fkParentColumn)
{
this.fkParentColumn=fkParentColumn;
}
public String getFKParentColumn()
{
return this.fkParentColumn;
}
public void setForeignKey(String fkParentClass,String fkParentColumn)
{
this.isForeignKey=true;
this.fkParentClass=fkParentClass;
this.fkParentColumn=fkParentColumn;
}
public String toString()
{
return "FieldSchema {"
+"fieldName='"+fieldName+"',"
+"columnName='"+columnName+"',"
+"type="+type.getName()+","
+"isPrimaryKey="+isPrimaryKey+","
+"isAutoIncrement="+isAutoIncrement+","
+"isForeignKey="+isForeignKey+","
+"fkParentClass='"+fkParentClass+"',"
+"fkParentColumn='"+fkParentColumn+"'"
+"}";
}
}
