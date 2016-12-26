package com.yl.teacher.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 版本升级
 * Created by yiban on 2016/2/3.
 */
@Table(name = "msgupdate")
public class MsgUpdate implements Serializable{
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "mydate")
    private String mydate;
    @Column(name = "show")
    private int show;
    @Column(name = "shownum")
    private int shownum;


    public int getShownum() {
        return shownum;
    }

    public void setShownum(int shownum) {
        this.shownum = shownum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMydate() {
        return mydate;
    }

    public void setMydate(String mydate) {
        this.mydate = mydate;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }
}
