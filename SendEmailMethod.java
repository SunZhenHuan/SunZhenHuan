import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author 发送邮件的方法
 */
public class SendEmailMethod {
    long end;
    int Connection=1;//表示网络连接
    static Thread thread;
    public static int flag = 1;//表示单例
    public String MailTheme = "";//主题名字
    public String sendName = "";//发送人
    public String sendMessage = "";//发送的内容
    // PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）,
    //     对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。
    public String MyQQEmail = "";//qq账号
    public String MyAuthorizationCode = "";//授权码
    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    public String MyEmailSMTPHost = "smtp.qq.com";//qq邮箱Smtp服务器地址
    public String Addressee = "";//收件人,自己知道的有效邮箱单例
    public Set<String> AddresseeList;//多例
    public final String smtpPort = "465";//QQ邮箱的ssl安全连接的SMTP端口号465
    public int sendCount;//发送的次数
    public int createEmailFlag = -1;//用于标记创建邮件的标志

    //构造方法方便主窗体传递值
    public SendEmailMethod(String mailTheme, String sendName, String sendMessage, String MyQQEmail, String MyAuthorizationCode, String Addressee, Set<String> AddresseeList, int sendCount) {
        //依次是1.邮件主题2.发送人名3.发送的内容,4.自己的QQ邮箱号,5.自己邮箱的授权码,6.收件人的邮箱账号
        this.MailTheme = mailTheme;
        this.sendName = sendName;
        this.sendMessage = sendMessage;
        this.MyQQEmail = MyQQEmail;
        this.MyAuthorizationCode = MyAuthorizationCode;
        this.Addressee = Addressee;//单例
        this.AddresseeList = AddresseeList;//多例
        this.sendCount = sendCount;//发送的次数
    }

    //设置邮件基本配置信息
    public void SetEmailMethod() throws MessagingException, UnsupportedEncodingException {
        // 1.先设置参数配置, 用于连接邮件服务器的参数配置
        Properties properties = new Properties();//参数对象
        properties.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        properties.setProperty("mail.smtp.host", MyEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        properties.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
//        /*
        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)

        //这个是qq邮箱的smtp端口号465如果你使用的邮箱没有ssl安全连接这把下面代码注释掉
        properties.setProperty("mail.smtp.port", smtpPort);
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.port", smtpPort);


        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(properties);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
        if (Addressee != null)//单例不为空
        {
            // 3. 创建一封邮件
            System.err.println("我是单例");
            createEmailFlag = 1;//执行下面第1
            MimeMessage message = createMimeMessage(session, MyQQEmail, sendName, Addressee, null);//创建一个邮件传入会话对象,自己的邮箱号,自己知道的qq邮箱号
            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();
            try {
                //5.进行连接
                transport.connect(MyQQEmail, MyAuthorizationCode);//传入我的qq邮箱号和授权码进行连接
                // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
                transport.sendMessage(message, message.getAllRecipients());
                // 7. 关闭连接
                transport.close();
            } catch (MessagingException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "网络可能未连接！","网络未连接",JOptionPane.ERROR_MESSAGE);
                Connection=0;//网络没链接
            }
        } else if (!AddresseeList.isEmpty())///集合不是空
        {
            System.err.println("我是多例");
            createEmailFlag = 2;//执行下面第二
            for (String s : AddresseeList) {
                // 3. 创建一封邮件
                System.err.println("集合" + s);
                MimeMessage message = createMimeMessage(session, MyQQEmail, sendName, null, s);//创建一个邮件传入会话对象,自己的邮箱号,自己知道的qq邮箱号
                // 4. 根据 Session 获取邮件传输对象
                Transport transport = session.getTransport();
                try {
                    //5.进行连接
                    transport.connect(MyQQEmail, MyAuthorizationCode);//传入我的qq邮箱号和授权码进行连接
                    // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
                    transport.sendMessage(message, message.getAllRecipients());
                    // 7. 关闭连接
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "网络可能未连接！","网络未连接",JOptionPane.ERROR_MESSAGE);
                    Connection=0;//网络没链接
                }
            }
        }
        //连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。

    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session   和服务器交互的会话
     * @param MyQQEmail 发件人邮箱
     * @param addressee 收件人邮箱
     * @param List      集合中的信息
     */
    public MimeMessage createMimeMessage(Session session, String MyQQEmail, String sendName, String addressee, String List) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = null;
        if (createEmailFlag == 1) {
            System.err.println(createEmailFlag);
            // 1. 创建一封邮件
            message = new MimeMessage(session);
            // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
            message.setFrom(new InternetAddress(MyQQEmail, sendName, "UTF-8"));
            // 3. To: 收件人（可以增加多个收件人、抄送、密送）
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(addressee, sendName, "UTF-8"));
            // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
            message.setSubject(MailTheme, "UTF-8");
            // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
            message.setContent(sendMessage, "text/html;charset=utf-8");
            // 6. 设置发件时间
            message.setSentDate(new Date());
            // 7. 保存设置
            message.saveChanges();
        } else if (createEmailFlag == 2) {
            System.err.println(createEmailFlag);
            // 1. 创建一封邮件
            message = new MimeMessage(session);
            // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
            message.setFrom(new InternetAddress(MyQQEmail, sendName, "UTF-8"));
            // 3. To: 收件人（可以增加多个收件人、抄送、密送）
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(List, sendName, "UTF-8"));
            // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
            message.setSubject(MailTheme, "UTF-8");
            // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
            message.setContent(sendMessage, "text/html;charset=utf-8");
            // 6. 设置发件时间
            message.setSentDate(new Date());
            // 7. 保存设置
            message.saveChanges();
        }
        return message;
    }

    //开始发送
    public void StartSend() throws MessagingException, UnsupportedEncodingException {
        if (flag == 1) {//单例
            ThreadFor();//调用循环的方法
            System.gc();
        } else if (flag == 2) {//多例
            ThreadFor();
            System.gc();
        }
    }
    //for循环普通方法
