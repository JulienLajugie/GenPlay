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
package edu.yu.einstein.genplay.gui.action.binListTrack;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOIntervalsScoring;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.NumberOptionPane;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;



/**
 * Computes the average, sum or max of the selected track on intervals defined by another track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAIntervalsScoring extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -3736735803307616477L;			// generated ID
	private static final String 	ACTION_NAME = "Intervals Scoring";	// action name
	private static final String 	DESCRIPTION =
			"Compute the average, the sum or the max of the " +
					"selected track on intervals defined by another track";					// tooltip
	private BinListTrack 			selectedTrack;		// selected track
	private Track<?> 				intervalTrack;		// track defining the intervals
	private Number 					percentage;			// percentage of the greatest values
	private ScoreCalculationMethod 	method;				// method of calculation
	private Track<?> 				resultTrack;		// result track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAIntervalsScoring";


	/**
	 * Creates an instance of {@link BLAIntervalsScoring}
	 */
	public BLAIntervalsScoring() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			intervalTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose the track defining the intervals:", getTrackList().getBinListTracks());
			if(intervalTrack != null) {
				percentage = NumberOptionPane.getValue(getRootPane(), "Enter a percentage", "Perform the calculation on the x% greatest values of each interval:", new DecimalFormat("0"), 0, 100, 100);
				if (percentage != null) {
					method = Utils.chooseScoreCalculation(getRootPane());
					if (method != null) {
						resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
						if (resultTrack != null) {
							DataPrecision precision = Utils.choosePrecision(getRootPane());;
							if (precision != null) {
								BinList valueBinList = selectedTrack.getData();
								BinList scoringBinList = ((BinListTrack)intervalTrack).getData();
								Operation<BinList> operation = new BLOIntervalsScoring(scoringBinList, valueBinList, percentage.intValue(), method, precision);
								return operation;
							}
						}
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			BinListTrack newTrack = new BinListTrack(index + 1, actionResult);
			// add info to the history
			newTrack.getHistory().add("Result of the " + method + " of " + selectedTrack.getName() + " calculated on the intervals defined by " + intervalTrack.getName() + " on the " + percentage + "% greatest values", Color.GRAY);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Colors.GREY);
			getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), "average of " + selectedTrack.getName() + " from intervals of  " + intervalTrack.getName(), null, null, null);
		}
	}
}
