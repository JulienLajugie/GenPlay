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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;


/**
 * Right panel of an {@link OptionDialog} Defines the common attributes of the
 * different panels of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
abstract class OptionPanel extends JPanel {

	private static final long serialVersionUID = 4821469631755757767L; 	// Generated ID
	final ConfigurationManager configurationManager; 			// ConfigurationManager


	/**
	 * Constructor. Creates an instance of {@link OptionPanel}
	 * @param name name of the category of configuration
	 */
	OptionPanel(String name) {
		super();
		setName(name);
		configurationManager = ConfigurationManager.getInstance();
	}


	/**
	 * Override of toString use for the JTree in order to set the name of a
	 * category.
	 */
	@Override
	public String toString() {
		return getName();
	}
}
