package GUI;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class SamplersTableCellRenderer extends DefaultTableCellRenderer {
	int bigBodyDataSize;
	
	public SamplersTableCellRenderer(int bigSize) {
		bigBodyDataSize = bigSize;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		UneditableTableModel tableModel = (UneditableTableModel)table.getModel();
		String bodyData = tableModel.parser.httpSamplers.get(table.convertRowIndexToModel(row)).bodyData;
		if (bodyData == null) {
			com.setBackground(new Color(1,.5f,.2f));
			return com;
		}
		int bodyDataSize = bodyData.length();
		if (bodyDataSize == 0) bodyDataSize = 1;
		float r = (float)bodyDataSize/(float)bigBodyDataSize;
		if (r < 0) r = 0; if (r > .8f) r = .8f;
		float g = .8f - (float)bodyDataSize/(float)bigBodyDataSize;
		if (g < 0) g = 0; if (g > .8f) g = .8f;
		float b = 0;
		com.setBackground(Color.white);
		com.setForeground(new Color(r,g,b));
		return com;
	}
}
