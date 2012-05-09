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
package edu.yu.einstein.genplay.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOSumScore;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.ChromosomeChooser;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;



/**
 * Returns the sum of the scores on the
 * selected chromosomes of the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLASumScore extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -5973104354521841885L;	// generated ID
	private static final String 	ACTION_NAME = "Score Count";		// action name
	private static final String 	DESCRIPTION = 
		"Return the sum of the scores on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLASumScore";


	/**
	 * Creates an instance of {@link SCWLASumScore}
	 */
	public SCWLASumScore() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList inpuList = selectedTrack.getData();
				Operation<Double> operation = new SCWLOSumScore(inpuList, selectedChromo);
				return operation;
			}
		}	
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Score count: \n" + new DecimalFormat("###,###,###,###,###,###,###.###").format(actionResult), "Score Count", JOptionPane.INFORMATION_MESSAGE);
		}		
	}		
}