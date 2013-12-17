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
package edu.yu.einstein.genplay.gui.action;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;


/**
 * Abstract class. Represents an action on a TrackList
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackListAction extends AbstractAction {

	private static final long serialVersionUID = 1383058897700926018L; 		// generated ID


	/**
	 * Constructor
	 */
	public TrackListAction() {
		super();
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
}
