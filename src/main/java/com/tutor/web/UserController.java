package com.tutor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tutor.cache.RedisCache;
import com.tutor.model.User;
import com.tutor.service.UserService;
import com.tutor.util.BaseController;
import com.tutor.util.CommonUtil;
import com.tutor.util.EnumUtil;
import com.tutor.util.StringUtil;

/**
 * 
 * @author 蒋渊 joe
 *
 */

@Controller
@RequestMapping("api/user")
public class UserController extends BaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2313977613293171452L;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisCache redisCache;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject register(@RequestParam(value = "code", required = true) String code, HttpSession session) {
		logger.info("invoke------------------>register?code=" + code);
		if (StringUtil.isempty(code)) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		User user = null;
		try {
			user = userService.register(code);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("注册用户发送错误", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "db error", null);
		}
		session.setAttribute("user", user);
		return CommonUtil.constructResponse(EnumUtil.OK, "注册成功 并 登录成功", user);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject login(@RequestParam(value = "openId", required = true) String openId, HttpSession session) {
		logger.info("invoke------------->login?openId=" + openId);
		// 直接登录
		// 登录失败的话 返回前端去注册
		// 登陆成功 返回前端 code 1
		if (StringUtil.isempty(openId)) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		User user = null;
		try {
			user = userService.login(openId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("user login error", e);
			CommonUtil.constructResponse(EnumUtil.DB_ERROR, "db error", null);
		}
		if (user == null) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "登录失败,请尝试注册", null);
		}
		session.setAttribute("user", user);
		return CommonUtil.constructResponse(EnumUtil.OK, "登录成功", user);
	}

	/**
	 * 验证用户是否绑定电话号码
	 * 
	 * @param request
	 * @param session
	 * @return
	 */

	@RequestMapping(value = "isBindPhone", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject isBindPhone(HttpServletRequest request, HttpSession session) {
		logger.info("invoke-----------------isBindPhone");
		String phone = null;
		String phoneToken = null;
		try {
			phone = userService.isBindPhone(getLoginUser(request).getUserOpenId());
			if ("notBind".equals(phone)) {
				phoneToken = StringUtil.getRandomString(6);
				logger.info("产生发送短信的凭证：" + phoneToken);
				// 放置 发短信的凭证
				session.setAttribute("phoneToken", phoneToken);
				return CommonUtil.constructResponse(22, "未绑定", phoneToken);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("验证用户是否绑定电话号码发生错误", e);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "ok", phone);
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param phone
	 * @param phoneToken
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "phoneCode", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject bindPhone(@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "phoneToken", required = true) String phoneToken, HttpSession session,
			HttpServletRequest request) {
		logger.info("invoke--------------------bind?phone" + phone + "&phoneToken=" + phoneToken);
		String pToken = null;

		Object o = session.getAttribute("phoneToken");
		if (o == null) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		} else {
			pToken = o.toString();
		}

		if (StringUtil.isempty(phone) || !(StringUtil.isMobileNO(phone)) || StringUtil.isempty(phoneToken)
				|| StringUtil.isempty(pToken) || !(pToken.equals(phoneToken))) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		// 这里的代码写的 稀烂 后来人 注意修改
		String openId = getLoginUser(request).getUserOpenId();
		// 短信接口防止被刷操作
		String redisFlag = redisCache.getCache(openId, String.class);
		// System.out.println(redisFlag);
		if (redisFlag != null && Integer.parseInt(redisFlag) >= 3) {
			return CommonUtil.constructResponse(110, "请稍后再试", null);
		}

		String code = StringUtil.getRandomString(4);
		Boolean flag = null;
		try {
			flag = userService.sendCode(phone, code);
			// flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("发短信未知错误", e);
			return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, null, null);
		}
		if (flag) {
			session.setAttribute("code", code);
			session.setAttribute("phone", phone);
			// 发送完移除 phoneToken
			session.removeAttribute("phoneToken");

			// 在 redis 中存放 发送的次数
			String sendTime = redisCache.getCache(openId, String.class);
			// System.out.println(sendTime);
			if (sendTime == null) {
				redisCache.putCacheWithExpireTime(openId, "1", 3600);// 缓存一个小时
			} else {
				String time = Integer.parseInt(sendTime) + 1 + "";
				redisCache.putCacheWithExpireTime(openId, time, 3600); // 发送次数加一
			}

			return CommonUtil.constructResponse(EnumUtil.OK, "发送成功", null);
		} else {
			return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, "未知错误", null);
		}
	}

	@RequestMapping(value = "bind", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject bind(@RequestParam(value = "code", required = true) String code, HttpSession session,
			HttpServletRequest request) {
		logger.info("invoke-----------------bind?code=" + code);
		// 这里的代码写的 稀烂 后来人 注意修改
		String sessionCode = null;

		String phone = null;
		try {
			sessionCode = session.getAttribute("code").toString();// 获取session中的code
			phone = session.getAttribute("phone").toString();
		} catch (Exception e) {
			// TODO: handle exception
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}

		if (StringUtil.isempty(phone) || StringUtil.isempty(sessionCode)) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "验证码过期", null);
		}
		if (StringUtil.isempty(code) || !(code.equals(sessionCode))) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}

		Boolean flag = null;
		User user = getLoginUser(request);
		try {
			flag = userService.toBindPhone(phone, user.getUserOpenId());
		} catch (Exception e) {
			// TODO: handle exception
			CommonUtil.constructResponse(EnumUtil.DB_ERROR, "db error", null);
		}
		if (flag) {
			// 用户 绑定 电话号码后 session 中的用户 的电话 设置
			user.setUserPhone(phone);
			// session 移除code 和电话号码
			session.removeAttribute("code");
			session.removeAttribute("phone");
			return CommonUtil.constructResponse(EnumUtil.OK, "ok", null);
		}
		return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, "未知错误", null);

	}

}
