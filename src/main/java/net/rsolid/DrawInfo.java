package net.rsolid;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class DrawInfo {
    JFrame jf;
    JTable table1;
    JScrollPane table1_1;
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


}
