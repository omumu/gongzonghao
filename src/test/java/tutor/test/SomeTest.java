package tutor.test;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.tutor.util.PropertiesUtil;
import com.tutor.util.StringUtil;
import com.tutor.util.wx.HttpRequest;

public class SomeTest {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		// System.out.println(URLEncoder.encode("http://www.lingshi321.com/tutor.html"));

		// JSONObject jb = (JSONObject) JSONObject
		// .parse("{\"errcode\":40029,\"errmsg\":\"invalid code, hints: [
		// req_id: cb.xoa0718ns89 ]\"}");
		// System.out.println(jb.get("errcode"));

		// Map<String, String> param = new HashMap<>();
		// param.put("appid", "wx413b77af2e5023eb");
		// param.put("code", "031HwDIk0B0CKm1ahuEk01xnIk0HwDIF");
		// param.put("secret", "bb60b70e8c3d69255bdb6d8f8e57fa86");
		// param.put("grant_type", "authorization_code");
		// JSONObject jb =
		// HttpRequest.doGETReturnJSON("https://api.weixin.qq.com/sns/oauth2/access_token",
		// param);
		// System.out.println(jb.get("openid"));// oU1l4wNd8pB1L7NxK8hA1mRboKHs
		// // 获取openId accesstoken 成功

		// System.out.println(System.getProperty("os.name")); 获取 当前机器的系统名称

		// System.out.println(PropertiesUtil.getValue("msg_appSecret"));
		// System.out.println(StringUtil.getUuid());
		// System.out.println(StringUtil.getUuid().length());

		// BigDecimal price = new BigDecimal("3.211");
		// BigDecimal multResult=price.multiply(new BigDecimal(3));
		// //保留 四舍五入 两位数操作
		// BigDecimal result=multResult.setScale(2,BigDecimal.ROUND_CEILING);
		// String totalFee=result.toString();
		// System.out.println(totalFee);

		// Integer a=0;
		// System.out.println(a==0);

		// System.out.println(StringUtil.isMobileNO("18908352144"));

		// byte[] aa=ProtoStuffSerializerUtil.serialize("aa");
		// System.out.println(ProtoStuffSerializerUtil.deserialize(aa,
		// String.class));
		BigDecimal price = new BigDecimal("3.21");
		System.out.println(price.toString());
		BigDecimal a = price.multiply(new BigDecimal(100));
		System.out.println(a.setScale(0, BigDecimal.ROUND_UP));

	}
}
