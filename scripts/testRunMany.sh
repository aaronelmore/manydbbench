#!/bin/bash

#clean
python scripts/multi.py -c -n 0

rm *log

CRT="python scripts/multi.py -n "
RUN="java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -t 30 -n "

CNT=0
DBSTOADD=( 1 2 1)



echo "running"
for i in "${DBSTOADD[@]}"
do
   crtcmd="$CRT $i -s $CNT"
   dbi="echo #db$i"
   $dbi >> perf.log
   $dbi >> manydb.log
   echo $crtcmd
   $crtcmd
   #$runcmd &> nt$i.log
   
   
   CNT=$(($CNT+$i))
   runcmd="$RUN $CNT"
   echo $runcmd
   $runcmd
done