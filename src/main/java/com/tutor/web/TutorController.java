package com.tutor.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.tutor.service.TutorService;
import com.tutor.util.BaseController;
import com.tutor.util.CommonUtil;
import com.tutor.util.EnumUtil;

@Controller
@RequestMapping("api/tutor")
public class TutorController extends BaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8313440595759769342L;
	@Autowired
	private TutorService tutorService;

	/**
	 * h5分享家详情页获取分享家详细信息
	 * 
	 * @param tutorId
	 * @return
	 */
	@RequestMapping(value = "/getTutorInfo", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getTutorInfo(@RequestParam(value = "tutorId", required = true) Integer tutorId) {

		logger.info("invoke--------------------user/getTutorInfo?tutorId=" + tutorId);
		if (tutorId == null) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		Map<String, Object> tutorInfo = null;

		try {
			tutorInfo = tutorService.getTutorInfoById(tutorId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询咨询家详细信息出错", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "数据库错误", null);
		}

		return CommonUtil.constructResponse(EnumUtil.OK, "success", tutorInfo);
	}

	/**
	 * h5咨询家评论列表详情页
	 * 
	 * @param tutorId
	 * @return
	 */
	@RequestMapping(value = "/getComments", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getComments(@RequestParam(value = "tutorId", required = true) Integer tutorId,
			@RequestParam(value = "page", required = true) Integer page) {
		logger.info("invoke--------------------user/getComments?tutorId=" + tutorId + "&page=" + page);
		if(tutorId==null||page==null||page<1){
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		List<Map<String, Object>> comments = null;
		try {
			comments = tutorService.getComments(tutorId, page);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询咨询家评论信息出错", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "数据库错误", null);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "success", comments);

	}

	/**
	 * h5首页咨询家列表
	 * 
	 * @param tutorId
	 * @return
	 */
	@RequestMapping(value = "/getHomeList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getHomeList(@RequestParam(value = "page", required = true) Integer page) {
		logger.info("invoke--------------------user/getHomeList?page=" + page);
		if (page == null || page < 1) {
			return CommonUtil.constructResponse(EnumUtil.ARG_ERROR, "参数错误", null);
		}
		List<Map<String, Object>> homeList = null;
		try {
			homeList = tutorService.getHomeList(page);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询咨询家详细列表出错", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "数据库错误", null);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "success", homeList);

	}

	/**
	 * 根据用户openid判断是否绑定
	 * 
	 * @param tutorId
	 * @return
	 */
	@RequestMapping(value = "/checkBind", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject checkBind(@RequestParam(value = "openId", required = true) String openId) {
		logger.info("invoke--------------------tutor/checkBind?openId=" + openId);
		List<Map<String, Object>> bindResult = null;
		int result = 0;
		try {
			bindResult = tutorService.checkBind(openId);
			if (bindResult.isEmpty()) {
				result = 2;
			} else {
				result = 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询用户是否绑定出错", e);
			return CommonUtil.constructResponse(EnumUtil.DB_ERROR, "数据库错误", null);
		}
		return CommonUtil.constructResponse(EnumUtil.OK, "success", result);

	}

}
