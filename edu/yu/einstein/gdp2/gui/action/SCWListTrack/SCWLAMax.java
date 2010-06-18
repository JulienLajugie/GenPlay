/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOMax;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Shows the maximum score of the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAMax extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -3864460354387970028L;	// generated ID
	private static final String 	ACTION_NAME = "Maximum";			// action name
	private static final String 	DESCRIPTION = 
		"Show the maximum score of the selected track";					// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAMax";


	/**
	 * Creates an instance of {@link SCWLAMax}
	 */
	public SCWLAMax() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList scwList = selectedTrack.getData();
				Operation<Double> operation = new SCWLOMax(scwList, selectedChromo);
				return operation;
			}		
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), actionResult, "Maximum of \"" + selectedTrack.getName() +"\":", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
