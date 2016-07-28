package com.example.hdj008k_location.obj;

public class AdStatus {
	
	private int id;

	private String adpath;
	private String packageName;
	private int status;//0,代表创建    1，代表安装完     2，代表卸载完成；
	private long time;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAdpath() {
		return adpath;
	}

	public void setAdpath(String adpath) {
		this.adpath = adpath;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
