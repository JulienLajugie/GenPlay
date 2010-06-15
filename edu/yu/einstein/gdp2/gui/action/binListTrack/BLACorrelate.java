/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOCorrelate;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Computes the coefficient of correlation between the selected
 * {@link BinListTrack} and another {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLACorrelate extends TrackListActionOperationWorker<Double> {

	private static final long serialVersionUID = -3513622153829181945L; // generated ID
	private static final String 	ACTION_NAME = "Correlation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the coefficient of correlation between " +
		"the selected track and another track";							// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACorrelate";


	/**
	 * Creates an instance of {@link BLACorrelate}
	 */
	public BLACorrelate() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Double> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinListTrack otherTrack = (BinListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the correlation with:", getTrackList().getBinListTracks());
			if (otherTrack != null) {
				boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
				if (selectedChromo != null) {
					BinList binList1 = selectedTrack.getBinList();
					BinList binList2 = otherTrack.getBinList();
					Operation<Double> operation = new BLOCorrelate(binList1, binList2, selectedChromo);
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Double actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Correlation coefficient: \n" + new DecimalFormat("0.000").format(actionResult), "Correlation", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}