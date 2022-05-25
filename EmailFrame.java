import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;
import jdk.nashorn.internal.scripts.JO;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTextUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author 孙振寰
 */
public class EmailFrame extends JFrame {
    Point p=new Point();
    static int ChangeFlag=1;//单例按钮可以点击
    static int allFlag=1;//表示可以点击变成多例
    static long start;
    Color smallColor=new Color(0,0,255);
    int smallWidth=30,smallHeight=30,smallX=0,smallY=25;
    final JLabel smallLabel=new JLabel(){
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(smallColor);
            g.fillOval(0,0,smallWidth,smallHeight);
            g.setColor(Color.red);
            g.setFont(new Font("姚体",Font.PLAIN,30));
            g.drawString("—",smallX,smallY);
        }
    };
    Color exitColor=new Color(0,255,0);
    int exitWidth=30,exitHeight=30,exitX=7,exitY=25;
    final JLabel exit=new JLabel()
    {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(exitColor);
            g.fillOval(0,0,exitWidth,exitHeight);
            g.setColor(Color.white);
            g.setFont(new Font("姚体",Font.PLAIN,25));
            g.drawString("X",exitX,exitY);
        }
    };
    final Dimension toolkit= Toolkit.getDefaultToolkit().getScreenSize();
    final int width=toolkit.width/2;
    final int height=toolkit.height/2;
    final JLabel hiticon=new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//使用图标.png").getScaledInstance(100,100,0)));
    final JButton Change=new JButton("改为单例模式");
    final JTextField QQ=new JTextField();//自己邮箱号码
    final JTextField AuthorizationCode=new JTextField();//授权码
    final JTextField Addressee=new JTextField();//对方邮箱
    final JTextArea Message=new JTextArea();//发送的内容
    final JLabel MailTheme=new JLabel("邮件主题:");//邮件主题
    final JTextField ThemeField=new JTextField();//邮件主题填写框
    final JLabel sendName=new JLabel("发送人:");//发送人
    final JTextField sendNameField=new JTextField();//发送人框
    final JScrollPane scrollPane=new JScrollPane(Message);
    final static JButton send=new JButton("发送");
    final JButton addQQEmail=new JButton("改为多例模式向多个联系人发送邮件");
    final JLabel codeCount=new JLabel();//记录授权码位数
    final JLabel thisModel=new JLabel();
    static boolean isSingleCase=true;//判断是否为单例模式
    static Set<String>AddresseeList=new HashSet<>();//存储所有邮件信息
    static JTextField sendCount=new JTextField("1");
    Image img=Toolkit.getDefaultToolkit().createImage("images//邮件背景.jpg");
    final JLabel Background=new JLabel(new ImageIcon(img));
    final JLabel MyQQLabel=new JLabel("自己的QQ邮箱号:");
    final JLabel code=new JLabel("授权码:");
    final JLabel AdRes=new JLabel("别人QQ邮箱号:");
    String str="";//记录邮件图标上方信息
    static int emailIconFlag=0;//记录邮件是否可以点击
    static int sendFlag=1;//记录可以发送还是不可以
    final JLabel emailIcon=new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//邮箱图片.png")));
    final JLabel hit=new JLabel()
    {
        //画按钮
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g1=(Graphics2D) g;
            g1.setColor(new Color(0,240,0,150));
            g1.fillOval(0,0,30,30);
            g1.setColor(Color.white);
            g1.setFont(new Font("姚体",Font.BOLD,20));
            g1.drawString("?",10,20);
        }
    };
    static int addFlag=1;//代表添加
    public EmailFrame()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width,height);
        setLocationRelativeTo(null);
        //设置让平台选择窗口位置
//        setLocationByPlatform(true);
        setLayout(null);
//        setAlwaysOnTop(true);//坏处就是对话框在最底层你看不到也点不了任何东西
        setResizable(false);
        setUndecorated(true);

        //背景
        Background.setSize(this.getWidth(), this.getHeight());

        //多个联系人邮箱图片
        emailIcon.setSize(80,55);
        emailIcon.setToolTipText("邮件列表");
