package com.tutor.service;

import java.util.List;
import java.util.Map;

public interface OrderService {

	/**
	 * 通过openId获取用户ID
	 * 
	 * @param openId
	 * @return
	 */
	String SelectuserId(String openId);

	/**
	 * 创建订单
	 * 
	 * @param orderId
	 *            订单id
	 * @param userOpenId
	 *            用户openId
	 * @param tutorId
	 *            咨询家id
	 * @param period
	 *            小时数
	 * @param ip
	 *            下单ip
	 * @return
	 */
	Map<String, Object> insertOrder(String orderId, String userOpenId, int tutorId, int period, String ip);

	/**
	 * 获取订单列表
	 * 
	 * @param openId
	 * @param pageSize
	 * @param page
	 * @return
	 */
	List<Map<String, Object>> getOrderList(String openId, int page, int state);

	/**
	 * 根据微信回调 修改订单的状态
	 * 
	 * @param notifyXml
	 * @return
	 */
	String updateOrderStateForCallBack(String notifyXml);

	/**
	 * 在订单列表中支付
	 * 
	 * @param orderId
	 * @return
	 */
	Map<String, Object> payInOrderList(String orderId,String ip);
}
