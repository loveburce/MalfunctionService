package com.dawn.apollo;

import com.baidu.location.service.LocationService;
import com.baidu.mapapi.SDKInitializer;
import com.dawn.apollo.utils.SharePreferenceUtils;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import org.xutils.x;

/**
 * Created by dawn-pc on 2016/4/15.
 */
public class MyApplication extends Application{
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());


        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }

}
