#!/bin/bash

#clean
python scripts/multi.py -c -n 0

#create 1
python scripts/multi.py -n 1

#create 9 (start w2)
python scripts/multi.py -n 9 -s 2

#create 90
python scripts/multi.py -n 90 -s 11



