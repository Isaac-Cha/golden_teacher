package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 待入班的家长列表
 * Created by yiban on 2016/5/19.
 */
public class Join implements Serializable{

    private String id;

    private String schoolId;

    private int status;

    private String name;

    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static List<Join> getJoinListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Join> list = new ArrayList<Join>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getJoinFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Join getJoinFromJsonObj(JSONObject jsonObj) {
        Join join = new Join();
        join.id = jsonObj.optString("id");
        join.schoolId = jsonObj.optString("SchoolClass_id");
        join.status = jsonObj.optInt("status");
        join.name = jsonObj.optString("listTitle");
        join.comment = jsonObj.optString("comment");
        return join;
    }

}
