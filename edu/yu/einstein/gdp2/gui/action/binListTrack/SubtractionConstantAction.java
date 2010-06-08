/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOSubtractConstant;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Subtracts a constant from the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SubtractionConstantAction extends TrackListAction {


	private static final long serialVersionUID = 9085714881046182620L;	// generated ID
	private static final String 	ACTION_NAME = "Subtraction (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Subtract a constant from the scores of the selected track";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SubtractionConstantAction";


	/**
	 * Creates an instance of {@link SubtractionConstantAction}
	 */
	public SubtractionConstantAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Subtracts a constant from the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Enter a value C to subtract: f(x)=x - C", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0)) {
				final BinList binList = ((BinListTrack)selectedTrack).getBinList();
				final BinListOperation<BinList> operation = new BLOSubtractConstant(binList, constant.doubleValue());
				// thread for the action
				new ActionWorker<BinList>(getTrackList(), "Subtracting") {
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

