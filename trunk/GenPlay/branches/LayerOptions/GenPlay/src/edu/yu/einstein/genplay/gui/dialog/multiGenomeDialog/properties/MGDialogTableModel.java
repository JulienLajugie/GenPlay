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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGDialogTableModel extends AbstractTableModel {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 3478197435828366331L;


	private String[] columnNames;	// column names
	private Object[][] data;		// list of data


	/**
	 * Constructor of {@link MGDialogTableModel}
	 * @param columnNames 	the names of columns
	 * @param data 			the data array
	 */
	public MGDialogTableModel (String[] columnNames, Object[][] data) {
		this.columnNames = columnNames;
		this.data = data;
	}


	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	@Override
	public int getRowCount() {
		return data.length;
	}


	@Override
	public boolean isCellEditable(int row, int col)	{
		return false;
	}


	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
        fireTableCellUpdated(row, col);
	}


	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
		//return getValueAt(0, column).getClass();
	}


	/**
	 * @return the data
	 */
	public Object[][] getData() {
		return data;
	}

}
