package GUI;

import javax.swing.table.DefaultTableModel;

import testPlanPackage.TestPlanParser;

public class UneditableTableModel extends DefaultTableModel {
	
	TestPlanParser parser;
	MainWindow window;
	
	public UneditableTableModel(TestPlanParser pars, MainWindow window) {
		parser = pars;
		this.window = window;
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
