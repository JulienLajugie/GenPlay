/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOMultiplyConstant;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Multiplies the scores of the selected {@link SCWListTrack} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAMultiplyConstant extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Multiplication (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Multiply the scores of the selected track by a constant";			// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAMultiplyConstant";


	/**
	 * Creates an instance of {@link SCWLAMultiplyConstant}
	 */
	public SCWLAMultiplyConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Multiply the scores of the track by", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				ScoredChromosomeWindowList scwList = ((SCWListTrack)selectedTrack).getData();
				operation = new SCWLOMultiplyConstant(scwList, constant.doubleValue());
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			int trackNumber = selectedTrack.getTrackNumber();
			GenomeWindow displayedGenomeWindow = selectedTrack.getGenomeWindow();			
			Track resultTrack = new SCWListTrack(displayedGenomeWindow, trackNumber, actionResult);
			getTrackList().setTrack(trackNumber - 1, resultTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName(), null);
		}		
	}
}
