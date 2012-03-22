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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing;

import java.util.List;

import javax.swing.JTable;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K> class of the data that are used in the table
 */
public abstract class ContentTable<K> extends JTable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3342831482530035559L;
	
	
	/**
	 * @return the data
	 */
	@SuppressWarnings("unchecked")
	public List<K> getData() {
		return ((ContentTableModel<K>)getModel()).getData();
	}
	

	/**
	 * Add a row in the table
	 * @param data data to add
	 */
	@SuppressWarnings("unchecked")
	protected void addRow (K data) {
		((ContentTableModel<K>)getModel()).addRow(data);
	}
	
	
	/**
	 * Delete rows
	 * @param rows row indexes to delete
	 */
	@SuppressWarnings("unchecked")
	protected void removeRows (int[] rows) {
		rows = edu.yu.einstein.genplay.util.Utils.reverse(rows);
		for (int row: rows) {
			((ContentTableModel<K>)getModel()).deleteRow(row);
		}
	}
	
	
	/**
	 * Move the selected rows to the top of the table
	 */
	@SuppressWarnings("unchecked")
	protected void moveRowsUp () {
		((ContentTableModel<K>)getModel()).move(getSelectedRows(), true);
	}
	
	
	/**
	 * Move the selected rows to the bottom of the table
	 */
	@SuppressWarnings("unchecked")
	protected void moveRowsDown () {
		((ContentTableModel<K>)getModel()).move(getSelectedRows(), false);
	}

	
	/**
	 * @return an array containing size of each column
	 */
	protected int[] getColumnSize () {
		@SuppressWarnings("unchecked")
		int columnNumber = ((ContentTableModel<K>)getModel()).getColumnCount();
		int[] widths = new int[columnNumber];

		for (int i = 0; i < columnNumber; i++) {
			widths[i] = getColumnModel().getColumn(i).getPreferredWidth();
		}
		return widths;
	}
	
	
	/**
	 * @param data the data to set
	 */
	protected abstract void setData(List<K> data);
	
		
	/**
	 * This method scans all cells of each column to find the maximum width for each of them.
	 * Then, it sets the column size according to the width.
	 */
	protected abstract void updateColumnSize ();
	
	
}
