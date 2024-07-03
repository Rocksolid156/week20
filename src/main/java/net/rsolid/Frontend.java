package net.rsolid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class Frontend {
    ShowWindow sw;
    private GetConnection sqlconn;
    private RunStatement command;
    private String URL;
    private final JLabel connectStatus;
    private int SQLstatus;
    private DrawInfo infoWindow;

    public Frontend() {
        sw = new ShowWindow();
        this.AddListener();
        connectStatus = new JLabel();
        sw.cd.add(connectStatus);
        this.SetContent();
    }

    public void GenURL(String domain, String user, String pwd) {
        this.URL = "jdbc:mysql://" + domain + "/experiment2?user=" + user + "&password=" + pwd + "&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";
    }

    private void AddListener() {
        ShowWindow.CredentialsDialog infodialog = sw.cd;
        ShowWindow.InitDBDialog initdialog = sw.idb;
        sw.mi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infodialog.setVisible(true);
            }
        });
        sw.mi2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sqlconn.CloseConn();
                    SQLstatus = 0;
                    sw.setEnable(SQLstatus);
                    connectStatus.setVisible(false);
                    System.out.println("数据库已关闭连接");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.mi3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog Info = new JDialog(sw, "提示", false);
                Info.setLayout(new BorderLayout());
                Info.setSize(300, 150);
                Info.setLocationRelativeTo(sw);
                JTextArea Info2 = new JTextArea();
                JButton Info3 = new JButton("确定");
                try {
                    Info2.setText(sqlconn.GetInfo());
                    Info2.setEditable(false);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                Info3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Info.setVisible(false);
                    }
                });
                Info.add(Info2, BorderLayout.CENTER);
                Info.add(Info3, BorderLayout.SOUTH);
                Info.setVisible(true);
            }
        });
        sw.cd.submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenURL(sw.cd.domainField.getText(), sw.cd.usernameField.getText(), new String(sw.cd.passwordField.getPassword()));
                InitConn(URL);
            }
        });
        sw.cd.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectDoneFunc();
            }
        });
        sw.cd.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectDoneFunc();
            }
        });
        sw.fi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
        sw.fi2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sqlconn.CloseConn();
                    System.exit(0);
                } catch (SQLException ex) {
                    System.out.println("发生错误，无法关闭");
                    int choice = JOptionPane.showConfirmDialog(sw, "发生错误，如果要退出程序请按确定，若要调试程序请按取消", "错误", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION)
                        System.exit(100);
                    else if (choice == JOptionPane.CANCEL_OPTION) {
                        throw new RuntimeException();
                    } else {
                        throw new RuntimeException();
                    }
                } catch (NullPointerException ignore) {
                    System.out.println("还未连接过数据库，忽略关闭操作");
                    System.exit(0);
                }
            }
        });
        sw.ai1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initdialog.setVisible(true);
            }
        });
        sw.hi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog aboutDialog = new JDialog();
                aboutDialog.setTitle("关于");
                aboutDialog.setLayout(new BorderLayout());
                aboutDialog.setSize(300, 200);
                JTextArea aboutText = new JTextArea();
                aboutText.setText("程序版本: 20240707\n这是一个简单的菜单示例程序。");
                aboutText.setEditable(false);
                aboutDialog.add(aboutText, BorderLayout.CENTER);
                aboutDialog.setVisible(true);
                aboutDialog.setLocationRelativeTo(sw);
            }
        });
        sw.idb.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.idb.setVisible(false);
            }
        });
        sw.idb.executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    command = new RunStatement(sqlconn.getConn());
                    command.initDB();
                } catch (SQLException ignore) {
                }
                sw.idb.EnablePrepared(chkAllDB(SQLstatus));
            }
        });
        sw.idb.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.idb.EnablePrepared(chkAllDB(SQLstatus));
            }
        });
        sw.idb.delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    command = new RunStatement(sqlconn.getConn());
                    command.removeDB();
                    sw.idb.EnablePrepared(chkAllDB(SQLstatus));
                } catch (SQLException ex) {
                    throw new RuntimeException();
                }
            }
        });
        sw.avi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void InitConn(String addr) {
        System.out.println(addr);
        try {
            //TODO
            addr = "jdbc:mysql://***REMOVED***/experiment2?user=Rocksolid&password=***REMOVED***&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";
            sqlconn = new GetConnection(addr);
            SQLstatus = 1;
            setConnectStatus();
            command = new RunStatement(sqlconn.getConn());
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
            SQLstatus = 0;
            setConnectStatus();
        }
    }

    private void setConnectStatus() {
        if (SQLstatus == 0) {
            connectStatus.setForeground(Color.RED);
            connectStatus.setText("连接失败");
        } else if (SQLstatus == 1) {
            connectStatus.setForeground(Color.GREEN);
            connectStatus.setText("连接成功！");
        } else {
            connectStatus.setForeground(Color.BLUE);
            connectStatus.setText("未知错误");
        }
    }

    public boolean chkAllDB(int status) {
        if (status == 1) {
            try {
                boolean medicineExists = command.checkExist("med");
                boolean importExists = command.checkExist("imp");
                boolean exportExists = command.checkExist("export");
                boolean departmentExists = command.checkExist("department");
                boolean docExists = command.checkExist("doc");
                boolean patientExists = command.checkExist("patient");
                boolean prescriptionExists = command.checkExist("prescription");
                boolean billingExists = command.checkExist("billing");

                sw.idb.checkBox0.setSelected(medicineExists);
                sw.idb.checkBox1.setSelected(importExists);
                sw.idb.checkBox2.setSelected(exportExists);
                sw.idb.checkBox3.setSelected(departmentExists);
                sw.idb.checkBox4.setSelected(docExists);
                sw.idb.checkBox5.setSelected(patientExists);
                sw.idb.checkBox6.setSelected(prescriptionExists);
                sw.idb.checkBox7.setSelected(billingExists);

                boolean triggerExists=command.checkTrigger("import_autoupdate")&&
                        command.checkTrigger("export_autoupdate");
                sw.idb.checkBox8.setSelected(triggerExists);

                return medicineExists&&importExists&&exportExists&&departmentExists&&docExists&&patientExists&&prescriptionExists
                        &&billingExists&triggerExists;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
        return false;
    }

    private void ConnectDoneFunc() {
        sw.cd.setVisible(false);
        sw.setEnable(SQLstatus);
        sw.idb.EnablePrepared(chkAllDB(SQLstatus));
    }

    private void SetContent(){
        infoWindow=new DrawInfo(sw);
    }

    public static void main(String[] args) {
        new Frontend();
    }
}

class ShowWindow extends JFrame {
    boolean noCloseFlag = true;

    JMenuItem mi1, mi2, mi3;
    JMenuItem fi1, fi2;
    JMenuItem ai1, ai2, ai3, ai4;
    JMenuItem hi1, hi2, hi3;
    JMenuItem avi1;
    CredentialsDialog cd;
    InitDBDialog idb;

    public ShowWindow() {
        super("Simple DB Query Frontend By 2211110209");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(800, 600);
        this.setVisible(true);
        this.AddMenu();
        this.setLayout(new BorderLayout());
        this.revalidate();
        this.repaint();
    }

    public void AddMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu m0 = new JMenu("文件");
        fi1 = new JMenuItem("修改数据");
        fi2 = new JMenuItem("安全退出");
        JMenu m1 = new JMenu("连接");
        mi1 = new JMenuItem("创建新链接");
        mi2 = new JMenuItem("断开连接");
        mi3 = new JMenuItem("连接情况");
        JMenu m2 = new JMenu("库");
        ai1 = new JMenuItem("初始化数据库");
        ai2=new JMenuItem("查询历史就诊数据");
        JMenu m3 = new JMenu("帮助");
        hi1 = new JMenuItem("关于");
        mi2.setEnabled(false);
        mi3.setEnabled(false);
        fi1.setEnabled(false);
        ai1.setEnabled(false);
        ai2.setEnabled(false);
        JMenu m4 = new JMenu("高级");
        avi1 = new JMenuItem("测试");
        mb.add(m0);
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        mb.add(m4);
        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m0.add(fi1);
        m0.addSeparator();
        m0.add(fi2);
        m2.add(ai1);
        m2.add(ai2);
        m3.add(hi1);
        m4.add(avi1);

        this.setJMenuBar(mb);
        cd = new CredentialsDialog();
        idb = new InitDBDialog();
    }

    public void setEnable(int status) {
        if (status == 1) {
            mi2.setEnabled(true);
            mi3.setEnabled(true);
            fi1.setEnabled(true);
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    JOptionPane.showMessageDialog(ShowWindow.this, "关闭按钮已被禁用，请断开连接再关闭", "提示", JOptionPane.WARNING_MESSAGE);
                    noCloseFlag = false;
                }
            });
            ai1.setEnabled(true);
        } else if (status == 0) {
            mi2.setEnabled(false);
            mi3.setEnabled(false);
            fi1.setEnabled(false);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            ai1.setEnabled(false);
            if (!noCloseFlag) {
                this.removeWindowListener(this.getWindowListeners()[0]);
                noCloseFlag = true;
            }
        }
    }

    protected class CredentialsDialog extends JDialog {
        public JTextField domainField;
        public JTextField usernameField;
        public JPasswordField passwordField;
        public JButton submitButton;
        public JButton closeButton;

        public CredentialsDialog() {
            super(ShowWindow.this, "请输入用户凭证", true);
            setLayout(new GridLayout(5, 2));

            add(new JLabel("域名："));
            domainField = new JTextField();
            add(domainField);

            add(new JLabel("用户名："));
            usernameField = new JTextField();
            add(usernameField);

            add(new JLabel("密码："));
            passwordField = new JPasswordField();
            add(passwordField);

            submitButton = new JButton("确认");
            add(submitButton);

            closeButton = new JButton("关闭");
            add(closeButton);

            // Initialize the size and location
            this.setSize(300, 200);
            this.setLocationRelativeTo(ShowWindow.this);
        }
    }

    protected class InitDBDialog extends JDialog {
        public JButton executeButton, refreshButton, closeButton, delButton;
        public JCheckBox checkBox0, checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10;
        public JLabel isPrepared;

        public InitDBDialog() {
            super(ShowWindow.this, "初始化数据库", true);
            String[] index = {"药品管理", "入库管理", "出库管理", "科室管理", "医生管理", "病人管理", "处方登记管理", "收费管理", "触发器", "存储过程", "视图查询"};
            setLayout(new BorderLayout());
            add(new JLabel("下述表全部被勾选后即可进行信息管理操作"), BorderLayout.NORTH);

            JPanel checkboxpanel = new JPanel();
            checkboxpanel.setLayout(new BoxLayout(checkboxpanel, BoxLayout.Y_AXIS));
            checkboxpanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            checkBox0 = new JCheckBox(index[0]);
            checkBox0.setEnabled(false);
            checkBox1 = new JCheckBox(index[1]);
            checkBox1.setEnabled(false);
            checkBox2 = new JCheckBox(index[2]);
            checkBox2.setEnabled(false);
            checkBox3 = new JCheckBox(index[3]);
            checkBox3.setEnabled(false);
            checkBox4 = new JCheckBox(index[4]);
            checkBox4.setEnabled(false);
            checkBox5 = new JCheckBox(index[5]);
            checkBox5.setEnabled(false);
            checkBox6 = new JCheckBox(index[6]);
            checkBox6.setEnabled(false);
            checkBox7 = new JCheckBox(index[7]);
            checkBox7.setEnabled(false);
            checkBox8 = new JCheckBox(index[8]);
            checkBox8.setEnabled(false);
            checkBox9 = new JCheckBox(index[9]);
            checkBox9.setEnabled(false);
            checkBox10 = new JCheckBox(index[10]);
            checkBox10.setEnabled(false);
            checkboxpanel.add(checkBox0);
            checkboxpanel.add(checkBox1);
            checkboxpanel.add(checkBox2);
            checkboxpanel.add(checkBox3);
            checkboxpanel.add(checkBox4);
            checkboxpanel.add(checkBox5);
            checkboxpanel.add(checkBox6);
            checkboxpanel.add(checkBox7);
            checkboxpanel.add(checkBox8);
            checkboxpanel.add(checkBox9);
            checkboxpanel.add(checkBox10);
            add(checkboxpanel, BorderLayout.CENTER);

            JPanel buttonpanel = new JPanel();
            buttonpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            executeButton = new JButton("初始化");
            refreshButton = new JButton("刷新");
            closeButton = new JButton("关闭");
            delButton = new JButton("重置");
            buttonpanel.add(executeButton);
            buttonpanel.add(refreshButton);
            buttonpanel.add(delButton);
            buttonpanel.add(closeButton);
            add(buttonpanel, BorderLayout.SOUTH);

            isPrepared = new JLabel("可以开始操作管理系统了");
            isPrepared.setForeground(Color.decode("0x00E0FF"));
            isPrepared.setVisible(false);
            checkboxpanel.add(isPrepared);

            this.setSize(300, 600);
            this.setLocationRelativeTo(ShowWindow.this);
        }

        public void EnablePrepared(boolean flag){
            if(flag)
                this.isPrepared.setVisible(true);
            else
                this.isPrepared.setVisible(false);
        }
    }

}