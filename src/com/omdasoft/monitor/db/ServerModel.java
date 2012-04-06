package com.omdasoft.monitor.db;

import java.io.Serializable;

public class ServerModel implements Serializable {
	
	private static final long serialVersionUID = 6928050741633921018L;
	
	private long id = -1;
	private String url;
	private int minutes;
	private int isMonitored;
	private String verifyTitle;
	private String checkList;
	
	private String userId;
	private String password;
	
	
	public ServerModel(){
	}
	
	public ServerModel(int id){
		this.id = id;
	}
	
	public ServerModel(String url, int minutes, String verifyTitle){
		this.url = url;
		this.minutes = minutes;
		this.verifyTitle = verifyTitle;
	}
	
	public ServerModel(long id, String url, int minutes, String verifyTitle){
		this.id = id;
		this.url = url;
		this.minutes = minutes;
		this.isMonitored = 1;
		this.verifyTitle = verifyTitle;
	}
	
	public ServerModel(long id, String url, int minutes, int isMonitored, String verifyTitle){
		this.id = id;
		this.url = url;
		this.minutes = minutes;
		this.isMonitored = isMonitored;
		this.verifyTitle = verifyTitle;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getMonitored() {
		return isMonitored;
	}
	public void setMonitored(int isMonitored) {
		this.isMonitored = isMonitored;
	}
	public String getVerifyTitle() {
		return verifyTitle;
	}
	public void setVerifyTitle(String verifyTitle) {
		this.verifyTitle = verifyTitle;
	}
		public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
		public String getPath(){
			return this.id + ".html";
		}

		public boolean isMonitored() {
			return this.isMonitored == 0 ? false:true;
		}

		public String getCheckList() {
			return checkList;
		}

		public void setCheckList(String checkList) {
			this.checkList = checkList;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

}
