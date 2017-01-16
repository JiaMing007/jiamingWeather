package com.itxiaoming.jiamingweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 16:19.
 * 该类的作用:
 * 版本号:
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
