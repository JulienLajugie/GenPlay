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
 * Copies the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATACopy extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = -1436541643590614314L; // generated ID
	private static final String ACTION_NAME = "Copy"; 					// action name
	private static final String DESCRIPTION = "Copy the selected track";// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_C; 				// mnemonic key

	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATACopy";


	/**
	 * Creates an instance of {@link ATACopy}
	 */
	public ATACopy() {
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
			notifyActionStart("Copying Track #" + selectedTrack.getTrackNumber(), 1);
			getTrackList().copyTrack();
		}
		return null;
	}
}
