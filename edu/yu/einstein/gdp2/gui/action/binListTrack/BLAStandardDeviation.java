/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOStandardDeviation;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Returns the standard deviation on the
 * selected chromosomes of the selected track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAStandardDeviation extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -3906549904760962910L;	// generated ID
	private static final String 	ACTION_NAME = "Standard Deviation";	// action name
	private static final String 	DESCRIPTION = 
		"Return the standard deviation on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAStandardDeviation";


	/**
	 * Creates an instance of {@link BLAStandardDeviation}
	 */
	public BLAStandardDeviation() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				BinList binList = selectedTrack.getData();
				Operation<Double> operation = new BLOStandardDeviation(binList, selectedChromo);
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
