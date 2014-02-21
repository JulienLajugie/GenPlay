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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTable;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TableButtonListener extends MouseAdapter {

	private final JTable table;

	/**
	 * Constructor of {@link TableButtonListener}
	 * @param table the table to listen
	 */
	public TableButtonListener(JTable table) {
		this.table = table;
	}


	@Override public void mouseClicked(MouseEvent e) {
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());
		int row    = e.getY()/table.getRowHeight();

		if ((row < table.getRowCount()) && (row >= 0) && (column < table.getColumnCount()) && (column >= 0)) {
			Object value = table.getValueAt(row, column);
			if (value instanceof JButton) {
				((JButton)value).doClick();
			}
		}
	}

}
