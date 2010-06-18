/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOAverage;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Computes the average of the scores of the selected {@link SCWListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWLAAverage extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = 4662911501034876210L;
	private static final String 	ACTION_NAME = "Average";			// action name
	private static final String 	DESCRIPTION = 
		"Compute the average of the scores of the selected track";		// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAAverage";


	/**
	 * Creates an instance of {@link SCWLAAverage}
	 */
	public SCWLAAverage() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	
	
	@Override
	public Operation<Double> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList scwList = selectedTrack.getData();
				operation = new SCWLOAverage(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Average: \n" + new DecimalFormat("0.000").format(actionResult), "Average", JOptionPane.INFORMATION_MESSAGE);		
		}		
	}
}
