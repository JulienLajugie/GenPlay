/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Removes the stripes on the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATARemoveStripes extends TrackListAction {

	private static final long serialVersionUID = -5710099956650330270L; // generated ID
	private static final String ACTION_NAME = "Remove Stripes"; // action name
	private static final String DESCRIPTION = "Remove stripes on the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATARemoveStripes";


	/**
	 * Creates an instance of {@link ATARemoveStripes}
	 */
	public ATARemoveStripes() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Removes the stripes on the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			selectedTrack.setStripes(null);
		}
	}
}
