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
package edu.yu.einstein.genplay.gui.customComponent.tableComponents;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * JTable Editor that can be used to select an element in a combox box
 * @author Julien Lajugie
 */
public class ComboBoxEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -8754967887212589985L; // generated serial ID
	private final JComboBox comboBox;	// components of this editor


	/**
	 * Creates an instance of {@link ComboBoxEditor}
	 * @param comboValues values available in the combox
	 */
	public ComboBoxEditor(Object[] comboValues) {
		comboBox = new JComboBox(comboValues);
	}


	@Override
	public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		comboBox.setSelectedItem(value);
		return comboBox;
	}
}
