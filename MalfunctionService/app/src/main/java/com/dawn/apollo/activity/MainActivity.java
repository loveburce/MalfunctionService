package com.dawn.apollo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dawn.apollo.R;
import com.dawn.apollo.adapter.TunnelInfoAdapter;
import com.dawn.apollo.config.Constant;
import com.dawn.apollo.customview.DividerDecoration;
import com.dawn.apollo.customview.WrapContentLinearLayoutManager;
import com.dawn.apollo.http.HttpClientRequest;
import com.dawn.apollo.model.TunnelInfo;
import com.dawn.apollo.utils.JsonUtil;
import com.dawn.apollo.utils.PhoneUtils;
import com.dawn.apollo.utils.SharePreferenceUtils;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.swipe_refresh_widget)
    SwipeRefreshLayout mSwipeRefreshWidget;
    @Bind(R.id.swipe_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;

    TunnelInfoAdapter tunnelInfoAdapter;
    List<TunnelInfo> tunnelInfoList;
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    SharePreferenceUtils sharePreferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getPersimmions();
        sharePreferenceUtils = new SharePreferenceUtils(MainActivity.this,"MyApplication");
        sharePreferenceUtils.setValue("LatitudeLongitude", "");
        sharePreferenceUtils.setValue("Address", "");
        sharePreferenceUtils.setValue("Describe", "");
        sharePreferenceUtils.setValue("TunnelId", "");


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

//                Intent intent = new Intent(MainActivity.this,MalfunctionActivity.class);
//                MainActivity.this.startActivity(intent);
            }
        });

        mSwipeRefreshWidget.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false){
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 6000;
            }
        };
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerDecoration(this));
        tunnelInfoList = new ArrayList<>();

        mSwipeRefreshWidget.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshWidget.setRefreshing(true);
            }
        });

        getChannelData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter();
                mSwipeRefreshWidget.setRefreshing(false);
            }
        });
    }

    private void setAdapter(){
        if(tunnelInfoAdapter==null){
            tunnelInfoAdapter = new TunnelInfoAdapter(this,tunnelInfoList);
            mRecyclerView.setAdapter(tunnelInfoAdapter);
        }else{
            tunnelInfoAdapter.notifyDataSetChanged();
        }

        tunnelInfoAdapter.setOnItemClickListener(new TunnelInfoAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , String data){

                Log.d("tunnelId", "tunnelId onItemClick : " + data);

                Intent intent = new Intent(MainActivity.this,MalfunctionActivity.class);
                intent.putExtra("TunnelId",data);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onRefresh() {
//        mData.clear();
        InitView();
    }

    private void getChannelData(){
        String httpurl = Constant.getAllChannel;
        HttpClientRequest httpClientRequest = HttpClientRequest.getInstance(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,httpurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ffffffffffffff", "response -> " + response);

                        JSONObject jsonObject = null;
                        String results;
                        try {
                            jsonObject = new JSONObject(response);
                            results = jsonObject.getString("results");

                            tunnelInfoList = JsonUtil.fromJson(results, new TypeToken<ArrayList<TunnelInfo>>() {
                            }.getType());
                            Log.d("ffffffffffffff", "response -> " + tunnelInfoList.size());

                            InitView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ffffffffffffff", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String,String> params = new HashMap<String, String>();

                params.put("imei", PhoneUtils.getPhoneInfo(MainActivity.this));

//                String sign = null;
//                try {
//                    sign = URLEncoder.encode(MD5Tools.GetMD5Code(Constant.PublicKey), "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                params.put("sign", sign);
//                Log.e("ffffffffffffff", " sign : "+sign);
                return params;
            }
        };

        httpClientRequest.addRequest(stringRequest);
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
