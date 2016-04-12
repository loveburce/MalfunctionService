package com.dawn.apollo.malfunctionservice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.dawn.apollo.adapter.TunnelInfoAdapter;
import com.dawn.apollo.config.Constant;
import com.dawn.apollo.customview.DividerDecoration;
import com.dawn.apollo.customview.WrapContentLinearLayoutManager;
import com.dawn.apollo.http.HttpClientRequest;
import com.dawn.apollo.model.TunnelInfo;
import com.dawn.apollo.utils.JsonUtil;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}
