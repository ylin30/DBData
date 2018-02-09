package com.cloudmon.oneapm;

import java.util.List;

/**
 * Created by hehaiyuan on 1/31/18.
 */
//    {
//        "code": 0,
//            "msg": "SUCCESS",
//            "results": [
//        {
//            "name": "WebTransaction",
//                "points": [
//            {
//                "time": {
//                "endTime": 1517371037333,
//                        "startTime": 1517370977333
//            }
//            },
//            {
//                "time": {
//                "endTime": 1517371097333,
//                        "startTime": 1517371037333
//            }
//            },
//            {
//                "time": {
//                "endTime": 1517371157333,
//                        "startTime": 1517371097333
//            }
//            },
//            {
//                "time": {
//                "endTime": 1517371217333,
//                        "startTime": 1517371157333
//            }
//            }
//            ]
//        }
//        ]
//    }

public class Points {
    String code;
    String msg;
    List<Results> results;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
}

//    {
//        "data": {
//            "error_rate": 0
//        },
//        "time": {
//            "endTime": 1517371021379,
//            "startTime": 1517370961379
//        }
//    }




