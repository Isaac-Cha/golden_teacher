package com.yl.teacher.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by $USER_NAME on 2016/10/19.
 */
public class Student implements Serializable {
    /**
     * status : true
     * data : {"id":253,"School_id":139,"SchoolClass_id":147,"User_id":40682,"studentName":"王小栋","birthday":"0000-00-00 00:00:00","createTime":"2016-10-18 17:07:37","sex":null,"headpicUrl":"http://devimg.lexuetao.com/avatar/40682/100_100"}
     * message : 请求成功!
     */
    public boolean status;
    /**
     * id : 253
     * School_id : 139
     * SchoolClass_id : 147
     * User_id : 40682
     * studentName : 王小栋
     * birthday : 0000-00-00 00:00:00
     * createTime : 2016-10-18 17:07:37
     * sex : null
     * headpicUrl : http://devimg.lexuetao.com/avatar/40682/100_100
     */
    public DataBean data;
    public String message;

    public static class DataBean implements Serializable {
        public int id;
        @SerializedName("School_id")
        public int schoolId;
        @SerializedName("SchoolClass_id")
        public int classId;
        @SerializedName("User_id")
        public int studentId;
        public String studentName;
        public String birthday;
        public String createTime;
        public int sex;
        @SerializedName("headpicUrl")
        public String studentAvatar;
    }
}
