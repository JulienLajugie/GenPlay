/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;

/**
 * Compresses / uncompresses the data of the selected track 
 * @author Julien Lajugie
 * @version 0.1
 */
public class CompressionAction extends TrackListAction {

	private static final long serialVersionUID = 5156554955152029111L;	// generated ID
	private static final String 	ACTION_NAME = "Compress";			// action name
	private static final String 	DESCRIPTION = 
		"Compress the data of the selected track";					// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "CompressionAction";


	/**
	 * Creates an instance of {@link CompressionAction}
	 * @param trackList a {@link TrackList}
	 */
	public CompressionAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Compresses / uncompresses the data of the selected track 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			String actionDescription = new String();
			if (selectedTrack.getBinList().isCompressed()) {
				actionDescription = "Uncompressing Data";
			} else { 
				actionDescription = "Compressing Data";
			}
			// thread for the action
			new ActionWorker<BinList>(trackList, actionDescription) {
				@Override
				protected BinList doAction() {
					BinList binList = selectedTrack.getBinList();
					if (binList.isCompressed()) {
						binList.uncompress();
					} else {
						binList.compress();
					}
					return binList;
				}
				@Override
				protected void doAtTheEnd(BinList resultList) {
					if (resultList != null) {
						String description = new String();
						if (resultList.isCompressed()) {
							description = "Compressed Mode On";
						} else {
							description = "Compressed Mode Off";
						}
						selectedTrack.setBinList(resultList,  description);
					}
				}
			}.execute();
		}
	}
}
