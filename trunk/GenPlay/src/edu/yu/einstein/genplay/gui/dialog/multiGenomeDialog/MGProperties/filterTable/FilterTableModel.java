/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.gui.MGDisplaySettings.FiltersData;


/**
 * @author Nicolas Fourel
 */
public class FilterTableModel extends AbstractTableModel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;

	/** Name of the columns of the table */
	protected static final String[] COLUMN_NAMES = {"Edit", "Layer", "ID", "Filter", "File"};

	/** Index used for edit column */
	protected static final int EDIT_BUTTON_INDEX = 0;

	/** Index used for layer column */
	protected static final int LAYER_INDEX = 1;

	/** Index used for the vcf header id column */
	protected static final int ID_INDEX = 2;

	/** Index used for the filter column */
	protected static final int FILTER_INDEX = 3;

	/** Index used for vcf file column */
	protected static final int VCF_FILE_INDEX = 4;

	protected final 	String[]			columnNames;	// the table column names
	protected 			List<FiltersData>	data;			// list of data
	protected			List<JButton>		buttons;		// list of buttons
	private				FiltersData			currentData;


	/**
	 * Constructor of {@link FilterTableModel}
	 */
	protected FilterTableModel () {
		columnNames = COLUMN_NAMES;
		data = new ArrayList<FiltersData>();
		buttons = new ArrayList<JButton>();
		currentData = null;
	}


	/**
	 * Add a row
	 * @param row row to insert
	 */
	protected void addRow (FiltersData row) {
		data.add(row);
		buttons.add(getNewButton());
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}


	/**
	 * Deletes a row
	 * @param row the row number
	 */
	protected void deleteRow(int row) {
		data.remove(row);
		buttons.remove(row);
		fireTableRowsDeleted(row, row);
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case VCF_FILE_INDEX:
			return String.class;
		case ID_INDEX:
			return String.class;
		case FILTER_INDEX:
			return String.class;
		case LAYER_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}


	public FiltersData getCurrentData () {
		return currentData;
	}


	/**
	 * @return the data
	 */
	protected List<FiltersData> getData() {
		return data;
	}


	/**
	 * @return the index of the edit column
	 */
	protected int getEditColumnIndex () {
		return EDIT_BUTTON_INDEX;
	}


	protected JButton getNewButton () {
		JButton button = new JButton();
		//button.setMargin(new Insets(0, 0, 0, 0));
		button.setText("e");
		//button.setPreferredSize(new Dimension(10, 10));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton button = (JButton) arg0.getSource();
				currentData = data.get(buttons.indexOf(button));
			}
		});
		return button;
	}


	@Override
	public int getRowCount() {
		return data.size();
	}


	@Override
	public Object getValueAt(int row, int col) {
		if (col == EDIT_BUTTON_INDEX) {
			return buttons.get(row);
		}
		FiltersData filtersData = data.get(row);
		switch (col) {
		case VCF_FILE_INDEX:
			return filtersData.getReaderForDisplay();
		case ID_INDEX:
			return filtersData.getIDForDisplay();
		case FILTER_INDEX:
			return filtersData.getFilterForDisplay();
		case LAYER_INDEX:
			return filtersData.getLayersForDisplay();
		default:
			return new Object();
		}
	}


	@Override
	public boolean isCellEditable(int row, int col)	{
		return false;
	}


	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false
	 */
	protected int[] move (int[] list, boolean toUp) {
		int[] movedRows = new int[list.length];
		int cpt = 0;
		if (toUp) {
			for (int i: list) {
				movedRows[cpt] = moveDataUp(i);
				cpt++;
			}
		} else {
			int[] reversedList = reverseIntArray(list);
			for (int i: reversedList) {
				movedRows[cpt] = moveDataDown(i);
				cpt++;
			}
		}
		fireTableDataChanged();
		return movedRows;
	}


	/**
	 * Move a data one step higher in the list in order to show it one row closer to the bottom of the table.
	 * @param index index of the row
	 */
	private int moveDataDown (int index) {
		if (index < (data.size() - 1)) {
			FiltersData dataToMove = data.get(index);
			FiltersData dataToReplace = data.get(index + 1);
			List<FiltersData> newDataList = new ArrayList<FiltersData>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				FiltersData currentData = data.get(currentIndex);
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
			return (index + 1);
		}
		return index;
	}


	/**
	 * Move a data one step lower in the list in order to show it one row closer to the top of the table.
	 * @param index index of the row
	 */
	private int moveDataUp (int index) {
		if (index > 0) {
			FiltersData dataToMove = data.get(index);
			FiltersData dataToReplace = data.get(index - 1);
			List<FiltersData> newDataList = new ArrayList<FiltersData>();

			int currentIndex = 0;
			while (currentIndex < data.size()){
				FiltersData currentData = data.get(currentIndex);
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
			return (index - 1);
		}
		return index;
	}


	public void resetCurrentData () {
		currentData = null;
	}


	/**
	 * Reverse an array: the last value becomes the firt one and so on.
	 * @param array array to reverse
	 * @return the reversed array
	 */
	private int[] reverseIntArray (int[] array) {
		int[] newArray = new int[array.length];
		for (int i = 0; i < newArray.length; i++) {
			int index = array.length - 1 - i;
			newArray[i] = array[index];
		}
		return newArray;
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<FiltersData> data) {
		this.data = data;
		buttons = new ArrayList<JButton>();
		for (int row = 0; row <data.size(); row++) {
			buttons.add(getNewButton());
			fireTableCellUpdated(row, VCF_FILE_INDEX);
			fireTableCellUpdated(row, ID_INDEX);
			fireTableCellUpdated(row, FILTER_INDEX);
			fireTableCellUpdated(row, LAYER_INDEX);
			fireTableCellUpdated(row, EDIT_BUTTON_INDEX);
		}
	}
}
