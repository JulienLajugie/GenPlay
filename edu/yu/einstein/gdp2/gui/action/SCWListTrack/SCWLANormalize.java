/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLONormalize;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Computes a Standard Score normalization on a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLANormalize extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 3820923997838773226L;	// generated ID
	private static final String 	ACTION_NAME = "Nomalize";			// action name
	private static final String 	DESCRIPTION = 
		"Normalize the scores of the selected track";					// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLANormalize";


	/**
	 * Creates an instance of {@link SCWLANormalize}
	 */
	public SCWLANormalize() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {		
			Number factor = NumberOptionPane.getValue(getRootPane(), "Multiplicative constant", "Enter a factor of X:", new DecimalFormat("###,###,###,###"), 0, 1000000000, 10000000);
			if(factor != null) {
				ScoredChromosomeWindowList inputList = selectedTrack.getData();
				Operation<ScoredChromosomeWindowList> operation = new SCWLONormalize(inputList, factor.doubleValue());
				return operation;
			}	
		}	
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}
}
