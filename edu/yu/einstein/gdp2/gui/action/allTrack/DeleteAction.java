/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Deletes the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DeleteAction extends TrackListAction {

	private static final long serialVersionUID = -832588159357836362L; 		// generated ID
	private static final String ACTION_NAME = "Delete"; 					// action name
	private static final String DESCRIPTION = "Delete the selected track"; 	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_D; 					// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "delete";


	/**
	 * Creates an instance of {@link DeleteAction}
	 */
	public DeleteAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Deletes the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			getTrackList().deleteTrack();
		}
	}
}
