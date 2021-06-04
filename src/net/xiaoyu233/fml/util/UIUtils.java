package net.xiaoyu233.fml.util;

import javax.swing.*;

public class UIUtils {
    public static void showErrorDialog(String msg){
        JFrame jFrame = new JFrame();
        jFrame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(jFrame, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }
}
