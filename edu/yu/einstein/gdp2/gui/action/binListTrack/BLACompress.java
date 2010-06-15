/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Compresses / uncompresses the data of the selected track 
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLACompress extends TrackListActionWorker<BinList> {

	private static final long serialVersionUID = 5156554955152029111L;	// generated ID
	private static final String 	ACTION_NAME = "Compress";			// action name
	private static final String 	DESCRIPTION = 
		"Compress the data of the selected track";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACompress";


	/**
	 * Creates an instance of {@link BLACompress}
	 */
	public BLACompress() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			String description = new String();
			if (actionResult.isCompressed()) {
				description = "Compressed Mode On";
			} else {
				description = "Compressed Mode Off";
			}
			selectedTrack.setBinList(actionResult,  description);
		}		
	}


	@Override
	protected BinList processAction() throws Exception {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String actionDescription = new String();
			if (selectedTrack.getBinList().isCompressed()) {
				actionDescription = "Uncompressing Data";
			} else { 
				actionDescription = "Compressing Data";
			}
			BinList binList = selectedTrack.getBinList();
			notifyActionStart(actionDescription, 1);
			if (binList.isCompressed()) {
				binList.uncompress();
			} else {
				binList.compress();
			}
			return binList;
		}
		return null;
	}
}
