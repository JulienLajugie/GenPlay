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
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import edu.yu.einstein.genplay.core.enums.VCFType;

/**
 * This class handles the data contains in the VCF loader.
 * @author Nicolas Fourel
 * @version 0.1
 */
class VCFData {

	private VCFLoader 			loader;			// VCF loader
	private List<List<Object>> 	data;			// table data
	private VCFList[] 			dialogList;		// the dialog list editors array
	private String[] 			columnNames;	// column names
	private Hashtable<Integer, TableCellEditor> tableCellEditor;	// map to store all table cell editor


	/**
	 * Constructor of {@link VCFData}
	 * @param loader the VCF loader
	 */
	protected VCFData (VCFLoader loader) {
		this.loader = loader;
		data = new ArrayList<List<Object>>();
		tableCellEditor = new Hashtable<Integer, TableCellEditor>();
		initColumnNames();
		initDialogsList();
	}


	/**
	 * Initializes the name of columns
	 */
	private void initColumnNames () {
		this.columnNames = new String[5];
		this.columnNames[0] = "Group";
		this.columnNames[1] = "Genome";
		this.columnNames[2] = "Type";
		this.columnNames[3] = "File";
		this.columnNames[4] = "Raw name(s)";
	}


	/**
	 * Initializes the dialog list editors
	 */
	protected void initDialogsList() {
		if(dialogList == null) {
			dialogList = new VCFList[5];
			for (int i = 0; i < 5; i++) {
				dialogList[i] = new VCFList(getColumnName(i), getFirstList(i));
			}
			dialogList[3].setFile(true);
		}
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<List<Object>> data) {
		this.data = new ArrayList<List<Object>>();
		for (List<Object> list: data) {
			this.data.add(list);
		}

		initDialogsList();
		for(int i = 0; i < dialogList.length; i++) {
			for (List<Object> elements: data) {
				dialogList[i].addElement(elements.get(i).toString());
			}
		}
		VCFLoader.getInstance().setAllCellEditor();
	}


	/**
	 * @return the data
	 */
	protected List<List<Object>> getData() {
		return data;
	}


	/**
	 * Adds a row
	 */
	protected void addRow () {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < 5 ; i++) {
			list.add(getNewComboBox(i).getSelectedItem().toString());
		}
		data.add(list);
	}


	/**
	 * Removes rows
	 * @param rows rows to remove
	 */
	protected void removeRows (int[] rows) {
		for (int i: rows) {
			data.remove(i);
		}
		List<List<Object>> newList = new ArrayList<List<Object>>();
		for (List<Object> line: data) {
			newList.add(line);
		}
		data = newList;
	}


	/**
	 * @param col the column index
	 * @return a combo box according to the column type
	 */
	protected JComboBox getNewComboBox (int col) {
		JComboBox box = new JComboBox();
		box.setName("" + col);
		List<String> list = dialogList[col].getElementsList();
		for (String s: list) {
			box.addItem(s);
		}

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox box = (JComboBox)arg0.getSource();
				String value = box.getSelectedItem().toString();
				if (box.getName().equals("3")) {
					for (int i = 0; i < data.size(); i++) {
						if (value.equals(getValueAt(i, 3))) {
							loader.updateRawNames(value, i, null);
							break;
						}
					}
				}
			}
		});

		return box;
	}


	/**
	 * @param col the column index
	 * @return the first according to the column
	 */
	private List<String> getFirstList (int col) {
		List<String> list = new ArrayList<String>();
		switch (col) {
		case 0:
			list.add(VCFLoader.GROUP_LIST);
			break;
		case 1:
			list.add(VCFLoader.GENOME_LIST);
			break;
		case 2:
			list.add(VCFLoader.TYPE_LIST);
			list.add(VCFType.INDELS.toString());
			list.add(VCFType.SNPS.toString());
			list.add(VCFType.SV.toString());
			break;
		case 3:
			list.add(VCFLoader.FILE_LIST);
			break;
		case 4:
			list.add(VCFLoader.RAW_NAMES_LIST);
			break;
		default:
			break;
		}
		return list;
	}


	/**
	 * @return the number of column
	 */
	protected int getColumnCount() {
		return columnNames.length;
	}


	/**
	 * @return the column names
	 */
	protected String[] getColumnNames() {
		return columnNames;
	}


	/**
	 * @param col the column index
	 * @return the name of the column
	 */
	protected String getColumnName(int col) {
		return columnNames[col];
	}


	/**
	 * @return the number of row
	 */
	protected int getRowCount() {
		return data.size();
	}


	/**
	 * @param row the row index
	 * @param col the column index
	 * @return the object at the row/col position
	 */
	protected Object getValueAt(int row, int col) {
		if (row >= 0 && row < getRowCount()) {
			return data.get(row).get(col);
		} else {
			return null;
		}
	}


	/**
	 * @param value value to put at the row/col position
	 * @param row the row index
	 * @param col the column index
	 */
	protected void setValueAt(Object value, int row, int col) {
		if (row < getRowCount()) {
			data.get(row).set(col, value);
		} else {
			loader.fixRowBug(row, col);
		}
	}


	/**
	 * @param col the column index
	 * @return the list of string at the column
	 */
	protected List<String> getColumnList (int col) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < getRowCount(); i++) {
			list.add(getValueAt(i, col).toString());
		}
		return list;
	}


	/**
	 * Adds an editor for a specific row
	 * @param row 	row index
	 * @param e		table cell editor
	 */
	protected void addEditorForRow(int row, TableCellEditor e ) {
		tableCellEditor.put(new Integer(row), e);
	}


	/**
	 * Removes an editor at specific rows
	 * @param rows row index array
	 */
	protected void removeEditorForRow(int[] rows) {
		for (int row: rows) {
			removeEditorForRow(row);
		}
	}


	/**
	 * Removes an editor at a specific row
	 * @param row row index
	 */
	protected void removeEditorForRow(int row) {
		if (tableCellEditor.get(row) != null) {
			tableCellEditor.remove(new Integer(row));
		}
	}


	/**
	 * @param row the row index
	 * @param col the column index
	 * @return the table cell editor at the specifi row/col position
	 */
	protected TableCellEditor getEditor(int row, int col) {
		if (tableCellEditor.get(row) != null) {
			return (TableCellEditor)tableCellEditor.get(row);
		}
		return null;
	}


	/**
	 * Displays the dialog list editor of a column
	 * @param col the column index
	 */
	protected void displayList(int col) {
		dialogList[col].display();
	}


	/**
	 * @param col the column index
	 * @return the number of element in a list
	 */
	protected int getNumberElementList (int col) {
		List<String> list = getColumnList(col);
		int size;
		List<String> listCleared = new ArrayList<String>();
		for (String s: list) {
			if (!listCleared.contains(s)) {
				listCleared.add(s);
			}
		}
		list = listCleared;
		size = list.size();
		if (list.contains(VCFLoader.GROUP_LIST) ||
				list.contains(VCFLoader.GENOME_LIST) ||
				list.contains(VCFLoader.FILE_LIST)) {
			size--;
		}
		return size;
	}

}