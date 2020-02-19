package testPlanPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
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
	
	JFileChooser fc = new JFileChooser();
	
	public MainWindow(TestPlanParser parser) {
		MainWindow thisWindow = this;
		
		loadSettings();
		
		testPlanParser = parser;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				saveSettings();
			}
			public void windowOpened(WindowEvent e) {}public void windowClosed(WindowEvent e) {}public void windowIconified(WindowEvent e) {}public void windowDeiconified(WindowEvent e) {}public void windowActivated(WindowEvent e) {}public void windowDeactivated(WindowEvent e) {}
		});
		
		JTextField searchField = new JTextField();
		searchField.setMaximumSize(new Dimension(500, 20));
		
		JTextArea bodyDataText = new JTextArea(30,80);
		JScrollPane bodyDataScrollPane = new JScrollPane(bodyDataText); 
		bodyDataText.setEditable(false);
		bodyDataText.setLineWrap(true);
		
		samplersTableModel = new UneditableTableModel(testPlanParser);
		samplersTableModel.addColumn("Samplers");
		JTable samplersTable = new JTable(samplersTableModel);
		for (int i = 0; i < samplersTable.getColumnCount(); i++) {
			SamplersTableCellRenderer cr = new SamplersTableCellRenderer(1500);
			samplersTable.setDefaultRenderer(samplersTable.getColumnClass(i), cr);
		}

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
			public void mouseEntered(MouseEvent arg0) {}public void mouseExited(MouseEvent arg0) {}public void mousePressed(MouseEvent arg0) {}public void mouseReleased(MouseEvent arg0) {}
		});
		samplersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] rowIndices = samplersTable.getSelectionModel().getSelectedIndices();
				if (rowIndices.length > 0) {
					String bodyData = testPlanParser.httpSamplers.get((String) samplersTable.getModel().getValueAt(rowIndices[0],0));
					bodyDataText.setText(bodyData);
				}
			}
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

		JPanel samplersPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(samplersTable);
		
		samplersPanel.setLayout(new BoxLayout(samplersPanel, BoxLayout.Y_AXIS));
		
		setJMenuBar(menuBar);
		samplersPanel.add(searchField);
		samplersPanel.add(scrollPane);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		add(samplersPanel);
		add(bodyDataScrollPane);
		
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
		
//		sorter.setRowFilter(RowFilter.regexFilter(filterText, 0));
		sorter.setRowFilter(new SimpleRowFilter(filterText));
	}
	
	public void saveSettings() {
		File file = new File("settings");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			String settingsString = String.format("lastOpenFileLocation=%s", fc.getCurrentDirectory().getAbsolutePath());
			writer.write(settingsString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadSettings() {
		File file = new File("settings");
		if (file.exists()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					if (line.startsWith("lastOpenFileLocation=")) {
						String directory = line.split("=")[1];
						fc.setCurrentDirectory(new File(directory));
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class UneditableTableModel extends DefaultTableModel {
		
		TestPlanParser parser;
		
		public UneditableTableModel(TestPlanParser pars) {
			parser = pars;
		}
		
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
	class SamplersTableCellRenderer extends DefaultTableCellRenderer {
		int bigBodyDataSize;
		
		public SamplersTableCellRenderer(int bigSize) {
			bigBodyDataSize = bigSize;
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			UneditableTableModel tableModel = (UneditableTableModel)table.getModel();
			String rowValue = (String) tableModel.getValueAt(row, col);
			String bodyData = tableModel.parser.httpSamplers.get(rowValue);
			if (bodyData == null) {
				com.setBackground(new Color(1,.5f,.2f));
				return com;
			}
			int bodyDataSize = bodyData.length();
			if (bodyDataSize == 0) bodyDataSize = 1;
			float r = (float)bodyDataSize/(float)bigBodyDataSize;
			if (r < 0) r = 0; if (r > 1) r = 1;
			float g = 1f - (float)bodyDataSize/(float)bigBodyDataSize;
			if (g < 0) g = 0; if (g > 1) g = 1;
			float b = 0;
			com.setBackground(Color.white);
			com.setForeground(new Color(r,g,b));
			return com;
		}
	}
	
	class SimpleRowFilter extends RowFilter {
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
	
}
