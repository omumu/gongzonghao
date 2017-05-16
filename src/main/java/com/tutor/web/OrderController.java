package com.tutor.web;

import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tutor.model.User;
import com.tutor.service.OrderService;
import com.tutor.util.BaseController;
import com.tutor.util.CommonUtil;
import com.tutor.util.EnumUtil;
import com.tutor.util.StringUtil;

@Controller
@RequestMapping("api/order")
public class OrderController extends BaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4456876882941201653L;

	@Autowired
	private OrderService orderService;

	/**
	 * @author 蒋渊 创建订单
	 * 
	 * @param tutorId
	 * @param period
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject Createorder(@RequestParam(value = "tutorId", required = true) Integer tutorId,
			@RequestParam(value = "period", required = true) Integer period, HttpServletRequest request,
			HttpSession session) {
		logger.info("开始下单：invoke--------------order/create---->用户:" + getLoginUser(request).toString());
		if (tutorId == null || period == null) {
			logger.info("参数错误下单失败---->tutorId=" + tutorId.toString() + "&period=" + period.toString());
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		User user = getLoginUser(request);

		// 下单之前 判断用户是否绑定绑定电话号码
		if (user.getUserPhone() == null || "".equals(user.getUserPhone())) {
			return CommonUtil.constructResponse(22, "用户未绑定电话", null);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		String orderId = StringUtil.getUuid();
		String ip = request.getRemoteAddr(); // 线上用这个
		// String ip ="127.0.0.1";//本地测试时的ip
		String openId = user.getUserOpenId();
		try {
			result = orderService.insertOrder(orderId, openId, tutorId, period, ip);
			if (result == null) {
				logger.error("发生异常下单失败---->openId=" + openId);
				return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, "未知错误下单失败", null);
			}
		} catch (Exception e) {
			logger.error("发生异常下单失败---->openId" + openId, e);
			return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, "未知错误下单失败Exception", null);
		}
		logger.info(result.toString());
		return CommonUtil.constructResponse(EnumUtil.OK, "下单成功", result);
	}

	/**
	 * 查询订单列表
	 * 
	 * @param tutorId
	 * @return
	 */
	@RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getOrderList(@RequestParam(value = "page", required = true) Integer page,
			HttpServletRequest request, @RequestParam(value = "state", required = true) Integer state) {
		logger.info("invoke--------------------user/getOrderList?page=" + page);
		if (page == null || page < 1 || state == null || state < 0 || state > 7) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		List<Map<String, Object>> orderList = null;

		try {
			orderList = orderService.getOrderList(getLoginUser(request).getUserOpenId(), page, state);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询订单列表出错", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "数据库错误", null);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "success", orderList);

	}

	/**
	 * 这是 微信支付后的 回调的接口 这个接口不能带 参数
	 * 
	 * @return
	 */
	@RequestMapping(value = "/callBack") // 不知道微信回调是什么请求所以 GET 和POST 都可以
	public void callBack(HttpServletRequest request, HttpServletResponse response) {
		logger.info("--------------->微信支付回调");
		// 微信通知XML
		StringBuilder notityXml = new StringBuilder();

		String inputLine;
		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notityXml.append(inputLine);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("---------->读取微信返回的xml发生错误");
		}
		String resultXml = orderService.updateOrderStateForCallBack(notityXml.toString());
		try {
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			logger.info("通知微信支付成功---------->xml:" + resultXml);
			out.write(resultXml.toString().getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("-------->回调函数 response返回写xml的时候发生错误");
		}
	}

	/**
	 * 在订单列表中支付
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/payInOrderList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject payInOrderList(HttpServletRequest request,
			@RequestParam(value = "orderId", required = true) String orderId) {
		if (StringUtil.isempty(orderId)) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		logger.info("invoke-----------------------payInOrderList?orderId=" + orderId);
		 String ip = request.getRemoteAddr();// 线上用这个
//		String ip = "127.0.0.1";// 线下测试用这个
		Map<String, Object> result = null;
		try {
			result = orderService.payInOrderList(orderId, ip);
			if (result == null) {
				return CommonUtil.constructResponse(EnumUtil.UNKNOW_ERROR, "未知错误", null);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("在订单列表支付错误", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "支付错误", null);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "ok", result);
	}
}
