/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOInvertConstant;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Adds a constant to the scores of the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAInvertConstant extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Invert (Constant)";	// action name
	private static final String 	DESCRIPTION = 
		"Invert the values of the selected track";						// tooltip
	private SCWListTrack 			selectedTrack;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAInvertConstant";


	/**
	 * Creates an instance of {@link SCWLAInvertConstant}
	 */
	public SCWLAInvertConstant() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C in: f(x)= C / x", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				if (constant.doubleValue() == 0) {
					JOptionPane.showMessageDialog(getRootPane(), "The constant must be different from 0", "Invalid Parameter", JOptionPane.WARNING_MESSAGE);
				} else {
					ScoredChromosomeWindowList scwList = ((SCWListTrack)selectedTrack).getData();
					operation = new SCWLOInvertConstant(scwList, constant.doubleValue());
					return operation;
				}
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
