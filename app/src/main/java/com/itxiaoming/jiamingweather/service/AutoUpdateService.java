package com.itxiaoming.jiamingweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.itxiaoming.jiamingweather.WeatherActivity;
import com.itxiaoming.jiamingweather.gson.Weather;
import com.itxiaoming.jiamingweather.util.HttpUtil;
import com.itxiaoming.jiamingweather.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //更新天气
        updateWeather();
        //更新图片
        upateImage();
        //定时
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*1000;
        long time = SystemClock.elapsedRealtime() + anHour;
        Intent mIntent = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,mIntent,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    //更新天气
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            Weather weather = JsonUtil.parseWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            //请求地址
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=b28e8726a49f4abeb411055c17bb9973";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = JsonUtil.parseWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        edit.putString("weather", responseText);
                        edit.apply();
                    }
                }
            });
        }
    }
    //更新图片
    private void upateImage() {
        String imageUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(imageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseImage = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("bing_pic",responseImage);
                edit.apply();
            }
        });
    }
}
