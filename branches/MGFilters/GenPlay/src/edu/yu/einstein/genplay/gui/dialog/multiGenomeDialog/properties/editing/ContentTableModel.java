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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K> class of the data that are used in the table
 */
public abstract class ContentTableModel<K> extends AbstractTableModel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;

	protected final 	String[]	columnNames;	// the table column names
	protected 			List<K>		data;			// list of data


	/**
	 * Constructor of {@link ContentTableModel}
	 * @param columnNames name of the columns
	 */
	protected ContentTableModel (String[] columnNames) {
		this.columnNames = columnNames;
		data = new ArrayList<K>();
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public boolean isCellEditable(int row, int col)	{
		return false;
	}
	
	
	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}
	
	
	/**
	 * @return the data
	 */
	protected List<K> getData() {
		return data;
	}
	

	/**
	 * Add a row
	 * @param row row to insert
	 */
	protected void addRow (K row) {
		data.add(row);
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}


	/**
	 * Deletes a row
	 * @param row the row number
	 */
	protected void deleteRow(int row) {
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}
	

	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false 
	 */
	protected void move (int[] list, boolean toUp) {
		if (toUp) {
			for (int i: list) {
				moveStripeDataUp(i);
			}
		} else {
			for (int i: list) {
				moveStripeDataDown(i);
			}
		}
		fireTableDataChanged();
	}
	
	
	/**
	 * Move a stripe data one step lower in the list in order to show it one row closer to the top of the table.
	 * @param index index of the row
	 */
	private void moveStripeDataUp (int index) {
		if (index > 0) {
			K dataToMove = data.get(index);
			K dataToReplace = data.get(index - 1);
			List<K> newDataList = new ArrayList<K>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				K currentData = data.get(currentIndex);
				if (currentData.equals(dataToReplace)) {
					newDataList.add(dataToMove);
					newDataList.add(dataToReplace);
					currentIndex++;
				} else {
					newDataList.add(currentData);
				}
				currentIndex++;
			}
			data = newDataList;
		}
	}
	
	
	/**
	 * Move a stripe data one step higher in the list in order to show it one row closer to the bottom of the table.
	 * @param index index of the row
	 */
	private void moveStripeDataDown (int index) {
		if (index < (data.size() - 1)) {
			K dataToMove = data.get(index);
			K dataToReplace = data.get(index + 1);
			List<K> newDataList = new ArrayList<K>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				K currentData = data.get(currentIndex);
				if (currentData.equals(dataToMove)) {
					newDataList.add(dataToReplace);
					newDataList.add(dataToMove);
					currentIndex++;
				} else {
					newDataList.add(currentData);
				}
				currentIndex++;
			}
			data = newDataList;
		}
	}
	

	@Override
	public abstract Object getValueAt(int row, int col);


	@Override
	public abstract Class<?> getColumnClass(int column);


	/**
	 * @param data the data to set
	 */
	protected abstract void setData(List<K> data);

}
