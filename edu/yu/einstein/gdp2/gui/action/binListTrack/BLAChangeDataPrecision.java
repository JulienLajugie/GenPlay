/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOChangeDataPrecision;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Changes the {@link DataPrecision} of a {@link BinListTrack} 
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAChangeDataPrecision extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 259517972989514480L;		// generated ID
	private static final String 	ACTION_NAME = "Change Precision";		// action name
	private static final String 	DESCRIPTION = 
		"Change the precision of the data of the selected track";			// tooltip
	private BinListTrack 			selectedTrack;							// selected track
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAChangeDataPrecision";


	/**
	 * Creates an instance of {@link BLAChangeDataPrecision}
	 */
	public BLAChangeDataPrecision() {
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
			DataPrecision precision = Utils.choosePrecision(getRootPane(), binList.getPrecision());
			if (precision != null) {	
				BinListOperation<BinList> operation = new BLOChangeDataPrecision(binList, precision);
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
