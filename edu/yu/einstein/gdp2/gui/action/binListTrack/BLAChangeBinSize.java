/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOChangeBinSize;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Changes the size of the bins of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAChangeBinSize extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4743270937529673599L;		// generated ID
	private static final String 	ACTION_NAME = "Change Bin Size";		// action name
	private static final String 	DESCRIPTION = 
		"Change the size of the bins of the selected track ";				// tooltip
	private BinListTrack 			selectedTrack;							// selected track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAChangeBinSize";


	/**
	 * Creates an instance of {@link BLAChangeBinSize}
	 */
	public BLAChangeBinSize() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		BinList binList = selectedTrack.getData();
		if (selectedTrack != null) {
			Number binSize = NumberOptionPane.getValue(getTrackList().getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				ScoreCalculationMethod method = Utils.chooseScoreCalculation(getTrackList().getRootPane());
				Operation<BinList> operation = new BLOChangeBinSize(binList, binSize.intValue(), method);
				if (method != null) {
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
