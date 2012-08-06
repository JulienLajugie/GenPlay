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
package edu.yu.einstein.genplay.gui.customComponent.customComboBox;

import java.util.Collections;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;

/**
 * This class extends the {@link CustomComboBox} class.
 * It is specific for String content.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CustomStringComboBox extends CustomComboBox<String> {

	/**
	 * Generated default serial version
	 */
	private static final long serialVersionUID = -3013901769963683217L;


	/**
	 * Resets the combo list removing all items and adding the new ones.
	 * It also adds the ADD_TEXT value.
	 */
	@Override
	public void resetCombo () {
		this.removeAllItems();
		Collections.sort(elements);
		for (String element: elements) {
			if (!element.toString().equals("")) {
				this.addItem(element);
			}
		}
		this.addItem(ADD_TEXT);
	}


	@Override
	public void customComboBoxHasChanged (CustomComboBoxEvent evt) {
		if (evt.getAction() == CustomComboBoxEvent.SELECT_ACTION) {
			setSelectedItem(evt.getElement());
		} else if (evt.getAction() == CustomComboBoxEvent.ADD_ACTION) {
			addAction();
		} else if (evt.getAction() == CustomComboBoxEvent.REMOVE_ACTION) {
			removeAction(evt.getElement().toString());
		} else if (evt.getAction() == CustomComboBoxEvent.REPLACE_ACTION) {
			replaceAction(evt.getElement().toString());
		}
	}


	/**
	 * Adds a new element to the combo box.
	 * Shows a popup in order to define the new entry.
	 */
	@Override
	protected void addAction () {
		String element = JOptionPane.showInputDialog(this,
				"Please type a new entry.",
				"Entry insertion",
				JOptionPane.PLAIN_MESSAGE);
		if ((element != null) && !element.equals("")) {
			addElement(element);
			resetCombo();
			setSelectedItem(element);
		} else {
			setSelectedItem("");
		}
	}


	/**
	 * Removes an element from the combo box.
	 * Shows a popup in order to confirm the action.
	 * @param element element to remove
	 */
	@Override
	protected void removeAction (String element) {
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this,
				"Do you really want to erase '" + element + "' ?",
				"Entry deletion",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				options,
				options[0]);

		if (n == JOptionPane.YES_OPTION) {
			elements.remove(element);
			resetCombo();
			setSelectedIndex(0);
		}
	}


	/**
	 * Replaces an existing element by another one to the combo box.
	 * Shows a popup in order to define the new entry.
	 * @param element the element to replace
	 */
	@Override
	protected void replaceAction (String element) {
		String newElement = (String) JOptionPane.showInputDialog(
				this,
				"The new entry will replace '" + element + "'.",
				"Entry modification",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				element);

		if ((newElement != null) && !element.equals("")) {
			elements.remove(element);
			addElement( newElement);
			resetCombo();
			setSelectedItem(element);
		}
	}

}
