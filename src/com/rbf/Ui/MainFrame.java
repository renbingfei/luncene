package com.rbf.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.Rectangle;
import javax.swing.table.*;

import org.jvnet.substance.skin.SubstanceBusinessLookAndFeel;
import org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel;

import com.rbf.Lucene.LuceneProc;




public class MainFrame extends JFrame implements ActionListener {
    public static PrintWriter pw = null; //
    String srcpath, indexpath = ""; //srcpath要索引的文档路径，indexpath索引存储路径。
    String queryString = ""; //查询词。
    int rows = 5; //结果输出每页5行。
    int begin = 0; //从第一个结果开始显示。
    ArrayList reslist = new ArrayList(); //存放查询后得到的路径和score。
    String logname = "Retrieve.log";
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JTextField jTextField1 = new JTextField();
    JTextField jTextField2 = new JTextField();
    JTextField jTextField3 = new JTextField();
    JTextField jTextField4 = new JTextField();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    JButton jButton4 = new JButton();
    JButton jButton5 = new JButton();
    JButton jButton6 = new JButton();
    JPanel jPanel1 = new JPanel(new BorderLayout());
    int height;
    int width;
    MyTable table = new MyTable();
    JTable restable = new JTable(table);
    TableColumn column = null;

    public MainFrame() {
        super();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                logname)), true); //建立输出流
        setTitle("小型文档检索系统");
        this.setVisible(true);
        setSize(400, 330);
        setLocation(400, 150);
        width = this.getWidth();
        height = this.getHeight();
        setLayout(null);

        Container con = this.getContentPane();
        jLabel1.setText("文档路径");
        jLabel1.setBounds(new Rectangle(16, 15, 66, 19));
        jLabel2.setText("索引路径");
        jLabel2.setBounds(new Rectangle(16, 38, 67, 20));
        jLabel3.setText("查询关键字");
        jLabel3.setBounds(new Rectangle(16, 69, 81, 24));
        jLabel4.setText("查询结果如下");
        jLabel4.setBounds(new Rectangle(16, 108, 107, 19));
        jTextField1.setBounds(new Rectangle(82, 14, 207, 20));
        jTextField2.setBounds(new Rectangle(82, 38, 207, 20));
        jTextField2.setText("c:\\index\\");
        jTextField3.setBounds(new Rectangle(87, 69, 201, 24));
        jTextField4.setBounds(new Rectangle(172, 106, 210, 20));
        jTextField4.setBackground(this.getBackground());
        jTextField4.setBorder(BorderFactory.createEmptyBorder());
        jTextField4.setEditable(false);
        jButton1.setBounds(new Rectangle(293, 19, 92, 27));
        jButton1.setText("建立索引");
        jButton1.addActionListener(new MainFrame_jButton1_actionAdapter(this));
        jButton2.setBounds(new Rectangle(293, 69, 92, 26));
        jButton2.setText("开始查询");
        jButton2.addActionListener(new MainFrame_jButton2_actionAdapter(this));
        jButton3.setBounds(new Rectangle(116, 267, 74, 23));
        jButton3.setText("上一页");
        jButton3.addActionListener(this);
        jButton4.setBounds(new Rectangle(16, 267, 78, 23));
        jButton4.setText("首页");
        jButton4.addActionListener(this);
        jButton5.setBounds(new Rectangle(213, 267, 76, 22));
        jButton5.setText("下一页");
        jButton5.addActionListener(this);
        jButton6.setBounds(new Rectangle(312, 267, 72, 21));
        jButton6.setText("尾页");
        jButton6.addActionListener(this);
        jPanel1.setBackground(Color.BLUE);
        jPanel1.setBounds(new Rectangle(16, 127, 365, 133));
        //给显示结果的table设定列宽。
        for (int i = 0; i < 3; i++) {
            column = restable.getColumnModel().getColumn(i);
            switch (i) {
            case 0:
                column.setPreferredWidth(width / 8);
                break;
            case 1:
                column.setPreferredWidth(width * 280 / 400);
                break;
            case 2:
                column.setPreferredWidth(width * 70 / 400);
                break;
            }
        }
        //给显示结果的table设定行高。
        restable.setRowHeight(23);
        restable.setColumnSelectionAllowed(false);
        restable.setRowSelectionAllowed(true);
        restable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        restable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    if(restable.getSelectedColumn()!=1)
                        return;
                    String fpath = (String) table.getValueAt(restable.getSelectedRow(), restable.getSelectedColumn());
                    Runtime   r   =   Runtime.getRuntime();
                    try {
                        r.exec("cmd   /c   start   " + fpath);
                    } catch (IOException ex) {
                    }
                }
            }
        });

        jPanel1.add(restable.getTableHeader(), BorderLayout.NORTH);
        jPanel1.add(restable, BorderLayout.CENTER);
        con.addComponentListener(new MainFrame_con_componentAdapter(this)); //自动调整大小。
        con.add(jLabel1);
        con.add(jLabel2);
        con.add(jTextField1);
        con.add(jTextField2);
        con.add(jLabel3);
        con.add(jTextField3);
        con.add(jTextField4);
        con.add(jLabel4);
        con.add(jButton4);
        con.add(jButton3);
        con.add(jButton5);
        con.add(jButton2);
        con.add(jButton1);
        con.add(jButton6);
        con.add(jPanel1);
        WindowDestroyer myListener = new WindowDestroyer();
        addWindowListener(myListener);
    }

    public static void main(String[] args) {
    	//设置外观感觉
    			JFrame.setDefaultLookAndFeelDecorated(true);
    			JDialog.setDefaultLookAndFeelDecorated(true);
    			try {
    				//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    				UIManager.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
    				UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
    				
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
        MainFrame mf = new MainFrame();
        mf.setVisible(true);
    }

    //Auto resize.
    public void con_componentResized(ComponentEvent e) {
        int oldheight = height;
        int oldwidth = width;
        height = this.getHeight();
        width = this.getWidth();
        jLabel1.setSize(jLabel1.getWidth() * width / oldwidth,
                        jLabel1.getHeight() * height / oldheight);
        jLabel2.setSize(jLabel2.getWidth() * width / oldwidth,
                        jLabel2.getHeight() * height / oldheight);
        jLabel3.setSize(jLabel3.getWidth() * width / oldwidth,
                        jLabel3.getHeight() * height / oldheight);
        jLabel4.setSize(jLabel4.getWidth() * width / oldwidth,
                        jLabel4.getHeight() * height / oldheight);
        jTextField1.setSize(jTextField1.getWidth() * width / oldwidth,
                            jTextField1.getHeight() * height / oldheight);
        jTextField2.setSize(jTextField2.getWidth() * width / oldwidth,
                            jTextField2.getHeight() * height / oldheight);
        jTextField3.setSize(jTextField3.getWidth() * width / oldwidth,
                            jTextField3.getHeight() * height / oldheight);
        jTextField4.setSize(jTextField4.getWidth() * width / oldwidth,
                            jTextField4.getHeight() * height / oldheight);
        jButton1.setSize(jButton1.getWidth() * width / oldwidth,
                         jButton1.getHeight() * height / oldheight);
        jButton2.setSize(jButton2.getWidth() * width / oldwidth,
                         jButton2.getHeight() * height / oldheight);
        jButton3.setSize(jButton3.getWidth() * width / oldwidth,
                         jButton3.getHeight() * height / oldheight);
        jButton4.setSize(jButton4.getWidth() * width / oldwidth,
                         jButton4.getHeight() * height / oldheight);
        jButton5.setSize(jButton5.getWidth() * width / oldwidth,
                         jButton5.getHeight() * height / oldheight);
        jButton6.setSize(jButton6.getWidth() * width / oldwidth,
                         jButton6.getHeight() * height / oldheight);
        jPanel1.setSize(jPanel1.getWidth() * width / oldwidth,
                        jPanel1.getHeight() * height / oldheight);
        jLabel1.setLocation(jLabel1.getLocation().x * width / oldwidth,
                            jLabel1.getLocation().y * height / oldheight);
        jLabel2.setLocation(jLabel2.getLocation().x * width / oldwidth,
                            jLabel2.getLocation().y * height / oldheight);
        jLabel3.setLocation(jLabel3.getLocation().x * width / oldwidth,
                            jLabel3.getLocation().y * height / oldheight);
        jLabel4.setLocation(jLabel4.getLocation().x * width / oldwidth,
                            jLabel4.getLocation().y * height / oldheight);
        jTextField1.setLocation(jTextField1.getLocation().x * width / oldwidth,
                                jTextField1.getLocation().y * height /
                                oldheight);
        jTextField2.setLocation(jTextField2.getLocation().x * width / oldwidth,
                                jTextField2.getLocation().y * height /
                                oldheight);
        jTextField3.setLocation(jTextField3.getLocation().x * width / oldwidth,
                                jTextField3.getLocation().y * height /
                                oldheight);
        jTextField4.setLocation(jTextField4.getLocation().x * width / oldwidth,
                                jTextField4.getLocation().y * height /
                                oldheight);
        jButton1.setLocation(jButton1.getLocation().x * width / oldwidth,
                             jButton1.getLocation().y * height / oldheight);
        jButton2.setLocation(jButton2.getLocation().x * width / oldwidth,
                             jButton2.getLocation().y * height / oldheight);
        jButton3.setLocation(jButton3.getLocation().x * width / oldwidth,
                             jButton3.getLocation().y * height / oldheight);
        jButton4.setLocation(jButton4.getLocation().x * width / oldwidth,
                             jButton4.getLocation().y * height / oldheight);
        jButton5.setLocation(jButton5.getLocation().x * width / oldwidth,
                             jButton5.getLocation().y * height / oldheight);
        jButton6.setLocation(jButton6.getLocation().x * width / oldwidth,
                             jButton6.getLocation().y * height / oldheight);
        jPanel1.setLocation(jPanel1.getLocation().x * width / oldwidth,
                            jPanel1.getLocation().y * height / oldheight);
        restable.setSize(restable.getSize().width * width / oldwidth,
                         restable.getSize().height * height / oldheight);
        for (int i = 0; i < 3; i++) {
            column = restable.getColumnModel().getColumn(i);
            switch (i) {
            case 0:
                column.setPreferredWidth(width / 8);
                break;
            case 1:
                column.setPreferredWidth(width * 280 / 400);
                break;
            case 2:
                column.setPreferredWidth(width * 70 / 400);
                break;
            }
        }
        restable.setRowHeight(restable.getRowHeight() * height / oldheight);
    }

    public void jButton1_actionPerformed(ActionEvent e) {
        String srcpath, indexpath;
        srcpath = jTextField1.getText();
        indexpath = jTextField2.getText();
        switch (LuceneProc.CreateIndex(srcpath, indexpath)) {
        case 0:
            JOptionPane.showMessageDialog(this, "索引已经建好!");
            break;
        case 1:
        case 2:
        default:
            JOptionPane.showMessageDialog(this, "建立索引出错，请检查路径是否正确！");
        }
    }

    public void jButton2_actionPerformed(ActionEvent e) {
        indexpath = jTextField2.getText();
        queryString = jTextField3.getText();
        reslist.clear();
        if (queryString.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "请输入查询词！");
        } else {
            switch (LuceneProc.SearchProc(indexpath, queryString, reslist)) {
            case 0:
                jTextField4.setText("查询结果共" + reslist.size() + "条,当前为第" +
                                    (begin / rows + 1) + "页");
                String[] ss = new String[2];
                table.clear();
                begin = 0;
                for (int i = begin; i < reslist.size() && i < begin + rows; i++) {
                    ss = (String[]) reslist.get(i);
                    table.setValueAt(1 + i, i - begin, 0);
                    table.setValueAt(ss[0], i - begin, 1);
                    table.setValueAt(ss[1], i - begin, 2);
                }
                break;
            case 1:
            case 2:
            default:
                JOptionPane.showMessageDialog(this, "检索出错，请检查索引是否存在，路径是否正确！");
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (reslist.size() == 0) {
            JOptionPane.showMessageDialog(this, "你还没有查询或查询结果为空！");
        } else if (e.getActionCommand().equals("首页")) {
            jTextField4.setText("查询结果共" + reslist.size() + "条,当前为第" +
                                (begin / rows + 1) + "页");
            begin = 0;
            String[] ss = new String[2];
            table.clear();
            for (int i = begin; i < reslist.size() && i < begin + rows; i++) {
                ss = (String[]) reslist.get(i);
                table.setValueAt(1 + i, i - begin, 0);
                table.setValueAt(ss[0], i - begin, 1);
                table.setValueAt(ss[1], i - begin, 2);
            }
        } else if (e.getActionCommand().equals("下一页")) {
            if (begin + rows >= reslist.size()) {
                JOptionPane.showMessageDialog(this, "已经是最后一页！");
                return;
            }
            begin += rows;
            jTextField4.setText("查询结果共" + reslist.size() + "条,当前为第" +
                                (begin / rows + 1) + "页");
            String[] ss = new String[2];
            table.clear();
            for (int i = begin; i < reslist.size() && i < begin + rows; i++) {
                ss = (String[]) reslist.get(i);
                table.setValueAt(1 + i, i - begin, 0);
                table.setValueAt(ss[0], i - begin, 1);
                table.setValueAt(ss[1], i - begin, 2);
            }
        } else if (e.getActionCommand().equals("上一页")) {
            if (begin == 0) {
                JOptionPane.showMessageDialog(this, "已经是第一页！");
                return;
            }
            begin -= rows;
            jTextField4.setText("查询结果共" + reslist.size() + "条,当前为第" +
                                (begin / rows + 1) + "页");
            String[] ss = new String[2];
            table.clear();
            for (int i = begin; i < reslist.size() && i < begin + rows; i++) {
                ss = (String[]) reslist.get(i);
                table.setValueAt(1 + i, i - begin, 0);
                table.setValueAt(ss[0], i - begin, 1);
                table.setValueAt(ss[1], i - begin, 2);
            }
        } else if (e.getActionCommand().equals("尾页")) {
            begin = reslist.size() / rows * rows;
            if (reslist.size() % rows == 0) {
                begin -= rows;
            }
            jTextField4.setText("查询结果共" + reslist.size() + "条,当前为第" +
                                (begin / rows + 1) + "页");
            String[] ss = new String[2];
            table.clear();
            for (int i = begin; i < reslist.size() && i < begin + rows; i++) {
                ss = (String[]) reslist.get(i);
                table.setValueAt(1 + i, i - begin, 0);
                table.setValueAt(ss[0], i - begin, 1);
                table.setValueAt(ss[1], i - begin, 2);
            }
        }
    }
}


class MainFrame_con_componentAdapter extends ComponentAdapter {
    private MainFrame adaptee;
    MainFrame_con_componentAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void componentResized(ComponentEvent e) {
        adaptee.con_componentResized(e);
    }
}


class WindowDestroyer extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
        MainFrame.pw.close();
        System.exit(0);
    }
}


class MainFrame_jButton2_actionAdapter implements ActionListener {
    private MainFrame adaptee;
    MainFrame_jButton2_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton2_actionPerformed(e);
    }
}


class MainFrame_jButton1_actionAdapter implements ActionListener {
    private MainFrame adaptee;
    MainFrame_jButton1_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButton1_actionPerformed(e);
    }
}
