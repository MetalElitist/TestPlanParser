package GUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import testPlanPackage.TestPlanParser;
import testPlanPackage.httpSampler;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	TestPlanParser testPlanParser;
	
	SettingsWindow settingsWindow;
	
	public JTable samplersTable;
	UneditableTableModel samplersTableModel;
	SamplersTableCellRenderer cr = new SamplersTableCellRenderer(1000);
	
	JTextArea bodyDataText;
	
	JFileChooser fc = new JFileChooser();
	boolean sortOrderState;
	
	int SAMPLER_NAMES_COLUMN = 0;
	int SAMPLER_ID_COLUMN = 1;
	int BODY_DATA_SIZE_COLUMN = 2;
	
	// FIXME: move to table selection model, should be converted to table model
	int lastSelectedRow = -1;

	public MainWindow(TestPlanParser parser) {
		loadSettings();
		settingsWindow = new SettingsWindow(this, cr.bigBodyDataSize);

		testPlanParser = parser;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel sortLabel = new JLabel("Sort by: ");
		JComboBox sortType = new JComboBox(new String[] {"Execution order", "Name", "Data size"});
		sortType.setMaximumSize(new Dimension(150,20));
		
		JTextField searchField = new JTextField();
		searchField.setMaximumSize(new Dimension(500, 20));
		
		bodyDataText = new JTextArea(30,80);
		JScrollPane bodyDataScrollPane = new JScrollPane(bodyDataText); 
//		bodyDataText.setEditable(false);
		bodyDataText.setLineWrap(true);
		
		samplersTableModel = new UneditableTableModel(testPlanParser, this);
		samplersTableModel.addColumn("Samplers");
		samplersTable = new JTable(samplersTableModel);
		for (int i = 0; i < samplersTable.getColumnCount(); i++) {
			samplersTable.setDefaultRenderer(samplersTable.getColumnClass(i), cr);
		}

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(samplersTable.getModel());
		samplersTable.setRowSorter(sorter);
		
		sortOrderState = true;
		
		JTableHeader header = samplersTable.getTableHeader();
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openFileMenuItem = new JMenuItem("Open");
		JMenuItem saveFileMenuItem = new JMenuItem("Save");
		saveFileMenuItem.setEnabled(false);
		
		JMenu editMenu = new JMenu("Edit");
		JMenuItem settingsMenuItem = new JMenuItem("Settings");
		
		addListeners(sortType, sorter, header, bodyDataText, searchField, openFileMenuItem, saveFileMenuItem, settingsMenuItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		fileMenu.add(openFileMenuItem);
		fileMenu.add(saveFileMenuItem);
		editMenu.add(settingsMenuItem);

		JPanel samplersPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(samplersTable);
		
		samplersPanel.setLayout(new BoxLayout(samplersPanel, BoxLayout.Y_AXIS));
		
		JPanel samplersPanelTop = new JPanel();
		samplersPanelTop.setLayout(new BoxLayout(samplersPanelTop, BoxLayout.X_AXIS));
		
		setJMenuBar(menuBar);
		samplersPanelTop.add(searchField);
		samplersPanelTop.add(sortLabel);
		samplersPanelTop.add(sortType);
		samplersPanel.add(samplersPanelTop);
		samplersPanel.add(scrollPane);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		add(samplersPanel);
		add(bodyDataScrollPane);
		
		pack();
		
	}
	
	private void addListeners(JComboBox sortType, TableRowSorter<TableModel> sorter, JTableHeader samplersColumnHeader, 
			JTextArea bodyDataText, JTextField searchField, JMenuItem openFileMenuItem, JMenuItem saveFileMenuItem, 
			JMenuItem settingsMenuItem) {
		this.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent e) {
				saveSettings();
			}
			public void windowOpened(WindowEvent e) {}public void windowClosed(WindowEvent e) {}public void windowIconified(WindowEvent e) {}public void windowDeiconified(WindowEvent e) {}public void windowActivated(WindowEvent e) {}public void windowDeactivated(WindowEvent e) {}
		});
		
		sortType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SortOrder sortOrder = SortOrder.ASCENDING;
				if (!sortOrderState) sortOrder = SortOrder.DESCENDING;
				sort(sorter, sortType.getSelectedItem().toString(), sortOrder);
			}
		});
		
		samplersColumnHeader.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int column = samplersTable.columnAtPoint(e.getPoint());
				if (column == 0) {
					sortOrderState = !sortOrderState;
					SortOrder order;
					if (sortOrderState) order = SortOrder.ASCENDING; else {
						order = SortOrder.DESCENDING;
					}
					sort(sorter, sortType.getSelectedItem().toString(), order);
				}
			}
			public void mousePressed(MouseEvent e) {}public void mouseReleased(MouseEvent e) {}public void mouseEntered(MouseEvent e) {}public void mouseExited(MouseEvent e) {}
		});
		
		samplersTable.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int row = samplersTable.rowAtPoint(e.getPoint());
					samplersTable.changeSelection(row, 0, false, false);
					JPopupMenu menu = new JPopupMenu();
					JMenuItem copyItem = new JMenuItem("Copy");
					copyItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String myString = getSelectedRowValue().bodyData;
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
				
				if (lastSelectedRow != -1) {
					httpSampler sampler = getRowValue(lastSelectedRow);
					setSamplerBodyData(sampler);
				}
				if (rowIndices.length > 0) {
					String bodyData = getSelectedRowValue().bodyData;
					if (bodyData != null) {
						bodyDataText.setText(bodyData);
						bodyDataText.setEnabled(true);
					} else {
						bodyDataText.setEnabled(false);
					}
					lastSelectedRow = samplersTable.convertRowIndexToModel(samplersTable.getSelectedRow());
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
		
		MainWindow thisWindow = this;
		
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(thisWindow);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					testPlanParser.openFile(file);
					saveFileMenuItem.setEnabled(true);
				}
			}
		});
		
		saveFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setSamplerBodyData(getSelectedRowValue());
					testPlanParser.saveFile();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		settingsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsWindow.setVisible(true);
			}
		});
	}
	
	public void setHttpSamplers(httpSampler[] httpSamplers) {
		lastSelectedRow = -1;
		
		Object[][] samplersRow = new Object[httpSamplers.length][3];
		for (int i = 0; i < samplersRow.length; i++) {
			samplersRow[i][SAMPLER_NAMES_COLUMN] = httpSamplers[i].name;
			samplersRow[i][SAMPLER_ID_COLUMN] = httpSamplers[i].id;
			if (httpSamplers[i].bodyData != null) 
				samplersRow[i][BODY_DATA_SIZE_COLUMN] = httpSamplers[i].bodyData.length();
			else
				samplersRow[i][BODY_DATA_SIZE_COLUMN] = 0;
		}
		String[] samplersCol = new String[] {"Samplers", "id", "dataSize"};
		
		samplersTableModel.setDataVector(samplersRow, samplersCol);
		
		TableColumnModel tcm = samplersTable.getColumnModel();
		
		tcm.removeColumn(tcm.getColumn(2));
		tcm.removeColumn(tcm.getColumn(1));
		
		bodyDataText.setText("");
	}
	
	public void setSamplerBodyData(httpSampler sampler) {
		if (sampler.bodyData != null) {
			sampler.bodyData = bodyDataText.getText();
			samplersTableModel.setValueAt(sampler.bodyData.length(), lastSelectedRow, BODY_DATA_SIZE_COLUMN);
		}
	}
	
	public void filterSamplers(TableRowSorter sorter, String filterText) {
//		sorter.setRowFilter(RowFilter.regexFilter(filterText, 0));
		sorter.setRowFilter(new SimpleRowFilter(filterText));
	}
	
	public httpSampler getSelectedRowValue() {
		int index = samplersTable.convertRowIndexToModel(samplersTable.getSelectedRow());
		return getRowValue(index);
	}
	
	// rowIndex should be converted to model
	public httpSampler getRowValue(int rowIndex) { 
		return testPlanParser.httpSamplers.get(rowIndex);
	}
	
	public void sort(TableRowSorter<TableModel> sorter, String sortType, SortOrder order) {
		String sort = sortType;

		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		if (sort == "Name") {
			sortKeys.add(new RowSorter.SortKey(SAMPLER_NAMES_COLUMN, order));
			sorter.setSortKeys(sortKeys);
		} else if (sort == "Data size") {
			sortKeys.add(new RowSorter.SortKey(BODY_DATA_SIZE_COLUMN, order));
			sorter.setSortKeys(sortKeys);
		} else if (sort == "Execution order") {
			sortKeys.add(new RowSorter.SortKey(SAMPLER_ID_COLUMN, order));
			sorter.setSortKeys(sortKeys);
		}
	}
	
	public void saveSettings() {
		File file = new File("settings");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			String settingsString = String.format(
                                                  "lastOpenFileLocation=%s\n\r"
                                                + "bigBodyDataSize=%d\n\r", 
					fc.getCurrentDirectory().getAbsolutePath(), cr.bigBodyDataSize);
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
					} else if (line.startsWith("bigBodyDataSize=")) {
						String size = line.split("=")[1];
						cr.bigBodyDataSize = Integer.parseInt(size);
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
}
