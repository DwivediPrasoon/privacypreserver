package org.gui.pp;



import weka.gui.ResultHistoryPanel;
import weka.gui.explorer.ClassifierPanel;
import weka.gui.explorer.Explorer;

import javax.swing.*;
import java.awt.*;

public class DataPreprocessor extends JFrame {

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public DataPreprocessor() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 838, 638);
		
		Explorer explorer = new Explorer();
		getContentPane().add(explorer, BorderLayout.CENTER);
		
		JPanel title = new JPanel();
		getContentPane().add(title, BorderLayout.NORTH);
		
		JLabel lblDataPreprocessor = new JLabel("Choose the best Analytics Model");
		lblDataPreprocessor.setHorizontalAlignment(SwingConstants.CENTER);
		title.add(lblDataPreprocessor);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Add the model to Centralized list");
		btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnNewButton);
		
		JTabbedPane tp = explorer.getTabbedPane();
		//ClassifierPanel classify = (ClassifierPanel) tp.getComponentAt(1);
		//ResultHistoryPanel rs = classify.getResultHistory();


		Thread t = new Thread(){
			public void run(){
					ClassifierPanel cp = null;
					ResultHistoryPanel rs = null;
					while(cp==null)
						cp = (ClassifierPanel) (ClassifierPanel) tp.getComponentAt(1);
					while(cp!=null && rs==null)
						rs = cp.getResultHistory();
					JList js = null;
					while(js==null)
						js = rs.getList();
					DefaultListModel dm = null;
					while(dm == null)
					dm = (DefaultListModel) js.getModel();
					while(dm.isEmpty());
					System.out.println(dm.getElementAt(0).toString());
					System.out.println(rs.getNamedBuffer(dm.getElementAt(0).toString()));




			}
		};

		t.start();



		
	}
	
	
	
	
	
	

	
}
