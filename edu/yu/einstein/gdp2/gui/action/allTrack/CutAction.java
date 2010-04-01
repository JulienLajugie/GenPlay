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
 * Cuts the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class CutAction extends TrackListAction {

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
	public static final String ACTION_KEY = "cut";


	/**
	 * Creates an instance of {@link CutAction}
	 * @param trackList a {@link TrackList}
	 */
	public CutAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Cuts the selected track 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = trackList.getSelectedTrack();
		if (selectedTrack != null) {
			// thread for the action
			new ActionWorker<Void>(trackList, "Cutting Track #" + selectedTrack.getTrackNumber()) {
				@Override
				protected Void doAction() {
					trackList.cutTrack();
					return null;
				}
				@Override
				protected void doAtTheEnd(Void actionResult) {}
			}.execute();	
		}
	}
}
