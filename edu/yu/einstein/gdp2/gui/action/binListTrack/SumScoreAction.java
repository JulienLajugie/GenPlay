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
import yu.einstein.gdp2.core.list.binList.operation.BLOSumScore;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Returns the sum of the scores on the
 * selected chromosomes of the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SumScoreAction extends TrackListAction {

	private static final long serialVersionUID = -7198642565173540167L;	// generated ID
	private static final String 	ACTION_NAME = "Score Count";		// action name
	private static final String 	DESCRIPTION = 
		"Return the sum of the scores on the " +
		"selected chromosomes of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BinListScoreCount";


	/**
	 * Creates an instance of {@link SumScoreAction}
	 */
	public SumScoreAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Returns the sum of the scores on the selected
	 * chromosomes of the selected {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane(), ChromosomeManager.getInstance());
			if (selectedChromo != null) {
				final BinList binList = selectedTrack.getBinList();
				final BinListOperation<Double> operation = new BLOSumScore(binList, selectedChromo);
				// thread for the action
				new ActionWorker<Double>(getTrackList(), "Calculating Score Count") {
					@Override
					protected Double doAction() throws Exception {
						return operation.compute();
					}
					@Override
					protected void doAtTheEnd(Double actionResult) {
						if (actionResult != null) {
							JOptionPane.showMessageDialog(getRootPane(), "Score count: \n" + new DecimalFormat("###,###,###,###,###,###,###.###").format(actionResult), "Score Count", JOptionPane.INFORMATION_MESSAGE);
						}
					}							
				}.execute();
			}
		}	
	}		
}