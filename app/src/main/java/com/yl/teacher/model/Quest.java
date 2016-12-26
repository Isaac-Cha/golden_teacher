package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求列表
 * Created by yiban on 2016/5/19.
 */
public class Quest implements Serializable{

    private String id;

    private String userId;

    private String classId;

    private String requestCode;

    private String content;

    private String status;

    private String replyStatus;

    private String studentName;

    private String className;

    private String title;

    //备注
    private String extra;

    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<Quest> getQuestListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Quest> list = new ArrayList<Quest>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getQuestFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Quest getQuestFromJsonObj(JSONObject jsonObj) {
        Quest quest = new Quest();
        quest.id = jsonObj.optString("id");
        quest.userId = jsonObj.optString("apply_User_id");
        quest.classId = jsonObj.optString("SchoolClass_id");
        quest.requestCode = jsonObj.optString("requestCode");
        quest.content = jsonObj.optString("content");
        quest.status = jsonObj.optString("status");
        quest.studentName = jsonObj.optString("studentName");
        quest.className = jsonObj.optString("className");
        quest.title = jsonObj.optString("title");
        quest.replyStatus = jsonObj.optString("replyStatus");
        quest.extra = jsonObj.optString("replyContent");
        quest.createTime = jsonObj.optString("createTime");
        return quest;
    }

}
