/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Applies a log2 function to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Log2WithDamperAction extends TrackListAction {

	private static final long serialVersionUID = -8640599725095033450L;	// generated ID
	private static final String 	ACTION_NAME = "Log2 With Damper";	// action name
	private static final String 	DESCRIPTION = 
		"Apply a log2 + dumper function to the scores of " +
		"the selected track";											// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "log2withDamper";


	/**
	 * Creates an instance of {@link Log2WithDamperAction}
	 * @param trackList a {@link TrackList}
	 */
	public Log2WithDamperAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Applies a log2 + damper function to the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {		
			final Number damper = NumberOptionPane.getValue(getRootPane(), "Damper", "Enter a value for damper to add: f(x)=x + damper", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if(damper != null) {
				final BinList binList = selectedTrack.getBinList();
				// thread for the action
				new ActionWorker<BinList>(trackList, "Logging Track") {
					@Override
					protected BinList doAction() {
						return BinListOperations.log2(binList, damper.doubleValue(), binList.getPrecision());
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						String description = "log with a damper of " + damper;
						selectedTrack.setBinList(actionResult, description);
					}
				}.execute();
			}
		}		
	}
}