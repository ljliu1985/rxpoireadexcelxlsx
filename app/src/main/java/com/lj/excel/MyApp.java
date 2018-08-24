package com.lj.excel;

import android.app.Application;

import com.lj.excellib.utils.AppUtils;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
    }
}
