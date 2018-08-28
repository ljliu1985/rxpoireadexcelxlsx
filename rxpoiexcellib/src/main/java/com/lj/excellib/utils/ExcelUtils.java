package com.lj.excellib.utils;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;

import com.blankj.utilcode.util.Utils;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.lj.excellib.R;
import com.lj.excellib.common.CheckDataMethodAnnotation;
import com.lj.excellib.common.CnNameFieldAnnotation;
import com.lj.excellib.common.ICustomDeal;
import com.lj.excellib.common.ProgressListener;
import com.lj.excellib.common.RxExcelMsg;
import com.udisk.lib.IOCloseUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public final class ExcelUtils {

    private static final String TAG = ExcelUtils.class.getSimpleName();


    public static void importObjFromExcel(final Class cla, final Object objFile, final ProgressListener progressListener, final ICustomDeal customDeal) {
        Observable.create(new ObservableOnSubscribe<RxExcelMsg>() {
            @Override
            public void subscribe(ObservableEmitter<RxExcelMsg> p) throws Exception {
                InputStream fis = null;
                try {
                    RxExcelMsg rxMsg = null;
                    if (objFile instanceof UsbFile) {
                        fis = new UsbFileInputStream((UsbFile) objFile);
                        rxMsg = new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, String.format(Utils.getApp().getString(R.string.msg_loading_file), ((UsbFile) objFile).getName()));
                    } else if (objFile instanceof File) {
                        fis = new FileInputStream((File) objFile);
                        rxMsg = new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, String.format(Utils.getApp().getString(R.string.msg_loading_file), ((File) objFile).getName()));
                    } else if (objFile instanceof InputStream) {
                        fis = (InputStream) objFile;
                        rxMsg = new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, Utils.getApp().getString(R.string.msg_loading));
                    } else {
                        p.onError(new UnknownError(Utils.getApp().getString(R.string.not_support_file_or_stream)));
                        return;
                    }
                    p.onNext(rxMsg);
                    Workbook wb = WorkbookFactory.create(fis);
                    p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, Utils.getApp().getString(R.string.msg_parser_data)));
                    Sheet sheet = wb.getSheetAt(0);
                    //1.excel title
                    SparseArray<String> titleMap = new SparseArray<>();
                    int titleCount = 0;
                    for (Cell cell : sheet.getRow(0)) {
                        if (cell == null) {
                            break;
                        }
                        cell.setCellType(CellType.STRING);
                        String value = cell.getStringCellValue().trim();
                        titleMap.put(titleCount, value);
                        titleCount++;
                    }

                    //2.mapping: name--field
                    HashMap<Field, String> fieldAndCnNameMap = getFieldAndCnNameMap(cla);
                    SparseArray<Field> fieldSparseArray = new SparseArray();
                    for (int i = 0; i < titleMap.size(); i++) {
                        for (Field field : fieldAndCnNameMap.keySet()) {
                            if (fieldAndCnNameMap.get(field).equals(titleMap.get(i))) {
                                fieldSparseArray.put(i, field);
                            }
                        }
                    }

                    if (fieldSparseArray.size() == 0) {
                        p.onError(new UnknownError(Utils.getApp().getString(R.string.msg_not_match_obj)));
                        return;
                    }

                    //3.excel content to mapping
                    ArrayList<SparseArray<String>> mapList = new ArrayList<>();
                    int len = titleMap.size();
                    String msgParsering = Utils.getApp().getString(R.string.msg_parsering_data);
                    p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, String.format(msgParsering, String.valueOf(sheet.getLastRowNum()))));
                    long startTime = SystemClock.elapsedRealtime();
                    for (int i = 1; i < sheet.getLastRowNum(); i++) {
                        if (SystemClock.elapsedRealtime() - startTime >= 1000) {
                            startTime = SystemClock.elapsedRealtime();
                            p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, String.format(msgParsering, i + "/" + sheet.getLastRowNum())));
                        }
                        Cell cell;
                        SparseArray<String> map = new SparseArray<>();
                        Row row = sheet.getRow(i);
                        for (int x = 0; x < len; x++) {
                            cell = row.getCell(x);
                            if (cell == null) {
                                continue;
                            }
                            cell.setCellType(CellType.STRING);
                            String value = cell.getStringCellValue().trim();
                            map.put(x, value);
                        }
                        mapList.add(map);
                    }


                    //check values method
                    Method dealDataMethod = getDealDataMethod(cla);
                    p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, "正在生成对象,size:" + mapList.size()));
                    //创建对象
                    List list = new ArrayList<>();
                    for (int i = 0; i < mapList.size(); i++) {
                        if (SystemClock.elapsedRealtime() - startTime >= 1000) {
                            startTime = SystemClock.elapsedRealtime();
                            p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, "正在生成对象," + i + "/" + mapList.size()));
                        }
                        Object obj = cla.newInstance();
                        SparseArray<String> valueSparseArray = mapList.get(i);
                        int index;
                        for (int k = 0; k < fieldSparseArray.size(); k++) {
                            index = fieldSparseArray.keyAt(k);
                            String value = valueSparseArray.get(index);
                            if (!TextUtils.isEmpty(value)) {
                                fieldSparseArray.get(index).set(obj, value);
                            }
                        }

                        if (dealDataMethod != null) {
                            boolean flag = (boolean) dealDataMethod.invoke(obj);
                            if (!flag) {
                                String msg = String.format("行:%s, 数据不完整.", (i + 2));
                                p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, msg));
                                continue;
                            }
                        }
                        list.add(obj);
                    }
                    if (null != customDeal) {
                        p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, "正在处理数据,共" + list.size() + "条"));
                        customDeal.customDealDataOnSubThread(list);
                        p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_LOADING, "数据处理完成"));
                    }
                    p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SUCCESS, list));
                    p.onComplete();
                } catch (Exception e) {
                    p.onError(e);
                } finally {
                    IOCloseUtils.closeStream(fis);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RxExcelMsg>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RxExcelMsg rxMsg) {
                        if (null != progressListener) {
                            switch (rxMsg.type) {
                                case RxExcelMsg.TYPE_SEND_LOADING:
                                    progressListener.onProgress(rxMsg.msg);
                                    break;
                                case RxExcelMsg.TYPE_SUCCESS:
                                    progressListener.onSuccess((List) rxMsg.object);
                                    break;

                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (null != progressListener) {
                            progressListener.onFailed(e.getMessage());
                        }
                        System.gc();
                    }

                    @Override
                    public void onComplete() {
                        System.gc();
                    }

                });

    }


    /**
     * 导出Excel
     */
    public static void exportExcel(final String savePath, final String sheetName, final String[] title, final List list, final ProgressListener progressListener) {
        Observable.create(new ObservableOnSubscribe<RxExcelMsg>() {
            @Override
            public void subscribe(ObservableEmitter<RxExcelMsg> p) {
                HSSFWorkbook wb = new HSSFWorkbook();
                HSSFSheet sheet = wb.createSheet(sheetName);
                HSSFRow row = sheet.createRow(0);
                HSSFCell cell = null;

                //创建标题
                for (int i = 0; i < title.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(title[i]);
                }

                //创建内容
                Class cla = list.get(0).getClass();
                HashMap<Field, String> fieldAndCnNameMap = getFieldAndCnNameMap(cla);
                SparseArray<Field> indexMap = new SparseArray();
                for (int i = 0; i < title.length; i++) {
                    for (Field field : fieldAndCnNameMap.keySet()) {
                        if (fieldAndCnNameMap.get(field).equals(title[i])) {
                            indexMap.put(i, field);
                        }
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    row = sheet.createRow(i + 1);
                    Object obj = list.get(i);
                    try {
                        for (int j = 0; j < indexMap.size(); j++) {
                            int index = indexMap.keyAt(j);
                            String value = (String) indexMap.get(index).get(obj);
                            if (!TextUtils.isEmpty(value)) {
                                row.createCell(index).setCellValue(value);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_MSG, e.getMessage()));
                    }
                }


                String path = savePath + sheetName + ".xls";
                OutputStream out = null;
                try {
                    File tempFile = new File(path);
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    out = new FileOutputStream(tempFile);
                    wb.write(out);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    p.onNext(new RxExcelMsg(RxExcelMsg.TYPE_SEND_MSG, e.getMessage()));
                } finally {
                    IOCloseUtils.closeStream(out, wb);
                }
                p.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RxExcelMsg>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RxExcelMsg rxMsg) {
                        if (null != progressListener) {
                            switch (rxMsg.type) {
                                case RxExcelMsg.TYPE_SEND_MSG:
                                    progressListener.onProgress(rxMsg.msg);
                                    break;
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (null != progressListener) {
                            progressListener.onFailed(e.getMessage());
                        }
                        System.gc();
                    }

                    @Override
                    public void onComplete() {
                        System.gc();
                        if (null != progressListener) {
                            progressListener.onSuccess();
                        }
                    }
                });


    }


    /**
     * 属性与中名称对应
     *
     * @param cla
     */
    public static HashMap<Field, String> getFieldAndCnNameMap(Class cla) {
        // 获取所有属性
        Field[] fields = cla.getDeclaredFields();
        // 设置所有属性方法可访问
        AccessibleObject.setAccessible(fields, true);
        HashMap<Field, String> namesMap = new HashMap<>();
        //属性与中文名称对应
        for (Field field : fields) {
            boolean fieldHasAnno = field.isAnnotationPresent(CnNameFieldAnnotation.class);
            if (fieldHasAnno) {
                CnNameFieldAnnotation cnNameFieldAnnotation = field.getAnnotation(CnNameFieldAnnotation.class);
                namesMap.put(field, cnNameFieldAnnotation.cnName());
            }
        }
        //属性无中文名时使用原属性
        if (namesMap.size() == 0) {
            for (Field field : fields) {
                namesMap.put(field, field.getName());
            }
        }
        return namesMap;
    }


    public static Method getDealDataMethod(Class cla) {
        //解析方法上的注解
        Method[] methods = cla.getDeclaredMethods();
        AccessibleObject.setAccessible(methods, true);
        for (Method method : methods) {
            boolean methodHasAnno = method.isAnnotationPresent(CheckDataMethodAnnotation.class);
            if (methodHasAnno) {
                return method;
            }
        }
        return null;
    }


    public final static boolean checkValueIsEmpty(String... values) {
        for (String v : values) {
            if (TextUtils.isEmpty(v)) {
                return true;
            }
        }
        return false;
    }

    public final static boolean checkIsValue(String value) {
        return value.matches("^[-+]?\\d+(\\.\\d+)?$");
    }

}
