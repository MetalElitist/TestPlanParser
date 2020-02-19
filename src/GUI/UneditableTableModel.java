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
	
	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 0) {
			return String.class;
		}
		if (col == 1) {
			return Integer.class;
		}
		if (col == 2) {
			return Integer.class;
		}
		return getValueAt(0, col).getClass();
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
