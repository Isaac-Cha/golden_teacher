package com.yl.teacher.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by $USER_NAME on 2016/9/21.
 * 环信群组ID model
 */
public class EMMobGroup implements Serializable {

    public String emGroupId;
    public String nickname;
    public String avatar;

    public static EMMobGroup getEMGroupIdFromJsonObj(JSONObject jsonObj) {

        EMMobGroup emMobGroup = new EMMobGroup();

        emMobGroup.emGroupId = jsonObj.optString("group_id");
        emMobGroup.nickname = jsonObj.optString("nickname");
        emMobGroup.avatar = jsonObj.optString("avatar");

        return emMobGroup;

    }

}
