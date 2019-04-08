package org.gui.pp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeWindow extends JFrame{
    private JButton openWekaWindowButton;
    private JPanel root;
    private JButton openARXWindowButton;
    private JButton compareButton;
    JFrame wekaWindow;
    JFrame arxWindow;
    JFrame compareWindow;

    public WelcomeWindow() {
        add(root);
        setSize(1000,1000);
        openWekaWindowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wekaWindow == null)
                    wekaWindow = new wekaWindow();
                wekaWindow.setVisible(true);
            }
        });
        openARXWindowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(arxWindow == null)
                    arxWindow = new ARXMain();
                arxWindow.setVisible(true);
            }
        });
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(compareWindow == null)
                    compareWindow = new CompareWindow();
                compareWindow.setVisible(true);
            }
        });
    }
}
