package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiban on 2016/5/20.
 */
public class Reply implements Serializable{

    private String id;

    private String questId;

    private String userId;

    private String content;

    private String createTime;

    private String trueName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public static List<Reply> getReplyListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Reply> list = new ArrayList<Reply>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getReplyFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Reply getReplyFromJsonObj(JSONObject jsonObj) {
        Reply reply = new Reply();
        reply.id = jsonObj.optString("id");
        reply.questId = jsonObj.optString("ClassMemberRequest_id");
        reply.userId = jsonObj.optString("User_id");
        reply.content = jsonObj.optString("content");
        reply.createTime = jsonObj.optString("createTime");
        reply.trueName = jsonObj.optString("truename");

        return reply;
    }

}
