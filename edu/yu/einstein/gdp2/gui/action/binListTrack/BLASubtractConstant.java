/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOSubtractConstant;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Subtracts a constant from the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLASubtractConstant extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 9085714881046182620L;	// generated ID
	private static final String 	ACTION_NAME = "Subtraction (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Subtract a constant from the scores of the selected track";	// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLASubtractConstant";


	/**
	 * Creates an instance of {@link BLASubtractConstant}
	 */
	public BLASubtractConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to subtract: f(x)=x - C", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				BinList binList = ((BinListTrack)selectedTrack).getBinList();
				BinListOperation<BinList> operation = new BLOSubtractConstant(binList, constant.doubleValue());
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
