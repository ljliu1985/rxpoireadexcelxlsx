package com.lj.excel;

import android.Manifest;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lj.excel.bean.Student;
import com.lj.excellib.common.CommonProgressListener;
import com.lj.excellib.common.ICustomDeal;
import com.lj.excellib.common.PaserExcelAppCompatActivity;
import com.lj.excellib.common.ProgressListener;
import com.lj.excellib.utils.ExcelUtils;
import com.udisk.lib.CommonSelectCallBack;
import com.udisk.lib.RxPermissionsUtil;
import com.udisk.lib.SelectMode;
import com.udisk.lib.UsbDialogFrament;
import com.udisk.lib.UsbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends PaserExcelAppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.btn_import_assets)
    Button btnImportAssets;
    @BindView(R.id.btn_import_sdcard)
    Button btnImportSdcard;
    @BindView(R.id.btn_import_usb)
    Button btnImportUsb;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.btn_export)
    Button btnExport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        RxPermissionsUtil.requestPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    @OnClick({R.id.btn_import_assets, R.id.btn_import_sdcard, R.id.btn_import_usb, R.id.btn_export})
    public void onViewClicked(View view) {
        tvContent.setText("");
        switch (view.getId()) {
            case R.id.btn_import_assets:
                try {
                    showLoading();
                    ExcelUtils.importObjFromExcel(Student.class, getAssets().open("students.xls"), new CommonProgressListener() {
                        @Override
                        public void onProgress(String progress) {
                            Log.i(TAG, "onProgress:" + progress);
                            tvContent.append(progress + "\r\n");
                            showLoading(progress);

                        }

                        @Override
                        public void onFailed(String msg) {
                            hideLoading();
                            Log.i(TAG, "onFailed:" + msg);
                        }

                        @Override
                        public void onSuccess(List list) {
                            Log.i(TAG, "onSuccess:" + list.size());
                            hideLoading();
                            tvContent.append("data size:" + list.size());
                            for (Student student : (List<Student>) list) {
                                Log.i(TAG, student.toString());
                            }
                        }
                    }, new ICustomDeal() {
                        @Override
                        public void customDealDataOnSubThread(List list) {
                            //to do something(eg:save to db)
                            Log.i(TAG, "to do something start");
                            Log.i(TAG, "to do something finish");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_import_sdcard:
            case R.id.btn_import_usb:

                new UsbDialogFrament.Builder(this).setFilter(UsbHelper.REGEX_EXCEl_FILE).setSelectCallBack(new CommonSelectCallBack() {
                    @Override
                    public void onSelectSingleCallBack(Object objFile) {
                        showLoading();
                        ExcelUtils.importObjFromExcel(Student.class, objFile, new CommonProgressListener() {
                            @Override
                            public void onProgress(String progress) {
                                Log.i(TAG, "onProgress:" + progress);
                                showLoading(progress);
                            }

                            @Override
                            public void onFailed(String msg) {
                                hideLoading();
                                Log.i(TAG, "onFailed:" + msg);
                            }

                            @Override
                            public void onSuccess(List list) {
                                hideLoading();
                                Log.i(TAG, "onSuccess:" + list.size());
                                for (Student student : (List<Student>) list) {
                                    Log.i(TAG, student.toString());
                                }
                            }
                        }, new ICustomDeal() {
                            @Override
                            public void customDealDataOnSubThread(List list) {
                                //to do something(eg:save to db)
                                Log.i(TAG, "to do something....");
                            }
                        });


                    }
                }).setSelectMode(SelectMode.SelectSingleFile).show();

                break;

            case R.id.btn_export:
                String path = "/sdcard/";
                String name = "test";
                String[] title = new String[]{"姓名", "性别", "出生日期"};
                String[] title2 = new String[]{"姓名", "性别", "学籍号"};
                List<Student> list = new ArrayList<>();
                Student student;
                for (int i = 0; i < 10; i++) {
                    student = new Student();
                    student.name = "name" + i;
                    student.sex = "男";
                    student.studentNum = "1000" + i;
                    student.birthDay = "1990-01-1" + i;
                    list.add(student);
                }
                showLoading();
                ExcelUtils.exportExcel(path, name, title, list, new CommonProgressListener() {
                    @Override
                    public void onProgress(String progress) {
                        showLoading(progress);
                    }

                    @Override
                    public void onSuccess() {
                        hideLoading();
                    }

                    @Override
                    public void onFailed(String msg) {
                        hideLoading();
                    }
                });
                break;
        }
    }

}
