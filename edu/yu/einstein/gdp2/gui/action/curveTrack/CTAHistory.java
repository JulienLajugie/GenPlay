/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.curveTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.HistoryDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.util.History;

/**
 * Shows the history of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class CTAHistory extends TrackListAction {

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
	public static final String ACTION_KEY = "CTAHistory";


	/**
	 * Creates an instance of {@link CTAHistory}
	 */
	public CTAHistory() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getTrackList().getSelectedTrack() instanceof CurveTrack<?>) {
			CurveTrack<?> selectedTrack = (CurveTrack<?>) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				String trackName = selectedTrack.getName();
				History history = selectedTrack.getHistory();
				HistoryDialog.showHistoryDialog(getRootPane(), trackName, history);
			}		
		}
	}
}
