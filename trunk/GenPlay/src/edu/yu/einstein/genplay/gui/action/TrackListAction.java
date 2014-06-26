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
package edu.yu.einstein.genplay.gui.action;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;


/**
 * Abstract class. Represents an action on a TrackList
 * @author Julien Lajugie
 */
public abstract class TrackListAction extends AbstractAction {

	private static final long serialVersionUID = 1383058897700926018L; 		// generated ID

	/** Action key for the URL of the help page of the action */
	protected static final String HELP_URL_KEY = "HELP_URL_KEY";

	protected static final String HELP_TOOLTIP_SUFFIX = " (alt + click to show help)";


	/**
	 * Constructor
	 */
	public TrackListAction() {
		super();
	}


	/**
	 * Shows the help page of the action if alt + click was pressed. Process the action otherwise
	 * {@inheritDoc}
	 */
	@Override
	public final void actionPerformed(ActionEvent e) {
		if ((e != null) && ((e.getModifiers() & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)) {
			showHelp(getRootPane());
		} else {
			trackListActionPerformed(e);
		}
	}


	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackListPanel().getRootPane();
	}


	/**
	 * Shortcut for MainFrame.getInstance().getTrackList()
	 * @return the track list of the project
	 */
	protected TrackListPanel getTrackListPanel() {
		return MainFrame.getInstance().getTrackListPanel();
	}


	/**
	 * Shows the help page associated with the action
	 * @param parentComponent parent component of the window that will display the help
	 */
	protected void showHelp(Component parentComponent) {
		String helpURL = (String) getValue(HELP_URL_KEY);
		if (helpURL != null) {
			try {
				if (Desktop.isDesktopSupported()) {
					URI uri = new URI(helpURL);
					Desktop.getDesktop().browse(uri);
				}
			} catch (Exception e) {
				ExceptionManager.getInstance().notifyUser(parentComponent, e, "Cannot open Internet Browser.");
			}
		}
	}


	/**
	 * Invoked when an action occurs.
	 * @param e
	 */
	public abstract void trackListActionPerformed(ActionEvent e);
}
