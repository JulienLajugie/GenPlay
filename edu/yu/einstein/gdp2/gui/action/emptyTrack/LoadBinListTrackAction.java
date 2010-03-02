/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.extractorWorker.BinListExtractorWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link BinListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class LoadBinListTrackAction extends TrackListAction {

	private static final long serialVersionUID = -3974211916629578143L;	// generated ID
	private static final String ACTION_NAME = "Load Fixed Window Track"; // action name
	private static final String DESCRIPTION = "Load a track with a fixed window size"; // tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "loadBinList";


	/**
	 * Creates an instance of {@link LoadBinListTrackAction}
	 * @param trackList a {@link TrackList}
	 */
	public LoadBinListTrackAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link BinListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
		String logFile = trackList.getConfigurationManager().getLogFile();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Fixed Window Track", defaultDirectory, Utils.getBinListFileFilters());
		if (selectedFile != null) {
			new BinListExtractorWorker(trackList, logFile, selectedFile, trackList.getChromosomeManager()).execute();
		}
	}
}
