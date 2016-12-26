package com.yl.teacher.model;

import java.io.Serializable;

/**
 * EventBus使用的工具类
 * Created by yiban on 2016/5/16.
 */
public class Action implements Serializable{

    private int pos;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    private int id;

    public int moduleId;

    //投票图片地址
    private VoteOption vote;

    // 极光Model
    public JPushNotifyModel jPushModel;

    public VoteOption getVote() {
        return vote;
    }

    public void setVote(VoteOption vote) {
        this.vote = vote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
