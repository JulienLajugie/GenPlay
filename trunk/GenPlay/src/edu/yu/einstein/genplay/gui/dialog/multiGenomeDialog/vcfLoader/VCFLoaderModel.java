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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomComboBox;

/**
 * This class is the table model for {@link VCFLoaderTable}
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderModel extends AbstractTableModel {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 3478197435828366331L;


	private String[] columnNames;	// column names
	private List<VCFData> data;		// list of data


	/**
	 * Constructor of {@link VCFLoaderModel}
	 */
	public VCFLoaderModel () {
		initializeColumnNames();
		data = new ArrayList<VCFData>();
	}


	/**
	 * Adds an empty row
	 */
	protected void addEmptyRow() {
		VCFData newData = new VCFData();
		data.add(newData);
		fireTableRowsInserted(
				data.size() - 1,
				data.size() - 1);
		fireTableRowsUpdated(data.size() - 1, data.size() - 1);
	}


	/**
	 * Deletes a row
	 * @param row the row number
	 */
	public void deleteRow(int row) {
		data.remove(row);
		fireTableRowsDeleted(row, row);
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case VCFData.FILE_INDEX:
			return File.class;
		case VCFData.RAW_INDEX:
			return String.class;
		case VCFData.NICKNAME_INDEX:
			return String.class;
		case VCFData.GROUP_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}


	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		return data;
	}


	@Override
	public int getRowCount() {
		return data.size();
	}


	@Override
	public Object getValueAt(int row, int col) {
		VCFData vCFData = data.get(row);
		switch (col) {
		case VCFData.FILE_INDEX:
			return vCFData.getFile();
		case VCFData.RAW_INDEX:
			return vCFData.getRaw();
		case VCFData.NICKNAME_INDEX:
			return vCFData.getNickname();
		case VCFData.GROUP_INDEX:
			return vCFData.getGroup();
		default:
			return new Object();
		}
	}


	/**
	 * Initializes the column names
	 */
	private void initializeColumnNames () {
		columnNames = new String[4];
		columnNames[0] = VCFData.FILE_NAME;
		columnNames[1] = VCFData.RAW_NAME;
		columnNames[2] = VCFData.NICKNAME;
		columnNames[3] = VCFData.GROUP_NAME;
	}


	@Override
	public boolean isCellEditable(int row, int col)	{
		if (col == VCFData.RAW_INDEX) {
			Object value = getValueAt(row, VCFData.FILE_INDEX);
			if (value == null) {
				return false;
			} else if (value.toString().equals("") || value.toString().equals(CustomComboBox.ADD_TEXT)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		this.data = data;
		for (int row = 0; row <data.size(); row++) {
			fireTableCellUpdated(row, VCFData.FILE_INDEX);
			fireTableCellUpdated(row, VCFData.RAW_INDEX);
			fireTableCellUpdated(row, VCFData.NICKNAME_INDEX);
			fireTableCellUpdated(row, VCFData.GROUP_INDEX);
		}
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
		if (value != null) {
			if (row < getRowCount()) {
				VCFData vCFData = data.get(row);
				switch (col) {
				case VCFData.FILE_INDEX:
					vCFData.setFile(new File(value.toString()));
					break;
				case VCFData.RAW_INDEX:
					vCFData.setRaw(value.toString());
					break;
				case VCFData.NICKNAME_INDEX:
					vCFData.setNickname(value.toString());
					break;
				case VCFData.GROUP_INDEX:
					vCFData.setGroup(value.toString());
					break;
				default:
					System.out.println("Invalid column index");
				}
			} else {
				System.out.println("Invalid row index");
			}
			fireTableCellUpdated(row, col);
		}
	}

}
