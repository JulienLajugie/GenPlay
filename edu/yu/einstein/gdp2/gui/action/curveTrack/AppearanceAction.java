/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.curveTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.TrackAppearanceOptionPane;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Opens the appearance menu 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class AppearanceAction extends TrackListAction {

	private static final long serialVersionUID = -6622367991983310373L;	// generated ID
	private static final String ACTION_NAME = "Appearance"; // action name
	private static final String DESCRIPTION = "Change the appearance of the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "appearance";


	/**
	 * Creates an instance of {@link AppearanceAction}
	 * @param trackList a {@link TrackList}
	 */
	public AppearanceAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Opens the appearance menu 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CurveTrack selectedTrack = (CurveTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			boolean showHorizontalLines = selectedTrack.isShowHorizontalGrid();
			int xLineCount = selectedTrack.getVerticalLineCount();
			int yLineCount = selectedTrack.getHorizontalLinesCount();
			Color trackColor = selectedTrack.getTrackColor();
			GraphicsType trackType = selectedTrack.getTypeOfGraph();

			TrackAppearanceOptionPane taop = new TrackAppearanceOptionPane(showHorizontalLines, xLineCount, yLineCount, trackColor, trackType);

			if (taop.showTrackConfiguration(getRootPane()) == TrackAppearanceOptionPane.APPROVE_OPTION) {
				if (taop.getShowHorizontalGrid() != showHorizontalLines) {
					selectedTrack.setShowHorizontalGrid(taop.getShowHorizontalGrid());
				}
				if (taop.getXLineCount() != xLineCount) {
					selectedTrack.setVerticalLineCount(taop.getXLineCount());			
				}
				if (taop.getYLineCount() != yLineCount) {
					selectedTrack.setHorizontalLinesCount(taop.getYLineCount());
				}
				if (taop.getCurvesColor() != trackColor) {
					selectedTrack.setTrackColor(taop.getCurvesColor());
				}
				if (taop.getGraphicsType() != trackType) {
					selectedTrack.setTypeOfGraph(taop.getGraphicsType());
				}
			}
		}
	}
}
