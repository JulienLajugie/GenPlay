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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.table.EditingTableModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantsTableModel extends EditingTableModel<VariantData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3478197435828366331L;


	/**
	 * Constructor of {@link VariantsTableModel}
	 */
	protected VariantsTableModel () {
		super(new String[]{"Genome", "Allele", "Variation", "Layer"});
	}


	@Override
	public Object getValueAt(int row, int col) {
		if (col == buttonColumnIndex) {
			return buttons.get(row);
		}
		VariantData stripesData = data.get(row);
		switch (col) {
		case VariantData.GENOME_INDEX:
			return stripesData.getGenomeForDisplay();
		case VariantData.ALLELE_INDEX:
			return stripesData.getAlleleTypeForDisplay();
		case VariantData.VARIANT_INDEX:
			return stripesData.getVariationTypeListForDisplay();
		default:
			return new Object();
		}
	}


	@Override
	public Class<?> getColumnClass(int column) {
		if (column == buttonColumnIndex) {
			return JButton.class;
		}
		switch (column) {
		case VariantData.GENOME_INDEX:
			return String.class;
		case VariantData.ALLELE_INDEX:
			return String.class;
		case VariantData.VARIANT_INDEX:
			return JPanel.class;
		default:
			return Object.class;
		}
	}


	/**
	 * @return the data
	 */
	@Override
	protected List<VariantData> getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	@Override
	protected void setData(List<VariantData> data) {
		this.data = data;
		buttons = new ArrayList<JButton>();
		for (int row = 0; row <data.size(); row++) {
			buttons.add(getNewButton());
			fireTableCellUpdated(row, VariantData.GENOME_INDEX);
			fireTableCellUpdated(row, VariantData.ALLELE_INDEX);
			fireTableCellUpdated(row, VariantData.VARIANT_INDEX);
			fireTableCellUpdated(row, buttonColumnIndex);
		}
	}

}
