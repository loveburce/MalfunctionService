package com.dawn.apollo.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dawn-pc on 2016/4/13.
 */
//public class MultipartRequestF extends Request<String> {
//    private MultipartEntity entity = new MultipartEntity();
//
//    private final Response.Listener<String> mListener;
//
//    private List<File> mFileParts;
//    private String mFilePartName;
//    private Map<String, String> mParams;
//    /**
//     * 单个文件
//     * @param url
//     * @param errorListener
//     * @param listener
//     * @param filePartName
//     * @param file
//     * @param params
//     */
//    public MultipartRequestF(String url, Response.ErrorListener errorListener,
//                             Response.Listener<String> listener, String filePartName, File file,
//                             Map<String, String> params) {
//        super(Method.POST, url, errorListener);
//
//        mFileParts = new ArrayList<File>();
//        if (file != null) {
//            mFileParts.add(file);
//        }
//        mFilePartName = filePartName;
//        mListener = listener;
//        mParams = params;
//        buildMultipartEntity();
//    }
//    /**
//     * 多个文件，对应一个key
//     * @param url
//     * @param errorListener
//     * @param listener
//     * @param filePartName
//     * @param files
//     * @param params
//     */
//    public MultipartRequestF(String url, String filePartName,
//                             List<File> files, Map<String, String> params,
//                             Response.Listener<String> listener,
//                             Response.ErrorListener errorListener) {
//        super(Method.POST, url, errorListener);
//        mFilePartName = filePartName;
//        mListener = listener;
//        mFileParts = files;
//        mParams = params;
//        buildMultipartEntity();
//    }
//
//    MultipartBuilder builder;
//
//    public MultipartRequestF(String url, String filePartName,
//                             List<String> files, Map<String, String> params,
//                             String type,
//                             Response.Listener<String> listener,
//                             Response.ErrorListener errorListener) {
//        super(Method.POST, url, errorListener);
//        mFilePartName = filePartName;
//        mListener = listener;
////        mFileParts = files;
//        mParams = params;
//        buildMultipartEntity();
//
//        //参数类型
//         MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
//        //创建OkHttpClient实例
////        OkHttpClient client = new OkHttpClient();
//
//        builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//
//        //遍历map中所有参数到builder
//        for (String key : params.keySet()) {
//            builder.addFormDataPart(key, params.get(key));
//        }
//
//
//        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
//        for (String path : files) {
//            builder.addFormDataPart("upload", null, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
//        }
//
//
////        //构建请求体
////        RequestBody requestBody = builder.build();
////
////        //构建请求
////        Request request = new Request.Builder()
////                .url(url)//地址
////                .post(requestBody)//添加请求体
////                .build();
////
//////发送异步请求，同步会报错，Android4.0以后禁止在主线程中进行耗时操作
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Request request, IOException e) {
////                System.out.println("request = " + request.urlString());
////                System.out.println("e.getLocalizedMessage() = " + e.getLocalizedMessage())；
////            }
////
////            @Override
////            public void onResponse(Response response) throws IOException {
////                //看清楚是response.body().string()不是response.body().toString()
////                System.out.println("response = " + response.body().string());
////            }
////        });
//
//    }
//
//
//    private void buildMultipartEntity() {
//        if (mFileParts != null && mFileParts.size() > 0) {
//            for (File file : mFileParts) {
//                entity.addPart(mFilePartName, new FileBody(file));
//            }
//            long l = entity.getContentLength();
//        }
//
//        try {
//            if (mParams != null && mParams.size() > 0) {
//                for (Map.Entry<String, String> entry : mParams.entrySet()) {
//                    entity.addPart(
//                            entry.getKey(),
//                            new StringBody(entry.getValue(), Charset
//                                    .forName("UTF-8")));
//                }
//            }
//        } catch (UnsupportedEncodingException e) {
//            VolleyLog.e("UnsupportedEncodingException");
//        }
//    }
//
//    @Override
//    public String getBodyContentType() {
//        return entity.getContentType().getValue();
//    }
//
//    @Override
//    public byte[] getBody() throws AuthFailureError {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            entity.writeTo(bos);
//        } catch (IOException e) {
//            VolleyLog.e("IOException writing to ByteArrayOutputStream");
//        }
//        return bos.toByteArray();
//    }
//
//    @Override
//    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//
//        if (VolleyLog.DEBUG) {
//            if (response.headers != null) {
//                for (Map.Entry<String, String> entry : response.headers
//                        .entrySet()) {
//                    VolleyLog.d(entry.getKey() + "=" + entry.getValue());
//                }
//            }
//        }
//
//        String parsed;
//        try {
//            parsed = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers));
//        } catch (UnsupportedEncodingException e) {
//            parsed = new String(response.data);
//        }
//        return Response.success(parsed,
//                HttpHeaderParser.parseCacheHeaders(response));
//    }
//
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.android.volley.Request#getHeaders()
//     */
//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        VolleyLog.d("getHeaders");
//        Map<String, String> headers = super.getHeaders();
//
//        if (headers == null || headers.equals(Collections.emptyMap())) {
//            headers = new HashMap<String, String>();
//        }
//
//
//        return headers;
//    }
//
//    @Override
//    protected void deliverResponse(String response) {
//        mListener.onResponse(response);
//    }
//}
