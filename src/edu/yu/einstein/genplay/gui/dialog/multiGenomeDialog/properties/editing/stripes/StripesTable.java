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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.EditingTable;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesTable extends EditingTable<StripesData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3302281291755118456L;


	/**
	 * Constructor of {@link StripesTable}
	 */
	public StripesTable () {
		StripesTableModel model = new StripesTableModel();
		setModel(model);
		getColumnModel().getColumn(StripesData.VARIANT_INDEX).setCellRenderer(new StripesTableRenderer());
	}


	@Override
	protected void setData(List<StripesData> data) {
		List<StripesData> newData = new ArrayList<StripesData>();
		for (int i = 0; i < data.size(); i++) {
			StripesData rowData = new StripesData();
			rowData.setGenome(data.get(i).getGenome());
			rowData.setAlleleType(data.get(i).getAlleleType());
			rowData.setVariationTypeList(data.get(i).getVariationTypeList());
			rowData.setColorList(data.get(i).getColorList());
			rowData.setTrackList(data.get(i).getTrackList());
			newData.add(rowData);
		}
		((StripesTableModel)getModel()).setData(newData);
		updateColumnSize();
	}


	@Override
	protected void updateColumnSize () {
		int columnNumber = ((StripesTableModel)getModel()).getColumnCount();
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		String[] columnNames = ((StripesTableModel)getModel()).getColumnNames();

		// Scan all columns
		for (int i = 0; i < columnNumber; i++) {
			int currentWidth = fm.stringWidth(columnNames[i].toString()) + 10;

			for (StripesData stripesData: getData()) {
				int width;
				switch (i) {
				case StripesData.GENOME_INDEX:
					width = fm.stringWidth(stripesData.getGenomeForDisplay()) + 10;
					break;
				case StripesData.ALLELE_INDEX:
					width = fm.stringWidth(stripesData.getAlleleTypeForDisplay()) + 10;
					break;
				case StripesData.VARIANT_INDEX:
					width = fm.stringWidth(stripesData.getVariationTypeList().toString()) + 10;
					break;
				case StripesData.TRACK_INDEX:
					width = fm.stringWidth(stripesData.getTrackListForDisplay().toString()) + 10;
					break;
				default:
					width = 0;
					break;
				}

				if (width > currentWidth) {
					currentWidth = width;
				}
			}

			// Sets column width
			getColumnModel().getColumn(i).setPreferredWidth(currentWidth);
		}
	}

}
