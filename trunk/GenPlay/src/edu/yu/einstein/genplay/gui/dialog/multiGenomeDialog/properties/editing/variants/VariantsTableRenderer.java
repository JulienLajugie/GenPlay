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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantsTableRenderer extends DefaultTableCellRenderer {

	/** Generated serial version ID */
	private static final long serialVersionUID = 885064395349589079L;


	/**
	 * Fills the color of the label either selected or not
	 * @param table			the table
	 * @param label			the label
	 * @param isSelected	true if the label is selected
	 */
	private void fillColor (JTable table, JPanel panel, boolean isSelected, boolean hasFocus) {
		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
			panel.setForeground(table.getSelectionForeground());
		} else {
			panel.setBackground(table.getBackground());
			panel.setForeground(table.getForeground());
		}

		if (hasFocus) {
			Color color = new Color(99, 130, 191);
			panel.setBorder(BorderFactory.createLineBorder(color, 1));
		} else {
			panel.setBorder(BorderFactory.createEmptyBorder());
		}
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof JPanel) {
			JPanel panel = (JPanel) value;
			panel.setOpaque(true);
			fillColor(table, panel, isSelected, hasFocus);
			return panel;
		} else {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

}
