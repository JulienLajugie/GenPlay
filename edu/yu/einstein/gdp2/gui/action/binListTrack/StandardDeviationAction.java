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
import yu.einstein.gdp2.core.list.binList.operation.BLOStandardDeviation;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Returns the standard deviation on the
 * selected chromosomes of the selected track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class StandardDeviationAction extends TrackListAction {

	private static final long serialVersionUID = -3906549904760962910L;	// generated ID
	private static final String 	ACTION_NAME = "Standard Deviation";	// action name
	private static final String 	DESCRIPTION = 
		"Return the standard deviation on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BinListStandardDeviation";


	/**
	 * Creates an instance of {@link StandardDeviationAction}
	 */
	public StandardDeviationAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Returns the standard deviation on the
	 * selected chromosomes of the selected {@link BinListTrack}.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				final BinList binList = selectedTrack.getBinList();
				final BinListOperation<Double> operation = new BLOStandardDeviation(binList, selectedChromo);
				// thread for the action
				new ActionWorker<Double>(getTrackList(), "Calculating Standard Deviation") {
					@Override
					protected Double doAction() throws Exception {
						return operation.compute();
					}
					@Override
					protected void doAtTheEnd(Double actionResult) {
						JOptionPane.showMessageDialog(getRootPane(), "Standard deviation: \n" + new DecimalFormat("0.000").format(actionResult), "Standard Deviation", JOptionPane.INFORMATION_MESSAGE);
					}							
				}.execute();
			}
		}	
	}		
}
