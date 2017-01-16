package com.itxiaoming.jiamingweather.db;

import org.litepal.crud.DataSupport;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 14:16.
 * 该类的作用:创建数据库的表，关于全国的省
 * 版本号:
 */

public class Province extends DataSupport {
    private int id;
    //各省的名称
    private String provinceName;
    //各省的代码
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
