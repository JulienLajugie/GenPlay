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

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesTable extends JTable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3302281291755118456L;


	/**
	 * Constructor of {@link StripesTable}
	 */
	protected StripesTable () {
		StripesTableModel model = new StripesTableModel();
		setModel(model);
		getColumnModel().getColumn(StripeData.VARIANT_INDEX).setCellRenderer(new StripesTableRenderer());
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<StripeData> data) {
		List<StripeData> newData = new ArrayList<StripeData>();
		for (int i = 0; i < data.size(); i++) {
			StripeData rowData = new StripeData();
			rowData.setGenome(data.get(i).getGenome());
			rowData.setVariantList(data.get(i).getVariantList());
			rowData.setColorList(data.get(i).getColorList());
			rowData.setTrackList(data.get(i).getTrackList());
			newData.add(rowData);
		}
		((StripesTableModel)getModel()).setData(newData);
	}


	/**
	 * Add an empty row
	 */
	protected void addEmptyRow () {
		((StripesTableModel)getModel()).addEmptyRow();
	}


	/**
	 * Add a row in the table
	 * @param data data to add
	 */
	protected void addRow (StripeData data) {
		((StripesTableModel)getModel()).addRow(data);
	}


	/**
	 * Delete a row
	 * @param row row index to delete
	 */
	protected void removeRows (int[] rows) {
		rows = edu.yu.einstein.genplay.util.Utils.reverse(rows);
		for (int row: rows) {
			((StripesTableModel)getModel()).deleteRow(row);
		}
	}
	
	
	/**
	 * Move the selected rows to the top of the table
	 */
	protected void moveRowsUp () {
		((StripesTableModel)getModel()).move(getSelectedRows(), true);
	}
	
	
	/**
	 * Move the selected rows to the bottom of the table
	 */
	protected void moveRowsDown () {
		((StripesTableModel)getModel()).move(getSelectedRows(), false);
	}


	/**
	 * Set the size of the column according to the parameter
	 * @param widths array of size
	 */
	protected void updateColumnSize (int[] widths) {
		// Sets column width
		for (int i = 0; i < widths.length; i++) {
			getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}


	/**
	 * This method scans all cells of each column to find the maximum width for each of them.
	 * Then, it sets the column size according to the width.
	 */
	protected void updateColumnSize () {
		int columnNumber = ((StripesTableModel)getModel()).getColumnCount();
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		String[] columnNames = ((StripesTableModel)getModel()).getColumnNames();

		// Sets column width
		for (int i = 0; i < columnNumber; i++) {
			int currentWidth = fm.stringWidth(columnNames[i].toString()) + 10;

			for (StripeData stripeData: getData()) {
				int width;
				switch (i) {
				case StripeData.GENOME_INDEX:
					width = fm.stringWidth(stripeData.getGenomeForDisplay()) + 10;
					break;
				case StripeData.VARIANT_INDEX:
					width = fm.stringWidth(stripeData.getVariantList().toString()) + 10;
					break;
				case StripeData.TRACK_INDEX:
					width = fm.stringWidth(stripeData.getTrackListForDisplay().toString()) + 10;
					break;
				default:
					width = 0;
					break;
				}

				if (width > currentWidth) {
					currentWidth = width;
				}
			}

			getColumnModel().getColumn(i).setPreferredWidth(currentWidth);
		}
	}


	/**
	 * @return an array containing size of each column
	 */
	protected int[] getColumnSize () {
		int columnNumber = ((StripesTableModel)getModel()).getColumnCount();
		int[] widths = new int[columnNumber];

		for (int i = 0; i < columnNumber; i++) {
			widths[i] = getColumnModel().getColumn(i).getPreferredWidth();
		}
		return widths;
	}


	/**
	 * @return the data
	 */
	protected List<StripeData> getData() {
		return ((StripesTableModel)getModel()).getData();
	}

}
