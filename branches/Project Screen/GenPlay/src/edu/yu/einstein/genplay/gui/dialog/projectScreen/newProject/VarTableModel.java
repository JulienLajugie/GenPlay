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
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the table model used in {@link VarPanel} class.
 * @author Nicolas Fourel
 */
class VarTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 136782955769801093L;
	
	private String[] 							columnNames;	// Column names
	private Map<Integer, Map<Integer, Object>> 	data;			// table data		
	
	
	/**
	 * Constructor of {@link VarTableModel}
	 */
	public VarTableModel (String[] columNames) {
		super();
		this.columnNames = columNames;
		this.data = new HashMap<Integer, Map<Integer,Object>>();
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
		if (data.containsKey(rowIndex)) {
		if (columnIndex == 1) {
			return (Object)((File)data.get(rowIndex).get(columnIndex)).getName();
		} else {
			return data.get(rowIndex).get(columnIndex);
		}
		}
		return null;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
		if (c == 0) {
			return Integer.class;
		} else {
			return File.class;
		}
	}
	
	
	/**
	 * Defines if a cell is editable.
	 */
	public boolean isCellEditable(int row, int col) {
        return false;
    }
	
	
	/**
	 * This method sets the value of a precise cell.
	 * @param value	the value to add
	 * @param row	the row
	 * @param col	the column
	 */
	public void setValueAt(Object value, int row, int col) {
		if (data.containsKey(row)) {
			data.get(row).put(col, value);
			fireTableCellUpdated(row, col);
		} else {
			data.put(row, new HashMap<Integer, Object>());
			setValueAt(value, row, col);
		}
	}
	
	
	/**
	 * This method sets the value of a precise cell in a given list.
	 * @param list	the list where put the value
	 * @param value	the value to add
	 * @param row	the row
	 * @param col	the column
	 * @return		the list with the added value
	 */
	public Map<Integer, Map<Integer, Object>> setValueAt(Map<Integer, Map<Integer, Object>> list, Object value, int row, int col) {
		if (list.containsKey(row)) {
			list.get(row).put(col, value);
			fireTableCellUpdated(row, col);
		} else {
			list.put(row, new HashMap<Integer, Object>());
			setValueAt(list, value, row, col);
		}
		return list;
	}
	
	
	/**
	 * This method adds a row to the table according to a file.
	 * @param row	the row to add
	 * @param f		the file to add
	 */
	protected void addRow (int row, File f) {
		System.out.println(f.getName());
		setValueAt(row+1, row, 0);
		setValueAt(f, row, 1);
	}
	
	
	/**
	 * This method removes a list of rows from the table.
	 * @param list	the list of row numbers to remove
	 */
	protected void removeRows (int[] list) {
		Map<Integer, Map<Integer, Object>> 	data_tmp = new HashMap<Integer, Map<Integer, Object>>();
		boolean toKeep;
		int new_row = 0;
		for (Integer row: data.keySet()) {
			toKeep = true;
			for (int i: list) {
				if(row == i){
					toKeep = false;
				}
			}
			if (toKeep){
				data_tmp = setValueAt (data_tmp, new_row+1, new_row, 0);
				data_tmp = setValueAt (data_tmp, data.get(row).get(1), new_row, 1);
				new_row++;
			}
		}
		data = data_tmp;
		fireTableDataChanged();
	}
	
	
	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false 
	 */
	protected void move (int[] list, boolean toUp) {
		Object obj;
		if (toUp) {
			for (int i: list) {
				if (i > 0) {
					obj = data.get(i-1).get(1);
					setValueAt(i, i-1, 0);
					setValueAt(data.get(i).get(1), i-1, 1);
					setValueAt(i+1, i, 0);
					setValueAt(obj, i, 1);
				}
			}
		} else {
			list = reverse(list);
			for (int i: list) {
				if (i<(getRowCount()-1)){
					obj = data.get(i+1).get(1);
					setValueAt(i+2, i+1, 0);
					setValueAt(data.get(i).get(1), i+1, 1);
					setValueAt(i+1, i, 0);
					setValueAt(obj, i, 1);
				}
			}
		}
		fireTableDataChanged();
	}
	
	
	/**
	 * This methods reverse an array of int
	 * @param b the int array
	 * @return	the reversed array
	 */
	private int[] reverse(int[] b) {
		int left  = 0;          // index of leftmost element
		int right = b.length-1; // index of rightmost element
		
		while (left < right) {
			// exchange the left and right elements
			int temp = b[left];
			b[left]  = b[right];
			b[right] = temp;
			// move the bounds toward the center
			left++;
			right--;
		}
		return b;
	}
	
	
	/**
	 * @return the var file list
	 */
	protected List<File> getFiles () {
		List<File> list = new ArrayList<File>();
		for (Map<Integer, Object> row: data.values()) {
			list.add((File)row.get(1));
		}
		return list;
	}
	
}