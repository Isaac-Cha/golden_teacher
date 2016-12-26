package com.yl.teacher.model;

import java.io.Serializable;

public class GroupMemberBean implements Serializable{

	public String id; // 成员ID
	private String userId;
	private String phone;
	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}


}
