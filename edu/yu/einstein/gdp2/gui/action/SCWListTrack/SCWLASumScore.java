/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOSumScore;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Returns the sum of the scores on the
 * selected chromosomes of the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLASumScore extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -5973104354521841885L;	// generated ID
	private static final String 	ACTION_NAME = "Score Count";		// action name
	private static final String 	DESCRIPTION = 
		"Return the sum of the scores on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLASumScore";


	/**
	 * Creates an instance of {@link SCWLASumScore}
	 */
	public SCWLASumScore() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList inpuList = selectedTrack.getData();
				Operation<Double> operation = new SCWLOSumScore(inpuList, selectedChromo);
				return operation;
			}
		}	
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Score count: \n" + new DecimalFormat("###,###,###,###,###,###,###.###").format(actionResult), "Score Count", JOptionPane.INFORMATION_MESSAGE);
		}		
	}		
}
