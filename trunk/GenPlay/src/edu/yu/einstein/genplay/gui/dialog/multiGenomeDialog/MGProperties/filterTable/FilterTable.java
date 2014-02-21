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

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import edu.yu.einstein.genplay.gui.MGDisplaySettings.FiltersData;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FilterTable extends JTable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3342831482530035559L;



	FilterTableModel model;


	/**
	 * Constructor of {@link FiltersTable}
	 */
	public FilterTable () {
		FilterTableModel model = new FilterTableModel();
		setModel(model);
	}


	/**
	 * Add a row in the table
	 * @param data data to add
	 */
	public void addRow (FiltersData data) {
		model.addRow(data);
	}


	/**
	 * @return an array containing size of each column
	 */
	public int[] getColumnSize () {
		int columnNumber = getModel().getColumnCount();
		int[] widths = new int[columnNumber];
		for (int i = 0; i < columnNumber; i++) {
			widths[i] = getColumnModel().getColumn(i).getPreferredWidth();
		}
		return widths;
	}


	/**
	 * @return the data
	 */
	public List<FiltersData> getData() {
		return model.getData();
	}


	@Override
	public FilterTableModel getModel () {
		return model;
	}


	/**
	 * Move the selected rows to the bottom of the table
	 */
	public void moveRowsDown () {
		int[] movedRows = model.move(getSelectedRows(), false);
		setSelectedRows(movedRows);
	}


	/**
	 * Move the selected rows to the top of the table
	 */
	public void moveRowsUp () {
		int[] movedRows = model.move(getSelectedRows(), true);
		setSelectedRows(movedRows);
	}


	/**
	 * Delete rows
	 * @param rows row indexes to delete
	 */
	public void removeRows (int[] rows) {
		rows = edu.yu.einstein.genplay.util.Utils.reverse(rows);
		for (int row: rows) {
			model.deleteRow(row);
		}
	}



	public void setData(List<FiltersData> data) {
		List<FiltersData> newData = new ArrayList<FiltersData>();
		for (int i = 0; i < data.size(); i++) {
			FiltersData rowData = new FiltersData();
			rowData.setMGFilter(data.get(i).getMGFilter());
			rowData.setLayers(data.get(i).getLayers());
			newData.add(rowData);
		}
		getModel().setData(newData);
	}


	/**
	 * @param model the model to set
	 */
	public void setModel (FilterTableModel model) {
		this.model = model;
		super.setModel(model);
		getColumnModel().getColumn(FilterTableModel.EDIT_BUTTON_INDEX).setCellRenderer(new TableButtonRenderer());
		addMouseListener(new TableButtonListener(this));
	}


	/**
	 * Select the given rows
	 * @param rows rows to select
	 */
	private void setSelectedRows (int[] rows) {
		boolean first = true;
		for (int row: rows) {
			if (row < model.getRowCount()) {
				if (first) {
					first = false;
					setRowSelectionInterval(row, row);
				} else {
					addRowSelectionInterval(row, row);
				}
			}
		}
	}


	/**
	 * This method scans all cells of each column to find the maximum width for each of them.
	 * Then, it sets the column size according to the width.
	 */
	public void updateColumnSize () {
		int columnNumber = getModel().getColumnCount();
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		String[] columnNames = getModel().getColumnNames();

		// Scan columns
		for (int i = 0; i < columnNumber; i++) {
			int currentWidth = fm.stringWidth(columnNames[i].toString()) + 10;

			for (FiltersData filtersData: getData()) {
				int width;
				switch (i) {
				case FilterTableModel.VCF_FILE_INDEX:
					width = fm.stringWidth(filtersData.getReaderForDisplay()) + 10;
					break;
				case FilterTableModel.ID_INDEX:
					width = fm.stringWidth(filtersData.getIDForDisplay()) + 10;
					break;
				case FilterTableModel.FILTER_INDEX:
					width = fm.stringWidth(filtersData.getFilterForDisplay()) + 10;
					break;
				case FilterTableModel.LAYER_INDEX:
					width = fm.stringWidth(filtersData.getLayersForDisplay().toString()) + 10;
					break;
				default:
					width = 0;
					break;
				}

				// Sets column width
				if (width > currentWidth) {
					currentWidth = width;
				}
			}
			getColumnModel().getColumn(i).setPreferredWidth(currentWidth);
		}
	}
}
