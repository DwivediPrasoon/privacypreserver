package org.gui.pp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.gui.explorer.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class wekaWindow extends JFrame {

    private JPanel wekaExplorer;
    private JButton addModel;
    private JPanel root;
    Explorer explorer;
    public static AbstractClassifier abc;

    public wekaWindow(){
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1000,1000);
        add(root);
        explorer = new Explorer();
        setFile();
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

        addModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addModelToList(explorerTabs);
            }
        });
    }

    public void setFile(){
        System.out.println(explorer.getPreprocessPanel().getMenus());
    }


    public void addModelToList(JTabbedPane explorerTab){
        System.out.println(explorerTab.getSelectedComponent().getClass().toString());
        if(explorerTab.getSelectedComponent().getClass().equals(new weka.gui.explorer.ClassifierPanel().getClass())){
            ClassifierPanel c = (ClassifierPanel) explorerTab.getSelectedComponent();
            AbstractClassifier cp = (AbstractClassifier) c.getClassifier();
            abc = cp;
            System.out.println(cp.getCapabilities());
            for(String x: cp.getOptions())
                System.out.println(x);
        }
    }


}
