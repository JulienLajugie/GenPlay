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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.Track;



/**
 * Sets the number of vertical lines showed on a track
 * @author Julien Lajugie
 * @version 0.1
 */
public class TASetVerticalLineCount extends TrackListAction {

	private static final long serialVersionUID = -922309611412994050L;	// generated ID
	private static final String ACTION_NAME = "Set Vertical Line Count"; // action name
	private static final String DESCRIPTION = "Set the number of vertical lines showed on the track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "TASetVerticalLineCount";


	/**
	 * Creates an instance of {@link TASetVerticalLineCount}
	 */
	public TASetVerticalLineCount() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			int currentLineCount = selectedTrack.getBackgroundLayer().getData().getVerticalLineCount();
			Number lineCountNumber = NumberOptionPane.getValue(getRootPane(), "Vertical Line Count", "Set the number of vertical lines to display", new DecimalFormat("###"), 0, 100, currentLineCount);
			if ((lineCountNumber != null) && (lineCountNumber.intValue() != currentLineCount)) {
				selectedTrack.getBackgroundLayer().getData().setVerticalLineCount(lineCountNumber.intValue());
			}
		}
	}
}
