package com.example.hdj008k_location.obj;

import cn.bmob.v3.BmobObject;

public class MobileData extends BmobObject {

	private String num;
	private String imei;
	private Integer runTime;
	private Boolean isAllowRun;

	public Boolean getIsAllowRun() {
		return isAllowRun;
	}

	public void setIsAllowRun(Boolean isAllowRun) {
		this.isAllowRun = isAllowRun;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Integer getRunTime() {
		return runTime;
	}

	public void setRunTime(Integer runTime) {
		this.runTime = runTime;
	}

}
