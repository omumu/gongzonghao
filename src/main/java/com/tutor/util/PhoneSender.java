package com.tutor.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * 
 * @author 蒋渊 一个发短信的工具类
 */

public class PhoneSender {
	private static Logger logger = LoggerFactory.getLogger(PhoneSender.class);
	private static String msg_appKey;
	private static String msg_appSecret;
	private static String msg_signName;
	// 加载类的时候初始化参数
	static {
		msg_appKey = PropertiesUtil.getValue("msg_appKey");
		msg_appSecret = PropertiesUtil.getValue("msg_appSecret");
		msg_signName = PropertiesUtil.getValue("msg_signName");// 签名
	}

	/**
	 * 发送 随机验证码给用户
	 * 
	 * @author 蒋渊
	 * @param msg
	 * @return
	 */

	public static Boolean sendRandomCode(Map<String, String> msg) {
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", msg_appKey,
					msg_appSecret);
			AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
			// 这里填 normal 就可以了
			req.setSmsType("normal");
			// 这里填 签名的名称
			req.setSmsFreeSignName(msg_signName);// 这里解决了 签名中文乱码的问题
			// 这里传入 额外的参数
			req.setSmsParamString("{\"code\":\"" + msg.get("code") + "\",\"product\":\"【校招导师】\"}");
			// 短信 接收电话
			req.setRecNum(msg.get("pnum"));
			// 短信的 模板Id
			req.setSmsTemplateCode("SMS_48595141");
			rsp = client.execute(req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("发送验证码给" + msg.get("pnum") + "失败");
			return false;
		}
		JSONObject jb = new JSONObject();
		jb = JSONObject.parseObject(rsp.getBody());
		if (jb.get("error_response") != null) {
			logger.error("发送验证码给" + msg.get("pnum") + "失败");
			return false;
		}
		logger.info("发送验证码给" + msg.get("pnum") + "成功");
		return true;
	}

	public static void main(String[] args) {
		Map<String, String> msg = new HashMap<>();
		msg.put("code", "123");
		msg.put("pnum", "18908352144");
		System.out.println(sendRandomCode(msg));
	}

}
