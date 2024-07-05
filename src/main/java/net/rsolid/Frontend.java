package net.rsolid;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.HashMap;

public class Frontend {
    private final ShowWindow sw;
    private GetConnection sqlconn;
    private RunStatement statement;
    private final JLabel connectStatus;
    private int SQLstatus;
    private DefaultTableModel tableModel1;

    private int mode=1; //用于标识是否首次绘图JTable

    public Frontend() {
        sw = new ShowWindow();
        this.AddOnetimeListener();
        connectStatus = new JLabel();
        sw.cd.add(connectStatus);
    }

    public static String GenURL(String domain, String user, String pwd) {
        return  "jdbc:mysql://" + domain + "/experiment2?user=" + user + "&password=" + pwd + "&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";
    }

    private void AddOnetimeListener() {
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
                    sw.setCloseButtonEnable(SQLstatus);
                    connectStatus.setText(null);
                    refreshUI(mode,SQLstatus);
                    sw.ai2.setEnabled(false);
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

        infodialog.submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InitConn(GenURL(sw.cd.domainField.getText(), sw.cd.usernameField.getText(), new String(sw.cd.passwordField.getPassword())));
            }
        });
        infodialog.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnectDoneFunc();
            }
        });
        infodialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectDoneFunc();
            }
        });

        sw.ai1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initdialog.setVisible(true);
            }
        });
        initdialog.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.idb.setVisible(false);
            }
        });
        initdialog.executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    statement = new RunStatement(sqlconn.getConn());
                    statement.initDB();
                } catch (SQLException ignore) {
                }
                sw.idb.EnablePrepared(chkAllDB(SQLstatus));
            }
        });
        initdialog.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.idb.EnablePrepared(chkAllDB(SQLstatus));
            }
        });
        initdialog.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(sw, "确认重置信息管理系统吗？\n所有的数据将丢失！", "确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice==JOptionPane.YES_OPTION) {
                    try {
                        statement = new RunStatement(sqlconn.getConn());
                        statement.removeDB();
                        sw.idb.EnablePrepared(chkAllDB(SQLstatus));
                    } catch (SQLException ex) {
                        throw new RuntimeException();
                    }
                }
            }
        });

        sw.qi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Medicine");
                sw.setAlleiDisabled();
                sw.ei3.setEnabled(true);    sw.ei4.setEnabled(true);    sw.ei6.setEnabled(true);
            }
        });
        sw.qi2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Department");
                sw.setAlleiDisabled();
                sw.ei2.setEnabled(true);
            }
        });
        sw.qi3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Doctor");
                sw.setAlleiDisabled();
                sw.ei8.setEnabled(true);
            }
        });
        sw.qi4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Patient");
                sw.setAlleiDisabled();
                sw.ei7.setEnabled(true);
            }
        });
        sw.qi5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Prescription");
                sw.setAlleiDisabled();
                sw.ei1.setEnabled(true);
            }
        });
        sw.qi6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryDraw("View_Chinese_Billing");
                sw.setAlleiDisabled();
                sw.ei5.setEnabled(true);
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

        sw.ei1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(1);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        sw.ei2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO
                sw.ipd=sw.new InputDialog(tableModel1);
                try {
                    showInputDialog(2);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1);
                try {
                    showInputDialog(3);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(4);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(5);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(6);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(7);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        sw.ei8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sw.ipd=sw.new InputDialog(tableModel1, statement);
                try {
                    showInputDialog(8);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void addInputDialogListener(){
//        sw.ipd.closeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                sw.ipd.setVisible(false);
//            }
//        });

    }

    private void InitConn(String addr) {
        System.out.println(addr);
        try {
            //TODO
            addr = "jdbc:mysql://***REMOVED***/experiment2?user=Rocksolid&password=***REMOVED***&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true";
            sqlconn = new GetConnection(addr);
            SQLstatus = 1;
            ShowConnectStatus();
            statement = new RunStatement(sqlconn.getConn());
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
            SQLstatus = 0;
            ShowConnectStatus();
        }
    }   //尝试初始化连接并设置SQLstatus

    private void ShowConnectStatus() {
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
    }   //展示连接情况在initdialog

    public boolean chkAllDB(int status) {
        if (status == 1) {
            //type:1->表  2->存储过程 3->视图
            try {
                boolean medicineExists = statement.checkExists("med",1);
                boolean importExists = statement.checkExists("imp",1);
                boolean exportExists = statement.checkExists("export",1);
                boolean departmentExists = statement.checkExists("department",1);
                boolean docExists = statement.checkExists("doc",1);
                boolean patientExists = statement.checkExists("patient",1);
                boolean prescriptionExists = statement.checkExists("prescription",1);
                boolean billingExists = statement.checkExists("billing",1);

                sw.idb.checkBox0.setSelected(medicineExists);
                sw.idb.checkBox1.setSelected(importExists);
                sw.idb.checkBox2.setSelected(exportExists);
                sw.idb.checkBox3.setSelected(departmentExists);
                sw.idb.checkBox4.setSelected(docExists);
                sw.idb.checkBox5.setSelected(patientExists);
                sw.idb.checkBox6.setSelected(prescriptionExists);
                sw.idb.checkBox7.setSelected(billingExists);

                boolean triggerExists= statement.checkTrigger("import_autoupdate")&&
                        statement.checkTrigger("export_autoupdate");
                sw.idb.checkBox8.setSelected(triggerExists);

                boolean procedureExists= statement.checkExists("stats",2);
                boolean view1Exists = statement.checkExists("medicine_stock",3);
                boolean transExists1= statement.checkExists("View_Chinese_Billing",3);
                boolean transExists2= statement.checkExists("View_Chinese_Department",3);
                boolean transExists3= statement.checkExists("View_Chinese_Doctor",3);
                boolean transExists4= statement.checkExists("View_Chinese_Export",3);
                boolean transExists5= statement.checkExists("View_Chinese_Import",3);
                boolean transExists6= statement.checkExists("View_Chinese_Medicine",3);
                boolean transExists7= statement.checkExists("View_Chinese_Patient",3);
                boolean transExists8= statement.checkExists("View_Chinese_Prescription",3);
                boolean viewsExists=transExists1&&transExists2&&transExists3&&transExists4&&transExists5&&transExists6
                        &&transExists7&&transExists8&& view1Exists;
                sw.idb.checkBox9.setSelected(procedureExists);
                sw.idb.checkBox10.setSelected(viewsExists);

                return medicineExists&&importExists&&exportExists&&departmentExists&&docExists&&patientExists&&prescriptionExists
                        &&billingExists&triggerExists&procedureExists& viewsExists;
            } catch (SQLException ex) { //TODO
                throw new RuntimeException(ex);
            }

        }
        return false;
    }

    private void ConnectDoneFunc() {
        sw.cd.setVisible(false);
        sw.setCloseButtonEnable(SQLstatus);
        sw.idb.EnablePrepared(chkAllDB(SQLstatus));
    }

    private void refreshUI(int mode,int status){
        if (mode==1&&status==1){
            sw.getContentPane().remove(sw.info.hello);
            this.mode=2;
        } else if (mode == 2&&status==1) {
            sw.getContentPane().remove(sw.info.table1_1);
        } else if (mode == 2 && status == 0) {
            this.mode=1;
            sw.getContentPane().remove(sw.info.table1_1);
            sw.info.drawHello();
        }
        sw.revalidate();
        sw.repaint();
    }

    public void tryDraw(String tablename){
        ParseResult result;
        try {
            statement =new RunStatement(sqlconn.getConn());
            tableModel1=new DefaultTableModel(){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            result =new ParseResult(tablename,null,tableModel1, statement.getStmt());
            result.query();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        refreshUI(mode,SQLstatus);
        mode=sw.info.drawTable1(result.tableModel);
        sw.revalidate();
        sw.repaint();
    }

    public void showInputDialog(int mode) throws SQLException {
        JDialog dialog=sw.ipd;
        JLabel tip;
        JButton submitButton,closeButton;
        JLabel[] columnName;
        JTextField[] input;
        /*mode1->开药方   mode2->直接添加  mode3->入库   mode4->出库   mode5->开账单  mode6->添加药品数据   mode7->患者信息维护
            mode8->医生信息维护
        */
        if (mode==1){
            String[] patientIDs= statement.getNameSet("patient", sqlconn.getConn(),sw.ipd.hashmapPat);
            String[] medicineIDs=statement.getNameSet("med", sqlconn.getConn(),sw.ipd.hashmapMed);

            tip=new JLabel("请输入数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));

            JLabel patientLabel=new JLabel("患者名");
            JComboBox<String> patientComboBox=new JComboBox<>(patientIDs);
            JLabel medicinesLabel=new JLabel("药品");
            JComboBox<String> medicinesComboBox=new JComboBox<>(medicineIDs);
            JLabel amount=new JLabel("数量");
            JTextField countInput =new JTextField(3);

            infoPanel.add(patientLabel);    infoPanel.add(patientComboBox);
            infoPanel.add(medicinesLabel);  infoPanel.add(medicinesComboBox);
            infoPanel.add(amount);  infoPanel.add(countInput);

            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sw.ipd.setVisible(false);
                }
            });
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (countInput.getText().isEmpty()){
                        JOptionPane.showMessageDialog(sw.ipd,"错误，数量不得为空");
                    }else {
                        try {
                            String sql="INSERT INTO prescription(patient_id,medicine,date,amount) VALUES('"+sw.ipd.hashmapPat.get((String)patientComboBox.getSelectedItem())+"','"
                                    +sw.ipd.hashmapMed.get((String) medicinesComboBox.getSelectedItem())+"',NOW(),'"+countInput.getText()+"')";
                            if (sw.ipd.subRunStmt.getStmt().executeUpdate(sql)==1){
                                JOptionPane.showMessageDialog(sw.ipd,"成功");
                                tryDraw("View_Chinese_Prescription");
                            }else {
                                JOptionPane.showMessageDialog(sw.ipd,"失败");
                            }

                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                }
            });

            dialog.setVisible(true);
        }
        else if (mode==2){
            int count=sw.ipd.model.getColumnCount();
            dialog.setLayout(new BorderLayout(10,10));


            tip=new JLabel("请输入数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
            columnName=new JLabel[count];
            input=new JTextField[count];

            for (int i=0;i<count;i++){
                columnName[i]=new JLabel(sw.ipd.model.getColumnName(i));
                input[i]=new JTextField(15);

                infoPanel.add(columnName[i]);
                infoPanel.add(input[i]);
            }
            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel,BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);
        }
        else if (mode==3){
            dialog.setLayout(new BorderLayout(10,10));

            tip=new JLabel("请输入入库数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
            columnName=new JLabel[2];
            input=new JTextField[2];

            columnName[0]=new JLabel("药品批准文号");
            input[0]=new JTextField(15);

            columnName[1]=new JLabel("数量");
            input[1]=new JTextField(5);

            infoPanel.add(columnName[0]);   infoPanel.add(input[0]);
            infoPanel.add(columnName[1]);   infoPanel.add(input[1]);
            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel,BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);


        }
        else if (mode==4){
            String[] medicines=statement.getNameSet("med", sqlconn.getConn(),sw.ipd.hashmapMed);
            String[] departments=statement.getNameSet("department", sqlconn.getConn());

            tip=new JLabel("请输入出库数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));

            JLabel medicinesLabel=new JLabel("药品");
            JComboBox<String> medicinesComboBox=new JComboBox<>(medicines);
            JLabel amount=new JLabel("数量");
            JTextField countInput =new JTextField(3);
            JComboBox<String> departmentComboBox=new JComboBox<>(departments);
            JLabel target=new JLabel("到科室");

            infoPanel.add(medicinesLabel);  infoPanel.add(medicinesComboBox);
            infoPanel.add(amount);  infoPanel.add(countInput);
            infoPanel.add(target);    infoPanel.add(departmentComboBox);

            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(null);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);
        }
        else if(mode==5){
            String[] patients= statement.getNameSet("patient", sqlconn.getConn(),sw.ipd.hashmapPat);
            dialog.setLayout(new BorderLayout(10,10));

            tip=new JLabel("请输入收款数量");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
            columnName=new JLabel[2];

            columnName[0]=new JLabel("患者名");
            JComboBox<String> patientComboBox=new JComboBox<>(patients);

            columnName[1]=new JLabel("元");
            JTextField input_o =new JTextField(5);

            infoPanel.add(columnName[0]);   infoPanel.add(patientComboBox);
            infoPanel.add(input_o);    infoPanel.add(columnName[1]);
            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel,BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);


        }
        else if (mode == 6) {
            String[] medicines=statement.getNameSet("med", sqlconn.getConn(),sw.ipd.hashmapMed);
            String[] types={"非处方药","处方药","其他"};

            tip=new JLabel("请输入数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));

            JLabel medicinesLabel=new JLabel("药品");
            JComboBox<String> medicinesComboBox=new JComboBox<>(medicines);
            JLabel price=new JLabel("价格");
            JTextField priceInput =new JTextField(7);
            JLabel typeLabel=new JLabel("药物种类");
            JComboBox<String> typeComboBox=new JComboBox<>(types);

            infoPanel.add(medicinesLabel);  infoPanel.add(medicinesComboBox);
            infoPanel.add(price);  infoPanel.add(priceInput);
            infoPanel.add(typeLabel);   infoPanel.add(typeComboBox);

            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(null);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);
        }
        else if (mode == 7) {
            String[] patients= statement.getNameSet("patient", sqlconn.getConn(),sw.ipd.hashmapPat);
            String[] doctors= statement.getNameSet("doc", sqlconn.getConn());
            String[] sex={"男","女","?"};
            dialog.setLayout(new BorderLayout(10,10));

            tip=new JLabel("请输入患者数值");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
            columnName=new JLabel[4];

            columnName[0]=new JLabel("患者名");
            JComboBox<String> patientComboBox=new JComboBox<>(patients);
            columnName[1]=new JLabel("主治医师名");
            JComboBox<String> doctorComboBox=new JComboBox<>(doctors);
            columnName[2]=new JLabel("性别");
            JComboBox<String> sexComboBox=new JComboBox<>(sex);
//                columnName[3]=new JLabel("入院日期");
            infoPanel.add(columnName[0]);   infoPanel.add(patientComboBox);
            infoPanel.add(columnName[1]);    infoPanel.add(doctorComboBox);
            infoPanel.add(columnName[2]);   infoPanel.add(sexComboBox);

            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel,BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);


        }
        else if(mode==8){
            String[] departments= statement.getNameSet("department", sqlconn.getConn());
            dialog.setLayout(new BorderLayout(10,10));

            tip=new JLabel("请输入医生信息");
            tip.setHorizontalTextPosition(SwingConstants.CENTER);
            dialog.add(tip,BorderLayout.NORTH);

            JPanel infoPanel=new JPanel();
            infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
            columnName=new JLabel[3];

            columnName[0]=new JLabel("所属科室名");
            JComboBox<String> departmentComboBox=new JComboBox<>(departments);

            columnName[1]=new JLabel("医生名");
            JTextField input_o =new JTextField(5);

            columnName[2]=new JLabel("医生ID");
            JTextField input2=new JTextField(5);

            infoPanel.add(columnName[2]);   infoPanel.add(input2);
            infoPanel.add(columnName[1]);   infoPanel.add(input_o);
            infoPanel.add(columnName[0]);   infoPanel.add(departmentComboBox);
            dialog.add(infoPanel,BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
            submitButton = new JButton("提交");
            closeButton = new JButton("关闭");
            buttonPanel.add(submitButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel,BorderLayout.SOUTH);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(sw);

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setVisible(true);
        }
    }

    public static void main(String[] args) {
        new Frontend();
    }
}

