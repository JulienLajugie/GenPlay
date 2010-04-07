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
 * Normalizes the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class NormalizationAction extends TrackListAction {


	private static final long serialVersionUID = 1672001436769889976L;	// generated ID
	private static final String 	ACTION_NAME = "Nomalization";			// action name
	private static final String 	DESCRIPTION = 
		"Normalizes the scores of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "nomalize";


	/**
	 * Creates an instance of {@link NormalizationAction}
	 * @param trackList a {@link TrackList}
	 */
	public NormalizationAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Normalizes the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {		
			final Number factor = NumberOptionPane.getValue(getRootPane(), "Multiplicative constant", "Enter a factor of X:", new DecimalFormat("###,###,###,###"), 0, 1000000000, 10000000);
			if(factor != null) {
				final BinList binList = selectedTrack.getBinList();
				// thread for the action
				new ActionWorker<BinList>(trackList, "Normalizing") {
					@Override
					protected BinList doAction() {
						return BinListOperations.normalize(binList, factor.intValue(), binList.getPrecision());
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						String description = "normalize with a factor of " + factor;
						selectedTrack.setBinList(actionResult, description);						
					}
				}.execute();
			}	
		}		
	}
}