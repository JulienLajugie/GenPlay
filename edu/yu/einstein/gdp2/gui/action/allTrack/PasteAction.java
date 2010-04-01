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
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Pastes the copied/cut track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PasteAction extends TrackListAction {

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
	public static final String ACTION_KEY = "paste";


	/**
	 * Creates an instance of {@link PasteAction}
	 * @param trackList a {@link TrackList}
	 */
	public PasteAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Pastes the copied/cut track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = trackList.getSelectedTrack();
		if (selectedTrack != null) {
			new ActionWorker<Void>(trackList, "Pasting Clipboard on Track #" + selectedTrack.getTrackNumber()) {
				@Override
				protected Void doAction() {
					trackList.pasteCopiedTrack();
					return null;
				}
				@Override
				protected void doAtTheEnd(Void actionResult) {}
			}.execute();
		}
	}
}
