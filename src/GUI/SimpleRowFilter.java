package GUI;

import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;

public class SimpleRowFilter extends RowFilter {
	String searchText;
	public SimpleRowFilter(String SearchText) {
		super();
		searchText = SearchText;
	}
	public boolean include(Entry entry) {
		if (entry.getStringValue(0).contains(searchText)) {
			return true;
		}
		return false;
	}
}
