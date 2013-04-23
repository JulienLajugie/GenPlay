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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import java.awt.Color;
import java.util.concurrent.ExecutionException;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindIslands;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksDensity;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksStDev;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.PeakFinderDialog;
import edu.yu.einstein.genplay.gui.dialog.trackChooser.TrackChooser;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.colors.Colors;



/**
 * Searches the peaks of a {@link BinLayer}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAFindPeaks extends TrackListActionOperationWorker<BinList[]> {

	private static final long serialVersionUID = 1524662321569310278L;  // generated ID
	private static final String 	ACTION_NAME = "Find Peaks";			// action name
	private static final String 	DESCRIPTION =
			"Search the peaks of the selected layer";						// tooltip
	private BinLayer 				selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLAFindPeaks.class.getName();


	/**
	 * Creates an instance of {@link BLAFindPeaks}
	 */
	public BLAFindPeaks() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(BinList[] actionResult) {
		if (actionResult != null) {
			if (operation instanceof BLOFindIslands) {
				doAtTheEndOfIslandFinder(actionResult);
			} else {
				doAtTheEndDefaultFinder(actionResult[0]);
			}
		}
	}


	/**
	 * Action done at the end of all the peak finders that are not Island finders
	 * @param actionResult the output BinList from the operation
	 */
	private void doAtTheEndDefaultFinder(BinList actionResult) {
		Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackListPanel().getModel().getTracks());	// purposes tracks
		if (resultTrack != null) {
			BinLayer newLayer = new BinLayer(resultTrack, actionResult, "Peaks of " + selectedLayer.getName());
			newLayer.getHistory().add(operation.getDescription(), Colors.GREY);
			newLayer.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getScorePrecision(), Color.GRAY);
			resultTrack.getLayers().add(newLayer);
			resultTrack.setActiveLayer(newLayer);
		}
	}


	/**
	 * Action done at the end of a Island Finder operation
	 * @param actionResult the output array of BinList from the island finder
	 */
	private void doAtTheEndOfIslandFinder(BinList[] actionResult) {
		BLOFindIslands bloFindIslands = (BLOFindIslands) operation;
		for (int i=0; i < actionResult.length; i++) {	// we have to treat all actions result
			if (actionResult[i] != null){
				Track resultTrack = TrackChooser.getTracks(getRootPane(),
						"Choose A Track",
						"Generate the " + bloFindIslands.getResultTypes()[i].toString() + " result on track:",
						getTrackListPanel().getModel().getTracks());	// purposes tracks
				if (resultTrack != null) {
					BinLayer newLayer = new BinLayer(resultTrack, actionResult[i], "Islands of " + selectedLayer.getName());
					newLayer.getHistory().add(operation.getDescription() + ", Result Type: " + bloFindIslands.getResultTypes()[i].toString(), Colors.GREY);
					newLayer.getHistory().add("Window Size = " + actionResult[i].getBinSize() + "bp, Precision = " + actionResult[i].getScorePrecision(), Colors.GREY);
					resultTrack.getLayers().add(newLayer);
					resultTrack.setActiveLayer(newLayer);
				}
			}
		}
	}


	@Override
	public Operation<BinList[]> initializeOperation() throws InterruptedException, ExecutionException {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			BinList binList = selectedLayer.getData();
			BLOFindPeaksDensity bloDensity = new BLOFindPeaksDensity(binList);
			BLOFindPeaksStDev bloStdev = new BLOFindPeaksStDev(binList);
			BLOFindIslands bloIsland = new BLOFindIslands(binList);
			PeakFinderDialog peakFinderDialog = new PeakFinderDialog(bloDensity, bloStdev, bloIsland);
			if (peakFinderDialog.showFilterDialog(getRootPane()) == PeakFinderDialog.APPROVE_OPTION) {
				return peakFinderDialog.getOperation();
			}
		}
		return null;
	}
}
