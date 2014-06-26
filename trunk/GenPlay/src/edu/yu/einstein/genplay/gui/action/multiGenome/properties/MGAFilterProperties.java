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
package edu.yu.einstein.genplay.gui.action.multiGenome.properties;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.PropertiesDialog;


/**
 * Displays the filter section of the multi genome project properties dialog
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MGAFilterProperties extends TrackListAction{

	private static final long serialVersionUID = 5992588139324388042L;				// generated ID
	private static final 	String ACTION_NAME = "Filters";							// action name
	private static final 	String DESCRIPTION = "Apply Filters"; 					// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "MGAFilterProperties";


	/**
	 * Creates an instance of {@link MGAFilterProperties}
	 */
	public MGAFilterProperties() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Shows the Multi Genome Project Properties dialog
	 */
	@Override
	public void trackListActionPerformed(ActionEvent arg0) {
		MGAProperties properties = new MGAProperties();
		properties.setItemDialog(PropertiesDialog.FILTERS);
		properties.actionPerformed(arg0);
	}
}
