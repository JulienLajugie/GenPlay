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
		return data.size();
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


	@Override
	public Object getValueAt(int row, int col) {
		VCFData vCFData = data.get(row);
		switch (col) {
		case VCFData.GROUP_INDEX:
			return vCFData.getGroup();
		case VCFData.GENOME_INDEX:
			return vCFData.getGenome();
		case VCFData.RAW_INDEX:
			return vCFData.getRaw();
		case VCFData.FILE_INDEX:
			return vCFData.getFile();
		default:
			return new Object();
		}
	}


	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < getRowCount()) {
			VCFData vCFData = data.get(row);
			switch (col) {
			case VCFData.GROUP_INDEX:
				vCFData.setGroup(value.toString());
				break;
			case VCFData.GENOME_INDEX:
				vCFData.setGenome(value.toString());
				break;
			case VCFData.RAW_INDEX:
				vCFData.setRaw(value.toString());
				break;
			case VCFData.FILE_INDEX:
				vCFData.setFile(new File(value.toString()));
				break;
			default:
				System.out.println("Invalid column index");
			}
		} else {
			System.out.println("Invalid row index");
		}
		fireTableCellUpdated(row, col);
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case VCFData.GROUP_INDEX:
			return String.class;
		case VCFData.GENOME_INDEX:
			return String.class;
		case VCFData.RAW_INDEX:
			return String.class;
		case VCFData.FILE_INDEX:
			return File.class;
		default:
			return Object.class;
		}
	}


	/**
	 * Adds an empty row
	 */
	protected void addEmptyRow() {
		data.add(new VCFData());
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


	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		this.data = data;
		for (int row = 0; row <data.size(); row++) {
			fireTableCellUpdated(row, VCFData.GROUP_INDEX);
			fireTableCellUpdated(row, VCFData.GENOME_INDEX);
			fireTableCellUpdated(row, VCFData.RAW_INDEX);
			fireTableCellUpdated(row, VCFData.FILE_INDEX);
		}
	}


	/**
	 * Initializes the column names
	 */
	private void initializeColumnNames () {
		columnNames = new String[4];
		columnNames[0] = VCFData.GROUP_NAME;
		columnNames[1] = VCFData.GENOME_NAME;
		columnNames[2] = VCFData.FILE_NAME;
		columnNames[3] = VCFData.RAW_NAME;
	}

}
