package com.yl.teacher.model;

import java.util.ArrayList;

/**
 * 公告列表Model
 */
public class BulletinModel {

    public boolean status;
    public Data data;
    public String message;

    public class List {
        public int id;
        public int type;
        public String title;
        public String createTime;
        public int readStatus;
    }

    public class Data {
        public ArrayList<List> list;
        public int count;
    }

}
