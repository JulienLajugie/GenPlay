/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.curve;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;


/**
 * Asks the user a minimum value for the score 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoreMinAction extends TrackListAction {

	private static final long serialVersionUID = -876439325041410609L;	// generated ID
	private static final String ACTION_NAME = "Minimum Score"; // action name
	private static final String DESCRIPTION = "Minimum score value displayed"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "minimumScore";


	/**
	 * Creates an instance of {@link ScoreMinAction}
	 * @param trackList a {@link TrackList}
	 */
	public ScoreMinAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Asks the user a minimum value for the score
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CurveTrack selectedTrack = (CurveTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {		
			double currentMin = selectedTrack.getYMin();
			double currentMax = selectedTrack.getYMax();
			Number newMin = NumberOptionPane.getValue(getRootPane(), "Minimum score", "Enter a minimum bound for the score", new DecimalFormat("#.#"), Double.NEGATIVE_INFINITY, currentMax, currentMin);
			if ((newMin != null) && (newMin.doubleValue() != currentMin)) {
				selectedTrack.setYMin(newMin.doubleValue());
			}
		}
	}
}
