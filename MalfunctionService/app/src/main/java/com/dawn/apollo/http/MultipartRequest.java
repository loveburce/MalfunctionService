package com.dawn.apollo.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
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
public class MultipartRequest extends Request<String> {
//    private final Response.Listener<String> mListener;
    private Map<String, String> mParams;
    private Map<String, String> mHeader;

    MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    MultipartBuilder builder;


    public MultipartRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
//        mListener = listener;

    }

    public MultipartRequest(String url, String filePartName,
                             List<String> files, Map<String, String> params,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
//        mFilePartName = filePartName;
//        mListener = listener;
//        mFileParts = files;
        mParams = params;

        builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        //遍历map中所有参数到builder
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, params.get(key));
        }


        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
        for (String path : files) {
            builder.addFormDataPart("upload", null, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
        }

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        RequestBody requestBody = builder.build();
        return super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return super.getBody();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(String response) {

    }
}
