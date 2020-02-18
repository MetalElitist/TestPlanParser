package testPlanPackage;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	TestPlanParser testPlanParser;
	
	JTable samplersTable;
	UneditableTableModel samplersTableModel;
	
	public MainWindow(TestPlanParser parser) {
		MainWindow thisWindow = this;
		
		testPlanParser = parser;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextField searchField = new JTextField();
		
		samplersTableModel = new UneditableTableModel();
		JTable samplersTable = new JTable(samplersTableModel);

		TableRowSorter sorter = new TableRowSorter<TableModel>(samplersTable.getModel());
		samplersTable.setRowSorter(sorter);
		
		samplersTable.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int row = samplersTable.rowAtPoint(e.getPoint());
					samplersTable.changeSelection(row, 0, false, false);
					JPopupMenu menu = new JPopupMenu();
					JMenuItem copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String myString = testPlanParser.httpSamplers.get((String) samplersTable.getModel().getValueAt(samplersTable.getSelectedRow(), 0));
							StringSelection stringSelection = new StringSelection(myString);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, null);
						}
					});
					menu.add(copyItem);
					menu.show(samplersTable, e.getPoint().x, e.getPoint().y);
				}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent arg0) {
				filterSamplers(sorter, searchField.getText());
			}
			public void removeUpdate(DocumentEvent arg0) {
				filterSamplers(sorter, searchField.getText());
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openFileMenuItem = new JMenuItem("Open");
		
		JFileChooser fc = new JFileChooser();
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(thisWindow);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					testPlanParser.openFile(file);
				}
			}
		});
		
		menuBar.add(fileMenu);
		fileMenu.add(openFileMenuItem);

		JScrollPane scrollPane = new JScrollPane(samplersTable);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		setJMenuBar(menuBar);
		add(searchField);
		add(scrollPane);
		pack();
	}
	
	public void setHttpSamplers(String[] httpSamplers) {
		String[][] samplersRow = new String[httpSamplers.length][1];
		for (int i = 0; i < samplersRow.length; i++) {
			samplersRow[i][0] = httpSamplers[i];
		}
		String[] samplersCol = new String[] {"Samplers"};
		
		samplersTableModel.setDataVector(samplersRow, samplersCol);
	}
	
	public void filterSamplers(TableRowSorter sorter, String filterText) {
		sorter.setRowFilter(RowFilter.regexFilter(filterText, 0));
	}
	
	class UneditableTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
}
