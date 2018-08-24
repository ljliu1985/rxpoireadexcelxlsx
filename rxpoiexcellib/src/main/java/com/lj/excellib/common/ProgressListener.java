package com.lj.excellib.common;

import java.util.List;

public interface ProgressListener {


    void onProgress(String progress);

    void onFailed(String msg);

    void onSuccess(List list);

    void onSuccess();

}