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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters;

import java.util.List;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.ContentPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class FiltersContentPanel extends ContentPanel<FiltersData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 159376731917929812L;

	private FiltersTable table; // Table of data
	

	/**
	 * Constructor of {@link FiltersContentPanel}
	 */
	protected FiltersContentPanel () {
		super();

		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Filters settings"), gbc);

		// Table title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Table"), gbc);

		// Table
		table = new FiltersTable();
		String[] columnNames = ((FiltersTableModel)table.getModel()).getColumnNames();
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		gbc.weighty = 1;
		add(getTablePanel(table, columnNames), gbc);
	}

	
	/**
	 * Set the content panel with specific values
	 * @param list list of data
	 */
	public void setSettings (List<FiltersData> list) {
		table.setData(list);
	}
	
	
	/**
	 * @return the filters list
	 */
	public List<FiltersData> getFiltersData () {
		return table.getData();
	}
}
