package testPlanPackage;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	TestPlanParser testPlanParser;
	
	public MainWindow(String[] httpSamplers, TestPlanParser parser) {
		testPlanParser = parser;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JList jlist = new JList(httpSamplers);
		
		String[][] samplersRow = new String[httpSamplers.length][1];
		for (int i = 0; i < samplersRow.length; i++) {
			samplersRow[i][0] = httpSamplers[i];
		}
		String[] samplersCol = new String[] {"Samplers"};
		JTable samplersTable = new JTable(samplersRow, samplersCol);
//		RowFilter<TableModel, String> filter = new RowFilter<TableModel, String>();
//		samplersTable.setRowSorter(sorter);
		
		jlist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		jlist.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					jlist.setSelectedIndex(jlist.locationToIndex(e.getPoint()));
					JPopupMenu menu = new JPopupMenu();
					JMenuItem copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String myString = testPlanParser.httpSamplers.get((String) jlist.getSelectedValue());
							StringSelection stringSelection = new StringSelection(myString);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, null);
						}
					});
					menu.add(copyItem);
					menu.show(jlist, e.getPoint().x, e.getPoint().y);
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					Component comp = jlist.getComponent(jlist.locationToIndex(e.getPoint()));
					comp.setVisible(false);
				}
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
		});
		JScrollPane scrollPane = new JScrollPane(samplersTable);
		add(scrollPane);
		pack();
	}
	
}
