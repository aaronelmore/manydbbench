#!/bin/bash

#clean
python scripts/multi.py -c -n 0

rm *log

CRT="python scripts/multi.py -n "
RUN="java -cp target/manydbbench-1.0-SNAPSHOT-jar-with-dependencies.jar edu.mit.csail.ManyBench -t 120 -n "

CNT=0
DBSTOADD=( 1 9 90 100 300 500 1500 2500 2500 2500)
MAX_PER_PROC=1000


echo "running"
for i in "${DBSTOADD[@]}"
do
    crtcmd="$CRT $i -s $CNT"
    dbi="echo #db$i"
    $dbi >> perf.log
    $dbi >> manydb.log
    echo $crtcmd
    $crtcmd

    CNT=$(($CNT+$i))
    if [ "$CNT" -gt "$MAX_PER_PROC" ];
    then
    
        echo "Too DBs for one process splitting."
        #start one
        first_pass=true
        TMPCNT=$(($CNT)) #temp how many are left to execute
        STARTCNT=0 #how many we have executed
        while [ "$TMPCNT" -gt "0" ]; do   
            
            #should first db be on
            if [ "$first_pass" = true ]; 
            then
                starton="-startOn 1"
            else
                starton="-startOn 0"            
            fi
            
            #how mnay to execute this pass
            if [ "$TMPCNT" -gt "$MAX_PER_PROC" ];
            then
                TMPDBS=$MAX_PER_PROC
            else
                TMPDBS=$TMPCNT
            fi
            
            #how many left
            let "TMPCNT -= $TMPDBS"
            echo "TMPDBS $TMPDBS TMPCNT: $TMPCNT"
            if [ "$TMPCNT" -gt "0" ]; 
            then
                #run as BG
                runcmd="$RUN $TMPDBS $starton -s $STARTCNT &"
            else
                #run as FG (for blocking)
                runcmd="$RUN $TMPDBS $starton -s $STARTCNT"               
            fi
            let "STARTCNT += $TMPDBS"
            echo $runcmd
            $runcmd   
            first_pass=false   
        done            
    else
        runcmd="$RUN $CNT"
        echo $runcmd
        #$runcmd       
    fi

done
