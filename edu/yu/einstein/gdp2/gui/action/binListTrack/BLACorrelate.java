/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOCorrelate;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.CorrelationReportDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Computes the coefficient of correlation for every chromosome between 
 * the selected {@link BinListTrack} and another {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACorrelate extends TrackListActionOperationWorker<Double[]> {

	private static final long serialVersionUID = -3513622153829181945L; // generated ID
	private static final String 	ACTION_NAME = "Correlation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the coefficient of correlation between " +
		"the selected track and another track";							// tooltip
	private BinListTrack 			selectedTrack;						// 1st selected track  	
	private BinListTrack 			otherTrack;							// 2nd selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACorrelate";


	/**
	 * Creates an instance of {@link BLACorrelate}
	 */
	public BLACorrelate() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double[]> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the correlation with:", getTrackList().getBinListTracks());
			if (otherTrack != null) {
				BinList binList1 = selectedTrack.getData();
				BinList binList2 = otherTrack.getData();
				Operation<Double[]> operation = new BLOCorrelate(binList1, binList2);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double[] actionResult) {
		if (actionResult != null) {
			CorrelationReportDialog.showDialog(getRootPane(), actionResult, selectedTrack.getName(), otherTrack.getName());
		}
	}
}
