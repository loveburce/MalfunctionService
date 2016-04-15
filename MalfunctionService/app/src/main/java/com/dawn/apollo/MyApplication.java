package com.dawn.apollo;

import android.app.Application;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by dawn-pc on 2016/4/15.
 */
public class MyApplication extends Application{
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    //百度定位可以返回三种坐标系，分别是bd09,bd0911,gcj02,其中bd0911能无偏差得显示在百度地图上
    //gcj02 是测局定制
    private static final String COOR_TYPE = "gcj02";
    private static final String BAIDU_LOCAL_SDK_SERVICE_NAME = "com.baidu.location.service_v2.9";
    //定位SDK提供2种定位模式，定时模式和APP主动请求模式
    //setScanSpan < 1000则为APP主动请求定位
    //setScanSpan >= 1000，则为定时定位模式，（setScanSpan的值就是定时定位的时间间隔）
    private static int SCAN_SPAN_TIME = 500;
    public static double latitude;
    public static double longitude;
    public static String address;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initBaidu();
    }

    private void initBaidu(){
        SDKInitializer.initialize(this);
        initBaiduLocClient();
    }

    private void initBaiduLocClient(){
        mLocationClient = new LocationClient(this.getApplicationContext());
        setLocationOption();
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        startLocate();
    }

    //设置相关的参数
    private void setLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        //设置坐标类型
        option.setCoorType(COOR_TYPE);
        option.setIsNeedAddress(true);
        option.setScanSpan(SCAN_SPAN_TIME);
        mLocationClient.setLocOption(option);
    }

    public void startLocate(){
        if(mLocationClient.isStarted()){
            mLocationClient.requestLocation();
        } else {
            mLocationClient.start();
        }
    }

    public void stopLocate(){
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr();

            Log.d("BDLocation","BDLocation : "+location.getAddrStr());
//            if (lastPoint != null) {
//                if (lastPoint.getLatitude() == location.getLatitude()
//                        && lastPoint.getLongitude() == location.getLongitude()) {
//                    stopLocate();
//                    return;
//                }
//            }else{
//                //保存经纬度和地理位置信息
//                setAddressStr(String.valueOf(location.getAddrStr()));
//                setLatitude(String.valueOf(location.getLatitude()));
//                setLongitude(String.valueOf(location.getLongitude()));
//                address = String.valueOf(location.getAddrStr());
//            }
//            lastPoint = new BmobGeoPoint(longitude, latitude);
        }
    }
}
