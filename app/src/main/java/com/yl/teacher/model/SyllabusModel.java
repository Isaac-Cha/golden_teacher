package com.yl.teacher.model;

import java.util.ArrayList;

public class SyllabusModel {

    public boolean status;
    public Data data;
    public String message;

    public class List {
        public int id;
        public String picUrl;
        public String createTime;
        public String title;
        public int readStatus;
    }

    public class Data {
        public ArrayList<List> list;
        public int count;
    }

}
