package com.itxiaoming.jiamingweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 16:14.
 * 该类的作用:
 * 版本号:
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public Carwash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class Carwash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
