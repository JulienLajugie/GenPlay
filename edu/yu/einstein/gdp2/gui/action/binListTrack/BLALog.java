/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOLog;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Applies a log function to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLALog extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -7633526345952471304L; // generated ID
	private static final String 	ACTION_NAME = "Log";				// action name
	private static final String 	DESCRIPTION = 
		"Apply a log function to the scores of the selected track";	// tooltip
	private BinListTrack			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLALog";


	/**
	 * Creates an instance of {@link BLALog}
	 */
	public BLALog() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			LogBase logBase = Utils.chooseLogBase(getRootPane());
			if (logBase != null) {
				BinList binList = selectedTrack.getData();
				Operation<BinList> operation = new BLOLog(binList, logBase);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null)	{
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}