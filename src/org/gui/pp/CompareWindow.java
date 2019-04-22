package org.gui.pp;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

public class CompareWindow extends JFrame{
    private JTextArea before;
    private JTextArea after;
    private JButton browseAnonymizedFileButton;
    private JButton browseUnAnonymizedFileButton;
    private JButton checkResultsButton;
    private JPanel root;
    private JComboBox delimiterBox;
    File fileBefore = null, fileAfter = null;



    public CompareWindow(){
        add(root);
        setSize(1000,1000);
        JFileChooser fc = new JFileChooser();


        browseAnonymizedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fc.showOpenDialog(root);
                if(ret == JFileChooser.APPROVE_OPTION){
                    fileBefore = fc.getSelectedFile();
                }
            }
        });
        browseUnAnonymizedFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fc.showOpenDialog(root);
                if(ret == JFileChooser.APPROVE_OPTION){
                    fileAfter = fc.getSelectedFile();
                }
            }
        });
        checkResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileAfter!=null && fileBefore!=null)
                initializeResults();
            }
        });
    }


    void initializeResults(){
        AbstractClassifier modelUnAnonymized = WelcomeWindow.current.getWekaBefore().getCurrentModel();
        AbstractClassifier modelAnonymized = WelcomeWindow.current.getWekaAfter().getCurrentModel();
        try {

            CSVLoader loader = new CSVLoader();
            loader.setSource(fileBefore);
            loader.setFieldSeparator(Pattern.quote(delimiterBox.getSelectedItem().toString()));

            Instances instDS1 = loader.getDataSet();
            instDS1.setClassIndex(instDS1.numAttributes()-1);
            loader.setSource(fileAfter);
            Instances instDS2 = loader.getDataSet();
            instDS2.setClassIndex(instDS2.numAttributes()-1);

            //Prepare Classifiers
            Classifier class1 =  AbstractClassifier.forName(modelUnAnonymized.getClass().toString().split(" ")[1], modelUnAnonymized.getOptions());
            class1.buildClassifier(instDS1);
            Classifier class2 =  AbstractClassifier.forName(modelAnonymized.getClass().toString().split(" ")[1], modelUnAnonymized.getOptions());
            class2.buildClassifier(instDS2);


            //Prepare Evaluation Model
            Evaluation eval1 = new Evaluation(instDS1);
            eval1.crossValidateModel(class1, instDS1, 10,new Debug.Random(1));
            Evaluation eval2 = new Evaluation(instDS1);
            eval2.crossValidateModel(class2, instDS2, 10,new Debug.Random(1));

            before.setText(class1+"\n"+eval1.toSummaryString()+"\n"+eval1.toMatrixString());
            after.setText(class2+"\n"+eval2.toSummaryString()+"\n"+eval1.toMatrixString());








        }
        catch(Exception e){
            e.printStackTrace();
        }
    }




}
