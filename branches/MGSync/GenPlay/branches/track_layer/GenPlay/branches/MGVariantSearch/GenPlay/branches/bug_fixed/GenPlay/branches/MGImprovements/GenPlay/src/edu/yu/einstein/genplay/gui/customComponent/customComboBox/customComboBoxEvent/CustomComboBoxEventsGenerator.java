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
package edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent;


/**
 * Should be Implemented by objects generating {@link CustomComboBoxEvent}
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public interface CustomComboBoxEventsGenerator {
	
	/**
	 * Adds a {@link CustomComboBoxListener} to the listener list
	 * @param customComboBoxListener {@link CustomComboBoxListener} to add
	 */
	public void addCustomComboBoxListener(CustomComboBoxListener customComboBoxListener);
	

	/**
	 * @return an array containing all the {@link CustomComboBoxListener} of the current instance
	 */
	public CustomComboBoxListener[] getCustomComboBoxListeners();
	
	
	/**
	 * Removes a {@link CustomComboBoxListener} from the listener list
	 * @param customComboBoxListener {@link CustomComboBoxListener} to remove
	 */
	public void removeCustomComboBoxListener(CustomComboBoxListener customComboBoxListener);
}
