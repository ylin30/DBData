#! /bin/bash
function load_fcheck_log() {

    for file in /Users/hehaiyuan/Documents/Log/fc*
    do
        echo $file
        while read line
        do
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

            curl -X POST -H "Content-Type: application/json" -d  "{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":\"$files_uploads\", \"tags\":{\"host\":\"121.42.46.103\"}}"  http://192.168.65.182:4242/api/put
            curl -X POST -H "Content-Type: application/json" -d  "{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":\"$files_pends\", \"tags\":{\"host\":\"121.42.46.103\"}}"  http://192.168.65.182:4242/api/put
            curl -X POST -H "Content-Type: application/json" -d  "{\"metric\":\"tcp_connections\", \"timestamp\":\"$timeStamp\", \"value\":\"$tcp_connections\", \"tags\":{\"host\":\"121.42.46.103\"}}"  http://192.168.65.182:4242/api/put

           echo "{\"metric\":\"tcp_connections\", \"timestamp\":\"$timeStamp\", \"value\":\"$tcp_connections\", \"tags\":{\"host\":\"121.42.46.103\"}}"
        fi
        done < $file
    done
}


