
1. Compile the classes  ormfm
2. Create jar for creatJar.sh in classes folder. then copy that jar file to here in this lib folder to PWD.
3. use testTool.sh -> to create pojo & jar file for database tables + pojo for view too.
4. use testingThreaded.sh to test multiple statements to being work at the same time on different thread. Check thread safety.
5. There are more testing*.sh files to test and use.

--------------

To testing -. [testingCacheable]
We have to explicitely/manually have to give @Cacheable annotation to the class for which, we want to set in-memory database.
