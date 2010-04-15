/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Changes the {@link DataPrecision} of a {@link BinListTrack} 
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChangePrecisionAction extends TrackListAction {

	private static final long serialVersionUID = 259517972989514480L;		// generated ID
	private static final String 	ACTION_NAME = "Change Precision";		// action name
	private static final String 	DESCRIPTION = 
		"Change the precision of the data of the selected track";			// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ChangePrecisionAction";


	/**
	 * Creates an instance of {@link ChangePrecisionAction}
	 * @param trackList a {@link TrackList}
	 */
	public ChangePrecisionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Changes the {@link DataPrecision} of a {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final DataPrecision precision = Utils.choosePrecision(getRootPane(), binList.getPrecision());
			if (precision != null) {				
				// thread for the action
				new ActionWorker<BinList>(trackList, "Changing Precision") {
					@Override
					protected BinList doAction() {
						return BinListOperations.changePrecision(binList, precision);
					}
					@Override
					protected void doAtTheEnd(BinList resultList) {
						if (resultList != null) {
							selectedTrack.setBinList(resultList, "Precision Changed to " + precision);
						}
					}
				}.execute();
			}
		}
	}
}