class ShowWindow extends JFrame {

    JMenuItem mi1, mi2, mi3;
    JMenu fi1;
    JMenuItem ei1,ei2,ei3,ei4,ei5,ei6,ei7,ei8;
    JMenuItem  fi2;
    JMenuItem ai1, ai3, ai4;
    JMenu ai2;
    JMenuItem qi1,qi2,qi3,qi4,qi5,qi6;
    JMenuItem hi1, hi2, hi3;
    JMenuItem avi1;
    CredentialsDialog cd;
    InitDBDialog idb;
    DrawInfo info;
    InputDialog ipd;
    WindowAdapter noCLose;


    public ShowWindow() {
        super("Simple DB Query Frontend By 2211110209");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(800, 600);
        this.setVisible(true);
        this.AddMenu();
        this.setLayout(new BorderLayout());
        info=new DrawInfo(this);
        noCLose=new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(ShowWindow.this, "关闭按钮已被禁用，请断开连接再关闭", "提示", JOptionPane.WARNING_MESSAGE);
            }
        };

        this.revalidate();
        this.repaint();
    }

    public void AddMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu m0 = new JMenu("文件");
        fi1 = new JMenu("添加数据");
        ei1=new JMenuItem("开药方");
        ei2=new JMenuItem("编辑当前数据");
        ei3=new JMenuItem("入库");
        ei4=new JMenuItem("出库");
        ei5=new JMenuItem("管理收费");
        ei6=new JMenuItem("维护药品数据");
        ei7=new JMenuItem("维护患者数据");
        ei8=new JMenuItem("维护医生数据");
        fi2 = new JMenuItem("安全退出");
        JMenu m1 = new JMenu("连接");
        mi1 = new JMenuItem("创建新链接");
        mi2 = new JMenuItem("断开连接");
        mi3 = new JMenuItem("连接情况");
        JMenu m2 = new JMenu("库");
        ai1 = new JMenuItem("初始化数据库");
        ai2=new JMenu("查询数据");
        qi1=new JMenuItem("查询药品数据");
        qi2=new JMenuItem("查询科室信息");
        qi3=new JMenuItem("查询医生数据");
        qi4=new JMenuItem("查询病人数据");
        qi5=new JMenuItem("查询处方登记数据");
        qi6=new JMenuItem("查询收费数据");
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

        ai2.add(qi1);   ai2.add(qi2);   ai2.add(qi3);   ai2.add(qi4);   ai2.add(qi5);   ai2.add(qi6);
        fi1.add(ei1);   fi1.add(ei2);   fi1.add(ei3);   fi1.add(ei4);   fi1.add(ei5);   fi1.add(ei6);   fi1.add(ei7);   fi1.add(ei8);
        this.setAlleiDisabled();

        this.setJMenuBar(mb);
        cd = new CredentialsDialog();
        idb = new InitDBDialog();
