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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Table editor that displays a radio button
 * @author Julien Lajugie
 */
public class BooleanRadioButtonEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 2828724045541167111L; // generated serial ID

	private final JRadioButton radioButton;	// radio button


	/**
	 * Default constructor. Create an instance of {@link BooleanRadioButtonEditor}
	 */
	public BooleanRadioButtonEditor() {
		radioButton = new JRadioButton();
		radioButton.setHorizontalAlignment(JRadioButton.CENTER);
		radioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// prevent deselection to mimic button group
				if (!radioButton.isSelected()) {
					cancelCellEditing();
				}
				stopCellEditing();
			}
		});
	}


	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		radioButton.setSelected((Boolean)value);
		return radioButton;
	}


	@Override
	public Object getCellEditorValue() {
		return Boolean.valueOf(radioButton.isSelected());
	}
}