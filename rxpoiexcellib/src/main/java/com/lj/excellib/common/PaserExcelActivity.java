package com.lj.excellib.common;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.lj.excellib.utils.RefreshUtils;

public class PaserExcelActivity extends Fragment {


    protected RefreshUtils loaddingDialog;


    public void showLoading() {
        if (loaddingDialog == null) {
            loaddingDialog = RefreshUtils.createDialog(getContext());
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
