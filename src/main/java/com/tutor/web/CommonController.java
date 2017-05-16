package com.tutor.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.tutor.util.BaseController;
import com.tutor.util.StringUtil;
import com.tutor.util.wx.Constants;
import com.tutor.util.wx.SHA1Utils;
import com.tutor.util.wx.WXUtil;

/**
 * 
 * @author joe蒋渊
 *
 */
@Controller
@RequestMapping("api/common")
public class CommonController extends BaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3203049021678555304L;

	/**
	 * 跳转到微信授权页面
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/redirectUrl", method = RequestMethod.GET)
	public void redirectUrl(HttpServletRequest request, HttpServletResponse response) {
		logger.info("invoke---------------------/api/common/redirectUrl");
		try {
			response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.appId
					+ "&redirect_uri=" + Constants.redirectIndexUri
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
		} catch (IOException e) {
			logger.error("跳转微信授权页面失败", e);
		}
	}

	@RequestMapping(value = "/orderList", method = RequestMethod.GET)
	public void orderList(HttpServletRequest request, HttpServletResponse response) {
		logger.info("invoke---------------------/api/common/orderList");
		try {
			response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.appId
					+ "&redirect_uri=" + Constants.redirectOrderListUri
					+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("跳转微信订单列表页面失败", e);
		}
	}

	@RequestMapping(value = "/getJSSign", method = RequestMethod.POST)
	public void getJSSign(@RequestParam(value = "url", required = true) String url,HttpServletResponse response) {
		logger.info("invoke---------->url=" + url);
		// 获取票据
		String jsTicket = WXUtil.getJsApiTicket();
		if (jsTicket == null) {
			try {
				response.getWriter().println("{\"code\":-4,\"msg\":\"未知错误\",\"data\":\"\"}");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 获得当前时间戳
		long curTime = System.currentTimeMillis() / 1000;
		// 获取随机值
		String randStr = StringUtil.getRandomString(32);
		String signatureOriStr = "jsapi_ticket=" + jsTicket + "&noncestr=" + randStr + "&timestamp=" + curTime + "&url="
				+ url;
		SHA1Utils sha = new SHA1Utils();
		String signature = sha.getDigestOfString(signatureOriStr.getBytes());

		JSONObject result = new JSONObject();
		result.put("appId", Constants.appId);
		result.put("timestamp", curTime + "");
		result.put("noncestr", randStr);
		result.put("signature", signature);
		
		try {
			response.getWriter().println(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
