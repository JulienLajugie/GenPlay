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
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOUniqueScore;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;


/**
 * Adds a constant to the scores of the selected {@link SCWLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAUniqueScore extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 4027173438789911860L; 						// generated ID
	private static final String 	ACTION_NAME = "Unique Score (Constant)";				// action name
	private static final String 	DESCRIPTION = "Set a unique score for all windows.";	// tooltip
	private SCWLayer 				selectedLayer;											// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAUniqueScore.class.getName();


	/**
	 * Creates an instance of {@link SCWLAUniqueScore}
	 */
	public SCWLAUniqueScore() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedLayer = (SCWLayer) getValue("Layer");
		if (selectedLayer != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Unique Score", "Enter a score for all windows", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				ScoredChromosomeWindowList scwList = selectedLayer.getData();
				operation = new SCWLOUniqueScore(scwList, constant.doubleValue());
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}
}
