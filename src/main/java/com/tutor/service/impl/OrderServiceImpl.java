package com.tutor.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tutor.dao.OrderDao;
import com.tutor.dao.TutorDao;
import com.tutor.mq.Producer;
import com.tutor.service.OrderService;
import com.tutor.util.StringUtil;
import com.tutor.util.wx.UnifiedOrder;
import com.tutor.util.wx.WXUtil;
import com.tutor.util.wx.WxPay;
import com.tutor.util.wx.WxPayResult;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private TutorDao tutorDao;
	// 注入邮件发送 生产者
	@Autowired
	private Producer producer;

	@Override
	public String SelectuserId(String openId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getOrderList(String openId, int page, int state) {
		// TODO Auto-generated method stub
		int start = (page - 1) * 5;

		return orderDao.getOrderList(openId, start, state);
	}

	@Override
	public Map<String, Object> insertOrder(String orderId, String userOpenId, int tutorId, int period, String ip) {
		// TODO Auto-generated method stub

		Map<String, Object> tutorInfo = tutorDao.getTutorInfoById(tutorId);
		if (tutorInfo == null) {
			logger.error("下单的咨询家不存在--------->下单失败---->openId=" + userOpenId + "&tutorId=" + tutorId);
			return null;
		}
		// 精确计算
		BigDecimal price = new BigDecimal(tutorInfo.get("price").toString());
		BigDecimal multResult = price.multiply(new BigDecimal(period));
		// 保留 两位数操作 并且向上舍入 例如 6.333 舍入为 6.34
		BigDecimal result = multResult.setScale(2, BigDecimal.ROUND_CEILING);

		BigDecimal fenResult = result.multiply(new BigDecimal(100));// 将其转换为分
		String totalFee = fenResult.setScale(0, BigDecimal.ROUND_UP).toString();
		WxPay wxPay = new WxPay();
		wxPay.setBody(tutorInfo.get("tutor_name").toString());
		wxPay.setTotalFee(totalFee);
		wxPay.setSpbillCreateIp(ip);
		wxPay.setOrderId(orderId);
		wxPay.setOpenId(userOpenId);
		logger.info("开始向微信统一下单接口 请求---openId---->" + userOpenId + "&time=" + System.currentTimeMillis());
		// 向微信 统一 下单
		Map<String, Object> unifiedOrder = UnifiedOrder.unifiedOrder(wxPay);
		if (unifiedOrder == null) {
			logger.error("----------->向微信统一下单接口请求失败");
			return null;
		}
		logger.info("微信下单成功：time=" + System.currentTimeMillis());
		String returnCode = unifiedOrder.get("return_code").toString();
		String resultCode = unifiedOrder.get("result_code").toString();
		// 下单成功，生成订单
		if (returnCode.equals("SUCCESS") && resultCode.equals("SUCCESS")) {
			// 插入订单 //设置为 订单的状态 下单未付款
			int insertResult = orderDao.insertOrder(orderId, userOpenId, tutorId, period, result.toString());
			if (insertResult > 0) {
				return unifiedOrder;
			}
		}
		logger.error("----------->向微信统一下单接口请求失败");
		return null;
	}

	@Override
	public String updateOrderStateForCallBack(String notifyXml) {
		// TODO Auto-generated method stub
		// 返回微信XML
		StringBuilder resultXml = new StringBuilder();
		Map<String, String> map = WXUtil.parseXmlToList2(notifyXml);
		String orderNo = map.get("out_trade_no").toString();
		String returnCode = map.get("return_code").toString();
		if (returnCode.equals("SUCCESS")) {
			WxPayResult payResult = new WxPayResult();
			payResult.setAppid(map.get("appid").toString());
			payResult.setBankType(map.get("bank_type").toString());
			payResult.setCashFee(map.get("cash_fee").toString());
			payResult.setFeeType(map.get("fee_type").toString());
			payResult.setIsSubscribe(map.get("is_subscribe").toString());
			payResult.setMchId(map.get("mch_id").toString());
			payResult.setNonceStr(map.get("nonce_str").toString());
			payResult.setOpenid(map.get("openid").toString());
			// 商户订单
			payResult.setOutTradeNo(map.get("out_trade_no").toString());
			payResult.setResultCode(map.get("result_code").toString());
			payResult.setReturnCode(map.get("return_code").toString());
			payResult.setSign(map.get("sign").toString());
			payResult.setTimeEnd(map.get("time_end").toString());
			payResult.setTotalFee(map.get("total_fee").toString());
			payResult.setTradeType(map.get("trade_type").toString());
			// 微信订单
			payResult.setTransactionId(map.get("transaction_id").toString());

			// 根据订单id orderNo 查询 订单状态
			Integer orderState = orderDao.getOrderStateById(orderNo);
			if (orderState != null && 0 == orderState) {
				// 修改订单状态
				int updateResult = orderDao.updateStateByIdForWxPay(orderNo, 1);
				if (updateResult > 0) {
					// 微信返回：支付成功
					resultXml.append("<xml>");
					resultXml.append("<return_code><![CDATA[SUCCESS]]></return_code>");
					resultXml.append("<return_msg><![CDATA[OK]]></return_msg>");
					resultXml.append("</xml>");
					logger.info("------>支付成功----->发邮件通知运营协商时间");
					// 支付成功 mq 异步发邮件 通知运营
					String msg = "{\"orderId\": \"" + map.get("out_trade_no").toString() + "\"}";
					logger.info("--->mq消息拼接：" + msg);
					producer.sendMessage(msg);
				} else {
					resultXml.append("<xml>");
					resultXml.append("<return_code><![CDATA[FAIL]]></return_code>");
					resultXml.append("<return_msg><![CDATA[报文为空]]></return_msg>");
					resultXml.append("</xml>");
				}
			} else {
				resultXml.append("<xml>");
				resultXml.append("<return_code><![CDATA[FAIL]]></return_code>");
				resultXml.append("<return_msg><![CDATA[报文为空]]></return_msg>");
				resultXml.append("</xml>");
			}
			return resultXml.toString();
		} else

		{
			resultXml.append("<xml>");
			resultXml.append("<return_code><![CDATA[FAIL]]></return_code>");
			resultXml.append("<return_msg><![CDATA[报文为空]]></return_msg>");
			resultXml.append("</xml>");
			return resultXml.toString();
		}
	}

	@Override
	public Map<String, Object> payInOrderList(String orderId, String ip) {
		// TODO Auto-generated method stub

		Map<String, Object> orderInfo = orderDao.getOrderInfoByOrderId(orderId);
		if (orderInfo == null) {
			logger.error("订单列表中支付的订单不存在----->支付失败---->orderId=" + orderId);
			return null;
		}
		// 精确计算
		BigDecimal price = new BigDecimal(orderInfo.get("price").toString());
	
		BigDecimal result = price.setScale(2, BigDecimal.ROUND_CEILING);

		// 由于微信中不能一个订单号重复下单所以 我们要重新生成一个订单号

		String orderIdNew = StringUtil.getUuid();

		BigDecimal fenResult = result.multiply(new BigDecimal(100));// 将其转换为分
		String totalFee = fenResult.setScale(0, BigDecimal.ROUND_UP).toString();
		WxPay wxPay = new WxPay();
		wxPay.setBody(orderInfo.get("tutor_name").toString());
		wxPay.setTotalFee(totalFee);
		wxPay.setSpbillCreateIp(ip);
		wxPay.setOrderId(orderIdNew);
		wxPay.setOpenId(orderInfo.get("userOpenId").toString());
		logger.info("开始向微信统一下单接口 请求---openId---->" + orderInfo.get("userOpenId").toString() + "&time="
				+ System.currentTimeMillis());
		// 向微信 统一 下单
		Map<String, Object> unifiedOrder = UnifiedOrder.unifiedOrder(wxPay);
		if (unifiedOrder == null) {
			logger.error("----------->向微信统一下单接口请求失败");
			return null;
		}
		logger.info("微信下单成功：time=" + System.currentTimeMillis());
		String returnCode = unifiedOrder.get("return_code").toString();
		String resultCode = unifiedOrder.get("result_code").toString();
		// 下单成功
		if (returnCode.equals("SUCCESS") && resultCode.equals("SUCCESS")) {
			int updateResult = orderDao.updateOrderIdToNewOrderId(orderId, orderIdNew);
			if (updateResult > 0) {
				return unifiedOrder;
			}
		}
		logger.error("----------->向微信统一下单接口请求失败");
		return null;

	}

}
