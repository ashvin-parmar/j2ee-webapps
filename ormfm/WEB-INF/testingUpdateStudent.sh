#!/bin/bash

# Author: Ashvin
# Date: 2026-05-30
# Description:

@@

javac -classpath classes/:lib/*:dist/*:. testingUpdateStudent.java
java -classpath classes/:lib/*:dist/*:. testingUpdateStudent 10432 Visnu Yadav AADHAR1234 20 M "14/02/2001"
java -classpath classes/:lib/*:dist/*:. testingUpdateStudent 10001 Visnu Yadav AADHAR1234 20 M "14/02/2001"
java -classpath classes/:lib/*:dist/*:. testingUpdateStudent 10003 Visnu Yadav AADHAR12543 874 M "14/02/2001"
java -classpath classes/:lib/*:dist/*:. testingUpdateStudent 10003 Shreshtha yadav AADHAR123456 22 F "14/02/2001"
java -classpath classes/:lib/*:dist/*:. testingUpdateStudent 10010 Ayush salve  UID123455 22 M "14/02/2006"