//        emailIcon.setBorder(BorderFactory.createLineBorder(Color.red));
        emailIcon.setLocation((int)(this.getWidth()-emailIcon.getWidth()*2.7), (int)(this.getHeight()-emailIcon.getHeight()*1.2));
        //添加鼠标事件
        emailIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                    if (emailIconFlag==0)JOptionPane.showMessageDialog(null, "单例模式下是没有列表联系人的呢");
                    else if (emailIconFlag==1)
                    {
                        //弹出一个存储多个邮箱联系人的窗口视图
                        new SetListView(null,"",true);
                    }
            }
        });
        //退出按钮
        exit.setSize(40,40);
        exit.setToolTipText("关闭");
//        exit.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        exit.setLocation(getWidth()-exit.getWidth(),0);
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if (send.getText().contains("当前正在")){//如果包含这个文本说明正在发送中
                        int op=JOptionPane.showConfirmDialog(null,"你确定要退出吗？退出后邮箱可能会发送失败","退出",JOptionPane.YES_NO_OPTION);
                        if (op==JOptionPane.YES_OPTION)System.exit(0);//程序完全结束
                    }
                    else {
                        int op=JOptionPane.showConfirmDialog(null,"当前没有任务在执行\n你确定要退出吗？","退出",JOptionPane.YES_NO_OPTION);
                        if (op==JOptionPane.YES_OPTION)System.exit(0);//程序完全结束
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                exitColor=new Color(255,0,0);
                exitWidth=40;
                exitHeight=40;
                exitX=13;
                exitY=28;
                exit.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                exitColor=new Color(0,255,0);
                exitWidth=30;
                exitHeight=30;
                exitX=7;
                exitY=25;
                exit.repaint();
            }
        });


        //缩小按钮
        smallLabel.setSize(40,40);
        smallLabel.setToolTipText("最小化");
//        smallLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        smallLabel.setLocation(getWidth()-exit.getWidth()-smallLabel.getWidth(),0);
        smallLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                    setExtendedState(ICONIFIED);//最小化
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                smallColor=new Color(255,255,0);
                smallWidth=40;
                smallHeight=40;
                smallX=5;
                smallY=30;
                smallLabel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                smallColor=new Color(0,0,255);
                smallWidth=30;
                smallHeight=30;
                smallX=0;
                smallY=25;
                smallLabel.repaint();
            }
        });
        

        //发送次数
        sendCount.setSize(100,30);
        sendCount.setFont(new Font("姚体",Font.PLAIN,14));
        sendCount.setForeground(Color.RED);
        sendCount.setLocation(this.getWidth()-sendCount.getWidth()*2,(int)(this.getHeight()/1.5));

        //使用图标
        hiticon.setSize(120,120);
        hiticon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hiticon.setLocation(20,getHeight()/4);
        hiticon.setToolTipText("查看操作步骤");
//        hiticon.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        hiticon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ImageIcon icon=new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//操作步骤1.png").getScaledInstance(800,600,300));
                ImageIcon icon1=new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//操作步骤2.png").getScaledInstance(800,600,300));
                JOptionPane.showMessageDialog(null,"首先打开您的QQ邮箱设置","步骤1",JOptionPane.WARNING_MESSAGE,icon);
                JOptionPane.showMessageDialog(null,"找到授权码然后重新输入qq密码就可获得授权码\n没有打开SMTP的必须要打开","步骤2",JOptionPane.WARNING_MESSAGE,icon1);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                hiticon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//使用图标.png").getScaledInstance(110,110,0)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                hiticon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images//使用图标.png").getScaledInstance(100,100,0)));

            }
        });


        //主题标签
        MailTheme.setSize(100,30);
        MailTheme.setForeground(Color.white);
        MailTheme.setFont(new Font("宋体",Font.PLAIN,20));
        MailTheme.setLocation(this.getWidth()/3-MailTheme.getWidth()/2-80,20);
