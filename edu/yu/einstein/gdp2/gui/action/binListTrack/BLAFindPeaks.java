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
import java.util.concurrent.ExecutionException;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindIslands;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindPeaksDensity;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindPeaksStDev;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.dialog.peakFinderDialog.PeakFinderDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Searches the peaks of a track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAFindPeaks extends TrackListActionOperationWorker<BinList[]> {

	private static final long serialVersionUID = 1524662321569310278L;  // generated ID
	private static final String 	ACTION_NAME = "Find Peaks";			// action name
	private static final String 	DESCRIPTION = 
		"Search the peaks of the selected track";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAFindPeaks";


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
	public Operation<BinList[]> initializeOperation() throws InterruptedException, ExecutionException {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
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
	 * Action done at the end of a Island Finder operation
	 * @param actionResult the output array of BinList from the island finder 
	 */
	private void doAtTheEndOfIslandFinder(BinList[] actionResult) {
		BLOFindIslands bloFindIslands = (BLOFindIslands) operation;
		for (int i=0; i < actionResult.length; i++) {	// we have to treat all actions result
			if (actionResult[i] != null){
				Track<?> resultTrack = TrackChooser.getTracks(getRootPane(),
						"Choose A Track", 
						"Generate the " + bloFindIslands.getResultTypes()[i].toString() + " result on track:", 
						getTrackList().getEmptyTracks());	// purposes tracks
				if (resultTrack != null) {
					int index = resultTrack.getTrackNumber() - 1;
					BinListTrack newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult[i]);
					newTrack.getHistory().add(operation.getDescription() + ", Result Type: " + bloFindIslands.getResultTypes()[i].toString(), Color.GRAY);
					newTrack.getHistory().add("Window Size = " + actionResult[i].getBinSize() + "bp, Precision = " + actionResult[i].getPrecision(), Color.GRAY);
					getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "peaks of " + selectedTrack.getName() + ", " + bloFindIslands.getResultTypes()[i].toString(),	selectedTrack.getStripes());
				}
			}
		}
	}


	/**
	 * Action done at the end of all the peak finders that are not Island finders
	 * @param actionResult the output BinList from the operation 
	 */
	private void doAtTheEndDefaultFinder(BinList actionResult) {
		Track<?> resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());	// purposes tracks
		if (resultTrack != null) {
			int index = resultTrack.getTrackNumber() - 1;
			BinListTrack newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			newTrack.getHistory().add(operation.getDescription(), Color.GRAY);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "peaks of " + selectedTrack.getName(),	selectedTrack.getStripes());
		}
	}
}
