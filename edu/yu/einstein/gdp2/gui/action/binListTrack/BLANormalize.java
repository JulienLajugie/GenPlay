/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLONormalize;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Normalizes the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLANormalize extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 1672001436769889976L;	// generated ID
	private static final String 	ACTION_NAME = "Nomalization";			// action name
	private static final String 	DESCRIPTION = 
		"Normalizes the scores of the selected track";					// tooltip
	private BinListTrack selectedTrack;									// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLANormalize";


	/**
	 * Creates an instance of {@link BLANormalize}
	 */
	public BLANormalize() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {		
			Number factor = NumberOptionPane.getValue(getRootPane(), "Multiplicative constant", "Enter a factor of X:", new DecimalFormat("###,###,###,###"), 0, 1000000000, 10000000);
			if(factor != null) {
				BinList binList = selectedTrack.getBinList();
				Operation<BinList> operation = new BLONormalize(binList, factor.doubleValue());
				return operation;
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
