/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.extractorWorker.ChromosomeWindowListExtractorWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Sets the stripes on the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class LoadStripesAction extends TrackListAction {

	private static final long serialVersionUID = -900140642202561851L; // generated ID
	private static final String ACTION_NAME = "Load Stripes"; // action name
	private static final String DESCRIPTION = "Load stripes on the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "loadStripes";


	/**
	 * Creates an instance of {@link LoadStripesAction}
	 * @param trackList a {@link TrackList}
	 */
	public LoadStripesAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Sets the stripes on the selected track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track selectedTrack = trackList.getSelectedTrack();
		if (selectedTrack != null) {
			String logFile = trackList.getConfigurationManager().getLogFile();
			String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Stripe File", defaultDirectory, Utils.getStripeFileFilters());
			if (selectedFile != null) {
				new ChromosomeWindowListExtractorWorker(trackList, logFile, selectedFile, trackList.getChromosomeManager()).execute();
			}
		}
	}
}
