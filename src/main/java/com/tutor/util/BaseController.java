package com.tutor.util;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutor.model.User;

/**
 * 把业务中 常用的 方法和变量写在这里方便复用 减少代码量
 * 
 * @author joe蒋渊
 *
 */
public class BaseController implements Serializable {

	private static final long serialVersionUID = 8244392221127655858L;

	public Logger logger = LoggerFactory.getLogger(this.getClass());// 子类方法能够访问父类的protected作用域成员，不能够访问默认的作用域成员



	/**
	 * 获取登录用户
	 * 
	 * @param request
	 * @return
	 */
	protected User getLoginUser(HttpServletRequest request) {
		User loginUser = (User) request.getSession().getAttribute("user");
		return loginUser;
	}
}
