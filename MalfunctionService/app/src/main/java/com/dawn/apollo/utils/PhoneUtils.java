package com.dawn.apollo.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by dawn-pc on 2016/4/20.
 */
public class PhoneUtils {
    public static String getPhoneInfo(Context context){
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
