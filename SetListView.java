import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  SetListView set集合联系人视图
 */
public class SetListView extends JDialog {
    boolean listFlag=true;
    static int removeButton_flag = 1;
    static int clearFlag=1;//表示点击可以清除
    int k=0;//用于循环
    int j=0;//用于线程
    Point p=new Point();
    JButton allRemoveButton=new JButton("删除所有");
    Vector<String>vector=new Vector<>();
    Map<Integer, String>map= new HashMap<>();//存储序号和value
    final static JTextField input=new JTextField();//输入序号
    static int sequenceNumber =1;//标记联系人序号(key)
    final int width= Toolkit.getDefaultToolkit().getScreenSize().width/3;
    final int height= Toolkit.getDefaultToolkit().getScreenSize().height/3;
    final Image image=Toolkit.getDefaultToolkit().createImage("images//集合背景.png").getScaledInstance(width, height,0);
    final JButton removeButton=new JButton("删除");
    final JPanel content=new JPanel()
    {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(Color.white);
            g.setFont(new Font("姚体",Font.PLAIN,25));
            g.drawString("删除第",getWidth()/4,getHeight()-22);
            g.drawString("行", getWidth()/2,getHeight()-22);
        }
    };
    final  JLabel iconLabel=new JLabel(new ImageIcon(image));
    final JTextArea listArea=new JTextArea();
    final JScrollPane scroll=new JScrollPane(listArea);
    Color color=new Color(0,255,0);
    JLabel listCount=new JLabel();//记录集合内的人数标签
    static int count=0;//记录人数
    Set<String>list=EmailFrame.AddresseeList;//获取前者集合内容
    final JLabel exit=new JLabel()
    {
        public void paint(Graphics g)
        {
            g.setColor(color);
            g.fillRect(0,0,30,30);
            g.setColor(Color.blue);
            g.setFont(new Font("姚体",Font.PLAIN,30));
            g.drawString("X",7,25);
        }
    };
    public SetListView(JFrame jFrame,String title,boolean visible)
    {
        super(jFrame,title,visible);
        vector.addAll(list);//将列表中的元素添加到vector里面来
        System.err.println("Vector:"+vector);
        System.err.println(width+"\t"+height);
        setSize(width,height);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        setUndecorated(true);

        //主体面板
        content.setSize(getSize());
        content.setBackground(Color.black);
        content.setLayout(null);

        //序号框
        input.setSize(80,30);
        input.setFont(new Font("姚体",Font.PLAIN,20));
        input.setLocation((int)(getWidth()/2.7),getHeight()-input.getHeight()-15);
        input.setForeground(Color.RED);


        //人数标签
        listCount.setSize(150,30);
        listCount.setLocation(10,getHeight()-listCount.getHeight());
        listCount.setFont(new Font("姚体",Font.PLAIN,20));
        listCount.setForeground(Color.PINK);


        //背景
        iconLabel.setSize(getSize());
        iconLabel.setLocation(0,0);


        //退出按钮
        exit.setSize(30,30);
        exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exit.setLocation(getSize().width-exit.getWidth()-10,0);
        exit.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        exit.setToolTipText("关闭此窗口");
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton()==MouseEvent.BUTTON1) {
                    dispose();
                    sequenceNumber=1;//窗口关闭后重新赋值才会不变
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                color=new Color(255,0,0);
                exit.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                color=new Color(0,255,0);
                exit.repaint();
            }
        });
        //删除单个
        removeButton.setSize(100,40);
        removeButton.setBackground(Color.orange);
        removeButton.setBorder(BorderFactory.createEtchedBorder());
        removeButton.setFont(new Font("姚体",Font.PLAIN,25));
        removeButton.setForeground(Color.RED);
        removeButton.setFocusPainted(false);
        removeButton.setLocation((int)(getWidth()/1.8),getHeight()-removeButton.getHeight()-15);
        
        //删除所有按钮
        allRemoveButton.setSize(150,40);
        allRemoveButton.setBackground(Color.orange);
        allRemoveButton.setBorder(BorderFactory.createEtchedBorder());
        allRemoveButton.setFont(new Font("姚体",Font.PLAIN,25));
        allRemoveButton.setForeground(Color.RED);
        allRemoveButton.setFocusPainted(false);
        allRemoveButton.setLocation((int)(getWidth()/1.3),getHeight()-allRemoveButton.getHeight()-15);

        //文本域
        listArea.setLineWrap(true);
        listArea.setEditable(false);
        listArea.setForeground(Color.BLACK);
        listArea.setFont(new Font("姚体",Font.PLAIN,25));
        listArea.setCaretColor(Color.blue);
        listArea.setCaret(new BasicTextUI.BasicCaret());
        listArea.setSelectionColor(Color.DARK_GRAY);
        listArea.setSelectedTextColor(Color.white);
        if (list.isEmpty())//是空集合
        {
            listArea.setText("您当前没有添加任何联系人！");
            listCount.setText("当前没有联系人");

        }
        else
        {//只有不是空集合就可以执行操作
            Thread t=new Thread(()->{
                while (listFlag)
                {
                    if (k<list.size())
                    {
                        map.put(sequenceNumber,vector.get(k));//存到map里面
                        listArea.append(sequenceNumber + ":" + vector.get(k) + '\n');//用Map进行存储相对应的key取得value
                        sequenceNumber++;
                        //因为窗口每次打开的时候都会++，所以必须要在关闭的时候重新赋值
                        k++;
                    }
                    else listFlag=false;//停止
                }
            });
            t.start();
            Thread thread=new Thread(()->{
               while (true)
               {
//                   count=listArea.getLineCount()-1;//获取多少行一行一个联系人
                   count=list.size();
                   if (list.size()==0)listCount.setText("当前没有联系人");//(count-1)防止多出一个
                   else listCount.setText("当前"+count+"个联系人");//(count-1)防止多出一个
               }
            });
            thread.start();
        }
