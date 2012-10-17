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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog;

import javax.swing.table.AbstractTableModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLineTableModel extends AbstractTableModel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;

	private Object[] 	columnNames;
	private Object[][] 	data;


	/**
	 * Constructor of {@link VCFLineTableModel}
	 */
	protected VCFLineTableModel (Object[] columnNames, Object[][] data) {
		this.columnNames = columnNames;
		this.data = data;
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
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}


	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
