package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiban on 2016/5/18.
 */
public class School implements Serializable{

    private int id;

    private String name;

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

    public static List<School> getSchoolListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<School> list = new ArrayList<School>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getSchoolFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static School getSchoolFromJsonObj(JSONObject jsonObj) {
        School school = new School();
        school.id = jsonObj.optInt("id");
        school.name = jsonObj.optString("name");
        return school;
    }

}
