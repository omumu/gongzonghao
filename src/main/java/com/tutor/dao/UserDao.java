package com.tutor.dao;

import com.tutor.model.User;

public interface UserDao {
	/**
	 * 【订单】获取用户信息
	 * 
	 * @param openId
	 * @return
	 */
	User getUserInfo(String openId);

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return
	 */
	int addUser(User user);

	/**
	 * 通过openId 获取 电话号码
	 * 
	 * @param openId
	 * @return
	 */
	String getPhoneByOpenId(String openId);

	/**
	 * 通过openid 修改电话号码
	 * 
	 * @param openId
	 * @param phone
	 * @return
	 */
	int updatePhoneByOpenId(String openId, String phone);
}
