/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.projectFrame.newProject.vcf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.projectFrame.newProject.MultiGenomeInformationPanel;

/**
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoader extends JDialog {

	private static final long serialVersionUID = -1686983714031041874L;
	
	private final static 	String 	ICON_PATH = "edu/yu/einstein/genplay/resource/icon.png"; 	// path of the icon of the application
	private 	 			Image 	iconImage; 													// icon of the application

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;
	private int	approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	
	private static final int DIALOG_WIDTH = 700;
	private static final int DIALOG_HEIGHT = 450;
	private static final int TABLE_WIDTH = 700;
	private static final int TABLE_HEIGHT = 300;
	private static final int SCROLLPANE_WIDTH = 700;
	private static final int SCROLLPANE_HEIGHT = 320;
	private static final int BUTTON_PANEL_WIDTH = 700;
	private static final int BUTTON_PANEL_HEIGHT = 90;
	private static final int DIALOG_LIST_WIDTH = 200;
	private static final int DIALOG_LIST_HEIGHT = 300;
	protected static final int BUTTON_SIDE = 20;
	
	protected static final String ADD_ROW = "Add row";
	protected static final String REMOVE_ROW = "Remove row(s)";
	protected static final String EDIT_COLUMN = "Edit column";
	protected static final  String GROUP_LIST = "...";
	protected static final  String GENOME_LIST = "...";
	protected static final  String TYPE_LIST = "...";
	protected static final  String FILE_LIST = "...";
	protected static final  String RAW_NAMES_LIST = "...";
	protected static final  String ADD_ELEMENT = "Add element";
	protected static final  String REMOVE_ELEMENT = "Remove element(s)";
	
	private static VCFLoader		instance;
	private VCFData 		data;
	private VCFTableModel 	model;
	private VCFTable 		table;
	private JScrollPane 	scrollPane;
	private JPanel			buttonPanel;
	private boolean			validVCF = false;
	
	
	/**
	 * Constructor of {@link VCFLoader}
	 */
	public VCFLoader () {
		instance = this;
		
		setTitle("VCF loader");
		setResizable(true);
		iconImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(ICON_PATH));
		setIconImage(iconImage);
		setLocationRelativeTo(getRootPane());
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeDialog();
			}
			@Override
			public void windowClosed(WindowEvent arg0) {}
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		
		data = new VCFData(this);
		model = new VCFTableModel(data);
		table = new VCFTable(model);
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		
		initButtonPanel ();
		
		//Init
        setAllCellEditor();
		setAllSize();
		
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(false);
	}
	
	
	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
	
	
	/**
	 * Closes the VCF loader dialog
	 */
	public void closeDialog () {
		initStatisticsInformation();
		setVisible(false);
	}
	
	
	/**
	 * Initializes the statistics information
	 */
	public void initStatisticsInformation () {
		int group = data.getNumberElementList(0);
		int genome = data.getNumberElementList(1);
		int vcf = data.getNumberElementList(3);
		MultiGenomeInformationPanel.setInformation(group, genome, vcf);
	}
	
	
	private void initButtonPanel () {
		buttonPanel = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		buttonPanel.setLayout(layout);
		
		JPanel validationPanel = getValidationPanel();
		VCFTableTools tableTools = new VCFTableTools(this);
		
		//tableTools
		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonPanel.add(tableTools, gbc);
		
		//validationPanel
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(BUTTON_PANEL_HEIGHT / 2, DIALOG_WIDTH / 9, 0, 0);
		buttonPanel.add(validationPanel, gbc);
	}
	
	
	private JPanel getValidationPanel () {
		Dimension buttonDim = new Dimension(BUTTON_SIDE * 3, BUTTON_SIDE);
		Insets inset = new Insets(0, 0, 0, 0);
		
		JButton confirm = new JButton("Ok");
		confirm.setSize(buttonDim);
		confirm.setMinimumSize(buttonDim);
		confirm.setMaximumSize(buttonDim);
		confirm.setPreferredSize(buttonDim);
		confirm.setToolTipText("Ok");
		confirm.setMargin(inset);
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = APPROVE_OPTION;
				closeDialog();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.setSize(buttonDim);
		cancel.setMinimumSize(buttonDim);
		cancel.setMaximumSize(buttonDim);
		cancel.setPreferredSize(buttonDim);
		cancel.setToolTipText("Cancel");
		cancel.setMargin(inset);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				closeDialog();
			}
		});
		
		
		JPanel validationPanel = new JPanel();
		validationPanel.add(confirm);
		validationPanel.add(cancel);
		Dimension validationDimension = new Dimension(DIALOG_WIDTH / 3, BUTTON_PANEL_HEIGHT);
		validationPanel.setSize(validationDimension);
		validationPanel.setMinimumSize(validationDimension);
		validationPanel.setMaximumSize(validationDimension);
		validationPanel.setPreferredSize(validationDimension);
		
		
		getRootPane().setDefaultButton(confirm);
		
		return validationPanel;
	}
	
	
	
	private void setAllSize () {
		Dimension dialogDim = new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT);
		setSize(dialogDim);
		setMinimumSize(dialogDim);
		setMaximumSize(dialogDim);
		setPreferredSize(dialogDim);
		
		Dimension tableDim = new Dimension(TABLE_WIDTH, TABLE_HEIGHT);
		table.setSize(tableDim);
		table.setPreferredSize(tableDim);
		
		Dimension scrollPaneDim = new Dimension(SCROLLPANE_WIDTH, SCROLLPANE_HEIGHT);
		scrollPane.setSize(scrollPaneDim);
		scrollPane.setPreferredSize(scrollPaneDim);
		
		Dimension buttonPaneDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setSize(buttonPaneDim);
		buttonPanel.setMinimumSize(buttonPaneDim);
		buttonPanel.setMaximumSize(buttonPaneDim);
		buttonPanel.setPreferredSize(buttonPaneDim);
		
		setColumnSize();
	}
	
	
	private void setColumnSize () {
		for (int i = 0; i < 5; i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
		    column.setResizable(true);
		    switch (i) {
			case 0:
				column.setPreferredWidth((int)Math.round(table.getWidth() * 0.2));
				break;
			case 1:
				column.setPreferredWidth((int)Math.round(table.getWidth() * 0.2));
				break;
			case 2:
				column.setPreferredWidth((int)Math.round(table.getWidth() * 0.1));
				column.setResizable(false);
				break;
			case 3:
				column.setPreferredWidth((int)Math.round(table.getWidth() * 0.3));
				break;
			case 4:
				column.setPreferredWidth((int)Math.round(table.getWidth() * 0.2));
				break;
			default:
				break;
			}
		}
	}

	
	/**
	 * Adds a row
	 */
	protected void addRow () {
		data.addRow();
		table.updateTable();
	}
	
	
	/**
	 * @param rows rows to remove
	 */
	protected void removeRows (int[] rows) {
		data.removeRows(rows);
		data.removeEditorForRow(rows);
		table.updateTable();
		setAllCellEditor();
		table.validate();
		table.revalidate();
		table.repaint();
	}
	
	
	/**
	 * @param row row number
	 * @param col column number
	 */
	protected void fixRowBug (int row, int col) {
		model.fireTableDataChanged();
	}
	
	
	/**
	 * Sets all cell editors
	 */
	protected void setAllCellEditor () {
		for (int i = 0; i < 5 ; i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setCellEditor(new DefaultCellEditor(data.getNewComboBox(i)));
		}
	}
	
	
	/**
	 * Updates the raw names list
	 * @param path	VCF file path
	 * @param row	row to apply changes
	 * @param name	name of the selected genome (if exists)
	 */
	protected void updateRawNames (String path, int row, String name) {
		File vcf = new File(path);
		if (vcf.isFile()) {
			try {
				VCFReader reader = new VCFReader(vcf);
				List<String> rawNames = reader.getRawGenomesNames();
				JComboBox box = new JComboBox();
				for (String s: rawNames) {
					box.addItem(s);
				}
				if (name != null) {
					box.setSelectedItem(name);
				}
				DefaultCellEditor editor = new DefaultCellEditor(box);
				data.addEditorForRow(row, editor);
				//data.setEditor(row, 4, editor);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Initializes the data
	 * @param data the new data array
	 */
	public void setData (List<List<Object>> data) {
		this.data.setData(data);
		if (data.size() == 0) {
			this.data.addRow();
		} else {
			for (int i = 0; i < data.size(); i++) {
				List<Object> row = data.get(i);
				updateRawNames(row.get(3).toString(), i, row.get(4).toString());
			}
		}
	}
	
	
	/**
	 * @return the data
	 */
	public List<List<Object>> getData() {
		return this.data.getData();
	}
	
	
	/**
	 * @return the data
	 */
	protected VCFData getVCFData () {
		return data;
	}
	
	
	/**
	 * @return the table
	 */
	protected VCFTable getTable() {
		return table;
	}


	/**
	 * @param col 	the column number
	 * @return		the name of the column
	 */
	protected String getColumnName(int col) {
		return model.getColumnName(col);
	}
	
	
	/**
	 * @return the raw genome name/group association map
	 */
	public Map<String, List<String>> getGenomeGroupAssociation () {
		if (validVCF) {
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> groupList = data.getColumnList(0);
			List<String> rawNamesList = data.getColumnList(4);
			for (int i = 0; i < groupList.size(); i++) {
				String groupName = groupList.get(i);
				if (map.get(groupName) == null) {
					map.put(groupName, new ArrayList<String>());
				}
				if (!map.get(groupName).contains(rawNamesList.get(i))) {
					map.get(groupName).add(rawNamesList.get(i));
				}
				
			}
			return map;
		}
		return null;
	}
	
	
	/**
	 * @return the genome group name/VCF file association map
	 */
	public Map<String, List<File>> getGenomeFilesAssociation () {
		if (validVCF) {
			Map<String, List<File>> map = new HashMap<String, List<File>>();
			List<String> groupList = data.getColumnList(0);
			List<String> vcfList = data.getColumnList(3);
			for (int i = 0; i < groupList.size(); i++) {
				String groupName = groupList.get(i);
				String vcfName = vcfList.get(i);
				if (map.get(groupName) == null) {
					map.put(groupName, new ArrayList<File>());
				}
				if (!map.get(groupName).contains(vcfName)) {
					map.get(groupName).add(new File(vcfName));
				}
			}
			return map;
		}
		return null;
	}
	
	
	/**
	 * @return the raw/usual genome name association map
	 */
	public Map<String, String> getGenomeNamesAssociation () {
		if (validVCF) {
			Map<String, String> map = new HashMap<String, String>();
			List<String> namesList = data.getColumnList(1);
			List<String> rawNamesList = data.getColumnList(4);
			for (int i = 0; i < rawNamesList.size(); i++) {
				map.put(rawNamesList.get(i), namesList.get(i));
			}
			return map;
		}
		return null;
	}
	
	
	/**
	 * @return the VCF type/files association map
	 */
	public Map<VCFType, List<File>> getFilesTypeAssociation () {
		if (validVCF) {
			Map<VCFType, List<File>> map = new HashMap<VCFType, List<File>>();
			List<String> typeList = data.getColumnList(2);
			List<String> vcfList = data.getColumnList(3);
			for (int i = 0; i < typeList.size(); i++) {
				VCFType type = VCFType.getTypeFromString(typeList.get(i));
				String vcfName = vcfList.get(i);
				if (map.get(type) == null) {
					map.put(type, new ArrayList<File>());
				}
				if (!map.get(type).contains(vcfName)) {
					map.get(type).add(new File(vcfName));
				}
			}
			return map;
		}
		return null;
	}
	
	
	/**
	 * @return true if the multi genome settings are valid
	 */
	public boolean isValidMultigenomeProject () {
		validVCF = checkVCFValidity();
		return validVCF;
	}
	
	
	private boolean checkVCFValidity () {
		boolean valid;
		List<String> errors = new ArrayList<String>();
		for (int col = 0; col < 5; col++) {
			if (col != 2) {
				List<String> list = data.getColumnList(col);
				for (int row = 0; row < list.size(); row++) {
					String value = list.get(row);
					switch (col) {
					case 0:
						if (value.equals(GROUP_LIST)) {
							errors.add("Please select a correct group (row: " + (row + 1) + ")");
						}
						break;
					case 1:
						if (value.equals(GENOME_LIST)) {
							errors.add("Please select a correct genome (row: " + (row + 1) + ")");
						}
						break;
					case 3:
						if (value.equals(FILE_LIST)) {
							errors.add("Please select a correct file (row: " + (row + 1) + ")");
						}
						break;
					case 4:
						if (value.equals(RAW_NAMES_LIST)) {
							errors.add("Please select a correct genome raw file (row: " + (row + 1) + ")");
						}
						break;
					default:
						break;
					}
				}
			}
		}
		valid = showErrors(errors);
		return valid;
	}
	
	
	private boolean showErrors (List<String> errors) {
		boolean valid = true;
		if (errors.size() > 0) {
			valid = false;
			JOptionPane.showMessageDialog(getRootPane(), errors.get(0), "Invalid VCF selection", JOptionPane.WARNING_MESSAGE);
			/*for (String error: errors) {
				JOptionPane.showMessageDialog(getRootPane(), error, "Invalid VCF selection", JOptionPane.WARNING_MESSAGE);
			}*/
		}
		return valid;
	}
	

	/**
	 * @return the instance
	 */
	protected static VCFLoader getInstance() {
		return instance;
	}

	
	/**
	 * @return the dialogWidth
	 */
	protected static int getDialogWidth() {
		return DIALOG_WIDTH;
	}


	/**
	 * @return the dialogHeight
	 */
	protected static int getDialogHeight() {
		return DIALOG_HEIGHT;
	}


	/**
	 * @return the tableWidth
	 */
	protected static int getTableWidth() {
		return TABLE_WIDTH;
	}


	/**
	 * @return the tableHeight
	 */
	protected static int getTableHeight() {
		return TABLE_HEIGHT;
	}


	/**
	 * @return the scrollpaneWidth
	 */
	protected static int getScrollpaneWidth() {
		return SCROLLPANE_WIDTH;
	}


	/**
	 * @return the scrollpaneHeight
	 */
	protected static int getScrollpaneHeight() {
		return SCROLLPANE_HEIGHT;
	}


	/**
	 * @return the dialogListWidth
	 */
	protected static int getDialogListWidth() {
		return DIALOG_LIST_WIDTH;
	}


	/**
	 * @return the dialogListHeight
	 */
	protected static int getDialogListHeight() {
		return DIALOG_LIST_HEIGHT;
	}


	/**
	 * @return the buttonSide
	 */
	protected static int getButtonSide() {
		return BUTTON_SIDE;
	}
	
}
