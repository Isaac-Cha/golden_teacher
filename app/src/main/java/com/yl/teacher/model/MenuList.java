package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级管理菜单
 * Created by yiban on 2016/5/19.
 */
public class MenuList implements Serializable{

    private String name;

    private String icon;

    private String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static List<MenuList> getMenuDataFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<MenuList> list = new ArrayList<MenuList>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getMenuListFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static MenuList getMenuListFromJsonObj(JSONObject jsonObj) {
        MenuList menuList = new MenuList();
        menuList.icon = jsonObj.optString("icon");
        menuList.name = jsonObj.optString("name");
        menuList.tag = jsonObj.optString("tag");
        return menuList;
    }

}
