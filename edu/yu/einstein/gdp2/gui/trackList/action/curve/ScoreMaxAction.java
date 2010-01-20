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
 * Asks the user a maximum value for the score 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoreMaxAction extends TrackListAction {

	private static final long serialVersionUID = 2695583198943464561L; // generated ID
	private static final String ACTION_NAME = "Maximum Score"; // action name
	private static final String DESCRIPTION = "Maximum score value displayed"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "maximumScore";


	/**
	 * Creates an instance of {@link ScoreMaxAction}
	 * @param trackList a {@link TrackList}
	 */
	public ScoreMaxAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Asks the user a maximum value for the score 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CurveTrack selectedTrack = (CurveTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			double currentMin = selectedTrack.getYMin();
			double currentMax = selectedTrack.getYMax();
			Number newMax = NumberOptionPane.getValue(getRootPane(), "Maximum score", "Enter a maximum bound for the score", new DecimalFormat("#.#"), currentMin, Double.POSITIVE_INFINITY, currentMax);
			if ((newMax != null) && (newMax.doubleValue() != currentMax)) {
				selectedTrack.setYMax(newMax.doubleValue());
			}
		}
	}
}
