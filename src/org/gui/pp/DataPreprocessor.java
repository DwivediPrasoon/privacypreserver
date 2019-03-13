package org.gui.pp;

import weka.gui.explorer.*;

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
		
		java.util.Vector<Explorer.ExplorerPanel> list = explorer.getPanels();
		System.out.println(list.toString());
		
		ClassifierPanel cp = (ClassifierPanel) explorer.getPanels().get(0);
		
		
	}
	
	
	
	
	
	

	
}
