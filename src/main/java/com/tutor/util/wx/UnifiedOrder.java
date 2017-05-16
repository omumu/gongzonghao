package com.tutor.util.wx;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutor.util.StringUtil;

public class UnifiedOrder {
	private static Logger logger = LoggerFactory.getLogger(UnifiedOrder.class);

	/**
	 * 调用微信统一下单接口
	 * 
	 * @param type
	 */
	public static Map<String, Object> unifiedOrder(WxPay pay) {
		Map<String, Object> result = new HashMap<String, Object>();
		// 订单号
		String orderId = pay.getOrderId();
		// 附加数据 原样返回
		String attach = "微信支付";
		// 订单生成的机器 IP
		String spbill_create_ip = pay.getSpbillCreateIp();
		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notifyUrl = Constants.notifyUrl;
		// 支付方式
		String tradeType = "JSAPI";

		// 随机字符串
		String nonceStr = StringUtil.getRandomString(32);
		// 商品描述根据情况修改
		String body = pay.getBody();
		// 商户订单号
		String outTradeNo = orderId;
		// openId
		String openId = pay.getOpenId();
		String appid = null;
		String appSecret = null;
		String key = null;
		String mchId = null;

		// 是微信H5支付
		appSecret = Constants.appSecret;
		appid = Constants.appId;
		key = Constants.key;
		// 商户号
		mchId = Constants.mchId;

		// 简历排序map，以便生成签名
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mchId);
		packageParams.put("nonce_str", nonceStr);
		packageParams.put("body", body);
		packageParams.put("attach", attach);
		packageParams.put("out_trade_no", outTradeNo);
		packageParams.put("total_fee", String.valueOf(pay.getTotalFee()));
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notifyUrl);
		packageParams.put("openid", openId);
		packageParams.put("trade_type", tradeType);

		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(appid, appSecret, key);

		// 生成签名
		String sign = reqHandler.createSign(packageParams);
		// 合成XML格式
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>" + mchId + "</mch_id>" + "<nonce_str>"
				+ nonceStr + "</nonce_str>" + "<sign>" + sign + "</sign>" + "<body><![CDATA[" + body + "]]></body>"
				+ "<out_trade_no>" + outTradeNo + "</out_trade_no>" + "<attach>" + attach + "</attach>" + "<openid>"
				+ openId + "</openid>" + "<total_fee>" + pay.getTotalFee() + "</total_fee>" + "<spbill_create_ip>"
				+ spbill_create_ip + "</spbill_create_ip>" + "<notify_url>" + notifyUrl + "</notify_url>"
				+ "<trade_type>" + tradeType + "</trade_type>" + "</xml>";
		logger.info("微信统一接口下单--------拼接的xml为:" + xml);
		// 向微信发起统一下单请求
		Map<String, Object> map = WXUtil.sendQuery(Constants.unifiedorder, xml);
		logger.info("请求下单接口返回的结果--->" + map);
		if (map != null) {
			// 解析返回
			String returnCode = map.get("return_code").toString();
			String returnMsg = map.get("return_msg").toString();
			result.put("return_code", returnCode);
			result.put("return_msg", returnMsg);
			if (returnCode.equals("SUCCESS")) {
				String resultCode = map.get("result_code").toString();
				result.put("result_code", resultCode);
				if (resultCode.equals("SUCCESS")) {
					// 获取到prepay_id
					result.put("prepay_id", map.get("prepay_id").toString());
				}
			}
		}
		// 生成sign
		SortedMap<String, String> signParams = new TreeMap<String, String>();

		// 系统时间戳，精确到毫秒
		String timeStamp = String.valueOf(System.currentTimeMillis());
		// 转换成秒
		timeStamp = timeStamp.substring(0, timeStamp.length() - 3);
		result.put("time_stamp", timeStamp);
		result.put("appId", appid);
		result.put("mch_id", mchId);
		result.put("trade_type", tradeType);
		result.put("signType", "MD5");
		result.put("nonce_str", nonceStr);

		// 打包生成pay_sign的参数
		signParams.put("appId", appid);
		signParams.put("timeStamp", timeStamp);
		signParams.put("nonceStr", nonceStr);
		signParams.put("package", "prepay_id=" + map.get("prepay_id").toString());
		signParams.put("signType", "MD5");
		String pay_sign = reqHandler.createSign(signParams);
		result.put("pay_sign", pay_sign);
		return result;
	}
}
