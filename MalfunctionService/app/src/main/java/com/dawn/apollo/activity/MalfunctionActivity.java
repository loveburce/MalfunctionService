package com.dawn.apollo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.location.service.LocationService;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dawn.apollo.MyApplication;
import com.dawn.apollo.R;
import com.dawn.apollo.config.Constant;
import com.dawn.apollo.utils.SharePreferenceUtils;
import com.dawn.apollo.utils.photo.Bimp;
import com.dawn.apollo.utils.photo.FileUtils;
import com.dawn.apollo.utils.photo.ImageItem;
import com.dawn.apollo.utils.photo.PublicWay;
import com.dawn.apollo.utils.photo.Res;
import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dawn-pc on 2016/4/12.
 */
public class MalfunctionActivity extends Activity {
    private LocationService locationService;
    @Bind(R.id.noScrollgridview)
    GridView noScrollgridview;
    @Bind(R.id.activity_selectimg_send)
    TextView tv_submit;
    @Bind(R.id.activity_malfunction_location)
    TextView tv_location;
    @Bind(R.id.activity_malfunction_address)
    TextView tv_address;
    @Bind(R.id.activity_malfunction_location_ll)
    LinearLayout ll_location;
    @Bind(R.id.activity_back)
    ImageView iv_back;
    @Bind(R.id.activity_selectimg_describe)
    EditText et_describe;

    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap ;
    SharePreferenceUtils sharePreferenceUtils;
    private SVProgressHUD mSVProgressHUD;

    String tunnelId;
    String latitudeLongitude="";
    String describe;
    String address;
    String imei;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_malfunction, null);
        setContentView(parentView);
        ButterKnife.bind(this);
        sharePreferenceUtils = new SharePreferenceUtils(MalfunctionActivity.this,"MyApplication");

        Res.init(this);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        Init();
        initBaiduLocation();
        locationService.start();// 定位SDK

        mSVProgressHUD = new SVProgressHUD(this);

        tunnelId = getIntent().getStringExtra("TunnelId");

        if(tunnelId != null && !tunnelId.equals("")) {
            sharePreferenceUtils.setValue("TunnelId", tunnelId);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    private void initBaiduLocation(){
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
    }

    public void Init() {
        tv_location.setText(sharePreferenceUtils.getValue("LatitudeLongitude",""));
        tv_address.setText(sharePreferenceUtils.getValue("Address",""));
        et_describe.setText(sharePreferenceUtils.getValue("Describe",""));

        pop = new PopupWindow(MalfunctionActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sharePreferenceUtils.setValue("Describe", et_describe.getText().toString());

                Intent intent = new Intent(MalfunctionActivity.this, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(MalfunctionActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(MalfunctionActivity.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        tv_submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageFilesUtils();
            }
        });

        ll_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                mSVProgressHUD.showWithStatus("开始定位！");
                locationService.start();// 定位SDK
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:

                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i<PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            System.exit(0);
        }
        return true;
    }

    private void uploadImageFilesUtils(){
        mSVProgressHUD.showSuccessWithStatus("提交数据，请稍后！");
        String httpurl = Constant.submitTrouble;

        describe = et_describe.getText().toString();
        latitudeLongitude = tv_location.getText().toString();
        tunnelId = sharePreferenceUtils.getValue("TunnelId", "");
        imei = getPhoneInfo(MalfunctionActivity.this);

        String c_id="";
        String c_location="";
        String c_description="";
        String c_imei = "";
        try {
            c_id = URLEncoder.encode(tunnelId, "UTF-8");
            c_location = URLEncoder.encode(latitudeLongitude, "UTF-8");
            c_description = URLEncoder.encode(describe, "UTF-8");
            c_imei = URLEncoder.encode(imei, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(httpurl);
        // 加到url里的参数, http://xxxx/s?wd=xUtils
        params.addParameter("chunnel_id", c_id);
        params.addParameter("location", c_location);
        params.addParameter("description", c_description);
        params.addParameter("imei", c_imei);

        // 使用multipart表单上传文件
        params.setMultipart(true);

        for(int i=0;(i<3&&i<Bimp.tempSelectBitmap.size());i++){
            ImageItem imageItem =Bimp.tempSelectBitmap.get(i);
            Log.d("ImageItem","ImageItem : "+imageItem.toString());
            params.addBodyParameter("file"+(i+1),  new File(imageItem.getImagePath()), "application/octet-stream"); // 如果文件没有扩展名, 最好设置contentType参数.
        }

        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                Log.d("MultiPartStringRequest", "tunnelId : " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String err_no = jsonObject.getString("err_no");
                    if(err_no.equals("0")){
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }else{
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("MultiPartStringRequest", "tunnelId : " + ex.getMessage());

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                Log.d("MultiPartStringRequest", "tunnelId : " + cex.getMessage());
            }

            @Override
            public void onFinished() {

            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    sharePreferenceUtils.setValue("LatitudeLongitude", "");
                    sharePreferenceUtils.setValue("Address", "");
                    sharePreferenceUtils.setValue("Describe", "");
                    sharePreferenceUtils.setValue("TunnelId", "");

                    String string = (String) msg.obj;
                    Log.d("handleMessage", "handleMessage : " + string);
                    mSVProgressHUD.showSuccessWithStatus("恭喜，提交成功！");
//                    Intent intent = new Intent(MalfunctionActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
//                    MalfunctionActivity.this.finish();
                    break;
                case 2:
                    mSVProgressHUD.showSuccessWithStatus("提交失败！");
                    break;
            }
        }
    };

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                tv_location.setText(location.getLatitude()+" : "+location.getLongitude());
                tv_address.setText(location.getAddrStr());
                address = location.getAddrStr();
                latitudeLongitude = location.getLatitude()+","+location.getLongitude();

                sharePreferenceUtils.setValue("LatitudeLongitude", latitudeLongitude);
                sharePreferenceUtils.setValue("Address",address);

//                mSVProgressHUD.showWithStatus("定位完成！");
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");


                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
            }
        }

    };

    public  String getPhoneInfo(Context context){
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


}
