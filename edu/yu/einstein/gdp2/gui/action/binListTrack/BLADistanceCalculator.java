package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLODistanceCalculator;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;

public class BLADistanceCalculator extends TrackListActionOperationWorker<Double[][][]> {

	private static final long serialVersionUID = 1401297625985870348L;
	private static final String 	ACTION_NAME = "Distance Calculation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the number of base pairs at a specific distance between " +
		"the selected track and another track";							// tooltip
	private BinListTrack 			selectedTrack;						// 1st selected track  	
	private BinListTrack 			otherTrack;							// 2nd selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLADistanceCalculator";


	/**
	 * Creates an instance of {@link BLADistanceCalculator}
	 */
	public BLADistanceCalculator() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	@Override
	public Operation<Double[][][]> initializeOperation() throws Exception {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the correlation with:", getTrackList().getBinListTracks());
			if (otherTrack != null) {
				BinList binList1 = selectedTrack.getData();
				BinList binList2 = otherTrack.getData();
				Operation<Double[][][]> operation = new BLODistanceCalculator(binList1, binList2);
				return operation;
			}
		}
		return null;
	}

}