//        MailTheme.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        //主题框
        ThemeField.setSize(180,30);
        ThemeField.setSelectedTextColor(Color.MAGENTA);
        ThemeField.setOpaque(false);
        ThemeField.setCaret(new BasicTextUI.BasicCaret());
        ThemeField.setCaretColor(Color.white);
        ThemeField.setForeground(Color.PINK);
        ThemeField.setFont(new Font("宋体",Font.PLAIN,20));
        ThemeField.setLocation((int)(this.getWidth()/2-ThemeField.getWidth()/2)-100,20);
//        ThemeField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        ThemeField.setBorder(null);

        //发送人标签
        sendName.setSize(100,30);
        sendName.setForeground(Color.WHITE);
        sendName.setFont(new Font("宋体",Font.PLAIN,20));
        sendName.setLocation(this.getWidth()/2+30,20);
//        sendName.setBorder(BorderFactory.createLineBorder(Color.BLUE));


        //发送人框
        sendNameField.setSize(180,30);
        sendNameField.setForeground(Color.PINK);
        sendNameField.setCaret(new BasicTextUI.BasicCaret());
        sendNameField.setCaretColor(Color.white);
        sendNameField.setOpaque(false);
        sendNameField.setSelectedTextColor(Color.MAGENTA);
        sendNameField.setFont(new Font("宋体",Font.PLAIN,20));
        sendNameField.setLocation((int)(this.getWidth()/1.6),20);
