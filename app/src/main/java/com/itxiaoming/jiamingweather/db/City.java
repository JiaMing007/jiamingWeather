package com.itxiaoming.jiamingweather.db;

import org.litepal.crud.DataSupport;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 14:16.
 * 该类的作用:创建数据库的表，关于全国的市
 * 版本号:
 */

public class City extends DataSupport {
    private int id;
    //各市的名称
    private String cityName;
    //各市的代码
    private int cityCode;
    //各省的id，通过id来查找该省包含的市
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
