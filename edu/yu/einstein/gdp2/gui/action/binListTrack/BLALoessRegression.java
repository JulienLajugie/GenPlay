/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOLoessRegression;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Computes a Loess regression on the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLALoessRegression extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 6749094444366905914L;	// generated ID
	private static final String 	ACTION_NAME = "Loess Regression";	// action name
	private static final String 	DESCRIPTION = 
		"Compute a Loess regression on the selected track";				// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLALoessRegression";


	/**
	 * Creates an instance of {@link BLALoessRegression}
	 */
	public BLALoessRegression() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
			int windowSize = binList.getBinSize();
			if(windowSize > 0) {
				Integer sigma = GenomeWidthChooser.getMovingWindowSize(getRootPane(), windowSize);
				if(sigma != null) {
					int fillNull = JOptionPane.showConfirmDialog(getRootPane(), "Do you want to extrapolate the null windows", "Extrapolate null windows", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					Operation<BinList> operation = null;
					if (fillNull == JOptionPane.YES_OPTION) {
						operation = new BLOLoessRegression(binList, sigma, true);
					} else if (fillNull == JOptionPane.NO_OPTION) {
						operation = new BLOLoessRegression(binList, sigma, false);
					}
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}		
	}
}
