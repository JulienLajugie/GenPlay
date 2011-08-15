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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.gui.projectFrame.newProject.vcf.VCFLoader;


/**
 * This class is the table model used in {@link VCFLoader} class.
 * @author Nicolas Fourel
 * @version 0.1
 */
class ChromosomeChooserTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 136782955769801093L;
	
	private String[] 			columnNames;	// Column names
	private List<List<Object>> 	data;			// table data
	
	
	/**
	 * Constructor of {@link ChromosomeChooserTableModel}
	 * @param columNames column names
	 */
	public ChromosomeChooserTableModel (String[] columNames) {
		super();
		this.columnNames = columNames;
		this.data = new ArrayList<List<Object>>();
	}
	
	
	/**
	 * Constructor of {@link ChromosomeChooserTableModel}
	 * @param columNames 	column names
	 * @param data 			data
	 */
	public ChromosomeChooserTableModel (String[] columNames, List<List<Object>> data) {
		super();
		this.columnNames = columNames;
		this.data = data;
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
			return data.get(rowIndex).get(columnIndex);
		}
		return null;
	}
	
	
	public Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return Integer.class;
		case 3:
			return Boolean.class;
		default:
			return Integer.class;
		}
	}
	
	
	/**
	 * Defines if a cell is editable.
	 */
	public boolean isCellEditable(int row, int col) {
        if (col == 3) {
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
	public void setValueAt(Object value, int row, int col) {
		if (row < data.size()) {
			data.get(row).set(col, value);
			fireTableCellUpdated(row, col);
		} else {
			data.add(row, new ArrayList<Object>());
			setValueAt(value, row, col);
		}
	}
	
	
	/**
	 * This method adds a row to the table according to a file.
	 * @param row	the row to add
	 * @param chromosome the chromosome to add
	 */
	protected void addRow (int row, Chromosome chromosome) {
		setValueAt(row+1, row, 0);
		setValueAt(chromosome.getName(), row, 1);
		setValueAt(chromosome.getLength(), row, 2);
		setValueAt(true, row, 3);
	}
	
	
	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false 
	 */
	protected void move (int[] list, boolean toUp) {
		Object objName;
		Object objLength;
		Object objSelected;
		if (toUp) {
			for (int i: list) {
				if (i > 0) {
					objName = data.get(i-1).get(1);
					objLength = data.get(i-1).get(2);
					objSelected = data.get(i-1).get(3);
					
					setValueAt(i, i-1, 0);
					setValueAt(data.get(i).get(1), i-1, 1);
					setValueAt(data.get(i).get(2), i-1, 2);
					setValueAt(data.get(i).get(3), i-1, 3);
					
					
					setValueAt(i+1, i, 0);
					setValueAt(objName, i, 1);
					setValueAt(objLength, i, 2);
					setValueAt(objSelected, i, 3);
				}
			}
		} else {
			list = reverse(list);
			for (int i: list) {
				if (i<(getRowCount()-1)){
					objName = data.get(i+1).get(1);
					objLength = data.get(i+1).get(2);
					objSelected = data.get(i+1).get(3);
					
					
					setValueAt(i+2, i+1, 0);
					setValueAt(data.get(i).get(1), i+1, 1);
					setValueAt(data.get(i).get(2), i+1, 2);
					setValueAt(data.get(i).get(3), i+1, 3);
					
					
					setValueAt(i+1, i, 0);
					setValueAt(objName, i, 1);
					setValueAt(objLength, i, 2);
					setValueAt(objSelected, i, 3);
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
	 * Sets all selected column rows
	 * @param list
	 * @param value
	 */
	protected void setSelectedValue (int[] list, boolean value) {
		for (int i: list) {
			setValueAt(value, i, 3);
		}
	}
	
	
	/**
	 * @return the data
	 */
	protected  List<List<Object>> getData () {
		return data;
	}
	
	
	/**
	 * @param data	data to set
	 */
	protected  void setData (List<List<Object>> data) {
		List<Object> line;
		for  (List<Object> row: data) {
			line = new ArrayList<Object>();
			for (int i = 0; i < 4; i++) {
				line.add(row.get(i));
			}
			this.data.add(line);
		}
		fireTableDataChanged();
	}
	
	
	/**
	 * Select all basics chromosome.
	 * A basic chromosome is chr1....chrX, chrY and chrM
	 */
	protected void selectBasicChr () {
		String name;
		String end;
		Boolean select;
		for (List<Object> row: this.data){
			name = row.get(1).toString();
			if (name.substring(0, 3).equals("chr")) {
				select = true;
				end = name.substring(3, name.length());
				try {
					Integer.parseInt(end);
				} catch (Exception e) {
					if (!end.equals("X") && !end.equals("Y") && !end.equals("M")) {
						select = false;
					}
				}
				setValueAt(select, Integer.parseInt(row.get(0).toString())-1, 3);
			}
		}
	}
	
}