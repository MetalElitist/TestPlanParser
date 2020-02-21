package GUI;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class SettingsWindow extends JFrame {
	JTextField bigDataSizeField;
	MainWindow mainWindow;
	
	public SettingsWindow(MainWindow mainW, int bigDataSize) {
		mainWindow = mainW;
		
		JPanel titleBar = new JPanel() {
			public void paint(Graphics g) {
				g.setColor(Color.blue);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		titleBar.setMaximumSize(new Dimension(100000, 40));
		JButton closeButton = new JButton("X");
		
		JPanel bigDataSizePanel = new JPanel();
		JLabel bigDataSizeLabel = new JLabel("Big data size: ");
		bigDataSizeField = new JTextField();
		bigDataSizeField.setText(Integer.toString(bigDataSize));
		bigDataSizeField.setPreferredSize(new Dimension(50,20));
		
		AbstractDocument doc = (AbstractDocument) bigDataSizeField.getDocument();
		doc.setDocumentFilter(new DocumentFilter() {
			public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
				String text = fb.getDocument().getText(0, fb.getDocument().getLength());
				text += str;
				if (text.matches("^[0-9]*$")) {
					super.replace(fb, offs, length, str, a);
				}
			}
			
			public void insertString(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException { 
				String text = fb.getDocument().getText(0, fb.getDocument().getLength());
				text += str;
				if (text.matches("^[0-9]*$")) {
					super.replace(fb, offs, length, str, a);
				}
			}
		});
		
		JFrame thisWindow = this;
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisWindow.setVisible(false);
				mainWindow.cr.bigBodyDataSize = Integer.parseInt(bigDataSizeField.getText());
			}
		});
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		bigDataSizePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		titleBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		titleBar.setAlignmentY(TOP_ALIGNMENT);
		titleBar.add(closeButton);
		add(titleBar);
		
//		bigDataSizePanel.setMaximumSize(new Dimension(200,20));
		bigDataSizePanel.setAlignmentY(TOP_ALIGNMENT);
		bigDataSizePanel.add(bigDataSizeLabel);
		bigDataSizePanel.add(bigDataSizeField);
		add(bigDataSizePanel);
		
		setUndecorated(true);
		
		setSize(200,400);
	}
}
