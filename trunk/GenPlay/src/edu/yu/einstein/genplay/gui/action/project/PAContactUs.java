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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;

import edu.yu.einstein.genplay.exception.ExceptionManager;


/**
 * Open a new mail from user mailbox to contact us
 * @author Julien Lajugie
 */
public final class PAContactUs extends AbstractAction {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	MAILTO_GENPLAY_URI =
			"mailto:genplay.einstein.yu.edu";							// URI to send mail to genplay address
	private static final String 	DESCRIPTION = "Open a blank email to contact us"; 		// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_A; 			// mnemonic key
	private static final String 	ACTION_NAME = "Contact Us";			// action name
	private final 		Component 	parent;								// parent component


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PAContactUs.class.getName();


	/**
	 * Creates an instance of {@link PAContactUs}
	 * @param parent parent component
	 */
	public PAContactUs(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Shows the about dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			if (Desktop.isDesktopSupported()) {
				URI uri = new URI(MAILTO_GENPLAY_URI);
				Desktop.getDesktop().mail(uri);
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().notifyUser(parent, e, "Cannot open mailbox");
		}
	}
}
