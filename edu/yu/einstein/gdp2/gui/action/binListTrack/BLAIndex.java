/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOIndex;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Indexes the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAIndex extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Indexation";			// action name
	private static final String 	DESCRIPTION = 
		"Index the selected track";		 								// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAIndex";


	/**
	 * Creates an instance of {@link BLAIndex}
	 */
	public BLAIndex() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {			
			if (selectedTrack.getBinList().getPrecision() == DataPrecision.PRECISION_1BIT) {
				JOptionPane.showMessageDialog(getRootPane(), "Error, indexation is not available for 1-Bit tracks", "Error", JOptionPane.ERROR_MESSAGE);
			}
			Number indexMin = NumberOptionPane.getValue(getRootPane(), "Minimum", "New minimum score:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
			if (indexMin != null) {
				Number indexMax = NumberOptionPane.getValue(getRootPane(), "Maximum", "New maximum score:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
				if(indexMax != null) {
					BinList binList = selectedTrack.getBinList();
					Operation<BinList> operation = new BLOIndex(binList, indexMin.doubleValue(), indexMax.doubleValue());
					return operation;
				}
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
