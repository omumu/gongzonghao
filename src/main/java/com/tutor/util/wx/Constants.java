package com.tutor.util.wx;

import java.util.HashMap;

import com.tutor.util.PropertiesUtil;

/**
 * 微信中的配置项的常量 其实也可以通过PropertiesUitl获取 微信配置值 但是 那种方法需要写 配置变量名 key 写的越多 出错的几率就越大
 * 写在这里 直接调用 出错的几率小
 * 
 * @author joe蒋渊
 */

public class Constants {

	public static String appId;
	public static String appSecret;
	public static String redirectIndexUri;
	public static String token;
	public static String unifiedorder;
	public static String key;
	public static String mchId;// 商户id
	public static String redirectOrderListUri;
	public static String notifyUrl;
	// 加载类的时候初始化参数
	static {
		appId = PropertiesUtil.getValue("appId");
		appSecret = PropertiesUtil.getValue("appSecret");
		redirectIndexUri = PropertiesUtil.getValue("redirectIndexUri");
		token = PropertiesUtil.getValue("token");
		unifiedorder = PropertiesUtil.getValue("unifiedorder");
		key = PropertiesUtil.getValue("key");
		mchId = PropertiesUtil.getValue("mch_id");
		redirectOrderListUri = PropertiesUtil.getValue("redirectOrderListUri");
		notifyUrl=PropertiesUtil.getValue("notifyUrl");
	}
	/**
	 * 用来存取 accessToken 和 saveTime 字段
	 */

	public static HashMap<String, String> asscessMap = new HashMap<>();// accessToken
																		// saveTime
}
