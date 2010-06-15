/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOAddConstant;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Adds a constant to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAAddConstant extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Addition (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Add a constant to the scores of the selected track";			// tooltip
	private BinListTrack selectedTrack;
	private BinListOperation<BinList> operation;

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAAddConstant";


	/**
	 * Creates an instance of {@link BLAAddConstant}
	 */
	public BLAAddConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to add: f(x)=x + C", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				BinList binList = ((BinListTrack)selectedTrack).getBinList();
				operation = new BLOAddConstant(binList, constant.doubleValue());
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
