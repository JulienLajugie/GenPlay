/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Defines regions separated by gaps of a specified length and computes the average of these regions
 * @author Julien Lajugie
 * @version 0.1
 */
public class TransfragAction extends TrackListAction {

	private static final long serialVersionUID = 8388717083206483317L;	// generated ID
	private static final String 	ACTION_NAME = "Transfrag";			// action name
	private static final String 	DESCRIPTION = 
		"Define regions separated by gaps of a specified length " +
		"and compute the average of these regions";						// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "TransfragAction";


	/**
	 * Creates an instance of {@link TransfragAction}
	 * @param trackList a {@link TrackList}
	 */
	public TransfragAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Defines regions separated by gaps of a specified 
	 * length and computes the average of these regions
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
			if(gap != null) {
				final String description = "Transfrag, gap = "  + gap + " zero value successive window";
				// thread for the action
				new ActionWorker<BinList>(trackList, "Computing Transfrag") {
					@Override
					protected BinList doAction() {
						return BinListOperations.transfrag(binList, gap.intValue());
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						selectedTrack.setBinList(actionResult, description);

					}
				}.execute();
			}
		}
	}		
}
