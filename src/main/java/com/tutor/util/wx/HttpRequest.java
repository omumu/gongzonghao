package com.tutor.util.wx;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author joe蒋渊
 *
 */

@SuppressWarnings("deprecation")
public class HttpRequest {
	private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

	/**
	 * 这里封装了一个 http GET请求的方法 返回值是 响应的json
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws Exception
	 * 
	 */
	public static JSONObject doGETReturnJSON(String url, Map<String, String> param) throws Exception {
		if (url == null || ("").equals(url)) {
			logger.warn("HttpRequest----invoke---doGETReturnJSON with url is null");
			return null;
		}
		StringBuffer paramAll = new StringBuffer();
		String urlAll = null;
		if (param != null) {
			Iterator<Entry<String, String>> iter = param.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
				paramAll.append("&" + entry.getKey() + "=" + entry.getValue());
			}
			urlAll = url + "?" + (paramAll.toString().substring(1, paramAll.length()));
		} else {
			urlAll = url;
		}
		// 以上是 拼接请求地址 其实 httpClient 框架中 有个方法 可以直接调用 希望后来者可以 试试 改进一下这里
		@SuppressWarnings({ "resource" })
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(urlAll);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			int l;
			byte[] tmp = new byte[2048];
			StringBuffer result = new StringBuffer();
			while ((l = instream.read(tmp)) != -1) {
				if (l == 2048) {
					result.append(new String(tmp,"utf-8"));//更改为utf-8编码
				} else {
					result.append(new String(ArrayUtils.subarray(tmp, 0, l),"utf-8"));//更改为utf-8编码
				}
			}
			return (JSONObject) JSONObject.parse(result.toString());

		} else {
			logger.warn("doGetReturnJson response null");
			return null;
		}
	}

	public static byte[] doPost(String url, JSONObject json) {
		@SuppressWarnings("resource")
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		byte[] response = null;
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");// 发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				response = EntityUtils.toByteArray(res.getEntity());// 返回 数组
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}
}
