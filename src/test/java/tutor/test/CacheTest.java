package tutor.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tutor.cache.RedisCache;
import com.tutor.util.wx.WXUtil;

public class CacheTest extends BaseTest {
	@Autowired
	private RedisCache redisCache;

	@Test
	public void testCache() {
//		System.out.println("进入方法");
//		long a=System.currentTimeMillis();
//		redisCache.putCache("aaa", "aaa");
//		System.out.println((System.currentTimeMillis()-a));
//		System.out.println("放入成功");
//		System.out.println(redisCache.getCache("aaa", String.class));
		// redisCache.clearCache(); //清除所有的 缓存
//		System.out.println(WXUtil.getJsApiTicket());
	}
}
