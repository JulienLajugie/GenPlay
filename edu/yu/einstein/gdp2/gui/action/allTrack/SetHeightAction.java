/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Sets the height of the the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SetHeightAction extends TrackListAction {

	private static final long serialVersionUID = 9169503160914933578L; 					// generated ID
	private static final String ACTION_NAME = "Set Height"; 							// action name
	private static final String DESCRIPTION = "Set the height of the selected track"; 	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_H; 								// mnemonic key
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "setHeight";


	/**
	 * Creates an instance of {@link SetHeightAction}
	 * @param trackList a {@link TrackList}
	 */
	public SetHeightAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Sets the height of the the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = trackList.getSelectedTrack();
		if (selectedTrack != null) {
			int minimumHeight = selectedTrack.getMinimumSize().height;
			int currentHeight = selectedTrack.getSize().height;
			Number newPreferred = NumberOptionPane.getValue(getRootPane(), "Default Height", "Enter a new default size for the selected tracks:", new DecimalFormat("#"), minimumHeight, 500d, currentHeight);
			if (newPreferred != null) {
				selectedTrack.setPreferredHeight(newPreferred.intValue());
			}	
		}
	}
}
