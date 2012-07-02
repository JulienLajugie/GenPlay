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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.ExceptionManager;



/**
 * Shows the help dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAHelp extends AbstractAction {

	private static final long serialVersionUID = -8315224036423121225L; // generated ID
	private static final String 	HELP_URL = 
		"http://www.genplay.net/wiki/index.php/Documentation";	// URL of the help file
	private static final int 		MNEMONIC = KeyEvent.VK_H; 	// mnemonic key
	private static final String 	ACTION_NAME = "Help";		// action name
	private final 		 Component 	parent;						// parent component


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAHelp"; 


	/**
	 * Creates an instance of {@link PAHelp}
	 * @param parent parent component
	 */
	public PAHelp(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Shows the help dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			if (Desktop.isDesktopSupported()) {
				URI uri = new URI(HELP_URL);
				Desktop.getDesktop().browse(uri);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(parent, e, "The help file can't be loaded");
		}
	}
}
