package com.yl.teacher.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 统计推送各模块数量
 */
@Table(name = "JPushData")
public class Push {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "type")
    public int type;

    @Column(name = "classId")
    public int classId;

    @Column(name = "tid")
    public int tid;

}
