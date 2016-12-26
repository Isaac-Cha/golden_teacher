package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级管理
 * Created by yiban on 2016/5/19.
 */
public class Manager implements Serializable{

    private String classId;

    private int memberCount;

    private int waitApply;

    private String className;

    private List<MenuList> menu;

    public boolean isPermit;

    public List<String> moduleIds;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getWaitApply() {
        return waitApply;
    }

    public void setWaitApply(int waitApply) {
        this.waitApply = waitApply;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<MenuList> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuList> menu) {
        this.menu = menu;
    }

    public static List<Manager> getManagerListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Manager> list = new ArrayList<Manager>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getManagerFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Manager getManagerFromJsonObj(JSONObject jsonObj) {
        Manager manager = new Manager();
        manager.classId = jsonObj.optString("Class_id");
        manager.memberCount = jsonObj.optInt("memberCount");
        manager.waitApply = jsonObj.optInt("waitApply");
        manager.className = jsonObj.optString("className");
        manager.menu = MenuList.getMenuDataFromJsonObj(jsonObj.optJSONArray("menuList"));

        return manager;
    }

}
