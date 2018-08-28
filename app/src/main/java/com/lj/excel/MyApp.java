package com.lj.excel;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.udisk.lib.UsbSdk;


public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UsbSdk.init(this);
        Utils.init(this);
    }
}
