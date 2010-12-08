/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOLogOnAvgWithDamper;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Applies a log function to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLALogOnAvgWithDamper extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -8640599725095033450L;	// generated ID
	private static final String 	ACTION_NAME = "Log With Damper";	// action name
	private static final String 	DESCRIPTION = 
		"Apply a log + dumper function to the scores of " +
		"the selected track";											// tooltip
	private BinListTrack 			selectedTrack;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLALogOnAvgWithDamper";


	/**
	 * Creates an instance of {@link BLALogOnAvgWithDamper}
	 */
	public BLALogOnAvgWithDamper() {
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
				Number damper = NumberOptionPane.getValue(getRootPane(), "Damper", "Enter a value for damper where: f(x) = log((x + damper) / (avg + damper))", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
				if(damper != null) {
					BinList binList = selectedTrack.getData();
					Operation<BinList> operation = new BLOLogOnAvgWithDamper(binList, logBase, damper.doubleValue());
					return operation;
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
