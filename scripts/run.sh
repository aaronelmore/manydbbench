#!/bin/bash

#clean
python scripts/multi.py -c -n 0

#create 1
python scripts/multi.py -n 1
java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -n 1 -t 60 

#create 1 (start w2)
python scripts/multi.py -n 1 -s 2
java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -n 2 -t 60 




