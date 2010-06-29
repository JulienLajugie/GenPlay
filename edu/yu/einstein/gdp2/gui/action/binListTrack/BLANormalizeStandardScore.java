/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLONormalizeStandardScore;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Normalizes the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLANormalizeStandardScore extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4481408947601757066L;	// generated ID
	private static final String 	ACTION_NAME = "Standard Score";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the standard score of the selected track";				// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLANormalizeStandardScore";


	/**
	 * Creates an instance of {@link BLANormalizeStandardScore}
	 */
	public BLANormalizeStandardScore() {
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
			Operation<BinList> operation = new BLONormalizeStandardScore(binList);
			return operation;
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
