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
package edu.yu.einstein.genplay.gui.action.binListTrack;


import java.awt.Color;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationTwoTrackMethod;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOTwoTracks;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOTwoTracks;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.TrackChooser;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Adds a constant to the scores of the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLATwoTracks extends TrackListActionOperationWorker<ChromosomeListOfLists<?>> {

	private static final long 				serialVersionUID = 4027173438789911860L; 		// generated ID
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
	 * Creates an instance of {@link BLATwoTracks}
	 */
	public BLATwoTracks() {
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
						if (isSCWList()) {
							operation = new SCWLOTwoTracks(	(ChromosomeListOfLists<?>)selectedTrack.getData(),
															(ChromosomeListOfLists<?>)otherTrack.getData(),
															this.scm);
						} else {
							DataPrecision precision = Utils.choosePrecision(getRootPane());
							if (precision != null) {
								operation = new BLOTwoTracks(	((BinListTrack)selectedTrack).getData(),
																((BinListTrack)otherTrack).getData(),
																precision,
																scm);
							}
						}
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
			CurveTrack<?> newTrack;
			if (isSCWList()) {
				newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), index + 1, (ScoredChromosomeWindowList)actionResult);
			} else {
				newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, (BinList)actionResult);
			}
			// add info to the history
			newTrack.getHistory().add("Operation on two tracks", Color.GRAY);
			newTrack.getHistory().add("Operation: " + this.scm.toString(), Color.GRAY);
			newTrack.getHistory().add("First track: " + this.selectedTrack.getName(), Color.GRAY);
			newTrack.getHistory().add("Second track: " + this.otherTrack.getName(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " & " + otherTrack.getName(), null, null);
		}
	}
	
	private boolean isSCWList () {
		if (selectedTrack.getData() instanceof BinList & otherTrack.getData() instanceof BinList) {
			if (((BinList)selectedTrack.getData()).getBinSize() == ((BinList)otherTrack.getData()).getBinSize()) {
				return false;
			}
		}
		return true;
	}	
}
