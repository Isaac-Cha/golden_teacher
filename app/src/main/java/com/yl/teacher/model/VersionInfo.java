package com.yl.teacher.model;

import org.json.JSONObject;

import java.io.Serializable;

public class VersionInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	public String content;
	public int version;
	public String downUrl;
	public boolean isShow;
	public String sysDate;
	public int count;


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	//强制更新
	public boolean isforce;

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean show) {
		isShow = show;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public boolean isforce() {
		return isforce;
	}

	public void setIsforce(boolean isforce) {
		this.isforce = isforce;
	}

	public VersionInfo() {
		super();

		this.downUrl="";
		this.isforce=false;
	}
	public static VersionInfo getVersionInfoFromJson(JSONObject json) 
	{
		VersionInfo info=new VersionInfo();

		info.isforce=json.optBoolean("forceUpdate");
		info.version=json.optInt("appversion");
		info.downUrl=json.optString("apkUrl");
		info.isShow = json.optBoolean("ispromptUpdate");
		info.content = json.optString("syspromptMsg");
		info.sysDate =json.optString("sysDate");
		info.count = json.optInt("prompNum");
		return info;
		
	}
	public static VersionInfo getNoticeInfoFromJson(JSONObject json)
	{
		VersionInfo info=new VersionInfo();
		info.isShow = json.optBoolean("isshow");
		info.content = json.optString("sysMsg");
		info.sysDate =json.optString("sysDate");
		info.count = json.optInt("prompNum");
		return info;

	}
	@Override
	public String toString() {
		return "VersionInfo";
	}

	@Override
	public boolean equals(Object paramObject) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
