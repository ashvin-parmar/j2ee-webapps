# Improvements Required
Things that i have notice while building ORM framework, and need to modify. Because of time constraint, i am not moving forward with it right now, but in case if i want to improve it in future. For reference, i am writing this pendingREADME.
Object-Relation Mapping framework is completed from my side. I have implemented as many feature i can, its not completely similar to real ORM-Framework, but most of the features are implemented as same/similar like real one and modified too. I have tried hard to make it simple.


## Data-structure improvement
Thier are many failure pattern are discover with implementation, that our one DS for FieldSchema are find out using DatabaseMetaData for column(s) details. On the other hand, Setter/Getter Method instance(s) are stored in StatementDS seperately. To determine respective setter/getter method of which class, we have to see for complete fieldschema. To avoid that, we have to store the setter/getter methods too inside the same FieldSchema.

To set StatementDS, we simply store FieldSchema of instance to it, and not store seperate for sqlType, Setter/Getter method for sqlJDBC, javaClass.
[SOLUTION]: Add more properties to FieldSchema.

Properties to add:      //More specifically generalised the codebase. It will help to manage and invoke statement more dynamically. Also, it would help in get data from cache[in-memory DB] easily, because data available or managed based on properties column names.

FieldSchema
{
// old-one +
  //column related
  boolean isNullAllowed;
  int sizeConstraint=

  //statement related
  int sqlType;
  String sqlTypeName;
  Method setterClassMethod; //if setter allowed
  Method getterClassMethod; //if getter allowed
  Method setterJDBCMethod;
  Method getterJDBCMethod;
}



## Configuration improvements [conf.json]
src_folder_path
dist_folder_path
lib_folder_path

[package_name] -> only required for ORMFMTool, not required for DataManager or other(s) because DataManager manages complete src folder.

