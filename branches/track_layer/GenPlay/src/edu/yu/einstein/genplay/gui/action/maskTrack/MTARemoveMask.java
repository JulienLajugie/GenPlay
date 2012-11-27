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
package edu.yu.einstein.genplay.gui.action.maskTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.old.track.Track;



/**
 * Removes the mask on the selected track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class MTARemoveMask extends TrackListAction {

	private static final long serialVersionUID = -5710099956650330270L; // generated ID
	private static final String ACTION_NAME = "Remove Mask"; // action name
	private static final String DESCRIPTION = "Remove mask on the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATARemoveMask";


	/**
	 * Creates an instance of {@link MTARemoveMask}
	 */
	public MTARemoveMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Removes the stripes on the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			selectedTrack.setMask(null);
		}
	}
}
