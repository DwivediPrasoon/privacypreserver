package org.gui.pp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UploadDataWindow extends JFrame {

    private JPanel top;
    private JButton browseButton;
    private JButton submitButton;
    File file;
    JFrame thisFrame;

    public UploadDataWindow(){
        add(top);
        setSize(500,500);
        thisFrame = this;
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int ret = fc.showOpenDialog(top);
                if(ret == JFileChooser.APPROVE_OPTION){
                    file = fc.getSelectedFile();
                }
            }
        });
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thisFrame.setState(JFrame.HIDE_ON_CLOSE);
            }
        });
    }

}
