package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录
 * Created by yiban on 2016/5/19.
 */
public class Contact implements Serializable{

    private String id;

    private String userId;

    private String title;

    private String phone;

    private String studentName;

    private String age;

    private int sex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public static List<Contact> getContactListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Contact> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getContactFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Contact getContactFromJsonObj(JSONObject jsonObj) {
        Contact contact = new Contact();
        contact.id = jsonObj.optString("id");
        contact.userId = jsonObj.optString("User_id");
        contact.title = jsonObj.optString("title");
        contact.phone = jsonObj.optString("phone");
        contact.studentName = jsonObj.optString("studentName");
        contact.age = jsonObj.optString("old");
        contact.sex = jsonObj.optInt("sex");
        return contact;
    }

}
