package com.yl.teacher.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiban on 2016/5/23.
 */
public class VoteOption implements Serializable {

    public String id; // 唯一标识

    private String title;

    private String image;

    private String imageType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    public static List<VoteOption> getVoteOptionListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<VoteOption> list = new ArrayList<VoteOption>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getVoteOptionFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    public static VoteOption getVoteOptionFromJsonObj(JSONObject jsonObj) {
        VoteOption vote = new VoteOption();
        vote.title = jsonObj.optString("title");
        vote.image = jsonObj.optString("cover");
        vote.imageType = jsonObj.optString("imageType");
        return vote;
    }


}
