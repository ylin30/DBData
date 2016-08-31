#! /bin/bash

function load_metrics() {
    metricFile=$1
    while read line 
    do
        if [[ -z $line ]] || [[ $line == \#* ]]; then
	   continue
        else
	   echo $line
	   curl -X POST -H "Content-Type: application/json" -d "$line" http://localhost:4242/api/put 
	fi
    done < $metricFile
} 

