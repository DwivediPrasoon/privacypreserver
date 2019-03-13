package org.gui.pp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class WelcomeScreen extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public WelcomeScreen() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 584, 481);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblWelcomeToPrivacy = new JLabel("Welcome to Privacy Preserver");
		lblWelcomeToPrivacy.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblWelcomeToPrivacy,BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JButton btnClickHereTo_1 = new JButton("Click here");
		btnClickHereTo_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DataPreprocessor().setVisible(true);
				
			}
		});
		
		JLabel lblToSelectThe = new JLabel("To select the best Analytics model for unanonymized data");
		lblToSelectThe.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblToSelectThe = new GridBagConstraints();
		gbc_lblToSelectThe.insets = new Insets(0, 0, 5, 5);
		gbc_lblToSelectThe.gridx = 2;
		gbc_lblToSelectThe.gridy = 3;
		panel.add(lblToSelectThe, gbc_lblToSelectThe);
		GridBagConstraints gbc_btnClickHereTo_1 = new GridBagConstraints();
		gbc_btnClickHereTo_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnClickHereTo_1.gridx = 4;
		gbc_btnClickHereTo_1.gridy = 3;
		panel.add(btnClickHereTo_1, gbc_btnClickHereTo_1);
		
		JLabel lblToSelectThe_1 = new JLabel("To select the best privacy model");
		lblToSelectThe_1.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblToSelectThe_1 = new GridBagConstraints();
		gbc_lblToSelectThe_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblToSelectThe_1.gridx = 2;
		gbc_lblToSelectThe_1.gridy = 4;
		panel.add(lblToSelectThe_1, gbc_lblToSelectThe_1);
		
		JButton btnClickHere = new JButton("Click here");
		GridBagConstraints gbc_btnClickHere = new GridBagConstraints();
		gbc_btnClickHere.insets = new Insets(0, 0, 5, 0);
		gbc_btnClickHere.gridx = 4;
		gbc_btnClickHere.gridy = 4;
		panel.add(btnClickHere, gbc_btnClickHere);
		
		btnClickHere.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new ARXMain().setVisible(true);;
			}
		});
		
		
	}

}
