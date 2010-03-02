/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.HistoryDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.History;

/**
 * Shows the history of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ShowHistoryAction extends TrackListAction {

	private static final long serialVersionUID = 6153915221242216274L;  // generated ID
	private static final String 	ACTION_NAME = "Show History";		// action name
	private static final String 	DESCRIPTION = 
		"Show the history of the selected track";				 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "history";


	/**
	 * Creates an instance of {@link ShowHistoryAction}
	 * @param trackList a {@link TrackList}
	 */
	public ShowHistoryAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Shows the history of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (trackList.getSelectedTrack() instanceof BinListTrack) {
			BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
			if (selectedTrack != null) {
				String trackName = selectedTrack.getName();
				History history = selectedTrack.getHistory();
				HistoryDialog.showHistoryDialog(getRootPane(), trackName, history);
			}		
		}
	}
}
