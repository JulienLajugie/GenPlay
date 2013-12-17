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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;

/**
 * JTable Editor that can be used to select a color
 * @author Julien Lajugie
 */
public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	private static final long serialVersionUID = 8019042313436179591L; // generated serial ID
	private Color 			currentColor; 	// currently selected color
	private final JButton 	jbColor; 		// button for the editor


	/**
	 * Creates an instance of color Editor
	 * @param text text that appears on the component
	 */
	public ColorEditor(String text) {
		jbColor = new JButton();
		if (text != null) {
			jbColor.setText(text);
		}
		jbColor.addActionListener(this);
		jbColor.setBorderPainted(false);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Color colorTmp = GenPlayColorChooser.showDialog(jbColor, "Select a Color", currentColor);
		if (currentColor != null) {
			currentColor = colorTmp;
			jbColor.setBackground(currentColor);
		}
		fireEditingStopped();
	}


	@Override
	public Object getCellEditorValue() {
		return currentColor;
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		currentColor = (Color) value;
		jbColor.setBackground(currentColor);
		jbColor.setForeground(new Color(currentColor.getRGB() ^ 0xffffff));
		return jbColor;
	}
}