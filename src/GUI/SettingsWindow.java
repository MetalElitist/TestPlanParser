package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsWindow extends JFrame {
	public SettingsWindow() {
		JPanel bigDataSizePanel = new JPanel();
		JLabel bigDataSizeLabel = new JLabel("Big data size: ");
		JTextField bigDataSizeField = new JTextField();
		
		bigDataSizePanel.add(bigDataSizeLabel);
		bigDataSizePanel.add(bigDataSizeField);
		add(bigDataSizePanel);
		
		pack();
		
		setVisible(true);
	}
}
