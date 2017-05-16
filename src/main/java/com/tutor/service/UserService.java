package com.tutor.service;

import com.tutor.model.User;

public interface UserService {
	public User getUserInfo(String openId);

	/**
	 * 1.去得到openId 2.判断是否是新用户 3.是 注册之后 再登录 4.不是 直接登录
	 * 
	 * @param code
	 * @return
	 */
	public User register(String code) throws Exception;

	/**
	 * 用户登录
	 * 
	 * @param openId
	 * @return
	 */
	public User login(String openId);

	/**
	 * 判断用户是否绑定过电话号码
	 * 
	 * @param userId
	 * @return
	 */
	String isBindPhone(String userOpenId);

	/**
	 * 去给用户发送短信验证码 （未使用的mq的做法 同步发短信 以后待改善）
	 * 
	 * @param phone
	 * @param openId
	 * @return
	 */
	Boolean sendCode(String phone, String code);

	/**
	 * 去给用户绑定电话号码
	 * 
	 * @param phone
	 * @param openId
	 * @return
	 */
	Boolean toBindPhone(String phone, String openId);
}
