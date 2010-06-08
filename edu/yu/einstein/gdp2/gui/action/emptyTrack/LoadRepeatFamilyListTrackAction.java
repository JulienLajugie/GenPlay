/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.RepeatFamilyListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.extractorWorker.RepeatFamilyListExtractorWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link RepeatFamilyListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class LoadRepeatFamilyListTrackAction extends TrackListAction {

	private static final long serialVersionUID = -6264760599336397028L;	// generated ID
	private static final String ACTION_NAME = "Load Repeat Track";		// action name
	private static final String DESCRIPTION = "Load a track showing the repeats";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "loadRepeatList";


	/**
	 * Creates an instance of {@link LoadRepeatFamilyListTrackAction}
	 */
	public LoadRepeatFamilyListTrackAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link RepeatFamilyListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		String logFile = ConfigurationManager.getInstance().getLogFile();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Repeat Track", defaultDirectory, Utils.getReadableRepeatFileFilters());
		if (selectedFile != null) {
			new RepeatFamilyListExtractorWorker(getTrackList(), logFile, selectedFile).execute();
		}
	}
}
