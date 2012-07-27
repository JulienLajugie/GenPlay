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
package edu.yu.einstein.genplay.gui.action.allTrack.mask;


import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.MCWLOInvertMask;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * Reverts a mask reverting mask windows by white spaces and white spaces by mask windows.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ATAInvertMask extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Invert Mask";					// action name
	private static final String 			DESCRIPTION = "Invert the mask of the track";	// tooltip
	private Track<?> 						selectedTrack;									// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAInvertMask";


	/**
	 * Creates an instance of {@link ATAInvertMask}
	 */
	public ATAInvertMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			ScoredChromosomeWindowList mask = selectedTrack.getMask();
			operation = new MCWLOInvertMask(mask);
			return operation;
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			//int index = resultTrack.getTrackNumber() - 1;

			// add info to the history
			//selectedTrack.getHistory().add("Invert mask", Colors.GREY);
			//selectedTrack.getHistory().add("Track: " + this.selectedTrack.getName(), Colors.GREY);

			selectedTrack.setStripes(actionResult);
		}
	}

}
