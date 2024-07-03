package net.rsolid;

import javax.swing.*;
import java.awt.*;

public class DrawInfo {
    JTextArea info;
    public DrawInfo(JFrame jf){
        info=new JTextArea("欢迎使用，请点击上面的菜单栏进行操作");
        jf.getContentPane().add(info, BorderLayout.CENTER);
        info.setVisible(true);
    }
}
