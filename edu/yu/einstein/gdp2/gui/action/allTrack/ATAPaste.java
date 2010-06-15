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
 * Pastes the copied/cut track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATAPaste extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 1603320424932972117L; 			// generated ID
	private static final String ACTION_NAME = "Paste"; 							// action name
	private static final String DESCRIPTION = "Paste the last copied/cut track";// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAPaste";


	/**
	 * Creates an instance of {@link ATAPaste}
	 */
	public ATAPaste() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Void processAction() throws Exception {
		Track selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			notifyActionStart("Pasting Clipboard on Track #" + selectedTrack.getTrackNumber(), 1);
			getTrackList().pasteCopiedTrack();
		}
		return null;
	}
}
