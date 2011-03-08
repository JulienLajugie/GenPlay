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
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.awt.Color;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOTwoTracks;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Realizes operation on two tracks
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class SCWLATwoTracks extends TrackListActionOperationWorker<ChromosomeListOfLists<?>> {

	private static final long 				serialVersionUID = 4027173438789911860L; 				// generated ID
	private static final String 			ACTION_NAME = "Two Tracks Operation";			// action name
	private static final String 			DESCRIPTION = "Run operation on two trakcs";	// tooltip
	private Track<?> 						selectedTrack;									// selected track
	private Track<?> 						otherTrack = null;								// other track
	private Track<?>						resultTrack = null;								// result track
	private ScoreCalculationTwoTrackMethod 	scm;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLATwoTracks";


	/**
	 * Creates an instance of {@link SCWLATwoTracks}
	 */
	public SCWLATwoTracks() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ChromosomeListOfLists<?>> initializeOperation() {
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Select the second track of the operation:", getTrackList().getCurveTracks());
			if (otherTrack != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					this.scm = Utils.chooseScoreCalculationTwoTrackMethod(getRootPane());
					if (scm != null) {
						operation = new SCWLOTwoTracks(	(ChromosomeListOfLists<?>)selectedTrack.getData(),
								(ChromosomeListOfLists<?>)otherTrack.getData(),
								this.scm);
						return operation;
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ChromosomeListOfLists<?> actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			SCWListTrack newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), index + 1, (ScoredChromosomeWindowList)actionResult);
			// add info to the history
			newTrack.getHistory().add("Operation on two tracks", Color.GRAY);
			newTrack.getHistory().add("Operation: " + this.scm.toString(), Color.GRAY);
			newTrack.getHistory().add("First track: " + this.selectedTrack.getName(), Color.GRAY);
			newTrack.getHistory().add("Second track: " + this.otherTrack.getName(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " & " + otherTrack.getName(), null);
		}
	}
}
