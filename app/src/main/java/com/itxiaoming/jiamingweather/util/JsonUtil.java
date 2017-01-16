package com.itxiaoming.jiamingweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.itxiaoming.jiamingweather.db.City;
import com.itxiaoming.jiamingweather.db.Country;
import com.itxiaoming.jiamingweather.db.Province;
import com.itxiaoming.jiamingweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 14:33.
 * 该类的作用:用于解析JSON数据
 * 版本号:
 */

public class JsonUtil {
    //解析服务器返回的省的数据
    public static boolean parseProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject proviceObject = allProvinces.getJSONObject(i);
                    Province mProvince = new Province();
                    mProvince.setProvinceName(proviceObject.getString("name"));
                    mProvince.setProvinceCode(proviceObject.getInt("id"));
                    mProvince.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    //解析服务器返回的市的数据
    public static boolean parseCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City mCity = new City();
                    mCity.setCityName(cityObject.getString("name"));
                    mCity.setCityCode(cityObject.getInt("id"));
                    mCity.setProvinceId(provinceId);
                    mCity.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //解析服务器返回的县的数据
    public static boolean parseCountryResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray countryArray = new JSONArray(response);
                for(int i=0;i<countryArray.length();i++){
                    JSONObject countryObject = countryArray.getJSONObject(i);
                    Country mCountry = new Country();
                    mCountry.setCountryName(countryObject.getString("name"));
                    mCountry.setWeatherId(countryObject.getString("weather_id"));
                    mCountry.setCityId(cityId);
                    mCountry.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    //将返回的数据解析成实体类
    public static Weather parseWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
