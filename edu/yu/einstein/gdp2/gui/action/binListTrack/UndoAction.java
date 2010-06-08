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
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Undoes the last action performed on  the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class UndoAction extends TrackListAction {

	private static final long serialVersionUID = 7486534068270241965L; 	// generated ID
	private static final String 	ACTION_NAME = "Undo";				// action name
	private static final String 	DESCRIPTION = 
		"Undo the last action performed on the selected track"; 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK); 


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "undo";


	/**
	 * Creates an instance of {@link UndoAction}
	 */
	public UndoAction() {
		super();
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
		if (getTrackList().getSelectedTrack() instanceof BinListTrack) {
			final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				new ActionWorker<Void>(getTrackList(), "Undoing") {
					@Override
					protected Void doAction() {
						selectedTrack.undo();
						return null;
					}
					@Override
					protected void doAtTheEnd(Void actionResult) {};
				}.execute();		
			}
		}
	}
}
