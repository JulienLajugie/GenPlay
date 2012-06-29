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

import java.util.EventObject;


/**
 * The {@link CustomComboBoxEvent} event emitted by a {@link CustomComboBoxEventsGenerator} object.
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class CustomComboBoxEvent extends EventObject {
	
	private static final long serialVersionUID = -5909384700520572038L;	// generated ID
	
	/** Nothing must happen */
	public static final int NO_ACTION = -1;
	/** The element will be selected */
	public static final int SELECT_ACTION = 0;
	/** The element will be selected */
	public static final int ADD_ACTION = 1;
	/** The element will be replaced */
	public static final int REPLACE_ACTION = 2;
	/** The element will be removed */
	public static final int REMOVE_ACTION = 3;
	private final CustomComboBoxEventsGenerator source;
	private final Object element;
	private final int action;
	
	
	/**
	 * Creates an instance of {@link CustomComboBoxEvent}
	 * @param source 	the source of the event
	 * @param element 	the element
	 * @param action 	the action to perform
	 */
	public CustomComboBoxEvent(CustomComboBoxEventsGenerator source, Object element, int action) {
		super(source);
		this.source = source;
		this.element = element;
		this.action = action;
	}


	/**
	 * @return the source
	 */
	public final CustomComboBoxEventsGenerator getSource() {
		return source;
	}


	/**
	 * @return the element
	 */
	public final Object getElement() {
		return element;
	}


	/**
	 * @return the action
	 */
	public final int getAction() {
		return action;
	}
	
}