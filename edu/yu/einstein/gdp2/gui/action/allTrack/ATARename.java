/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Renames the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATARename extends TrackListAction {

	private static final long serialVersionUID = -6475180772964541278L; 	// generated ID
	private static final String ACTION_NAME = "Rename"; 					// action name
	private static final String DESCRIPTION = "Rename the selected track"; 	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_R; 					// mnemonic key
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATARename";


	/**
	 * Creates an instance of {@link ATARename}
	 */
	public ATARename() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Renames the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			String message = "Enter a name for the track #" + selectedTrack.getTrackNumber() + ":";
			String currentName = selectedTrack.getName();
			String newName = (String) JOptionPane.showInputDialog(getRootPane(), message, "Track Name", JOptionPane.QUESTION_MESSAGE, null, null, currentName);
			if (newName != null) {
				selectedTrack.setName(newName);
				selectedTrack.repaint();
			}
		}
	}
}
