package com.itxiaoming.jiamingweather.Frag;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itxiaoming.jiamingweather.MainActivity;
import com.itxiaoming.jiamingweather.R;
import com.itxiaoming.jiamingweather.WeatherActivity;
import com.itxiaoming.jiamingweather.db.City;
import com.itxiaoming.jiamingweather.db.Country;
import com.itxiaoming.jiamingweather.db.Province;
import com.itxiaoming.jiamingweather.util.HttpUtil;
import com.itxiaoming.jiamingweather.util.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 作者:xjm.
 * 邮箱:xiaojiaming@infosec.com.cn
 * 公司:Infosec Technology
 * 创建时间:Created on 2017/1/13 15:03.
 * 该类的作用:遍历省市县的数据
 * 版本号:
 */

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTRY = 2;

    private TextView tv_title;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    //当前选中的级别，总共三个级别，即省，市，县
    private int currentLevel;
    //初始化集合
    private  List<Province> mListProvince;
    private  List<City> mListCity;
    private  List<Country> mListCountry;

    //定义三个标志位，用于判断选中的省市县
    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;
    private ProgressDialog progresssDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        tv_title = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    //查询市
                    selectedProvince= mListProvince.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    //查询县
                    selectedCity = mListCity.get(position);
                    queryCountries();
                }else if(currentLevel==LEVEL_COUNTRY){
                    String weatherId =  mListCountry.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getContext(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTRY){
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY){
                    queryProvince();
                }

            }
        });
        //查询省
        queryProvince();
    }
    //显示省份
    private void queryProvince() {
        tv_title.setText("中国");
        backButton.setVisibility(View.GONE);
        mListProvince = DataSupport.findAll(Province.class);
        if(mListProvince.size() > 0){
            dataList.clear();
            for (Province pro : mListProvince) {
                dataList.add(pro.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //显示市区
    private void queryCountries() {
        tv_title.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        mListCountry = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(Country.class);
        if(mListCountry.size()>0){
            dataList.clear();
            for (Country country: mListCountry) {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }

    //显示市
    private void queryCities() {
        tv_title.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        mListCity = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(mListCity.size()>0){
            dataList.clear();
            for (City city: mListCity) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
    //从服务器去获取省得数据
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = JsonUtil.parseProvinceResponse(responseText);
                }else if("city".equals(type)){
                    JsonUtil.parseCityResponse(responseText,selectedProvince.getId());
                }else if("country".equals(type)){
                    JsonUtil.parseCountryResponse(responseText,selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCountries();
                            }
                        }
                    });
                }
            }
        });
    }
    //显示进度对话框
    private void showProgressDialog() {
        if(progresssDialog == null){
            progresssDialog = new ProgressDialog(getActivity());
            progresssDialog.setMessage("正在加载...");
            progresssDialog.setCanceledOnTouchOutside(false);
        }
        progresssDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if(progresssDialog!=null){
            progresssDialog.dismiss();
        }
    }
}
