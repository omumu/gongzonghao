package com.tutor.util.wx;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSONObject;
import com.tutor.cache.RedisCache;
import com.tutor.util.wx.http.HttpClientConnectionManager;

/**
 * 
 * @author joe蒋渊
 *
 */
@Component
public class WXUtil {
	private static Logger logger = LoggerFactory.getLogger(WXUtil.class);

	private static RedisCache redisCache;

	/**
	 * 微信基础服务 这个方法暂时没用
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param token
	 * @return
	 */
	/*
	 * public static boolean checkSignature(String signature, String timestamp,
	 * String nonce, String token) { ArrayList<String> strArray = new
	 * ArrayList<>(); strArray.add(timestamp); strArray.add(nonce);
	 * strArray.add(token); Collections.sort(strArray); String totalString =
	 * strArray.get(0) + strArray.get(1) + strArray.get(2);// 代码复用性不强 SHA1Utils
	 * sha = new SHA1Utils(); String encodedStr =
	 * sha.getDigestOfString(totalString.getBytes()); if (encodedStr != null &&
	 * encodedStr.equals(signature)) { return true; } else { return false; } }
	 */

	public RedisCache getRedisCache() {
		return redisCache;
	}

	@Autowired
	public void setRedisCache(RedisCache redisCache) {
		WXUtil.redisCache = redisCache;
	}

	/**
	 * 这里 获取token 多个线程改变一个 共有变量 Constants.asscessMap 线程不安全 加了一个同步关键字
	 * 
	 * @param code
	 * @return
	 */
	public synchronized static String getAccessToken(String code) {
		if (Constants.asscessMap.get("accessToken") != null) {
			logger.info("此时环境中已经有access_token");
			Long curTime = System.currentTimeMillis();
			if ((curTime - Long.parseLong(Constants.asscessMap.get("saveTime"))) / 1000 > 6000) {
				// 已经 6000秒了 接近超时了 重新获取
				logger.info("access_token接近过期 ------>重新获取");
				String accessToken = getAccessTokenAndOpenId(code).getString("access_token");
				Constants.asscessMap.put("accessToken", accessToken);
				Constants.asscessMap.put("saveTime", System.currentTimeMillis() + "");
				return accessToken;
			} else {
				logger.info("access_token暂未过期 ------>不用重新获取");
				return Constants.asscessMap.get("accessToken");
			}
		} else {
			logger.info("此时环境中没有access_token---->去获取");
			String accessToken = getAccessTokenAndOpenId(code).getString("access_token");
			logger.info("获取的access_token为：" + accessToken);
			Constants.asscessMap.put("accessToken", accessToken);
			Constants.asscessMap.put("saveTime", System.currentTimeMillis() + "");
			return accessToken;
		}

	}

