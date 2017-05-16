package tutor.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tutor.model.Mail;
import com.tutor.mq.Producer;
import com.tutor.util.MailSender;

public class MailTest extends BaseTest {
	@Autowired
	private MailSender mailSender;
	@Autowired
	private Producer producer;

	@Test
	public void sendMail() {
		// Mail mail = new Mail();
		// mail.setContentText("测试 school");
		// mail.setTo("469391363@qq.com");
		// mail.setSubject("测试test");
		// mailSender.sendSimpleMail(mail);
		// String msg="{ \"openId\": \"aaa\", \"orderId\": \"aaa\"}";
		// producer.sendMessage(msg);

		String msg = "{\"orderId\": \"fagsdfhjkhjljk\"}";
		System.out.println(msg);
		producer.sendMessage(msg);

	}
}
