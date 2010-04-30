/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOAverage;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Computes the average of the scores of the selected {@link BinListTrack}.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class AverageAction extends TrackListAction {

	private static final long serialVersionUID = 922723721396065388L;	// generated ID
	private static final String 	ACTION_NAME = "Average";			// action name
	private static final String 	DESCRIPTION = 
		"Compute the average of the scores of the selected track";			// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BinListAverage";


	/**
	 * Creates an instance of {@link AverageAction}
	 * @param trackList a {@link TrackList}
	 */
	public AverageAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Computes the average of the scores of the selected {@link BinListTrack}.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), trackList.getChromosomeManager());
			if (selectedChromo != null) {
				final BinList binList = selectedTrack.getBinList();
				final BinListOperation<Double> operation = new BLOAverage(binList, selectedChromo);
				// thread for the action
				new ActionWorker<Double>(trackList, "Computing Average") {
					@Override
					protected Double doAction() throws Exception {
						return operation.compute();
					}
					@Override
					protected void doAtTheEnd(Double actionResult) {
						JOptionPane.showMessageDialog(getRootPane(), "Average: \n" + new DecimalFormat("0.000").format(actionResult), "Average", JOptionPane.INFORMATION_MESSAGE);
					}							
				}.execute();
			}
		}
	}
}
