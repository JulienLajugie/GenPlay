/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.action.emptyTrack;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.TrackListAction;
import yu.einstein.gdp2.gui.trackList.worker.extractorWorker.SCWListExtractorWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link SCWListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class LoadSCWListTrackAction extends TrackListAction {

	private static final long serialVersionUID = -7836987725953057426L;	// generated ID
	private static final String ACTION_NAME = "Load Variable Window Track";	// action name
	private static final String DESCRIPTION = "Load a track with variable window sizes"; // tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "loadSCWL";


	/**
	 * Creates an instance of {@link LoadSCWListTrackAction}
	 * @param trackList a {@link TrackList}
	 */
	public LoadSCWListTrackAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link SCWListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
		String logFile = trackList.getConfigurationManager().getLogFile();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Variable Window Track", defaultDirectory);
		if (selectedFile != null) {
			new SCWListExtractorWorker(trackList, logFile, selectedFile, trackList.getChromosomeManager()).execute();
		}
	}
}
