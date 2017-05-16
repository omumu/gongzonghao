package com.tutor.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface OrderDao {

	/**
	 * 获取订单列表 用于订单页
	 * 
	 * @param openId
	 * @param state
	 * @param start
	 * @return
	 */
	List<Map<String, Object>> getOrderList(@Param("openId") String openId, @Param("start") int start,
			@Param("state") int state);

	/**
	 * 插入订单
	 * 
	 * @param orderId
	 * @param userOpenId
	 * @param tutorId
	 * @param period
	 * @param price
	 * @return
	 */
	int insertOrder(String orderId, String userOpenId, int tutorId, int period, String price);

	/**
	 * 通过 订单的id 获取订单的状态
	 * 
	 * @param orderId
	 * @return
	 */
	Integer getOrderStateById(String orderId);

	/**
	 * 微信支付成功 修改订单相关字段
	 * 
	 * @param orderId
	 * @param state
	 * @return
	 */
	int updateStateByIdForWxPay(String orderId, int state);

	/**
	 * 得到用户信息来 通知运营人员
	 * 
	 * @param orderId
	 * @return
	 */
	Map<String, Object> getOrderInfoForCallManage(String orderId);

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	Map<String, Object> getOrderInfoByOrderId(String orderId);

	/**
	 * 把订单id 改成新的订单id
	 * 
	 * @param orderIdOld
	 * @param OrderIdNew
	 * @return
	 */
	int updateOrderIdToNewOrderId(String orderIdOld, String orderIdNew);
}
