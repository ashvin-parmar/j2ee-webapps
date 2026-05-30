javac -classpath classes/:lib/*:dist/*:. testingDelete.java
java -classpath classes/:lib/*:dist/*:. testingDelete "20"
java -classpath classes/:lib/*:dist/*:. testingDelete "22"
java -classpath classes/:lib/*:dist/*:. testingDelete "31"
java -classpath classes/:lib/*:dist/*:. testingDelete "14"
java -classpath classes/:lib/*:dist/*:. testingUpdate "14" "English"
java -classpath classes/:lib/*:dist/*:. testingDeleteStudent "10001"
java -classpath classes/:lib/*:dist/*:. testingUpdate "14" "English"
java -classpath classes/:lib/*:dist/*:. testingDelete "14"
