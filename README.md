# DBData

    将从各种http 接口提取数据分成了几个不同的模块来实现. 能够通过配置文件来组装需要的接口.

    DBdata 主要有几部分组成.
    1. TimerTask: timertask 主要是定时轮询接口 获得数据并存放.
    2. Wrapper 作为context 会存在整个生命周期
    3. Request 获取wrapper 中返回的URL 然后想目标服务器进行访问.
    4. Formatter 通过配置文件动态指定使用的formatter 同时判断后面是否需要写入. 不写入返回Null
    5. Writer 将Formatter 得到的内容写进目标地址. 现在只是支持 TSDB