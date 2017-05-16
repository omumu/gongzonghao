package com.tutor.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此类是是用来 读取properties 文件
 * 
 * @author joe蒋渊
 *
 */

public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private static InputStreamReader in;
	private static Properties prop;

	private PropertiesUtil() {
		// 不能实例化
	}

	static {
		try {
			// 读取微信H5配置
			prop = new Properties();
			in = new InputStreamReader(
					Thread.currentThread().getContextClassLoader().getResourceAsStream("wechat.properties"), "UTF-8");// 解决配置文件中
																														// 中文乱码的问题
			prop.load(in);
			// 读取阿里大于的配置
			in = new InputStreamReader(
					Thread.currentThread().getContextClassLoader().getResourceAsStream("message.properties"), "UTF-8");// 解决配置文件中
																														// 中文乱码的问题
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("读取配置文件wechat.properties出错", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取 key 的值
	 * 
	 * @param key
	 * @return
	 */

	public static String getValue(String key) {
		return prop.getProperty(key);
	}

}
