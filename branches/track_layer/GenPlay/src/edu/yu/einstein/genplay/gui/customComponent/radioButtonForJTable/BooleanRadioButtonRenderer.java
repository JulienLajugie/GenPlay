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
package edu.yu.einstein.genplay.gui.customComponent.radioButtonForJTable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Table renderer that displays a radio button
 * @author Julien Lajugie
 */
public class BooleanRadioButtonRenderer implements TableCellRenderer {

	private final JRadioButton 	radioButton;	// radio button rendered
	private final Border 		emptyBorder;	// border of the button


	/**
	 * Default constructor. Create an instance of {@link BooleanRadioButtonRenderer}
	 */
	public BooleanRadioButtonRenderer() {
		radioButton = new JRadioButton();
		radioButton.setHorizontalAlignment(JRadioButton.CENTER);
		radioButton.setBorderPainted(true);
		radioButton.setOpaque(true);
		emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		if (isSelected) {
			radioButton.setBackground(table.getSelectionBackground());
			radioButton.setForeground(table.getSelectionForeground());
		} else {
			radioButton.setBackground(table.getBackground());
			radioButton.setForeground(table.getForeground());
		}
		if (hasFocus) {
			radioButton.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		} else {
			radioButton.setBorder(emptyBorder);
		}
		radioButton.setSelected((Boolean)value);
		return radioButton;
	}
}
