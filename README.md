manydbbench
===========


Requires
----------
Java 7
Mvn 2
python
PostgreSQL 

Prepare Eclipse (may need eclipse restart)
----------------
mvn -Declipse.workspace=<path to workspace> eclipse:add-maven-repo
mvn eclipse:eclipse 

download dependencies
----------
mvn install

build jar
----------
mvn package