	/**
	 * 获取token的 实际方法 不要直接调用这个方法
	 * 
	 * @param code
	 * @return
	 */
	private static JSONObject getAccessTokenAndOpenId(String code) {
		Map<String, String> param = new HashMap<>();
		param.put("appid", Constants.appId);
		param.put("code", code);
		param.put("secret", Constants.appSecret);
		param.put("grant_type", "authorization_code");
		JSONObject jb = null;
		try {
			jb = HttpRequest.doGETReturnJSON("https://api.weixin.qq.com/sns/oauth2/access_token", param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jb;

		/*
		 * { "access_token":"ACCESS_TOKEN", "expires_in":7200,
		 * "refresh_token":"REFRESH_TOKEN", "openid":"OPENID", "scope":"SCOPE" }
		 */
		// 返回的json 如下
	}

	public synchronized static String getUserOpenId(String code) {
		JSONObject jb = getAccessTokenAndOpenId(code);
		String openId = jb.getString("openid");
		Constants.asscessMap.put("accessToken", jb.getString("access_token"));
		logger.info("获取openId时获取的access_token:" + jb.getString("access_token"));
		Constants.asscessMap.put("saveTime", System.currentTimeMillis() + "");
		return openId;
	}

	/**
	 * 通过 openId 去获取 用户信息 传 code 的原因是 以防 access_token 过期
	 * 
	 * @param openId
	 * @param code
	 * @return
	 */
	public static JSONObject getUserInfo(String openId, String code) {
		String accessToken = getAccessToken(code);
		Map<String, String> param = new HashMap<>();
		param.put("access_token", accessToken);
		logger.info("开始去获取 用户微信的信息 openid:" + openId + "access_token" + accessToken);
		param.put("openid", openId);
		param.put("lang", "zh_CN");
		JSONObject jb = null;
		try {
			jb = HttpRequest.doGETReturnJSON("https://api.weixin.qq.com/sns/userinfo", param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jb;
	}

	/**
	 * 向微信服务器发起请求
	 * 
	 * @param url
	 * @param xmlParam
	 * @return
	 */
	public static Map<String, Object> sendQuery(String url, String xmlParam) {

		Map<String, Object> result = null;

		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		// 请求统一下单地址
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);

		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = client.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (jsonStr.indexOf("FAIL") != -1) {
				logger.warn("请求统一下单接口失败--->返回的xml是:" + jsonStr);
				return null;
			}
			result = XmlUtils.doXMLParse(jsonStr);
		} catch (Exception e) {
			logger.error("请求统一下单接口发送错误", e);
		}
		return result;
	}

	/**
	 * description: 解析微信通知xml
	 * 
	 * @param xml
	 * @return
	 * @author ex_yangxiaoyi
	 * @see
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map parseXmlToList2(String xml) {
		Map retMap = new HashMap();
		try {
			StringReader read = new StringReader(xml);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(read);
			// 创建一个新的SAXBuilder
			SAXBuilder sb = new SAXBuilder();
			// 通过输入源构造一个Document
			Document doc = (Document) sb.build(source);
			Element root = doc.getRootElement();// 指向根节点
			List<Element> es = root.getChildren();
			if (es != null && es.size() != 0) {
				for (Element element : es) {
					retMap.put(element.getName(), element.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public static String getJsApiTicket() {
		String jsTicket = redisCache.getCache("jsTicket", String.class);
		if (jsTicket == null) {
			logger.info("缓存中没有 去获取");
			Map<String, String> param = new HashMap<>();
			param.put("access_token", getAccessToken());
			param.put("type", "jsapi");
			JSONObject jb = null;
			try {
				jb = HttpRequest.doGETReturnJSON("https://api.weixin.qq.com/cgi-bin/ticket/getticket", param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("请求jsticket错误", e);
				return null;
			}
			// if (jb.get("errmsg") == null || !("ok".equals(jb.get("errmsg"))))
			// {
			// logger.error("获取jsticket发生错误！！");
			// return null;
			// }
			logger.info("获取到的jsTickes>>>>>>>>>" + jb.getString("ticket"));
			redisCache.putCacheWithExpireTime("jsTicket", jb.getString("ticket"), 7000);
			return jb.getString("ticket");
		} else {
			logger.info("缓存中存在---->直接使用");
			return jsTicket;
		}
	}

	private static JSONObject getAccessTokenPrivate() {

		Map<String, String> param = new HashMap<>();
		param.put("appid", Constants.appId);
		param.put("secret", Constants.appSecret);
		param.put("grant_type", "client_credential");
		JSONObject jb = null;
		try {
			jb = HttpRequest.doGETReturnJSON("https://api.weixin.qq.com/cgi-bin/token", param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jb;

	}

	/**
	 * 并发操作
	 * 
	 * @return
	 */
	public static synchronized String getAccessToken() {
		if (Constants.asscessMap.get("accessToken") != null) {
			logger.info("此时环境中已经有access_token");
			Long curTime = System.currentTimeMillis();
			if ((curTime - Long.parseLong(Constants.asscessMap.get("saveTime"))) / 1000 > 6000) {
				// 已经 6000秒了 接近超时了 重新获取
				logger.info("access_token接近过期 ------>重新获取");
				String accessToken = getAccessTokenPrivate().getString("access_token");
				logger.info("获取的access_token为：" + accessToken);
				Constants.asscessMap.put("accessToken", accessToken);
				Constants.asscessMap.put("saveTime", System.currentTimeMillis() + "");
				return accessToken;
			} else {
				logger.info("access_token暂未过期 ------>不用重新获取");
				return Constants.asscessMap.get("accessToken");
			}
		} else {
			logger.info("此时环境中没有access_token---->去获取");
			String accessToken = getAccessTokenPrivate().getString("access_token");
			logger.info("获取的access_token为：" + accessToken);
			Constants.asscessMap.put("accessToken", accessToken);
			Constants.asscessMap.put("saveTime", System.currentTimeMillis() + "");
			return accessToken;
		}
	}

	public static void main(String[] args) {
		// System.out.println(getJsApiTicket());
		System.out.println(getAccessTokenPrivate());
	}
}
