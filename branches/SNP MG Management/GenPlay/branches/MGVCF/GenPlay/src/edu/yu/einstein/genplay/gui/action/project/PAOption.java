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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;



/**
 * Shows the option screen
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAOption extends AbstractAction {

	private static final long serialVersionUID = -7328322178569010171L; // generated ID
	private static final int 		MNEMONIC = KeyEvent.VK_O;	// mnemonic key
	private static final String 	ACTION_NAME = "Option";		// action name
	private final MainFrame 		mainFrame;					// main frame of the application

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAOption";
	
	
	/**
	 * Creates an instance of {@link PAOption}
	 * @param mainFrame {@link MainFrame} of the application
	 */
	public PAOption(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame; 
        putValue(NAME, ACTION_NAME);
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(MNEMONIC_KEY, MNEMONIC);
	}
	
	
	/**
	 * Shows the option screen
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		mainFrame.showOption();
	}
}
