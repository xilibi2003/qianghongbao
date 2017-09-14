package com.luffy88.qianghongbao;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by Emmett on 2017/9/9.
 */

public class ServiceStatus {
    public static final String TAG = "ServiceStatus";


    private Context mCt;
    private static ServiceStatus sSelf;
    private boolean mServiceOn = false;
    private SharedPreferences mPref;

    public static ServiceStatus getInstance(Context ct) {
        if (sSelf == null) {
            sSelf = new ServiceStatus(ct);
        }
        return sSelf;
    }

    private ServiceStatus(Context ct) {
        mCt = ct;
        mPref = mCt.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public boolean settingOn() {
        return mPref.getInt("status", 0) == 1;
    }

    public void setSettingOn(boolean on) {
        if (on) {
            mPref.edit().putInt("status", 1).apply();
        } else {
            mPref.edit().putInt("status", 0).apply();
        }

    }

    public boolean serviceOn() {
        return mServiceOn;
    }

    public void  openService() {
        mServiceOn = true;

        mPref.edit().putLong("lastActiveTime", System.currentTimeMillis()).apply();
    }

    public long getLastActiveTime() {
        return mPref.getLong("lastActiveTime", 0);
    }

    public void  stopService() {
        mServiceOn = false;
    }



}
