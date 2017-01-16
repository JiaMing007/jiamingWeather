package com.itxiaoming.jiamingweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itxiaoming.jiamingweather.gson.Forecast;
import com.itxiaoming.jiamingweather.gson.Weather;
import com.itxiaoming.jiamingweather.service.AutoUpdateService;
import com.itxiaoming.jiamingweather.util.HttpUtil;
import com.itxiaoming.jiamingweather.util.JsonUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forceCastLayout;
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sporText;
    private Weather weather;
    private String responseText;
    private ImageView bingImageView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让背景图和状态栏融合
        if(Build.VERSION.SDK_INT>=21){
            //当系统版本大于21d的时候调用
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navButton = (Button) findViewById(R.id.navButton);
        bingImageView = (ImageView) findViewById(R.id.bingImage);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_city);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forceCastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        apiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sporText = (TextView) findViewById(R.id.sport_text);
        //定义临时存储的对象
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String weatherString = prefs.getString("weather", null);
            final String weatherId;
            if(weatherString != null){
            Weather mWeather = JsonUtil.parseWeatherResponse(weatherString);
                weatherId = mWeather.basic.weatherId;
            showWeatherInfo(mWeather);
        }else{
             weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        //设置下拉监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        String bing_pic = prefs.getString("bing_pic", null);
        if(bing_pic != null){
            Glide.with(this).load(bing_pic).into(bingImageView);
        }else{
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    //加载每日一图
    private void loadBingPic() {
        String imageUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(imageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseImage = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("bing_pic",responseImage);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseImage).into(bingImageView);
                    }
                });
            }
        });
    }

    //根据id请求城市的天气信息
    public void requestWeather(final String weather_id) {
        //请求地址
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weather_id+"&key=b28e8726a49f4abeb411055c17bb9973";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                loadBingPic();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseText = response.body().string();
//                Log.e("jiaming",responseText);
                weather = JsonUtil.parseWeatherResponse(responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showWeatherInfo(weather);
//                    }
//                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", responseText);
                            edit.apply();
                            showWeatherInfo(weather);
                            //启动服务，服务的时间间隔为8个小时
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    //展示Weather实体类中的数据
    private void showWeatherInfo(Weather mWeather) {
        String cityName = mWeather.basic.cityName;
        String updateTime = mWeather.basic.update.updateTime.split(" ")[1];
        String degree = mWeather.now.temperature + "°C";
        String weatherInfo = mWeather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forceCastLayout.removeAllViews();
        for(Forecast forecast: mWeather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forceCastLayout, false);
            TextView dataText = (TextView) view.findViewById(R.id.data_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forceCastLayout.addView(view);
        }
        if(mWeather.aqi!=null){
            apiText.setText(mWeather.aqi.city.aqi);
            pm25Text.setText(mWeather.aqi.city.pm25);
        }
        String comfort = "舒适度:"+mWeather.suggestion.comfort.info;
        String carWash = "洗车指数:"+mWeather.suggestion.carWash.info;
        String sport = "运动建议:"+mWeather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sporText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
