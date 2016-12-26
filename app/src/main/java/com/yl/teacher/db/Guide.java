package com.yl.teacher.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 新手引导页是否已经启用过
 * Created by yiban on 2016/2/3.
 */
@Table(name = "guide")
public class Guide implements Serializable{
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "version")
    private String version;
    @Column(name = "intial")
    private int intial;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIntial() {
        return intial;
    }

    public void setIntial(int intial) {
        this.intial = intial;
    }
}
