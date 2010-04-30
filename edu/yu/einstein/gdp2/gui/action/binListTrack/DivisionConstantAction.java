/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLODivideConstant;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Divides the scores of the selected {@link BinListTrack} by a constant
 * @author Julien Lajugie
 * @version 0.1
 */
public class DivisionConstantAction extends TrackListAction {

	private static final long serialVersionUID = 5335750661672754072L;	// generated ID
	private static final String 	ACTION_NAME = "Division (Constant)";// action name
	private static final String 	DESCRIPTION = 
		"Divide the scores of the selected track by a constant";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "DivisionConstantAction";


	/**
	 * Creates an instance of {@link DivisionConstantAction}
	 * @param trackList a {@link TrackList}
	 */
	public DivisionConstantAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Divides the scores of the selected {@link BinListTrack} by a constant
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number constant = NumberOptionPane.getValue(getRootPane(), "Constant", "Divide the scores of the track by", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if ((constant != null) && (constant.doubleValue() != 0) && (constant.doubleValue() != 1)) {
				final BinList binList = ((BinListTrack)selectedTrack).getBinList();
				final BinListOperation<BinList> operation = new BLODivideConstant(binList, constant.doubleValue());
				// thread for the action
				new ActionWorker<BinList>(trackList, "Dividing by Constant") {
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
