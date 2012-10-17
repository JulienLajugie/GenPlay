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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.table.EditingTable;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantsTable extends EditingTable<VariantData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3302281291755118456L;


	/**
	 * Constructor of {@link VariantsTable}
	 */
	public VariantsTable () {
		VariantsTableModel model = new VariantsTableModel();
		setModel(model);
		getColumnModel().getColumn(VariantData.VARIANT_INDEX).setCellRenderer(new VariantsTableRenderer());
	}


	@Override
	protected void setData(List<VariantData> data) {
		List<VariantData> newData = new ArrayList<VariantData>();
		for (int i = 0; i < data.size(); i++) {
			VariantData rowData = new VariantData();
			rowData.setGenome(data.get(i).getGenome());
			rowData.setAlleleType(data.get(i).getAlleleType());
			rowData.setVariationTypeList(data.get(i).getVariationTypeList());
			rowData.setColorList(data.get(i).getColorList());
			rowData.setTrackList(data.get(i).getTrackList());
			newData.add(rowData);
		}
		((VariantsTableModel)getModel()).setData(newData);
		updateColumnSize();
	}


	@Override
	protected void updateColumnSize () {
		int columnNumber = ((VariantsTableModel)getModel()).getColumnCount();
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		String[] columnNames = ((VariantsTableModel)getModel()).getColumnNames();

		// Scan all columns
		for (int i = 0; i < columnNumber; i++) {
			int currentWidth = fm.stringWidth(columnNames[i].toString()) + 10;

			for (VariantData stripesData: getData()) {
				int width;
				switch (i) {
				case VariantData.GENOME_INDEX:
					width = fm.stringWidth(stripesData.getGenomeForDisplay()) + 10;
					break;
				case VariantData.ALLELE_INDEX:
					width = fm.stringWidth(stripesData.getAlleleTypeForDisplay()) + 10;
					break;
				case VariantData.VARIANT_INDEX:
					width = fm.stringWidth(stripesData.getVariationTypeList().toString()) + 10;
					break;
				case VariantData.TRACK_INDEX:
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
