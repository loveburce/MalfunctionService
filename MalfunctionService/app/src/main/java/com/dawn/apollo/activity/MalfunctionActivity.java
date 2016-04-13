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
import android.util.Log;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dawn.apollo.config.Constant;
import com.dawn.apollo.http.HttpClientRequest;
import com.dawn.apollo.http.MultipartRequest;
import com.dawn.apollo.http.OkHttpClientManager;
import com.dawn.apollo.malfunctionservice.R;
import com.dawn.apollo.model.TunnelInfo;
import com.dawn.apollo.utils.JsonUtil;
import com.dawn.apollo.utils.MD5Tools;
import com.dawn.apollo.utils.photo.Bimp;
import com.dawn.apollo.utils.photo.FileUtils;
import com.dawn.apollo.utils.photo.ImageItem;
import com.dawn.apollo.utils.photo.PublicWay;
import com.dawn.apollo.utils.photo.Res;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dawn-pc on 2016/4/12.
 */
public class MalfunctionActivity extends Activity {

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap ;
    TextView tv_submit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_selectimg, null);
        setContentView(parentView);
        Init();
    }

    public void Init() {
        tv_submit = (TextView) findViewById(R.id.activity_selectimg_send);

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

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
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
                uploadImageFiles();
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


    private void uploadImageFiles(){
        String httpurl = Constant.submitTrouble;
        HttpClientRequest httpClientRequest = HttpClientRequest.getInstance(MalfunctionActivity.this);

        //在这里设置需要post的参数
        Map<String,String> params = new HashMap<String, String>();

        String sign = null;
        String chunnel_id = null;
        String location = null;
        String description = null;
        String imgs = null;
//东经：116°23′17〃，北纬：116°23′17〃
        try {
            chunnel_id = URLEncoder.encode("1", "UTF-8");
            location = URLEncoder.encode("116°23′17:116°23′17", "UTF-8");
            description = URLEncoder.encode("发动机坏了", "UTF-8");

            sign = URLEncoder.encode(MD5Tools.GetMD5Code(Constant.PublicKey), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("chunnel_id", chunnel_id);
        params.put("location", location);
        params.put("description", description);
        params.put("sign", sign);


        List<File> fileList = new ArrayList<>();
        for(int i=0; i<Bimp.tempSelectBitmap.size();i++){
            ImageItem imageItem = Bimp.tempSelectBitmap.get(i);
            Log.d("imageItemimageItem","imageItemimageItem : "+imageItem.toString());
            File file = new File(imageItem.getImagePath());
            fileList.add(file);
        }

//    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        OkHttpClientManager okHttpClientManager = OkHttpClientManager.getInstance();

//        okHttpClientManager.post(httpurl,fileList,);


        //参数类型
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        //创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        //遍历map中所有参数到builder
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, params.get(key));
        }


        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
        for (ImageItem imageItem : Bimp.tempSelectBitmap) {
            builder.addFormDataPart("imgs", null, RequestBody.create(MEDIA_TYPE_PNG, new File(imageItem.getImagePath())));
        }
//        for(int i=0; i<Bimp.tempSelectBitmap.size();i++){
//            ImageItem imageItem = Bimp.tempSelectBitmap.get(i);
//            Log.d("imageItemimageItem","imageItemimageItem : "+imageItem.toString());
//            File file = new File(imageItem.getImagePath());
//            fileList.add(file);
//        }

        //构建请求体
        RequestBody requestBody = builder.build();

        //构建请求
        Request request = new Request.Builder()
                .url(httpurl)//地址
                .post(requestBody)//添加请求体
                .build();

        //发送异步请求，同步会报错，Android4.0以后禁止在主线程中进行耗时操作
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("request = " + request.urlString());
                System.out.println("e.getLocalizedMessage() = " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //看清楚是response.body().string()不是response.body().toString()
                System.out.println("response = " + response.body().string());
            }
        });


    }
}
