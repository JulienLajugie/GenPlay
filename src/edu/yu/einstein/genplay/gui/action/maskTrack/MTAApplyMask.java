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


import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoTrackMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOTwoTracks;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.TrackColor;


/**
 * Applies a mask to a fixed/variable windows track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class MTAApplyMask extends TrackListActionOperationWorker<ChromosomeListOfLists<?>> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
	private static final String 			ACTION_NAME = "Apply Mask";			// action name
	private static final String 			DESCRIPTION = "Apply mask on track";	// tooltip
	private Track<?> 						selectedTrack;									// selected track								// other track
	private Track<?>						resultTrack = null;								// result track
	private ScoreCalculationTwoTrackMethod 	scm;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAApplyMask";


	/**
	 * Creates an instance of {@link MTAApplyMask}
	 */
	public MTAApplyMask() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ChromosomeListOfLists<?>> initializeOperation() {
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			ChromosomeListOfLists<?> data = (ChromosomeListOfLists<?>)selectedTrack.getData();
			ChromosomeListOfLists<?> mask = selectedTrack.getMask();
			resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
			if (resultTrack != null) {
				this.scm = ScoreCalculationTwoTrackMethod.MULTIPLICATION;
				operation = new SCWLOTwoTracks(data, mask, this.scm);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ChromosomeListOfLists<?> actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			CurveTrack<?> newTrack = new SCWListTrack(index + 1, (ScoredChromosomeWindowList)actionResult);
			newTrack.setTrackColor(TrackColor.getTrackColor());
			// add info to the history
			newTrack.getHistory().add("Apply mask", Colors.GREY);
			newTrack.getHistory().add("Track: " + this.selectedTrack.getName(), Colors.GREY);
			getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), selectedTrack.getName() + " masked", null, null, null);
		}
	}

}
