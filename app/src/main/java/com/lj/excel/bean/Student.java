package com.lj.excel.bean;

import android.text.TextUtils;

import com.lj.excel.utils.DataDealUtils;
import com.lj.excellib.common.CheckDataMethodAnnotation;
import com.lj.excellib.common.CnNameFieldAnnotation;

/**
 * 学生
 */
public class Student {
    @CnNameFieldAnnotation(cnName = "年级编号")
    public String gradeId;
    @CnNameFieldAnnotation(cnName = "班级编号")
    public String classNo;
    @CnNameFieldAnnotation(cnName = "班级名称")
    public String className;
    @CnNameFieldAnnotation(cnName = "姓名")
    public String name;
    @CnNameFieldAnnotation(cnName = "民族代码")
    public String nationId;
    @CnNameFieldAnnotation(cnName = "学籍号")
    public String studentNum;
    @CnNameFieldAnnotation(cnName = "身份证号")
    public String idCardNo;
    @CnNameFieldAnnotation(cnName = "出生日期")
    public String birthDay;
    @CnNameFieldAnnotation(cnName = "学生来源")
    public String studentSource;
    @CnNameFieldAnnotation(cnName = "家庭住址")
    public String address;

    @CnNameFieldAnnotation(cnName = "民族")
    public String nationName;

    @CnNameFieldAnnotation(cnName = "性别")
    public String sex;


    @CheckDataMethodAnnotation()
    public boolean dealData() {
        for (String s : new String[]{gradeId, classNo, className, studentNum, nationId, name, sex, name}) {
            if (TextUtils.isEmpty(s)) {
                return false;
            }
        }
        if (null == birthDay) {
            birthDay = "";
        }
        if (null == idCardNo) {
            idCardNo = "";
        }
        if (null == studentSource) {
            studentSource = "";
        }
        if (null == address) {
            address = "";
        }
        if (nationId.matches("\\d+")) {
            nationName = DataDealUtils.decodeNation(Integer.parseInt(nationId));
        } else {
            nationName = "";
        }
        return true;
    }


    @Override
    public String toString() {
        return "年级:" + DataDealUtils.getGradeName(gradeId) + ",姓名:" + name + ",性别:" + DataDealUtils.getSex(sex) + ",学籍号:" + studentNum + ",民族:" + nationName;
    }
}
