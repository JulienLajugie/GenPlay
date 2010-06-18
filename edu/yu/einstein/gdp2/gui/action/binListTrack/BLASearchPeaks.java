/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOSearchPeaks;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
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
			BinList binList = selectedTrack.getBinList();
			Number sizeMovingSD = GenomeWidthChooser.getMovingStdDevWidth(getRootPane(), binList.getBinSize());
			if(sizeMovingSD != null) {
				Number nbSDAccepted = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select only peak with a local SD x time higher than the global one", new DecimalFormat("0.0"), 0, 1000, 1).intValue(); 
				if(nbSDAccepted != null) {
					Operation<BinList> operation = new BLOSearchPeaks(binList, sizeMovingSD.intValue(), nbSDAccepted.intValue());
					return operation;
				}
			}
		}	
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setBinList(actionResult, operation.getDescription());
		}
	}
}
