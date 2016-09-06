#! /bin/bash

function load_fcheck_log() {

    for file in $(ls $1)
    do
        while read line
        do
            if [[ -z $line ]] || [[ $line == \#* ]]; then
                continue
            else
            filename=${file##*/}

            hour_min=$("$line" | awk -F "[[]|[]]| --" '{print $1}')
            files_uploads=$("$line" | awk -F "[[]|[]]| --" '{print $3}')
            files_pends=$("$line" | awk -F "[[]|[]]| --" '{print $5}')
            tcp_connections=$("$line" | awk -F "[[]|[]]| --" '{print $7}')

            time_stamp="${filename:7:10}"" $hour_min"
            #timeStamp=`date -d "$time_stamp" +%s`
            echo "{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":\""$files_uploads"\", \"tags\":{}}"
            echo "{\"metric\":\"files_pending\", \"timestamp\":"$timeStamp", \"value\":\""$files_pends"\", \"tags\":{}}"
            echo "{\"metric\":\"tcp_connections\", \"timestamp\":"$timeStamp", \"value\":\""$tcp_connections"\", \"tags\":{}}"

            #timeStamp=`date -d "$time_stamp" +%s`
            #echo "$line" | awk -F "[[]|[]]| --" '{print "{\"metric\":\"files_uploading\", \"timestamp\":"$timeStamp", \"value\":\""$3"\", \"tags\":{}}"}'
        fi
        done < $file
    done
}


