package com.tutor.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tutor.model.Mail;
import com.tutor.mq.Producer;
import com.tutor.util.CommonUtil;
import com.tutor.util.EnumUtil;
import com.tutor.util.MailSender;

@Controller
@RequestMapping("api/test")
public class TestController {
	@Autowired
	private Producer producer;
	@Autowired
	private MailSender mailSender;

	@RequestMapping(value = "/getMail", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject test1() {
		String msg = "{\"orderId\": \"fasdfsdafsd\"}";
		producer.sendMessage(msg);
		return CommonUtil.constructResponse(EnumUtil.OK, "ok", null);

	}

	@RequestMapping(value = "/getMail2", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject test2() {
		// String msg = "{\"orderId\": \"fasdfsdafsd\"}";
		// producer.sendMessage(msg);
		// return CommonUtil.constructResponse(EnumUtil.OK, "ok", null);
		String mailMsg = "用户：卖萝卜的小男孩 于 2017-02-13 14:44:21.0 (userId:9;openId:oU1l4wG7Q_69JSTEd1yw8Lgc3ryQ)购买了咨询家:陈佳欣 [1] 个订单。总价：34.00。用户的联系方式是：18883993224。请运营人员尽快与用户联系。喵~~~~~~";
		Mail mail = new Mail();
		mail.setContentText(mailMsg);
		mail.setTo("469391363@qq.com");
		mail.setSubject("有人下订单啦！");
		mailSender.sendSimpleMail(mail);
		return CommonUtil.constructResponse(EnumUtil.OK, "ok2", null);

	}

}
