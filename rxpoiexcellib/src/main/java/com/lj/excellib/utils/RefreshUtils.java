package com.lj.excellib.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.lj.excellib.R;

import java.util.HashMap;



public class RefreshUtils extends Dialog {

    private static final int ID = 0XCCAA0001;

    public static final int REFRESH = ID + 1;
    private Context mContext;
    private int dlgType;
    private HashMap<Integer, Integer> layoutMap;
    private String msgStr;
    private TextView msg_tx;

    private RefreshUtils(Context context, int dlgType) {
        super(context, R.style.dlg_refresh_style);
        setCanceledOnTouchOutside(false);
        this.dlgType = dlgType;
        this.mContext = context;
        initLayoutMap();
    }

    public static RefreshUtils createDialog(Context context) {
        return new RefreshUtils(context, REFRESH);
    }

    public static RefreshUtils createDialog(Context context, int dlgType) {
        return new RefreshUtils(context, dlgType);
    }

    /**
     * (non-Javadoc).
     *
     * @param savedInstanceState
     * @see android.app.AlertDialog#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutMap.get(this.dlgType));
        initViews();
    }

    /**
     * Description: .
     */
    @SuppressLint("UseSparseArrays")
    private void initLayoutMap() {
        layoutMap = new HashMap<Integer, Integer>();

        layoutMap.put(REFRESH, R.layout.view_dlg_refresh);// 刷新
    }

    /**
     * Description: .
     */
    private void initViews() {
        msg_tx = (TextView) findViewById(R.id.dlg_tv_message);
        switch (dlgType) {
            case REFRESH:
                if (msgStr == null) {
                    msgStr = "正在加载...";
                }
                msg_tx.setText(msgStr);
                break;
            default:

                break;
        }
    }

    public void refresh(String msg) {
        msg_tx.setText(msg);
    }

    public void setMessage(int messageId) {
        this.msgStr = mContext.getResources().getString(messageId);
    }

    public void setMessage(String message) {
        this.msgStr = message;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            if (isShowing()) {
                this.dismiss();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}
