package com.tutor.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tutor.dao.UserDao;
import com.tutor.model.User;
import com.tutor.service.UserService;
import com.tutor.util.PhoneSender;
import com.tutor.util.StringUtil;
import com.tutor.util.wx.WXUtil;

@Service("userService")
public class UserServiceImpl implements UserService {
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserDao userDao;

	@Override
	public User getUserInfo(String openId) {
		return userDao.getUserInfo(openId);
	}

	@Override
	public User register(String code) throws Exception {
		// TODO Auto-generated method stub
		String openId = WXUtil.getUserOpenId(code);
		logger.info("获取的openId:" + openId);
		User user = userDao.getUserInfo(openId);
		if (user == null) {
			// 注册用户
			logger.info("用户不存在---->开始注册");
			JSONObject userObject = WXUtil.getUserInfo(openId, code);
			logger.info("获取到的用户信息是：" + userObject);
			User registerUser = new User();
			registerUser.setUserName(userObject.getString("nickname"));
			
			logger.info("获取到的用户名为：--->"+userObject.getString("nickname"));
			
			registerUser.setUserOpenId(openId);
			registerUser.setUserPhoto(userObject.getString("headimgurl"));
			registerUser.setUserSex(Integer.parseInt(userObject.getString("sex")));
			int addResult = userDao.addUser(registerUser);
			if (addResult <= 0) {
				try {
					throw new Exception("插入注册用户失败");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return registerUser;
		} else {
			return user;
		}

	}

	@Override
	public User login(String openId) {
		// TODO Auto-generated method stub
		return userDao.getUserInfo(openId);
	}

	@Override
	public String isBindPhone(String userOpenId) {
		// TODO Auto-generated method stub
		String phone = userDao.getPhoneByOpenId(userOpenId);
		if (phone == null || "".equals(phone) || !(StringUtil.isMobileNO(phone))) {
			return "notBind";
		} else {
			return phone;
		}
	}

	@Override
	public Boolean sendCode(String phone, String code) {
		// TODO Auto-generated method stub
		Map<String, String> msg = new HashMap<>();
		msg.put("pnum", phone);
		msg.put("code", code);
		return PhoneSender.sendRandomCode(msg);
	}

	@Override
	public Boolean toBindPhone(String phone, String openId) {
		// TODO Auto-generated method stub
		int result = userDao.updatePhoneByOpenId(openId, phone);
		if (result > 0) {
			return true;
		}
		return false;
	}

}
