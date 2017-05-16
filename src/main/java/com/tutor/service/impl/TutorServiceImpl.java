package com.tutor.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tutor.dao.TutorDao;
import com.tutor.service.TutorService;

@Service("tutorService")
public class TutorServiceImpl implements TutorService {

	@Autowired
	private TutorDao tutorDao;

	@Override
	public List<Map<String, Object>> getComments(int tutorId, int page) {
		int start = (page - 1) * 3;
		return tutorDao.getComments(tutorId, start);
	}

	@Override
	public List<Map<String, Object>> getHomeList(int page) {
		int start = (page - 1) * 5;
		List<Map<String, Object>> tutorList = tutorDao.getHomeList(start);
		if (tutorList == null) {
			return null;
		} else {
			for (Map tutor : tutorList) {
				String[] tagArr = tutor.get("tags").toString().split(",");
				tutor.put("tags", tagArr);
			}
			return tutorList;
		}

	}

	public List<Map<String, Object>> checkBind(String openId) {
		return tutorDao.checkBind(openId);
	}

	@Override
	public Map<String, Object> getTutorInfoById(int tutorId) {
		// TODO Auto-generated method stub
		Map<String, Object> result = tutorDao.getTutorInfoById2(tutorId);
		String[] tagArr = result.get("tutor_label").toString().split(",");
		result.put("tutor_label", tagArr);
		return result;
	};
}
