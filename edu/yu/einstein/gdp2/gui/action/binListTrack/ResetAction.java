/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;

/**
 * Reset the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ResetAction extends TrackListAction {

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
	 * Creates an instance of {@link ResetAction}
	 * @param trackList a {@link TrackList}
	 */
	public ResetAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Undoes the last action performed on a {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (trackList.getSelectedTrack() instanceof BinListTrack) {
			final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
			if (selectedTrack != null) {
				new ActionWorker<Void>(trackList, "Reseting") {
					@Override
					protected Void doAction() {
						selectedTrack.resetBinList();
						return null;
					}
					@Override
					protected void doAtTheEnd(Void actionResult) {};
				}.execute();				
			}		
		}
	}
}
