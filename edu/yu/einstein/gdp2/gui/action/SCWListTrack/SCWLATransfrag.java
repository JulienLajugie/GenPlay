/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOTransfrag;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.util.Utils;

/**
 * Defines regions separated by gaps of a specified length and computes the average/sum/max of these regions
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLATransfrag extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 4913086320948928688L;
	private static final String 	ACTION_NAME = "Transfrag";// action name
	private static final String 	DESCRIPTION = 
		"Define regions separated by gaps of a specified length " +
		"and compute the average/max/sum of these regions";			// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLATransfrag";


	/**
	 * Creates an instance of {@link SCWLATransfrag}
	 */
	public SCWLATransfrag() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			ScoredChromosomeWindowList scwList = ((SCWListTrack)selectedTrack).getData();
			Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
			if (gap != null) {
				ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if (operationType != null) {						
					operation = new SCWLOTransfrag(scwList, gap.intValue(), operationType);
					return operation;
				}
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