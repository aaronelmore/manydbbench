#!/bin/bash

#clean
python scripts/multi.py -c -n 0

rm *log
#create 1
python scripts/multi.py -n 1
echo "#db1" >> perf.log
echo "#db1" >> manydb.log
java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -n 1 -t 60 

#create 1 (start w/#1)
python scripts/multi.py -n 1 -s 1
echo "#db2" >> perf.log
echo "#db2" >> manydb.log
java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -n 2 -t 60 




