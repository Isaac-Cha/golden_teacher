package com.yl.teacher.model;

import android.content.SharedPreferences;

import com.yl.teacher.global.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiban on 2016/4/13.
 */
public class User implements Serializable {

    public static User CurrentUser = null;
    private int id;

    private String email;

    private String phone;

    private String source;

    private String nickName;

    private String realName;

    private int sex;

    private String inviteCode;

    private String schoolId;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSelfIntro() {
        return selfIntro;
    }

    public void setSelfIntro(String selfIntro) {
        this.selfIntro = selfIntro;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String selfIntro;

    private String kind;

    private String regTime;

    private String provinceId;

    private String cityId;

    private String districtId;

    private String token;

    private String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public static List<User> getUserListFromJsonObj(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<User> list = new ArrayList<User>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(getUserFromJsonObj(jsonArray.optJSONObject(i)));
        }
        return list;
    }

    // 用户的环信信息
    public String hxId;
    // 环信密码
    public String hxPwd;
    // 头像url
    public String hxAvatar;
    // 昵称
    public String hxNickname;

    public static User getUserFromJsonObj(JSONObject jsonObj) {
        User user = new User();
        user.id = jsonObj.optInt("id");
        user.email = jsonObj.optString("email");
        user.phone = jsonObj.optString("phone");
        user.source = jsonObj.optString("source");
        user.nickName = jsonObj.optString("nickName");
        user.realName = jsonObj.optString("realName");
        user.sex = jsonObj.optInt("sex");
        user.selfIntro = jsonObj.optString("selfIntro");
        user.kind = jsonObj.optString("kind");
        user.regTime = jsonObj.optString("regTime");
        user.provinceId = jsonObj.optString("Region_provinceId");
        user.cityId = jsonObj.optString("Region_cityId");
        user.districtId = jsonObj.optString("Region_districtId");
        user.token = jsonObj.optString("token");
        user.district = jsonObj.optString("Region_district");
        user.inviteCode = jsonObj.optString("inviteCode");
        user.schoolId = jsonObj.optString("School_id");
        JSONObject hxUserInfo = jsonObj.optJSONObject("HxUserInfo");
        if (hxUserInfo != null) {
            user.hxId = hxUserInfo.optString("username");
            user.hxPwd = hxUserInfo.optString("password");
            user.hxAvatar = hxUserInfo.optString("avatar");
            user.hxNickname = hxUserInfo.optString("nickname");
        }

        return user;
    }

    public synchronized static User getCurrentUser() {
        if (CurrentUser == null) {
            CurrentUser = new User();
        }
        return CurrentUser;
    }

    public synchronized static void setCurrentUser(User user) {
        if (CurrentUser == null) {
            CurrentUser = new User();
        }
        CurrentUser = user;

        SharedPreferences.Editor editor = MyApplication.getInstance().getShareUser().edit();
        editor.putInt("id", user.getId());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.putString("source", user.getSource());
        editor.putString("nickName", user.getNickName());
        editor.putString("realName", user.getRealName());
        editor.putInt("sex", user.getSex());
        editor.putString("selfIntro", user.getSelfIntro());
        editor.putString("kind", user.getKind());
        editor.putString("regTime", user.getRegTime());
        editor.putString("provinceId", user.getProvinceId());
        editor.putString("cityId", user.getCityId());
        editor.putString("districtId", user.getDistrictId());
        editor.putString("token", user.getToken());
        editor.putString("district", user.getDistrict());
        editor.commit();

    }
}
