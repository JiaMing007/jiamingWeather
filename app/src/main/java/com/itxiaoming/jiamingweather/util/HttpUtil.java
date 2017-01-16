package com.itxiaoming.jiamingweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 14:28.
 * 该类的作用:用于网络请求
 * 版本号:
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.建立Request请求
        Request request = new Request.Builder().url(address).build();
        //3.调用newCall将request通过enqueue发送出去，最后会在回掉函数中返回响应结果
        okHttpClient.newCall(request).enqueue(callback);
    }
}
