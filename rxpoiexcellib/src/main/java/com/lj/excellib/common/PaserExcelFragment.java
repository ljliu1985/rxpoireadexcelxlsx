package com.lj.excellib.common;

import android.app.Activity;

import com.lj.excellib.utils.RefreshUtils;

public class PaserExcelFragment extends Activity {


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
