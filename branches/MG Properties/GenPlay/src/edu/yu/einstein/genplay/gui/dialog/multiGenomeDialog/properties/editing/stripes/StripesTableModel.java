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

import java.util.List;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.ContentTableModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesTableModel extends ContentTableModel<StripesData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;
	

	/**
	 * Constructor of {@link StripesTableModel}
	 */
	protected StripesTableModel () {
		super(new String[]{"Genome", "Variation", "Track"});
	}


	@Override
	public Object getValueAt(int row, int col) {
		StripesData stripesData = data.get(row);
		switch (col) {
		case StripesData.GENOME_INDEX:
			return stripesData.getGenomeForDisplay();
		case StripesData.VARIANT_INDEX:
			return stripesData.getVariationTypeListForDisplay();
		case StripesData.TRACK_INDEX:
			return stripesData.getTrackListForDisplay();
		default:
			return new Object();
		}
	}


	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case StripesData.GENOME_INDEX:
			return String.class;
		case StripesData.VARIANT_INDEX:
			return JPanel.class;
		case StripesData.TRACK_INDEX:
			return String.class;
		default:
			return Object.class;
		}
	}


	/**
	 * @return the data
	 */
	protected List<StripesData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	protected void setData(List<StripesData> data) {
		this.data = data;
		for (int row = 0; row <data.size(); row++) {
			fireTableCellUpdated(row, StripesData.GENOME_INDEX);
			fireTableCellUpdated(row, StripesData.VARIANT_INDEX);
			fireTableCellUpdated(row, StripesData.TRACK_INDEX);
		}
	}

}
