/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOStandardDeviation;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Returns the standard deviation on the
 * selected chromosomes of the selected track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAStandardDeviation extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -3906549904760962910L;	// generated ID
	private static final String 	ACTION_NAME = "Standard Deviation";	// action name
	private static final String 	DESCRIPTION = 
		"Return the standard deviation on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAStandardDeviation";


	/**
	 * Creates an instance of {@link SCWLAStandardDeviation}
	 */
	public SCWLAStandardDeviation() {
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
				Operation<Double> operation = new SCWLOStandardDeviation(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Standard deviation: \n" + new DecimalFormat("0.000").format(actionResult), "Standard Deviation", JOptionPane.INFORMATION_MESSAGE);
		}
	}		
}
