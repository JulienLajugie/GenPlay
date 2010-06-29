/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMultiplyConstant;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Multiplies the scores of the selected {@link BinListTrack} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAMultiplyConstant extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 8340235965333128192L;	// generated ID
	private static final String 	ACTION_NAME = "Multiplication (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Multiply the scores of the selected track by a constant";		// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAMultiplyConstant";


	/**
	 * Creates an instance of {@link BLAMultiplyConstant}
	 */
	public BLAMultiplyConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Multiply the score of the track by", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0) && (constant.doubleValue() != 1)) {
				BinList binList = ((BinListTrack)selectedTrack).getData();
				Operation<BinList> operation = new BLOMultiplyConstant(binList, constant.doubleValue());
				return operation;
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
