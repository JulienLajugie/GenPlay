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
package edu.yu.einstein.genplay.gui.dialog.SCWListStatsDialog;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Renderer of the table showing the stats of a {@link SCWList}
 * @author Julien Lajugie
 */
class SCWListStatsRenderer extends DefaultTableCellRenderer {


	/** Generated serial ID */
	private static final long serialVersionUID = 3055413631611320886L;


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		// style of the row (background of 1 row out of 2 is light grey)
		if ((!isSelected) && ((row % 2) == 1)) {
			component.setBackground(Colors.LIGHT_GREY);
		} else {
			component.setBackground(Colors.WHITE);
		}
		// set the alignment of the elements. header column is left aligned, number columns are right aligned
		if (column == 0) {
			((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			((JLabel) component).setHorizontalAlignment(SwingConstants.RIGHT);
		}
		// style for the genome wide row
		if (row == 0) {
			component.setFont(getFont().deriveFont(Font.BOLD));
		}
		return component;
	}
}
