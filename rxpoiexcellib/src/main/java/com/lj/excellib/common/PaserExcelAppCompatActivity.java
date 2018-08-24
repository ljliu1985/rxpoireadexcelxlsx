package com.lj.excellib.common;

import android.support.v7.app.AppCompatActivity;

import com.lj.excellib.utils.RefreshUtils;

public class PaserExcelAppCompatActivity extends AppCompatActivity {


    protected RefreshUtils loaddingDialog;


    public void showLoading() {
        if (loaddingDialog == null) {
            loaddingDialog = RefreshUtils.createDialog(this);
        }
        loaddingDialog.show();
    }

    public void showLoading(String msg) {
        if (loaddingDialog != null && loaddingDialog.isShowing()) {
            loaddingDialog.refresh(msg);
        }
    }

    public void hideLoading() {
        if (loaddingDialog != null && loaddingDialog.isShowing()) {
            loaddingDialog.dismiss();
        }
    }
}