//    public void For()
//    {
//        for (int i=1;i<sendCount;i++)
//        {
//            try {
//                SetEmailMethod();//发送信息的方法
//                EmailFrame.send.setText("当前已经发送"+i+"封");
//                end=System.currentTimeMillis();
//                System.gc();
//            } catch (MessagingException | UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        EmailFrame.sendFlag=1;
//        System.err.println("总用时：" + (end - EmailFrame.start) / 1000 + "秒");
//    }

    //while循环的线程方法
//    int i =0;
//    public void While() {
//        Thread t = new Thread(() -> {
//            while (i <sendCount) {
//                   try {
//                       EmailFrame.send.setText("当前正在发送第"+(i+1)+"封");
//                       SetEmailMethod();//发送信息的方法
//                       end=System.currentTimeMillis();
//                       System.gc();
//                   } catch (MessagingException | UnsupportedEncodingException e) {
//                       e.printStackTrace();
//                   }
//               i++;
//            }
//            System.err.println("总用时：" + (end - EmailFrame.start) / 1000 + "秒");
//            EmailFrame.sendFlag=1;
//            EmailFrame.send.setText(i+"封发送完毕！");
//        });
//        t.start();
//    }
    //for循环线程方法
    int i=0;
    public void ThreadFor()
    {
         thread=new Thread(()->{
           for (i=0;i<sendCount;i++)
           {
               try {
                   EmailFrame.send.setText("当前正在发送第"+(i+1)+"封");
                   SetEmailMethod();//发送信息的方法
                   end=System.currentTimeMillis();
                   System.gc();
               } catch (MessagingException | UnsupportedEncodingException e) {
                   e.printStackTrace();
               }
           }
           if(Connection == 1)//表示已经连接网络
           {
               System.err.println("总用时：" + (end - EmailFrame.start) / 1000 + "秒");
               EmailFrame.sendFlag = 1;
               EmailFrame.send.setText(i + "封发送完毕！");
               SetListView.clearFlag=1;//邮件列表窗体的清除按钮能点击
               SetListView.removeButton_flag=1;//单个清除按钮能点击
               EmailFrame.allFlag=1;//添加能点击
               EmailFrame.ChangeFlag=1;//单例按钮可以点击
               EmailFrame.sendCount.setEditable(true);//设置可以编辑
               SetListView.input.setEditable(true);//邮件列表窗体的文本框不能点击
           }
           else{//没链接
               EmailFrame.sendFlag = 1;
               EmailFrame.allFlag=0;//添加不能点击
               EmailFrame.ChangeFlag=0;//单例按钮不可以点击
               EmailFrame.sendCount.setEditable(false);//设置可以编辑
               SetListView.input.setEditable(false);//邮件列表窗体的文本框不能点击
               SetListView.clearFlag=-1;//邮件列表窗体的清除按钮不能点击
               SetListView.removeButton_flag=0;//单个清除按钮不能点击
               EmailFrame.send.setText("您的网络未连接哦~");
           }
             System.gc();
         });
        thread.start();
    }
}