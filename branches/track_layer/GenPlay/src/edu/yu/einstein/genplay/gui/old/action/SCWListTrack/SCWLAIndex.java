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
package edu.yu.einstein.genplay.gui.old.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOIndex;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.old.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;



/**
 * Indexes the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAIndex extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Indexation";			// action name
	private static final String 	DESCRIPTION =
			"Index the selected track";		 								// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAIndex";


	/**
	 * Creates an instance of {@link SCWLAIndex}
	 */
	public SCWLAIndex() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number indexMin = NumberOptionPane.getValue(getRootPane(), "Minimum", "New minimum score:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
			if (indexMin != null) {
				Number indexMax = NumberOptionPane.getValue(getRootPane(), "Maximum", "New maximum score:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
				if(indexMax != null) {
					ScoredChromosomeWindowList scwList = selectedTrack.getData();
					Operation<ScoredChromosomeWindowList> operation = new SCWLOIndex(scwList, indexMin.doubleValue(), indexMax.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
