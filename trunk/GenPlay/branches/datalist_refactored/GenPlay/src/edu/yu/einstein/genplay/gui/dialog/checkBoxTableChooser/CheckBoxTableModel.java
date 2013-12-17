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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.checkBoxTableChooser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;


/**
 * This class is the table model used in {@link CheckBoxTableChooserDialog} class.
 * The table shows 2 columns.
 * The first one is an item of anyclass
 * the second one is the checkbox for selection.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
class CheckBoxTableModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = 136782955769801093L;

	private String[] 			columnNames;	// Column names
	private List<List<Object>> 	data;			// table data


	/**
	 * Constructor of {@link CheckBoxTableModel}
	 */
	protected CheckBoxTableModel () {
		super();
		this.columnNames = CheckBoxTableChooserDialog.COLUMN_NAMES;
		this.data = new ArrayList<List<Object>>();
	}


	/**
	 * Sets the data
	 * @param fullList		list of items available for selection
	 * @param selectedList	list of selected items
	 */
	protected void setData (List<T> fullList, List<T> selectedList) {
		for (int i = 0; i < fullList.size(); i++) {
			T item = fullList.get(i);
			addRow(i, item);
			if ((selectedList) != null && (selectedList.contains(item))) {
				setValueAt(true, i, 1);
			} else {
				setValueAt(false, i, 1);
			}
		}
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	@Override
	public int getRowCount() {
		if (data != null) {
			return data.size();
		} else {
			return 0;
		}
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < data.size()) {
			switch (columnIndex) {
			case 0:
				return data.get(rowIndex).get(0);
			case 1:
				return data.get(rowIndex).get(1);
			default:
				return null;
			}
		}
		return null;
	}


	@Override
	public Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return String.class;
		case 1:
			return Boolean.class;
		default:
			return Integer.class;
		}
	}


	/**
	 * Defines if a cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 1) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * This method sets the value of a precise cell.
	 * @param value	the value to add
	 * @param row	the row
	 * @param col	the column
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < data.size()) {
			data.get(row).set(col, value);
			fireTableCellUpdated(row, col);
		} else {
			List<Object> line = new ArrayList<Object>();
			line.add(row);
			line.add(null);
			line.add(false);
			data.add(row, line);
			setValueAt(value, row, col);
		}
	}


	/**
	 * This method adds a row to the table
	 * @param row	the row to add
	 * @param item the item to add
	 */
	protected void addRow(int row, T item) {
		setValueAt(item, row, 0);
		setValueAt(false, row, 1);
	}


	/**
	 * Sets all selected column rows
	 * @param list
	 * @param value
	 */
	protected void setSelectedValue(int[] list, boolean value) {
		for (int i: list) {
			setValueAt(value, i, 1);
		}
	}


	/**
	 * @return the data
	 */
	protected  List<List<Object>> getData() {
		return data;
	}


	/**
	 * @return the list of selected items
	 */
	protected List<T> getSelectedItems () {
		List<T> result = new ArrayList<T>();
		for (List<Object> row: data) {
			if ((Boolean) row.get(1)) {
				@SuppressWarnings("unchecked")
				T item = (T)row.get(0);
				result.add(item);
			}
		}
		return result;
	}


	/**
	 * @return the full item list
	 */
	protected List<T> getFullItemList () {
		List<T> result = new ArrayList<T>();
		for (List<Object> row: data) {
			@SuppressWarnings("unchecked")
			T item = (T)row.get(0);
			result.add(item);
		}
		return result;
	}
	
	
	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false 
	 */
	protected void move (int[] list, boolean toUp) {
		Object objItem;
		Object objSelected;
		if (toUp) {
			for (int i: list) {
				if (i > 0) {
					objItem = data.get(i - 1).get(0);
					objSelected = data.get(i - 1).get(1);

					setValueAt(data.get(i).get(0), i - 1, 0);
					setValueAt(data.get(i).get(1), i - 1, 1);

					setValueAt(objItem, i, 0);
					setValueAt(objSelected, i, 1);
				}
			}
		} else {
			list = edu.yu.einstein.genplay.util.Utils.reverse(list);
			for (int i: list) {
				if (i < (getRowCount() - 1)){
					objItem = data.get(i + 1).get(0);
					objSelected = data.get(i + 1).get(1);

					setValueAt(data.get(i).get(0), i + 1, 0);
					setValueAt(data.get(i).get(1), i + 1, 1);

					setValueAt(objItem, i, 0);
					setValueAt(objSelected, i, 1);
				}
			}
		}
		fireTableDataChanged();
	}
}
