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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.stripesEditing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesTableModel extends AbstractTableModel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;

	private final 	String[] 	columnNames = {"Genome", "Variation", "Track"};	// the table column names
	private List<StripeData> 	data;		// list of data


	/**
	 * Constructor of {@link StripesTableModel}
	 */
	public StripesTableModel () {
		data = new ArrayList<StripeData>();
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


	@Override
	public Object getValueAt(int row, int col) {
		StripeData stripeData = data.get(row);
		switch (col) {
		case StripeData.GENOME_INDEX:
			return stripeData.getGenomeForDisplay();
		case StripeData.VARIANT_INDEX:
			return stripeData.getVariantListForDisplay();
		case StripeData.TRACK_INDEX:
			return stripeData.getTrackListForDisplay();
		default:
			return new Object();
		}
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case StripeData.GENOME_INDEX:
			return String.class;
		case StripeData.VARIANT_INDEX:
			return JPanel.class;
		case StripeData.TRACK_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}


	/**
	 * Add an empty row
	 */
	protected void addEmptyRow () {
		StripeData row = new StripeData();
		data.add(row);
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}


	/**
	 * Add an empty row
	 */
	protected void addRow (StripeData row) {
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
	 * @return the data
	 */
	public List<StripeData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<StripeData> data) {
		this.data = data;
		for (int row = 0; row <data.size(); row++) {
			fireTableCellUpdated(row, StripeData.GENOME_INDEX);
			fireTableCellUpdated(row, StripeData.VARIANT_INDEX);
			fireTableCellUpdated(row, StripeData.TRACK_INDEX);
		}
	}


	/**
	 * @return the columnNames
	 */
	protected String[] getColumnNames() {
		return columnNames;
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
			StripeData stripeDataToMove = data.get(index);
			StripeData stripeDataToReplace = data.get(index - 1);
			List<StripeData> newDataList = new ArrayList<StripeData>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				StripeData currentData = data.get(currentIndex);
				if (currentData.equals(stripeDataToReplace)) {
					newDataList.add(stripeDataToMove);
					newDataList.add(stripeDataToReplace);
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
			StripeData stripeDataToMove = data.get(index);
			StripeData stripeDataToReplace = data.get(index + 1);
			List<StripeData> newDataList = new ArrayList<StripeData>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				StripeData currentData = data.get(currentIndex);
				if (currentData.equals(stripeDataToMove)) {
					newDataList.add(stripeDataToReplace);
					newDataList.add(stripeDataToMove);
					currentIndex++;
				} else {
					newDataList.add(currentData);
				}
				currentIndex++;
			}
			data = newDataList;
		}
	}

}
