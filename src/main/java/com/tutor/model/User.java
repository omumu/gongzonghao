package com.tutor.model;

import java.util.Date;

/**
 * 用户实体类
 * 
 * @author joe蒋渊
 *
 */
public class User {

	private int userId; // 用户id
	private String userName; // 用户姓名
	private int userSex; // 用户性别
	private String userPhone; // 用户电话
	private String userPhoto; // 用户头像链接
	private Date userRegisterTime; // 用户注册时间
	private String userOpenId; // 唯一识别用户身份的标识
	private Date userLastLoginTime; // 用户最后登录时间

	public Date getUserLastLoginTime() {
		return userLastLoginTime;
	}

	public void setUserLastLoginTime(Date userLastLoginTime) {
		this.userLastLoginTime = userLastLoginTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserSex() {
		return userSex;
	}

	public void setUserSex(int userSex) {
		this.userSex = userSex;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public Date getUserRegisterTime() {
		return userRegisterTime;
	}

	public void setUserRegisterTime(Date date) {
		this.userRegisterTime = date;
	}

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", userSex=" + userSex + ", userPhone=" + userPhone
				+ ", userPhoto=" + userPhoto + ", userRegisterTime=" + userRegisterTime + ", userOpenId=" + userOpenId
				+ ", userLastLoginTime=" + userLastLoginTime + "]";
	}

}