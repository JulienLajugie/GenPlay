/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMultiplyConstant;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Multiplies the scores of the selected {@link BinListTrack} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiplicationConstantAction extends TrackListAction {

	private static final long serialVersionUID = 8340235965333128192L;	// generated ID
	private static final String 	ACTION_NAME = "Multiplication (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Multiply the scores of the selected track by a constant";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "MultiplicationConstantAction";


	/**
	 * Creates an instance of {@link MultiplicationConstantAction}
	 */
	public MultiplicationConstantAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Multiplies the scores of the selected {@link BinListTrack} by a constant
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Multiply the score of the track by", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0) && (constant.doubleValue() != 1)) {
				final BinList binList = ((BinListTrack)selectedTrack).getBinList();
				final BinListOperation<BinList> operation = new BLOMultiplyConstant(binList, constant.doubleValue());
				// thread for the action
				new ActionWorker<BinList>(getTrackList(), "Multiplicating by Constant") {
					@Override
					protected BinList doAction() throws Exception {
						return operation.compute();
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						selectedTrack.setBinList(actionResult, operation.getDescription());
					}
				}.execute();
			}
		}
	}
}
