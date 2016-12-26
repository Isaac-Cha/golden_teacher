package com.yl.teacher.util;

/**
 * Created by yiban on 2016/5/3.
 */
public class Static {

    //课程表
    public static final String TAG_SCHEDULE = "ClassSchedule";
    //学生点滴
    public static final String TAG_STUDENT = "StudentLog";
    //请求
    public static final String TAG_QUEST = "ClassMemeberRequest";
    //公告
    public static final String TAG_ANNOUNCE = "Announcement";
    //投票
    public static final String TAG_VOTE = "Vote";
    //作业
    public static final String TAG_HOMEWORK = "Homework";
    //班级空间
    public static final String TAG_ZONE = "ClassZone";
    // 讨论组
    public static final String TAG_IM = "ClassIM";

    public static final int KEY_CLASSSCHEDULE = 1; // 课表
    public static final int KEY_STUDENTLOG = 2; // 学生点滴
    public static final int KEY_CLASSMEMEBERREQUEST = 3; // 请求
    public static final int KEY_ANNOUNCEMENT = 4; // 公告
    public static final int KEY_VOTE = 5; // 投票
    public static final int KEY_HOMEWORK = 6; // 作业
    public static final int KEY_CLASSZONE = 7; // 班级空间
    public static final int KEY_PERMIT = 8; // 待审批
    public static final int KEY_IM = 9; // 讨论组

    public static final String APP_KEY = "key=6ec4ec85be8dc703fdc88968967f0678";

    //刷新列表数据
    public final static int LIST_REFRESH = 0;

    //列表加载更多数据
    public final static int LIST_MORE = 1;

    public static final int EVENTBUS_TYPE_VOTE = 1;
    public static final int EVENTBUS_TYPE_BROAD = 2;
    public static final int EVENTBUS_TYPE_SYLLABUS = 3;
    public static final int EVENTBUS_TYPE_SEND_BULLETIN_NOTIC = 4;
    public static final int EVENTBUS_TYPE_SEND_BULLETIN_ACTION = 5;
    public static final int EVENTBUS_TYPE_SEND_BULLETIN = 6;
    public static final int EVENTBUS_TYPE_HOMEWORK = 7;
    public static final int EVENTBUS_TYPE_VOTELIST = 8;
    public static final int EVENTBUS_TYPE_STATUS = 9;
    public static final int EVENTBUS_TYPE_QUEST = 10;
    public static final int EVENTBUS_TYPE_SOCKET = 11;
    public static final int EVENTBUS_TYPE_BULLETIN_GO_HOME = 12;

    //开发服图片地址
    public final static String LOCAL_IMAGE_IP = "http://devimg.lexuetao.com";
    //正服图片地址
    public final static String NORMAL_IMAGE_IP = "http://image.lexuetao.com";
    // 测试服图片地址
    public final static String TEST_IMAGE_IP = "http://image.lexuetao.cc";

    //开发服
    public final static String LOCAL_SERVER_IP = "http://devcampus.lexuetao.com";
    //正服
    public final static String NORMAL_SERVER_IP = "http://campus.lexuetao.com";
    // 测试服
    public final static String TEST_SERVER_IP = "http://campus.lexuetao.cc";

    // socket地址
    public final static String LOCAL_CHAT_SERVER = "http://120.55.166.147:3001";
    // 正式socket
    public final static String NORMAL_CHAT_SERVER = "http://139.196.173.251:3001";
    // 测试服socket
    public final static String TEST_CHAT_SERVER = "http://192.168.200.202:3001";

    //图片地址（切换正服时候要注意改过来）
    public final static String IMAGE_IP = NORMAL_IMAGE_IP;
    //服务端接口url（切换正服时候要注意改过来）
    public final static String URL_SERVER = NORMAL_SERVER_IP;
    // socket
    public final static String CHAT_SERVER = NORMAL_CHAT_SERVER;


    public static final String APPLICATION_NAME = "myApp";

    //单次最多发送图片数
    public static int MAX_IMAGE_SIZE = 9;
    //首选项:临时图片
    public static final String PREF_TEMP_IMAGES = "pref_temp_images";

    // websocket 事件名
    public static final String SOCKET_LOGIN = "campusLogin";
    public static final String SOCKET_LOGIN_RETRUN = "loginReturn";
    public static final String SOCKET_MODULE = "getModuleDots";
    public static final String SOCKET_MODULE_RETRUN = "moduleDotsReturn";
    //    public static final String SOCKET_ADD_DOT = "campusAddDot";
    public static final String SOCKET_ADD_DOT_RETRUN = "addDots";
    public static final String SOCKET_DEL_DOT = "campusDelDot";
    public static final String SOCKET_DEL_DOT_RETRUN = "delDotsReturn";

    public static final String SOURCE = "lxt-app-android";

    // 小米推送
    public static final String ML_MI_APP_ID = "2882303761517487204";
    public static final String ML_MI_APP_KEY = "5461748752204";
    // 华为推送
    public static final String ML_HUAWEI_APP_ID = "10601238";
    // 登录类型
    public static final String LOGIN_TYPE = "login_type";
    // 帐号已经被移除
    public static final int ACCOUNT_REMOVED = 110;
    // 帐号在其它设备登录
    public static final int ACCOUNT_CONFLICT = 119;
    // 环信信息存储
    public static final String EM_SP_NAME = "EMMobInfo";
    public static final String EM_USERNAME = "em_username";
    public static final String EM_PASSWORD="em_password";
    public static final String EM_NICKNAME = "em_nickname";
    public static final String EM_AVATAR_URL = "em_avatar_url";

}
