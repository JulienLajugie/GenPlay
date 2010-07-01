/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.LogBase;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOLogOnAvgWithDamper;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.util.Utils;


/**
 * Applies a log function to the scores of the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLALogOnAvgWithDamper extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -8640599725095033450L;	// generated ID
	private static final String 	ACTION_NAME = "Log With Damper";	// action name
	private static final String 	DESCRIPTION = 
		"Apply a log + dumper function to the scores of " +
		"the selected track";											// tooltip
	private SCWListTrack 			selectedTrack;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLALogOnAvgWithDamper";


	/**
	 * Creates an instance of {@link SCWLALogOnAvgWithDamper}
	 */
	public SCWLALogOnAvgWithDamper() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			LogBase logBase = Utils.chooseLogBase(getRootPane());
			if (logBase != null) {
				Number damper = NumberOptionPane.getValue(getRootPane(), "Damper", "Enter a value for damper to add: f(x)=x + damper", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
				if(damper != null) {
					ScoredChromosomeWindowList scwList = selectedTrack.getData();
					Operation<ScoredChromosomeWindowList> operation = new SCWLOLogOnAvgWithDamper(scwList, logBase, damper.doubleValue());
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
