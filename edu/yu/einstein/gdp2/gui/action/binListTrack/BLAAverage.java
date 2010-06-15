/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOAverage;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Computes the average of the scores of the selected {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAAverage extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = 922723721396065388L;	// generated ID
	private static final String 	ACTION_NAME = "Average";			// action name
	private static final String 	DESCRIPTION = 
		"Compute the average of the scores of the selected track";			// tooltip
	private BinListOperation<Double> operation;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAAverage";


	/**
	 * Creates an instance of {@link BLAAverage}
	 */
	public BLAAverage() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<Double> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				BinList binList = selectedTrack.getBinList();
				operation = new BLOAverage(binList, selectedChromo);
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
