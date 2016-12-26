package com.yl.teacher.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $USER_NAME on 2016/10/13.
 */
public class ClassCircle {
    /**
     * status : true
     * data : {"list":[{"id":266,"User_id":1412,"School_id":140,"SchoolClass_id":146,"title":"空军建军节","pictureUrls":["http://devimg.lexuetao.com/images/campus/e897121b7828f8aa8679a26437c113f1.png","http://devimg.lexuetao.com/images/campus/bd0c01049ebf2e4f5e54fd719186f9ff.jpeg","http://devimg.lexuetao.com/images/campus/530fcae5970defe8aca8ddaf99a808f6.gif","http://devimg.lexuetao.com/images/campus/cedd8ed0580b77b376f95a92911e57ad.png","http://devimg.lexuetao.com/images/campus/95477d7472329b3d452023157ecf92a4.png","http://devimg.lexuetao.com/images/campus/c36cb2f84d311f52576bc8eb95090da6.jpeg","http://devimg.lexuetao.com/images/campus/a8ee5fe42b251533f1dc41fdec5aaa4a.png","http://devimg.lexuetao.com/images/campus/bbb16904c47fa8639c7947c89fe73636.png","http://devimg.lexuetao.com/images/campus/12569ef80cc00c24d63862243aacb9f0.jpeg"],"createTime":"2016-10-13","realName":"萌萌","praise":0,"praiseStatus":2,"headpicUrl":"http://devimg.lexuetao.com/avatar/1412/100_100"},{"id":97,"User_id":1312,"School_id":140,"SchoolClass_id":-1,"title":"全校班级圈谢谢","pictureUrls":["http://devimg.lexuetao.com/img/campus/a5373af2057ddd84bea2c68a276b7d3a"],"createTime":"2016-06-27","realName":"管理员","praise":2,"praiseStatus":1,"headpicUrl":"http://devimg.lexuetao.com/avatar/1312/100_100"},{"id":95,"User_id":1312,"School_id":140,"SchoolClass_id":-1,"title":"后台发送班级圈 ---范围全校 有图","pictureUrls":["http://devimg.lexuetao.com/img/campus/106d85775a39cb30d1cdbaf54521d9a5"],"createTime":"2016-06-24","realName":"管理员","praise":1,"praiseStatus":1,"headpicUrl":"http://devimg.lexuetao.com/avatar/1312/100_100"}],"count":3}
     * message : 请求成功!
     */
    public boolean status;
    /**
     * list : [{"id":266,"User_id":1412,"School_id":140,"SchoolClass_id":146,"title":"空军建军节","pictureUrls":["http://devimg.lexuetao.com/images/campus/e897121b7828f8aa8679a26437c113f1.png","http://devimg.lexuetao.com/images/campus/bd0c01049ebf2e4f5e54fd719186f9ff.jpeg","http://devimg.lexuetao.com/images/campus/530fcae5970defe8aca8ddaf99a808f6.gif","http://devimg.lexuetao.com/images/campus/cedd8ed0580b77b376f95a92911e57ad.png","http://devimg.lexuetao.com/images/campus/95477d7472329b3d452023157ecf92a4.png","http://devimg.lexuetao.com/images/campus/c36cb2f84d311f52576bc8eb95090da6.jpeg","http://devimg.lexuetao.com/images/campus/a8ee5fe42b251533f1dc41fdec5aaa4a.png","http://devimg.lexuetao.com/images/campus/bbb16904c47fa8639c7947c89fe73636.png","http://devimg.lexuetao.com/images/campus/12569ef80cc00c24d63862243aacb9f0.jpeg"],"createTime":"2016-10-13","realName":"萌萌","praise":0,"praiseStatus":2,"headpicUrl":"http://devimg.lexuetao.com/avatar/1412/100_100"},{"id":97,"User_id":1312,"School_id":140,"SchoolClass_id":-1,"title":"全校班级圈谢谢","pictureUrls":["http://devimg.lexuetao.com/img/campus/a5373af2057ddd84bea2c68a276b7d3a"],"createTime":"2016-06-27","realName":"管理员","praise":2,"praiseStatus":1,"headpicUrl":"http://devimg.lexuetao.com/avatar/1312/100_100"},{"id":95,"User_id":1312,"School_id":140,"SchoolClass_id":-1,"title":"后台发送班级圈 ---范围全校 有图","pictureUrls":["http://devimg.lexuetao.com/img/campus/106d85775a39cb30d1cdbaf54521d9a5"],"createTime":"2016-06-24","realName":"管理员","praise":1,"praiseStatus":1,"headpicUrl":"http://devimg.lexuetao.com/avatar/1312/100_100"}]
     * count : 3
     */
    public DataBean data;
    public String message;

    public static class DataBean {
        public int count;
        /**
         * id : 266
         * User_id : 1412
         * School_id : 140
         * SchoolClass_id : 146
         * title : 空军建军节
         * pictureUrls : ["http://devimg.lexuetao.com/images/campus/e897121b7828f8aa8679a26437c113f1.png","http://devimg.lexuetao.com/images/campus/bd0c01049ebf2e4f5e54fd719186f9ff.jpeg","http://devimg.lexuetao.com/images/campus/530fcae5970defe8aca8ddaf99a808f6.gif","http://devimg.lexuetao.com/images/campus/cedd8ed0580b77b376f95a92911e57ad.png","http://devimg.lexuetao.com/images/campus/95477d7472329b3d452023157ecf92a4.png","http://devimg.lexuetao.com/images/campus/c36cb2f84d311f52576bc8eb95090da6.jpeg","http://devimg.lexuetao.com/images/campus/a8ee5fe42b251533f1dc41fdec5aaa4a.png","http://devimg.lexuetao.com/images/campus/bbb16904c47fa8639c7947c89fe73636.png","http://devimg.lexuetao.com/images/campus/12569ef80cc00c24d63862243aacb9f0.jpeg"]
         * createTime : 2016-10-13
         * realName : 萌萌
         * praise : 0
         * praiseStatus : 2
         * headpicUrl : http://devimg.lexuetao.com/avatar/1412/100_100
         */
        public List<ListBean> list;

        public static class ListBean {
            public int id;
            public int User_id;
            public int School_id;
            public int SchoolClass_id;
            public String title;
            public String createTime;
            public String realName;
            public int praise;
            public int praiseStatus;
            public String headpicUrl;
            public ArrayList<String> pictureUrls;
        }
    }
}
