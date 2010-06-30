/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Cuts the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATACut extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 5387375446702872880L;  // generated ID
	private static final String ACTION_NAME = "Cut"; 					// action name
	private static final String DESCRIPTION = "Cut the selected track"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_U; 				// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATACut";


	/**
	 * Creates an instance of {@link ATACut}
	 */
	public ATACut() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Void processAction() throws Exception {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			notifyActionStart("Cutting Track #" + selectedTrack.getTrackNumber(), 1, false);
			getTrackList().cutTrack();
		}
		return null;
	}
}
