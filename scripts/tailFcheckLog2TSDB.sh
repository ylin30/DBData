#! /bin/bash
# this script must be watched by supervisord.
# would always get the tail of the last modified log every 10 minutes
# e.g : sh ./tailFcheckLog2TSDB.sh &

TOKEN=062c663c3479f07a8ffdca05b02b52585307604ca253a2b542f98752df79c64350f5788d231b96c611ecdfd108fe94d47352
METRICS_SERVER=http://10.1.130.46:4242/api/put
INTERVAL=10m


function tail_latest_log() {
    while true;do
        file=$(echo file | ssh bigdata@121.42.46.103 find /app/log -mmin -1)
        ssh bigdata@121.42.46.103 tail -n 15 $file | while read line
        do
            read_line
        done
        sleep $INTERVAL
    done
}

function read_line(){

    if [[ -z $line ]] || [[ $line == \#* ]]; then
        continue
    else

       filename=${file##*/}
       hour_min=$(echo "$line" | awk -F "[[]|[]]| --" '{print $1}')
       files_uploads=$(echo "$line" | awk -F "[[]|[]]| --" '{print $3}')
       files_pends=$(echo "$line" | awk -F "[[]|[]]| --" '{print $5}')
       tcp_connections=$(echo "$line" | awk -F "[[]|[]]| --" '{print $7}')
       time_stamp="${filename:7:10}"" $hour_min"
       timeStamp=`date -d "$time_stamp" +%s`

       curl -X POST -H "Content-Type: application/json" -d  "{\"metrics\": [{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":"$files_uploads", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":"$files_pends", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"tcp_connections\",\"timestamp\":"$timeStamp", \"value\":"$tcp_connections", \"tags\":{\"host\":\"121.42.46.103\"}}],\"token\": \""$TOKEN"\"}" $METRICS_SERVER

       # echo "{\"metrics\": [{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":"$files_uploads", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":"$files_pends", \"tags\":{\"host\":\"121.42.46.103\"}},{\"metric\":\"tcp_connections\",\"timestamp\":"$timeStamp", \"value\":"$tcp_connections", \"tags\":{\"host\":\"121.42.46.103\"}}],\"token\": \""$token"\"}"
    fi
}

tail_latest_log