//        ipd=new InputDialog();
    }

    public void setCloseButtonEnable(int status) {

        if (status == 1) {
            mi2.setEnabled(true);
            mi3.setEnabled(true);
            fi1.setEnabled(true);
            if (this.getWindowListeners().length==0)
                this.addWindowListener(noCLose);
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            ai1.setEnabled(true);

        } else if (status == 0) {
            mi2.setEnabled(false);
            mi3.setEnabled(false);
            fi1.setEnabled(false);
            if (this.getWindowListeners().length!=0)
                this.removeWindowListener(noCLose);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            ai1.setEnabled(false);

        }
    }

    public void setAlleiDisabled(){
        ei1.setEnabled(false);  ei2.setEnabled(false);  ei3.setEnabled(false);  ei4.setEnabled(false);
        ei5.setEnabled(false);  ei6.setEnabled(false);  ei7.setEnabled(false);  ei8.setEnabled(false);
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
        public JButton executeButton, refreshButton, closeButton, resetButton;
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
            resetButton = new JButton("重置");
            buttonpanel.add(executeButton);
            buttonpanel.add(refreshButton);
            buttonpanel.add(resetButton);
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
            this.isPrepared.setVisible(flag);
            ShowWindow.this.ai2.setEnabled(flag);
        }
    }

    protected class InputDialog extends JDialog{
        public RunStatement subRunStmt;
        public final DefaultTableModel model;
        public HashMap<String,String> hashmapPat,hashmapMed,hashmapDepar,hashmapDoc;

        public InputDialog(DefaultTableModel model){
            super(ShowWindow.this,"输入窗口1",true);
            this.model=model;
        }

        public InputDialog(DefaultTableModel model,RunStatement subRunStmt) {
            super(ShowWindow.this,"输入窗口2",true);
            this.model = model;
            this.subRunStmt=subRunStmt;
        }


    }

}