package com.tutor.service;

import java.util.List;
import java.util.Map;


public interface TutorService {
	/**
	 * 获取咨询家个人信息
	 * @param tutor_id
	 * @return
	 */
	Map<String,Object> getTutorInfoById(int tutorId);
	/**
	 * 获取 咨询家的评论列表
	 * @param tutorId
	 * @param page
	 * @return
	 */
	List<Map<String, Object>> getComments(int tutorId,int page);
	/**
	 * h5 首页获取 列表
	 * @param page
	 * @return
	 */
	List<Map<String, Object>> getHomeList(int page);

	public List<Map<String,Object>> checkBind(String openId);
}
