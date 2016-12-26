package com.yl.teacher.model;

import java.util.ArrayList;

public class HomeworkModel {

    public boolean status;
    public Data data;
    public String message;

    public class List {
        public int id;
        public int User_id;
        public int SchoolClass_id;
        public String homeworkDate;
        public String content;
        public String createTime;
        public String className;
        public int unreadCount;
        public int readCount;
        public int number;
        public String title;
    }

    public class Data {
        public ArrayList<List> list;
        public int count;
    }

}
