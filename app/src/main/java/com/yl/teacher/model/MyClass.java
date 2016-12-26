package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级列表
 * Created by yiban on 2016/5/18.
 */
public class MyClass implements Serializable{

    private int id;

    private String name;

    private int schoolId;

    private String headMaster;

    private String classCode;

    private String createTime;

    private String realName;

    private String memberCount;

    private String joinInfo;

    private String school;

    public boolean isRed;

    public String getHeadMaster() {
        return headMaster;
    }

    public void setHeadMaster(String headMaster) {
        this.headMaster = headMaster;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getJoinInfo() {
        return joinInfo;
    }

    public void setJoinInfo(String joinInfo) {
        this.joinInfo = joinInfo;
    }

    public static List<MyClass> getMyClassListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<MyClass> list = new ArrayList<MyClass>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getMyClassFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static MyClass getMyClassFromJsonObj(JSONObject jsonObj) {
        MyClass myClass = new MyClass();
        myClass.id = jsonObj.optInt("id");
        myClass.name = jsonObj.optString("name");
        myClass.schoolId = jsonObj.optInt("School_id");
        myClass.classCode = jsonObj.optString("classCode");
        myClass.createTime = jsonObj.optString("createTime");
        myClass.realName = jsonObj.optString("realName");
        myClass.memberCount = jsonObj.optString("memberCount");
        myClass.joinInfo = jsonObj.optString("joinInfo");

        return myClass;
    }

    public static MyClass getCreateClassFromJsonObj(JSONObject jsonObj) {
        MyClass myClass = new MyClass();
        myClass.name = jsonObj.optString("name");
        myClass.classCode = jsonObj.optString("classCode");
        myClass.headMaster = jsonObj.optString("headmaster");
        myClass.id = jsonObj.optInt("id");
        return myClass;
    }

}
