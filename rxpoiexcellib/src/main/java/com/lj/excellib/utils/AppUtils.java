package com.lj.excellib.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;


public final class AppUtils {

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;


    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        AppUtils.sApplication = app;
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

}
