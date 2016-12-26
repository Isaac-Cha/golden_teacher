package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 投票
 * Created by yiban on 2016/5/16.
 */
public class Vote implements Serializable {

    private String id;

    private String title;

    private String status;

    private String maxRecord;

    private String record;

    private String unrecord;

    private String createTime;

    private List<VoteOption> option;

    private String anonymous; // 是否公开  0公开1不公开

    private String multiple; // 是否多选   0单选1多选

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaxRecord() {
        return maxRecord;
    }

    public void setMaxRecord(String maxRecord) {
        this.maxRecord = maxRecord;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getUnrecord() {
        return unrecord;
    }

    public void setUnrecord(String unrecord) {
        this.unrecord = unrecord;
    }

    public List<VoteOption> getOption() {
        return option;
    }

    public void setOption(List<VoteOption> option) {
        this.option = option;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getMultiple() {
        return multiple;
    }

    public static List<Vote> getVoteListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Vote> list = new ArrayList<Vote>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getVoteFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static Vote getVoteFromJsonObj(JSONObject jsonObj) {
        Vote vote = new Vote();
        vote.id = jsonObj.optString("id");
        vote.title = jsonObj.optString("title");
        vote.status = jsonObj.optString("status");
        vote.maxRecord = jsonObj.optString("maxRecord");
        vote.record = jsonObj.optString("record");
        vote.unrecord = jsonObj.optString("unrecord");
        vote.createTime = jsonObj.optString("createTime");
        vote.anonymous = jsonObj.optString("anonymous");
        vote.multiple = jsonObj.optString("multiple");
        vote.option = VoteOption.getVoteOptionListFromJsonObj(jsonObj.optJSONArray("options"));


        return vote;
    }


}
