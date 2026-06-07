#java -classpath ../lib/*:../classes/:. com.ashvin.orm.fm.ORMFMTool generate_pojo
#java -classpath ../lib/*:../classes/:. com.ashvin.orm.fm.ORMFMTool generate_view_pojo
java -classpath ../lib/*:lib/*:dist/*:. com.ashvin.orm.fm.ORMFMTool generate_pojo generate_view_pojo
java -classpath ../lib/*:lib/*:dist/*:. com.ashvin.orm.fm.ORMFMTool generate_jar
java -classpath ../lib/*:lib/*:dist/*:. com.ashvin.orm.fm.ORMFMTool generate_doc_pdf

# Over here, we have to take this 'lib/*' alongwith 'dist/*', need to generate jar and doc pdf respectively.

