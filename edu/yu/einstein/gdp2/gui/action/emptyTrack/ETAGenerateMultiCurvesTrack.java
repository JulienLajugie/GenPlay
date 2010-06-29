/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.track.MultiCurvesTrack;
import yu.einstein.gdp2.gui.track.Track;

/**
 * Shows the content of multiple tracks in a new one
 * @author Julien Lajugie
 * @version 0.1
 */
public class ETAGenerateMultiCurvesTrack extends TrackListAction {

	private static final long serialVersionUID = -7961676275489605138L; // generated ID
	private static final String ACTION_NAME = "Generate Multi Curves Track"; // action name
	private static final String DESCRIPTION = "Show the content of multiple fixed or vaiable window tracks in the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETAGenerateMultiCurvesTrack";


	/**
	 * Creates an instance of {@link ETAGenerateMultiCurvesTrack}
	 */
	public ETAGenerateMultiCurvesTrack() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Shows the content of multiple tracks in a new one
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track[] selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getCurveTracks());
		if (selectedTracks != null) {
			if (selectedTracks.length > 1) {
				CurveTrack<?>[] curveTracks = new CurveTrack[selectedTracks.length];
				String trackName = "";
				for (int i = 0; i < curveTracks.length; i++) {
					curveTracks[i] = (CurveTrack<?>) selectedTracks[i];
					if (i != curveTracks.length - 1) {
						trackName += selectedTracks[i].getName() + ", ";
					} else {
						trackName += selectedTracks[i].getName();
					}
				}
				int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
				ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
				MultiCurvesTrack newTrack = new MultiCurvesTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, curveTracks);
				getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), trackName, stripes);	
			} else {
				JOptionPane.showMessageDialog(getRootPane(), "You must select at least two tracks", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
