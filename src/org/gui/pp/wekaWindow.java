package org.gui.pp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.gui.explorer.*;

public class wekaWindow extends JFrame {

    private JPanel wekaExplorer;
    private JButton addModel;
    private JPanel root;

    public wekaWindow(){
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1000,1000);
        add(root);
        Explorer explorer = new Explorer();
        wekaExplorer.add(explorer);
        JTabbedPane explorerTabs = explorer.getTabbedPane();
        explorerTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = explorerTabs.getSelectedIndex();
                switch (selectedIndex){
                    case 1: addModel.setEnabled(true);
                            break;
                    case 2: addModel.setEnabled(true);
                            break;
                    case 3: addModel.setEnabled(true);
                            break;
                    default: addModel.setEnabled(false);
                            break;
                }
            }
        });

    }

}
