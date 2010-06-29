/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOInvertConstant;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;



/**
 * Inverts the values of the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAInvertConstant extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Invert (Constant)";	// action name
	private static final String 	DESCRIPTION = 
		"Invert the values of the selected track";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAInvertConstant";


	/**
	 * Creates an instance of {@link BLAInvertConstant}
	 */
	public BLAInvertConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C in: f(x)= C / x", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			if (constant != null) {
				if (constant.doubleValue() == 0) {
					JOptionPane.showMessageDialog(getRootPane(), "The constant must be different from 0", "Invalid Parameter", JOptionPane.WARNING_MESSAGE);
				} else {
					BinList binList = ((BinListTrack)selectedTrack).getData();
					operation = new BLOInvertConstant(binList, constant.doubleValue());
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
