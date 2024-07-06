package net.rsolid;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class DrawInfo {
    JFrame jf;
    JTable table1,table2,table3;
    JScrollPane table1_1,table2_1,table3_1;
    JLabel hello;
    public DrawInfo(JFrame jf){
        this.jf=jf;
        drawHello();
    }

    public void drawHello(){
        hello=new JLabel("欢迎使用，请点击上面的菜单栏进行操作",SwingConstants.CENTER);
        hello.setFont(new Font("Serif",Font.BOLD,24));
        jf.getContentPane().add(hello);
        hello.setVisible(true);
    }

    public int drawTable1(TableModel tableModel){
        table1=new JTable(tableModel);
        table1_1=new JScrollPane(table1);
        jf.getContentPane().add(table1_1);
        table1.setVisible(true);
        return 2;
    }

    public void drawTable2(TableModel tableModel1,TableModel tableModel2){
        JSplitPane splitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(.5);

        table2=new JTable(tableModel1);
        table2_1=new JScrollPane(table2);
        splitPane.setTopComponent(table2_1);

        table3=new JTable(tableModel2);
        table3_1=new JScrollPane(table3 );
        splitPane.setBottomComponent(table3_1);
        jf.add(splitPane);
        splitPane.setVisible(true);

        JDialog resultFrame = new JDialog();
        resultFrame.setTitle("查询结果");
        resultFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());

        resultFrame.add(splitPane);
        resultFrame.setSize(200, 200);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setVisible(true);

    }


}
