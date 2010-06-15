/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOLog2;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Applies a log2 function to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLALog2 extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -7633526345952471304L; // generated ID
	private static final String 	ACTION_NAME = "Log2";				// action name
	private static final String 	DESCRIPTION = 
		"Apply a log2 function to the scores of the selected track";	// tooltip
	private BinListTrack selectedTrack;									// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLALog2";


	/**
	 * Creates an instance of {@link BLALog2}
	 */
	public BLALog2() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getBinList();
			BinListOperation<BinList> operation = new BLOLog2(binList);
			return operation;
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null)	{
			selectedTrack.setBinList(actionResult, operation.getDescription());
		}
	}
}