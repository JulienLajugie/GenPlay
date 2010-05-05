/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOLog2;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Applies a log2 function to the scores of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Log2Action extends TrackListAction {

	private static final long serialVersionUID = -7633526345952471304L; // generated ID
	private static final String 	ACTION_NAME = "Log2";				// action name
	private static final String 	DESCRIPTION = 
		"Apply a log2 function to the scores of the selected track";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "log2";


	/**
	 * Creates an instance of {@link Log2Action}
	 * @param trackList a {@link TrackList}
	 */
	public Log2Action(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Applies a log2 function to the scores of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = ((BinListTrack)selectedTrack).getBinList();
			// thread for the action
			final BinListOperation<BinList> operation = new BLOLog2(binList);
			new ActionWorker<BinList>(trackList, "Logging") {
				@Override
				protected BinList doAction() throws Exception {
					return operation.compute();
				}
				@Override
				protected void doAtTheEnd(BinList actionResult) {
					((BinListTrack)selectedTrack).setBinList(actionResult, operation.getDescription());					
				}				
			}.execute();
		}		
	}
}