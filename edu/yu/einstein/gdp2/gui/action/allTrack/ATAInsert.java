/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;


/**
 * Inserts a blank track
 * @author Julien Lajugie
 * @version 0.1
 */
public class ATAInsert extends TrackListAction {

	private static final long serialVersionUID = 775293461948991915L;		// generated ID
	private static final String ACTION_NAME = "Insert"; 					// action name
	private static final String DESCRIPTION = "Insert a blank track " +
			"right above the selected track"; 								// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_I; 					// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAInsert";


	/**
	 * Creates an instance of {@link ATAInsert}
	 */
	public ATAInsert() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Inserts a blank track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getTrackList().getSelectedTrack() != null) {
			int trackIndex = getTrackList().getSelectedTrackIndex();
			getTrackList().insertTrack(trackIndex);
		}
	}
}
