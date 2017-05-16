package com.tutor.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface TutorDao {

	/**
	 * 【咨询家】详情页信息接口
	 * 
	 * @param tutorId
	 * @return
	 */
	public List<Map<String, Object>> getTutorInfo(int tutorId);

	/**
	 * 【咨询家】详情页获取评论列表接口
	 * 
	 * @param tutorId
	 * @param start
	 * @return
	 */
	public List<Map<String, Object>> getComments(int tutorId, int start);

	/**
	 * 获取首页导师列表
	 * 
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getHomeList(int page);

	/**
	 * 获取订单列表
	 * 
	 * @param openId
	 * @param pageSize
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getOrderList(String openId, int pageSize, int page);

	/**
	 * 【绑定】验证是否绑定
	 * 
	 * @param openId
	 * @return
	 */
	public List<Map<String, Object>> checkBind(String openId);
	/**
	 * 通过id 获取咨询家信息
	 *  获取了 tutor  name  title  price 
	 * @author 蒋渊 
	 * @param tutorId
	 * @return
	 */
	Map<String, Object> getTutorInfoById(int tutorId);
	/**
	 * 通过id 获取咨询家信息
	 *  获取了 tutor  name  title count_time  price  desc   lable  recommend
	 * @author 蒋渊 
	 * @param tutorId
	 * @return
	 */
	Map<String, Object> getTutorInfoById2(int tutorId);

}
