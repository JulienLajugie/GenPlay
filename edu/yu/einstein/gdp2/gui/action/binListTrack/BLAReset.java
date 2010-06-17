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
 * Resets the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAReset extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 4801183816800208961L;	// generated ID
	private static final String 	ACTION_NAME = "Reset";				// action name
	private static final String 	DESCRIPTION = 
		"Reset the selected track";								 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "reset";


	/**
	 * Creates an instance of {@link BLAReset}
	 */
	public BLAReset() {
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
				notifyActionStart("Reseting Track", 1, false);
				selectedTrack.resetBinList();				
			}		
		}
		return null;
	}
}
