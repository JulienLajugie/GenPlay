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
package edu.yu.einstein.genplay.gui.customComponent.tableComponents;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * JTable Renderer that can be used to show a color element
 * @author Julien Lajugie
 */
public class ColorRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -230094018870002634L; // generated serial ID
	private boolean isBordered = true;			// true if the borders are visible

	/**
	 * Creates an instance of {@link ColorRenderer}
	 * @param text text that appears on the component
	 * @param isBordered true if the borders are visible
	 */
	public ColorRenderer(String text, boolean isBordered) {
		this.isBordered = isBordered;
		if (text != null) {
			setHorizontalAlignment(SwingConstants.CENTER);
			setText(text);
		}
		setOpaque(true);
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,int row, int column) {
		Color newColor = (Color)color;
		if (newColor == null) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
				setForeground(table.getBackground());
			}
			setBorder(null);
		} else {
			setBackground(newColor);
			setForeground(new Color(newColor.getRGB() ^ 0xffffff));
			if (isBordered) {
				Border border;
				if (isSelected) {
					border = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
				} else {
					border = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
				}
				setBorder(border);
			}
		}
		return this;
	}
}
