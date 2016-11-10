#! /bin/bash

# this script would dump all 'fcheck_log' from remote machine
# after parse that will sent a Post query into the openTSDB
# e.g: sh ./loadFcheckLog2TSDB.sh &

DUMP_LOG_FILE_PATH=/home/fcheck
TOKEN=062c663c3479f07a8ffdca05b02b52585307604ca253a2b542f98752df79c64350f5788d231b96c611ecdfd108fe94d47352
METRICS_SERVE=http://10.1.130.46:4242/api/put

function copy_all_fcheck() {
    mkdir -p $DUMP_LOG_FILE_PATH
    scp -r bigdata@121.42.46.103:/app/log $DUMP_LOG_FILE_PATH
}

function load_all_fcheck_log() {
    for file in /home/fcheck/log/fc*
    do
        echo $file
        while read line
        do
            read_line
        done < $file
    done
}


function read_line(){
    if [[ -z $line ]] || [[ $line == \#* ]]; then
        continue
    else

       #parser
       filename=${file##*/}
       hour_min=$(echo "$line" | awk -F "[[]|[]]| --" '{print $1}')
       files_uploads=$(echo "$line" | awk -F "[[]|[]]| --" '{print $3}')
       files_pends=$(echo "$line" | awk -F "[[]|[]]| --" '{print $5}')
       tcp_connections=$(echo "$line" | awk -F "[[]|[]]| --" '{print $7}')
       time_stamp="${filename:7:10}"" $hour_min"
       timeStamp=`date -d "$time_stamp" +%s`

       #sender
       curl -X POST -H "Content-Type: application/json" -d  "{\"metrics\": [{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":"$files_uploads", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":"$files_pends", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"tcp_connections\",\"timestamp\":"$timeStamp", \"value\":"$tcp_connections", \"tags\":{\"host\":\"121.42.46.103\"}}],\"token\": \""$TOKEN"\"}" $METRICS_SERVE

       # echo "{\"metrics\": [{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":"$files_uploads", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":"$files_pends", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"tcp_connections\",\"timestamp\":"$timeStamp", \"value\":"$tcp_connections", \"tags\":{\"host\":\"121.42.46.103\"}}],\"token\": \"bd4d77d0e1dc8317abce2f51b52e08c35e9572a10d7d055ebb635e1f159b384464e85f0f1fd16a526fca440ff88377c3d308\"}"
    fi
}


copy_all_fcheck
load_all_fcheck_log