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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;


/**
 * This class offers a custom combo box.
 * This new combo box is editable, user can add, edit and remove items of the list.
 * Each operations are available by clicking on icons displayed when an item is selected.
 * In order to do that, this class uses a custom renderer {@link CustomComboBoxRenderer}.
 * Developer cannot instantiate this class, he must uses:
 * - {@link CustomStringComboBox}
 * - {@link CustomFileComboBox}
 * 
 * @author Nicolas Fourel
 * @version 0.1
 * @param <T> 
 */
public abstract class CustomComboBox<T> extends JComboBox implements CustomComboBoxListener {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 6864223363370055711L;

	/** Text to select for adding a value */
	public static final String ADD_TEXT = "Add...";

	protected List<T> elements;		// List of elements
	private final CustomComboBoxRenderer renderer;


	/**
	 * Constructor of {@link CustomComboBox}
	 */
	public CustomComboBox () {
		super();
		elements = new ArrayList<T>();
		resetCombo();
		setSelectedItem(ADD_TEXT);
		renderer = new CustomComboBoxRenderer();
		renderer.addCustomComboBoxListener(this);
		setRenderer(renderer);
	}


	/**
	 * Adds an element to the combo box
	 * @param e the element
	 */
	public void addElement (T e) {
		if (e != null && !elements.contains(e)) {
			elements.add(e);
		}
	}


	/**
	 * Set a default selected element of the combo box
	 */
	public void setDefaultSelectedElement () {
		if (elements.size() > 0) {
			this.setSelectedItem(ADD_TEXT);
		}
	}


	/**
	 * Resets the combo list removing all items and adding the new ones.
	 * It also adds the ADD_TEXT value.
	 */
	public void resetCombo () {
		this.removeAllItems();
		for (T element: elements) {
			if (!element.toString().equals("")) {
				this.addItem(element);
			}
		}
		this.addItem(ADD_TEXT);
	}
	

	/**
	 * @return the renderer
	 */
	public CustomComboBoxRenderer getRenderer() {
		return renderer;
	}


	@Override
	public abstract void customComboBoxChanged(CustomComboBoxEvent evt);


	/**
	 * Adds a new element to the combo box.
	 * Shows a popup in order to define the new entry.
	 */
	protected abstract void addAction ();


	/**
	 * Removes an element from the combo box.
	 * Shows a popup in order to confirm the action.
	 * @param element element to remove
	 */
	protected abstract void removeAction (T element);


	/**
	 * Replaces an existing element by another one to the combo box.
	 * Shows a popup in order to define the new entry.
	 * @param element the element to replace
	 */
	protected abstract void replaceAction (T element);

}
