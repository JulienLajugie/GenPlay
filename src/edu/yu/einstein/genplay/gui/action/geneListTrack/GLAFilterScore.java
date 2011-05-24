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
package edu.yu.einstein.genplay.gui.action.geneListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.list.geneList.operation.GLOFilterThreshold;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;

/**
 * Removes the genes that have a score lower than the specified value
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLAFilterScore extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = -5807756062510954560L;	// generated id
	private static final String 	ACTION_NAME = "Filter Score"; 		// action name
	private static final String 	DESCRIPTION = "Remove the genes " +
			"that have a score lower than the specified value"; 		// tooltip
	private GeneListTrack 			selectedTrack;						// selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLAFilterScore";


	/**
	 * Creates an instance of {@link GLAFilterScore}
	 */
	public GLAFilterScore() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GeneList> initializeOperation() {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			GeneList geneList = selectedTrack.getData();
			Number threshold = NumberOptionPane.getValue(getRootPane(), "Enter Value", "Remove genes with a score smaller than:", new DecimalFormat("0.###"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if (threshold != null) {
				//operation = new GLOFilterThreshold(geneList, threshold.doubleValue(), threshold.doubleValue()); 
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
