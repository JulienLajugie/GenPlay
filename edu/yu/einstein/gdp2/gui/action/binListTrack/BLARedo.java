/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Redoes the last action performed on the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLARedo extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6836640129258678255L; 	// generated ID
	private static final String 	ACTION_NAME = "Redo";				// action name
	private static final String 	DESCRIPTION = 
		"Redo the last action performed on the selected track"; 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "redo";


	/**
	 * Creates an instance of {@link BLARedo}
	 */
	public BLARedo() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getTrackList().getSelectedTrack() instanceof BinListTrack) {
			BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				notifyActionStart("Redoing", 1, false);
				selectedTrack.redo();
			}	
		}
		return null;
	}
}
