package com.freech;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailApplicationTest {
    @Autowired
    private JavaMailSender sender;
    //邮件发送者
    private String from = "freech1688@163.com";
    //邮件接收者
    private String to = "569265915@qq.com";

    /**
     * 发送文本邮件
     */
    @Test
    public void sendTextEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        //发送者
        message.setFrom(from);
        //接收者
        message.setTo(to);
        //邮件主题
        message.setSubject("文本邮件");
        //邮件内容
        message.setText("你好，这是一份文本文件");
        sender.send(message);
    }

    /**
     * 发送带有附件的邮件
     */
    @Test
    public void sendAttachmentFilesEmail() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("带有附件的邮件");
        helper.setText("你好，这是一份带有附件的邮件");

        //获取文件对象.
        FileSystemResource file1 = new FileSystemResource(new File("D:/test.rar"));
        //添加附件，切记attachmentFilename要带文件格式后缀，否则无法识别。
        helper.addAttachment("attachmentFile1.rar", file1);
        //添加多个附件，直接修改一下attachmentFilename。
        helper.addAttachment("attachmentFile2.rar", file1);

        sender.send(message);
    }


    /**
     * 发送HTML邮件
     */
    @Test
    public void sendHtmlMail() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("HTML邮件");
        String content = new StringBuffer().append("<html>").append("<head>").append("<title>HTML邮件</title>")
                .append("</head>").append("<body>").append("<h1>这是一份HTML邮件</h1>").append("</body>").append("</html>").toString();
        // 邮件内容，第二个参数指定发送的是HTML格式
        helper.setText(content, true);
        sender.send(message);

    }

    /**
     * 发送图片邮件
     */
    @Test
    public void sendImagesMail() throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("图片邮件");
        String imgPath1 = "D:/images/1.jpg";
        String imgPath2 = "D:/images/2.jpg";
        String rscId1 = "001";
        String rscId2 = "002";
        String conntent = "<html><body><h3>带图片的邮件：</h3>图片一：<img style='width:200px;height:200px' src='cid:"+rscId1+"'><br>图片二：</img><img style='width:200px;height:200px'  src='cid:"+rscId2+"'></img></body></html>";
        helper.setText(conntent, true);
        FileSystemResource file1 = new FileSystemResource(new File(imgPath1));
        FileSystemResource file2 = new FileSystemResource(new File(imgPath2));
        helper.addInline(rscId1,file1);
        helper.addInline(rscId2,file2);
        sender.send(message);
    }

    //注入FreeMarkerConfigurer
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    /**
     * 发送模板邮件
     */
    @Test
    public void sendTemplateMail() throws MessagingException, IOException, TemplateException {

        MimeMessage message = sender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("模板邮件");

        Map<String, Object> model = new HashMap<>();
        model.put("name", "Freech");

        freeMarkerConfigurer.setTemplateLoaderPath("classpath:templates");
        //加载模板文件
        Template template = freeMarkerConfigurer.getConfiguration().getTemplate("template.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setText(html, true);
        sender.send(message);
    }

}
