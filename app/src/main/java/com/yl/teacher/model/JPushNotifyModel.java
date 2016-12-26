package com.yl.teacher.model;

public class JPushNotifyModel {
    /**
     * type : 1
     * tid : 13
     */
    public int type;
    public int tid;
    public int classId;

    @Override
    public String toString() {
        return "JPushNotifyModel{" +
                "type=" + type +
                ", tid=" + tid +
                ", classId=" + classId +
                '}';
    }
}
