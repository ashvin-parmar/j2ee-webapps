# "14" code should be the one that have attached to one record of other class.
# "10001" is the record which is attached to that '14' code.
# once that record of another class delete, now, you can update or delete this record.


java -classpath classes/:lib/*:dist/*:. testingDelete "14"
java -classpath classes/:lib/*:dist/*:. testingUpdate "14" "English"
java -classpath classes/:lib/*:dist/*:. testingDeleteStudent "10001"
java -classpath classes/:lib/*:dist/*:. testingUpdate "14" "English"
java -classpath classes/:lib/*:dist/*:. testingDelete "14"