//        Thread thread=new Thread(()->
//        {
//           while(true)
//           {
//               try {
//                   System.err.println(map);
//                   Thread.sleep(2000);
//               } catch (InterruptedException e) {
//                   e.printStackTrace();
//               }
//           }
//        });
//        thread.start();
        //滚动面板
        scroll.setSize((int)(getSize().width/1.5),(int)(getSize().height/1.5));
        scroll.getVerticalScrollBar().setUI(new WindowsScrollBarUI());
        scroll.setLocation(getSize().width/2-scroll.getWidth()/2,getSize().height/2-scroll.getHeight()/2);


        content.add(iconLabel);
        iconLabel.add(input);
        iconLabel.add(allRemoveButton);
        iconLabel.add(removeButton);
        iconLabel.add(listCount);
        iconLabel.add(exit);
        iconLabel.add(scroll);
        add(content);
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!map.isEmpty()) {
                    if (removeButton_flag==1) {
                        String str = input.getText().replaceAll(" ", "");
                        if (input.getText().equals("")) JOptionPane.showMessageDialog(null, "您必须输入里面相对应的头序号");
                        else if (Integer.parseInt(str) > map.size()) JOptionPane.showMessageDialog(null, "不能输入大于行数的值");
                        else {
                            if (str.charAt(0) == '0') JOptionPane.showMessageDialog(null, "没有序号0最小序号是1");
                            else if (str.matches("\\d+"))//满足
                            {
                                System.err.println(Integer.parseInt(str));
                                System.err.println("key是:" + str + "\tvalue是:" + map.get(Integer.parseInt(str)));
                                map.remove(Integer.parseInt(str));//根据key取得value从map中移出相对应的序号
                                list.remove(vector.get(Integer.parseInt(str) - 1));//移出相对应的内容,这时vector也会相对应的移出
                                System.err.println("list:" + list);
                                input.setText("");//点击了之后变成空
                                listFlag = true;
                                j = 1;
                                new UpdateMap().Update();
                                count--;
                                JOptionPane.showMessageDialog(null, "是数字");
                            }
                        }
                    }
                    else if (removeButton_flag==0)JOptionPane.showMessageDialog(null, "当前信息正在发送中不能点击");
                }
                else JOptionPane.showMessageDialog(null,"当前并没有任何联系人");
            }
        });
        allRemoveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if (!map.isEmpty())
                    {
                        if (clearFlag==1)
                        {
                            int op=JOptionPane.showConfirmDialog(null,"您确定要全部清除吗？","清除全部",JOptionPane.YES_NO_OPTION);
                            if (op==JOptionPane.YES_OPTION)
                            {
                                list.clear();//清除list中的东西
                                EmailFrame.AddresseeList.clear();
                                map.clear();
                                new UpdateMap().v.clear();
                                new UpdateMap().seq=1;
                                new UpdateMap().i=0;
                                clearFlag=0;
                                sequenceNumber=1;
                                allRemoveButton.setText("正在清除中..");
                                new UpdateMap().clear();
                            }
                        }
                        else if (clearFlag==-1)JOptionPane.showMessageDialog(null, "当前信息正在发送中不能点击");

                    }
                    else JOptionPane.showMessageDialog(null,"当前并没有任何联系人");
                }
            }
        });
        //滚动事件
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
        setVisible(true);
    }

//    public static void main(String[] args) {
//        new SetListView(null,"",true);
//    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("宋体",Font.PLAIN,30));
        g.setColor(Color.white);
        g.drawString("下方是您要发送邮件的人",getSize().width/2-180,50);
    }
    class UpdateMap{
        int i=0;
        int seq=1;//序号
        Vector<String>v=new Vector<>();
        public void Update()
        {
            v.addAll(list);//先把当前list中的元素添加到一个新的vector中去
            if (j==1) {
                listArea.setText("");
                Thread updateThread = new Thread(() -> {
                    while (true) {
                        for (;i<v.size();i++) {
                            listArea.append(seq + ":" +v.get(i)+ "\n");
                            seq++;
                        }

                    }
                });
                updateThread.start();
                j=0;
            }
        }
        public void clear()
        {
            try {
                listArea.setText("");
                JOptionPane.showMessageDialog(null,"全部清除成功！");
                clearFlag=1;
                allRemoveButton.setText("删除所有");
                Thread.sleep(Math.round(Math.random()*2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
