package com.yl.teacher.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by yiban on 2015/11/30.
 */
public class Response implements Serializable{

    private JSONObject data;

    private boolean status;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
