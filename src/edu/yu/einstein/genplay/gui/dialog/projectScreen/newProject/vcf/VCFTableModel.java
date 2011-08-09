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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

/**
 * This class is the table model for the {@link VCFTable} object
 * @author Nicolas Fourel
 * @version 0.1
 */
class VCFTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8124585566610818031L;

	
	private VCFData		data;			// Table data	


	/**
	 * Constructor of {@link VCFTableModel}
	 * @param data the VCF data object
	 */
	protected VCFTableModel (VCFData data) {
		this.data = data;
	}

	
	@Override
	public int getColumnCount() {
		return data.getColumnCount();
	}


	@Override
	public int getRowCount() {
		return data.getRowCount();
	}

	
	@Override
	public String getColumnName(int col) {
		return data.getColumnName(col);
	}


	@Override
	public Object getValueAt(int row, int col) {
		return data.getValueAt(row, col);
	}
	
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		data.setValueAt(value, row, col);
	}


	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	
	protected TableCellEditor getCellEditor(int row, int col) {
		return data.getEditor(row, col);
	}
	
}