/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.PeakFinderType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFilterDensity;
import yu.einstein.gdp2.core.list.binList.operation.BLOSearchPeaks;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.peakFinderDialog.DensityFinderPanel;
import yu.einstein.gdp2.gui.dialog.peakFinderDialog.PeakFinderDialog;
import yu.einstein.gdp2.gui.dialog.peakFinderDialog.StDevFinderPanel;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Searches the peaks of a track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLASearchPeaks extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 1524662321569310278L;  // generated ID
	private static final String 	ACTION_NAME = "Search Peaks";		// action name
	private static final String 	DESCRIPTION = 
		"Search the peaks of the selected track";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLASearchPeaks";


	/**
	 * Creates an instance of {@link BLASearchPeaks}
	 */
	public BLASearchPeaks() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
			PeakFinderDialog peakFinderDialog = new PeakFinderDialog();
			if (peakFinderDialog.showFilterDialog(getRootPane()) == PeakFinderDialog.APPROVE_OPTION) {
				if (peakFinderDialog.getPeakFinderType() == PeakFinderType.STDEV) {
					StDevFinderPanel stDevPanel = (StDevFinderPanel) peakFinderDialog.getPeakFinderPanel();
					int regionWidth = stDevPanel.getRegionWidth();
					double threshold = stDevPanel.getThreshold();
					return new BLOSearchPeaks(binList, regionWidth, threshold);
				} else if (peakFinderDialog.getPeakFinderType() == PeakFinderType.DENSITY) {
					DensityFinderPanel densityPanel = (DensityFinderPanel) peakFinderDialog.getPeakFinderPanel();
					int regionWidth = densityPanel.getRegionWidth();
					double lowThreshold = densityPanel.getLowThreshold();
					double highThreshold = densityPanel.getHighThreshold();
					double percentage = densityPanel.getPercentage();
					return new BLOFilterDensity(binList, lowThreshold, highThreshold, percentage, regionWidth);
				}				
			}
		}	
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
