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
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLORepartition;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotData;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Generates an array containing the repartition of the score values of the selected {@link BinListTrack}
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class BLARepartition extends TrackListActionOperationWorker<double [][][]> {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a plot showing the repartition of the scores of the selected track";	// tooltip
	private Track<?>[] 				selectedTracks;
	private List<ScatterPlotData> 	scatPlotData;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLARepartition";


	/**
	 * Creates an instance of {@link BLARepartition}
	 */
	public BLARepartition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0#####"), 0 + Double.MIN_NORMAL, 1000, 1);
			if (scoreBin != null) {	
				// we ask the user to choose the tracks for the repartition only if there is more than one track
				if (getTrackList().getBinListTracks().length > 1) {
					selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getBinListTracks());
				} else {
					selectedTracks = getTrackList().getBinListTracks();
				}
				if ((selectedTracks != null)) {
					BinList[] binListArray = new BinList[selectedTracks.length];
					for (int i = 0; i < selectedTracks.length; i++) {
						binListArray[i] = ((BinListTrack)selectedTracks[i]).getData();						
					}	
					if (binListArray.length > 0) {
						Operation<double[][][]> operation = new BLORepartition(binListArray, scoreBin.doubleValue());
						return operation;
					}
				}
			}
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(double[][][] actionResult) {
		if (actionResult != null && selectedTracks.length != 0) {
			scatPlotData = new ArrayList<ScatterPlotData>();
			for (int k = 0; k < actionResult.length; k++) {
				Color trackColor = ((CurveTrack<?>) selectedTracks[k]).getTrackColor(); // retrieve the color of the track
				scatPlotData.add(new ScatterPlotData(actionResult[k], selectedTracks[k].toString(), trackColor));
			}
			ScatterPlotPane.showDialog(getRootPane(), "Score", "Bin Count", scatPlotData);
		}
	}
}
