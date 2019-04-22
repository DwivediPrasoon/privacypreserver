package org.gui.pp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeWindow extends JFrame{
    private JButton openWekaWindowButton;
    private JPanel root;
    private JButton openARXWindowButton;
    private JButton compareButton;
    private JButton openWekaWindowButton1;
    wekaWindow wekaWindow;
    JFrame arxWindow;
    wekaWindow wekaWindow2;
    JFrame compareWindow;
    static WelcomeWindow current;

    public WelcomeWindow() {
        current = this;
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
        openWekaWindowButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(wekaWindow2 == null)
                    wekaWindow2 = new wekaWindow();
                wekaWindow2.setVisible(true);
            }
        });
    }

    public wekaWindow getWekaBefore(){
        return wekaWindow;
    }

    public wekaWindow getWekaAfter(){
        return wekaWindow2;
    }


}