//        sendNameField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        sendNameField.setBorder(null);

        //改为单例
        Change.setSize(150,30);
        Change.setLocation((int)(this.getWidth()/1.5+Change.getWidth()-60),this.getHeight()/2-80);
        Change.setFocusPainted(false);
        Change.setBorder(BorderFactory.createEtchedBorder());
        Change.setBackground(Color.orange);
        Change.setFont(new Font("姚体",Font.PLAIN,15));

        //添加发送列表
        addQQEmail.setSize(260,30);
        addQQEmail.setBorder(BorderFactory.createEtchedBorder());
        addQQEmail.setFocusPainted(false);
        addQQEmail.setLocation((int)(this.getWidth()/2+addQQEmail.getWidth()-60), this.getHeight()/2-15);
        addQQEmail.setFont(new Font("姚体",Font.PLAIN,14));
        addQQEmail.setBackground(Color.orange);
        addQQEmail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if (allFlag==1)
                    {
                        if (addFlag==1) {
                            isSingleCase = false;//改成多例模式
                            str="(下方查看多例模式下的联系人列表)";
                            repaint();
                            JOptionPane.showMessageDialog(null, "改成多例模式成功！");
                            Addressee.setText("");
                            addQQEmail.setText("将该邮箱添加至发送列表内(无重复)");
                            addFlag=2;
                            emailIconFlag=1;//使邮箱图标可点击
                        }
                        else if (addFlag==2) {
                            String str = removeSpace(Addressee.getText());
                            if (CheckQQEmail.GetStr(str)) JOptionPane.showMessageDialog(null, "邮件格式错误！");
                            else if (str.equals(""))JOptionPane.showMessageDialog(null, "邮箱号不能为空！");
                            else {
                                //判断集合里面有没有该元素
                                if(AddresseeList.contains(str))//有的话
                                    JOptionPane.showMessageDialog(null, "列表中已经有了"+str+"账号");
                                else {//没有的话
                                    //添加至集合内
                                    AddresseeList.add(str);
                                    JOptionPane.showMessageDialog(null,"添加"+str+"成功！");
                                }
                            }
                        }
                    }
                    else if (allFlag==0)JOptionPane.showMessageDialog(null,"当前信息正在发送中不能点击");
                }
            }
        });
        //当前邮件模式
        thisModel.setSize(180,50);
        thisModel.setForeground(Color.orange);
        thisModel.setFont(new Font("字酷堂匡山楷书 简",Font.PLAIN,16));
        thisModel.setLocation(10,-10);
        Thread Model=new Thread(()->{
           while (true)
           {
               try {
                   if (isSingleCase)
                       thisModel.setText("当前邮件模式——单例");
                   else
                       thisModel.setText("当前邮件模式——多例");
                   Thread.sleep(1);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
        Model.start();//一直监控邮件模式，为多例则表明加入列表按钮被点击

        //QQ
        QQ.setSize(250,45);
        QQ.setForeground(Color.BLUE);
        QQ.setForeground(Color.PINK);
        QQ.setCaret(new BasicTextUI.BasicCaret());
        QQ.setCaretColor(Color.white);
        QQ.setOpaque(false);
        QQ.setSelectedTextColor(Color.MAGENTA);
        QQ.setFont(new Font("宋体",Font.PLAIN,25));
        QQ.setBorder(null);
//        QQ.setBorder(BorderFactory.createLineBorder(Color.red));
        QQ.setLocation(this.getWidth()/2-QQ.getWidth()/2,this.getHeight()/4-QQ.getHeight()/4);

        //QQ标签
        MyQQLabel.setSize(160,30);
        MyQQLabel.setForeground(Color.green);
        MyQQLabel.setFont(new Font("宋体",Font.PLAIN,20));
//        MyQQLabel.setBorder(BorderFactory.createLineBorder(Color.blue));
        MyQQLabel.setLocation(QQ.getX()-MyQQLabel.getWidth(),QQ.getY()+MyQQLabel.getHeight()/4);
        Background.add(MyQQLabel);
        //授权码
        AuthorizationCode.setSize(250,45);
        AuthorizationCode.setForeground(Color.PINK);
        AuthorizationCode.setCaret(new BasicTextUI.BasicCaret());
        AuthorizationCode.setCaretColor(Color.white);
        AuthorizationCode.setOpaque(false);
        AuthorizationCode.setSelectedTextColor(Color.MAGENTA);
        AuthorizationCode.setFont(new Font("宋体",Font.PLAIN,25));
        AuthorizationCode.setBorder(null);
//        AuthorizationCode.setBorder(BorderFactory.createLineBorder(Color.red));
        AuthorizationCode.setLocation(this.getWidth()/2-AuthorizationCode.getWidth()/2,(int)(this.getHeight()/2.7-AuthorizationCode.getHeight()/2.7));

        //授权码标签
        code.setSize(80,30);
        code.setForeground(Color.green);
        code.setFont(new Font("宋体",Font.PLAIN,20));
//        code.setBorder(BorderFactory.createLineBorder(Color.blue));
        code.setLocation(AuthorizationCode.getX()-code.getWidth(),AuthorizationCode.getY()+code.getHeight()/4);

        
        
        
        //授权码位数标签
        codeCount.setSize(150,45);
        codeCount.setForeground(Color.green);
        codeCount.setVisible(false);
        codeCount.setFont(new Font("字酷堂匡山楷书 简",Font.PLAIN,25));
        codeCount.setLocation(AuthorizationCode.getX()+codeCount.getWidth()+100,AuthorizationCode.getY());
//        codeCount.setBorder(BorderFactory.createLineBorder(Color.red));
        Thread count=new Thread(()->{
                while(true)
                {
                    try {
                        codeCount.setVisible(removeSpace(AuthorizationCode.getText()).length() >= 1);
                        if (removeSpace(AuthorizationCode.getText()).length()>16||removeSpace(AuthorizationCode.getText()).length()<16)codeCount.setForeground(Color.red);
                        else codeCount.setForeground(Color.green);
                        codeCount.setText("当前位数"+removeSpace(AuthorizationCode.getText()).length());
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });
        count.start();
        //对方QQ
        Addressee.setSize(250,45);
        Addressee.setForeground(Color.PINK);
        Addressee.setCaret(new BasicTextUI.BasicCaret());
        Addressee.setCaretColor(Color.white);
        Addressee.setOpaque(false);
        Addressee.setSelectedTextColor(Color.MAGENTA);
        Addressee.setFont(new Font("宋体",Font.PLAIN,25));
        Addressee.setBorder(null);
//        Addressee.setBorder(BorderFactory.createLineBorder(Color.red));
        Addressee.setLocation(this.getWidth()/2-Addressee.getWidth()/2, this.getHeight()/2-Addressee.getHeight()/2);

        //收件人标签
        AdRes.setSize(140,30);
        AdRes.setForeground(Color.green);
        AdRes.setFont(new Font("宋体",Font.PLAIN,20));
//        AdRes.setBorder(BorderFactory.createLineBorder(Color.blue));
        AdRes.setLocation(Addressee.getX()-AdRes.getWidth(),Addressee.getY()+AdRes.getHeight()/4);


        //提示
        hit.setSize(30,35);
        hit.setLocation(642,255);
        hit.setToolTipText("不知道请点击我");
        hit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                    JOptionPane.showMessageDialog(null,"你可以单个发送也可以列表形式多个发送\n(左边是单个发送可以不用点击右边直接填写好信息点击发送\n想要多个联系人请每次在左边输入指定邮箱号点击添加进来就行了)","提示",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //信息面板
        scrollPane.setSize(400,100);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.setLocation(this.getWidth()/2-scrollPane.getWidth()/2, (int) (this.getHeight()/1.7));
        scrollPane.getVerticalScrollBar().setUI(new WindowsScrollBarUI());
        //要发送的消息
        Message.setForeground(Color.PINK);
        Message.setCaret(new BasicTextUI.BasicCaret());
        Message.setCaretColor(Color.white);
        Message.setSelectedTextColor(Color.MAGENTA);
        Message.setFont(new Font("宋体",Font.PLAIN,25));
        Message.setCaretColor(Color.MAGENTA);
        Message.setLineWrap(true);

        //发送
        send.setSize(250,50);
        send.setFont(new Font("姚体",Font.PLAIN,20));
        send.setBackground(Color.orange);
        send.setFocusPainted(false);
//        send.setMultiClickThreshhold(200);
        send.setLocation(this.getWidth()/2-send.getWidth()/2,(int)(this.getHeight()/1.2));
        send.setBorder(BorderFactory.createEtchedBorder());

        send.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton()==MouseEvent.BUTTON1)
                {
                    //判断发送人，主题，自己的QQ邮箱，对方邮箱和授权码是否为空！
                    //这个是单个发送的！
                    String MyQQEmail=removeSpace(QQ.getText());//自己邮箱内容
                    String Name=removeSpace(sendNameField.getText());//发送人内容
                    String Theme=removeSpace(ThemeField.getText());//主题内容
                    String addressee=removeSpace(Addressee.getText());//对方内容
                    String code=removeSpace(AuthorizationCode.getText());//授权码内容
                    String Msg=removeSpace(Message.getText());//发送的信息内容
                    String count=removeSpace(sendCount.getText());//发送的次数
                    if (Theme.equals("")) {
                        JOptionPane.showMessageDialog(null, "邮件主题必须要填写");
                    }
                    else if (Name.equals("")) {
                        JOptionPane.showMessageDialog(null, "发送人的名字不能是空");
                    }
                    else if(MyQQEmail.equals("")) {
                        JOptionPane.showMessageDialog(null, "自己的邮箱不能为空！");
                    }
                    else if (CheckQQEmail.GetStr(MyQQEmail)) {
                        JOptionPane.showMessageDialog(null, "邮箱格式错误");
                    }
                    else if (code.equals("")||code.length()!=16) {
                        JOptionPane.showMessageDialog(null, "授权码的长度必须为16位且不能为空！");
                    }
                    else if (addressee.equals("")) {
                        JOptionPane.showMessageDialog(null, "收件人邮箱不能是空！");
                    }
                    else if (CheckQQEmail.GetStr(addressee)) {
                        JOptionPane.showMessageDialog(null, "收件邮箱格式错误");
                    }
                    else if (count.equals("")) {
                        JOptionPane.showMessageDialog(null, "发送的次数不能为空！");
                    }
                    else if(count.equals("0"))JOptionPane.showMessageDialog(null, "您无法发送信息因为你的发送次数为0");
                    else if (!count.matches("\\d+")||count.charAt(0)=='0') {
                        JOptionPane.showMessageDialog(null, "您输入的次数格式有误！");
                    }
                    else {//上述条件都满足了后
                        if(sendFlag==1)
                        {
                            int count1=Integer.parseInt(count);
                            //开始发送
                            if (thisModel.getText().equals("当前邮件模式——单例"))//先判断是单例模式还是多例发送模式,可以用文本内容判断
                            {
                                start=System.currentTimeMillis();
                                try {
                                    SendEmailMethod.flag=1;//改成单例执行for循环
                                    SetListView.clearFlag=-1;//邮件列表窗体的清除按钮不能点击
                                    SetListView.removeButton_flag=0;//单个清除按钮不能点击
                                    allFlag=0;//添加图标不能点击
                                    ChangeFlag=0;//单例按钮可以不可击
                                    sendCount.setEditable(false);//设置不可编辑次数
                                    SetListView.input.setEditable(false);//邮件列表窗体的文本框不能点击
                                    new SendEmailMethod(Theme, Name, Msg, MyQQEmail, code, addressee, null, count1).StartSend();//先传值再调用方法
                                } catch (MessagingException | UnsupportedEncodingException messagingException) {
                                    messagingException.printStackTrace();
                                }
                            }
                            else if (thisModel.getText().equals("当前邮件模式——多例"))
                            {
                                start=System.currentTimeMillis();
                                if (AddresseeList.isEmpty())JOptionPane.showMessageDialog(null,"因为当前模式是多例,所以你必须在列表中添加联系人邮箱");
                                else
                                {
                                    try {
                                        SendEmailMethod.flag=2;//改成多例执行for循环
                                        SetListView.clearFlag=-1;//邮件列表窗体的清除按钮不能点击
                                        SetListView.removeButton_flag=0;//单个清除按钮不能点击
                                        allFlag=0;//添加图标不能点击
                                        ChangeFlag=0;//单例按钮不可以点击
                                        sendCount.setEditable(false);//设置不可编辑次数
                                        SetListView.input.setEditable(false);//邮件列表窗体的文本框不能点击
                                        new SendEmailMethod(Theme, Name, Msg, MyQQEmail, code, null,AddresseeList,count1).StartSend();//先传值再调用方法
                                    } catch (MessagingException | UnsupportedEncodingException messagingException) {
                                        messagingException.printStackTrace();
                                    }
                                }
                            }
                            sendFlag=0;
                        }
                        else {
                            int op=JOptionPane.showConfirmDialog(null,"你确定要取消发送吗？","取消邮件发送",JOptionPane.YES_NO_OPTION);
                            if (op==JOptionPane.YES_OPTION)
                            {
                                if (thisModel.getText().equals("当前邮件模式——单例"))
                                {
                                    sendFlag=1;//马上可以点击
                                    SendEmailMethod.flag=1;//改成单例
                                    SendEmailMethod.thread.stop();
                                    send.setText("已经取消发送");
                                    SetListView.clearFlag=1;//邮件列表窗体的清除按钮能点击
                                    SetListView.removeButton_flag=1;//单个清除按钮能点击
                                    allFlag=1;//添加按钮能点击
                                    ChangeFlag=1;//单例按钮可以点击
                                    sendCount.setEditable(true);//设置可编辑次数
                                    SetListView.input.setEditable(true);//邮件列表窗体的文本框能点击
                                }
                                else if (thisModel.getText().equals("当前邮件模式——多例"))
                                {
                                    sendFlag=1;//马上可以点击
                                    SendEmailMethod.flag=2;//改成相对应的多例模式
                                    SendEmailMethod.thread.stop();
                                    send.setText("已经取消发送");
                                    SetListView.clearFlag=1;//邮件列表窗体的清除按钮能点击
                                    SetListView.removeButton_flag=1;//单个清除按钮能点击
                                    allFlag=1;//添加按钮能点击
                                    ChangeFlag=1;//单例按钮可以点击
                                    sendCount.setEditable(true);//设置不可编辑次数
                                    SetListView.input.setEditable(true);//邮件列表窗体的文本框不能点击
                                }
                            }
                        }
                    }
                }
            }
        });
        Change.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if (ChangeFlag==1)
                    {
                        if (!isSingleCase) {
                            //如果是多例才会出现对话框
                            int op=JOptionPane.showConfirmDialog(null, "您确定要改回单例模式吗？\n此操作会把列表中的信息清除.","改回单例模式",JOptionPane.YES_NO_OPTION);
                            if (op==JOptionPane.YES_OPTION)
                            {
                                isSingleCase = true;//改成单例
                                addQQEmail.setText("改为多例模式向多个联系人发送邮件");//改变添加按钮的文本
                                addFlag=1;//改变标志可以点击多例模式
                                emailIconFlag=0;//使邮箱图标不可点击
                                Addressee.setText("");//变为空
                                AddresseeList.clear();//改成单例后清空集合内的所有信息
//                        System.err.println(AddresseeList);
                                str="";//改成单例后变为空
                                repaint();
                                JOptionPane.showMessageDialog(null, "已改回单例模式");
                            }
                        }
                        else JOptionPane.showMessageDialog(null,"当前模式已经是单例模式");
                    }
                    else if (ChangeFlag==0)JOptionPane.showMessageDialog(null, "当前信息正在发送中不能点击");
                }
            }
        });

        add(Background);
        Background.add(codeCount);
        Background.add(Change);
        Background.add(code);
        Background.add(smallLabel);
        Background.add(sendCount);
        Background.add(hit);
        Background.add(hiticon);
        Background.add(thisModel);
        Background.add(AdRes);
        Background.add(addQQEmail);
        Background.add(sendName);
        Background.add(sendNameField);
        Background.add(ThemeField);
        Background.add(emailIcon);
        Background.add(MailTheme);
        Background.add(exit);
        Background.add(send);
        Background.add(scrollPane);
        Background.add(Addressee);
        Background.add(QQ);
        Background.add(AuthorizationCode);


        setVisible(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                //获取按下的坐标点
                if (e.getY()<height/2) {
                    p.x=e.getX();
                    p.y=e.getY();
                }
                System.err.println(e.getX()+"\t"+e.getY());
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (e.getY()<height/2) {
                    //拖动时产生一个新的
                    Point newPoint=getLocation();
                    //放下时定位一个新的位置
                    setLocation(newPoint.x+e.getX()-p.x, newPoint.y+e.getY()-p.y);
                }
            }
        });
    }
    //去除空格的方法
    public String removeSpace(String str)
    {
        return str.replaceAll(" ","");
    }
    public static void main(String[] args) {
        new EmailFrame();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.ORANGE);
        g.drawLine(ThemeField.getX(),ThemeField.getY()+ThemeField.getHeight(),ThemeField.getX()+ThemeField.getWidth(),ThemeField.getY()+ThemeField.getHeight());//主题框线X坐标就是他的x坐标Y坐标就是高度加上X坐标
        //发送人框
        g.setColor(Color.ORANGE);
        g.drawLine(sendNameField.getX(),sendNameField.getY()+sendNameField.getHeight(),sendNameField.getX()+sendNameField.getWidth(),sendNameField.getY()+sendNameField.getHeight());
        g.drawLine(QQ.getX(),QQ.getY()+QQ.getHeight(), QQ.getX()+QQ.getWidth(),QQ.getY()+QQ.getHeight());
        g.drawLine(AuthorizationCode.getX(),AuthorizationCode.getY()+AuthorizationCode.getHeight(),AuthorizationCode.getX()+AuthorizationCode.getWidth(),AuthorizationCode.getY()+AuthorizationCode.getHeight());
        g.drawLine(Addressee.getX(),Addressee.getY()+Addressee.getHeight(),Addressee.getX()+Addressee.getWidth(),Addressee.getY()+Addressee.getHeight());
        g.setFont(new Font("姚体",Font.PLAIN,20));
        g.drawString("信",241,327);
        g.drawString("息",241,357);
        g.drawString("内",241,387);
        g.drawString("容",241,417);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("姚体",Font.PLAIN,20));
        g.drawString("发送",712,383);
        g.drawString("次",863,380);
        g.setColor(Color.white);
        g.setFont(new Font("姚体",Font.PLAIN,20));
        g.drawString(str,630,458);
        g.setColor(Color.green);
        g.setFont(new Font("姚体",Font.PLAIN,13));
        g.drawString("不知道操作可查看上方具体步骤",0,290);
    }
}
