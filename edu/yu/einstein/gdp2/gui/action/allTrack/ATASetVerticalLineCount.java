/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Sets the number of vertical lines showed on a track
 * @author Julien Lajugie
 * @version 0.1
 */
public class ATASetVerticalLineCount extends TrackListAction {

	private static final long serialVersionUID = -922309611412994050L;	// generated ID
	private static final String ACTION_NAME = "Set Vertical Line Count"; // action name
	private static final String DESCRIPTION = "Set the number of vertical lines showed on a track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATASetVerticalLineCount";


	/**
	 * Creates an instance of {@link ATASetVerticalLineCount}
	 */
	public ATASetVerticalLineCount() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			int currentLineCount = selectedTrack.getVerticalLineCount();
			Number lineCountNumber = NumberOptionPane.getValue(getRootPane(), "Vertical Line Count", "Set the number of vertical lines to display", new DecimalFormat("###"), 0, 100, currentLineCount);
			if ((lineCountNumber != null) && (lineCountNumber.intValue() != currentLineCount)) {
				selectedTrack.setVerticalLineCount(lineCountNumber.intValue());
			}
		}		
	}
}
