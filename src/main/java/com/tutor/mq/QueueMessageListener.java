package com.tutor.mq;

import java.util.Date;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.tutor.dao.OrderDao;
import com.tutor.model.Mail;
import com.tutor.util.DateUtil;
import com.tutor.util.MailSender;

public class QueueMessageListener implements MessageListener {
	private Logger logger = LoggerFactory.getLogger(QueueMessageListener.class);

	@Autowired
	private OrderDao orderDao;
	@Autowired
	private MailSender mailSender;

	// 当收到消息时，自动调用该方法
	public void onMessage(Message message) {
		TextMessage tm = (TextMessage) message;
		try {
			logger.info("ConsumerMessageListener收到了文本消息：\t" + tm.getText());
			/**
			 * 消息内容: {"orderId": "aaa"}
			 */
			JSONObject jb = JSONObject.parseObject(tm.getText());

			String orderId = jb.get("orderId").toString();
			Map<String, Object> orderInfo = orderDao.getOrderInfoForCallManage(orderId);
			
//			System.out.println(orderInfo);

			if (orderInfo == null) {
				logger.error("错误消息：查不到此订单：" + tm.getText());
			}

			String mailMsg = "用户：" + orderInfo.get("user_name") + " 于 " + orderInfo.get("pay_time") + " (userId:"
					+ orderInfo.get("user_id") + ";openId:" + orderInfo.get("openId") + ")" + "购买了咨询家:"
					+ orderInfo.get("tutor_name") + " [" + orderInfo.get("order_num") + "] 个订单。总价："
					+ orderInfo.get("order_price") + "。用户的联系方式是：" + orderInfo.get("user_phone")
					+ "。请运营人员尽快与用户联系。喵~~~~~~";
			Mail mail = new Mail();
			mail.setContentText(mailMsg);
			mail.setTo(orderInfo.get("managerMail").toString());
			mail.setSubject("有人下订单啦！");
			mailSender.sendSimpleMail(mail);
			logger.info("---->成功消费");
		} catch (JMSException e) {
			logger.error("消费消息失败了", e);
			e.printStackTrace();
		}
	}

}