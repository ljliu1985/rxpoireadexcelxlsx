package com.lj.excel;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.udisk.lib.UsbSdk;


public class MyApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        UsbSdk.init(this);
        Utils.init(this);
    }
}
