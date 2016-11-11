## i.e: change file path .
## source loadFcheckLog2TSDB.sh
## load_fcheck_log

#! /bin/bash
function load_fcheck_log() {

    for file in /Users/hehaiyuan/Documents/cloudmon/sire/fcheckLog/log/fc*
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


           echo "{\"metric\":\"tcp_connections\", \"timestamp\":\"$timeStamp\", \"value\":\"$tcp_connections\", \"tags\":{\"host\":\"121.42.46.103\"}}"
        fi
        done < $file
    done
}


