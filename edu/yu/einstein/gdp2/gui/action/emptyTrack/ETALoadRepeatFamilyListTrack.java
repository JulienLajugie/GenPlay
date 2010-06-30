/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.generator.RepeatFamilyListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.track.RepeatFamilyListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link RepeatFamilyListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadRepeatFamilyListTrack extends TrackListActionExtractorWorker<RepeatFamilyList> {

	private static final long serialVersionUID = -6264760599336397028L;	// generated ID
	private static final String ACTION_NAME = "Load Repeat Track";		// action name
	private static final String DESCRIPTION = "Load a track showing the repeats";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadRepeatFamilyListTrack";


	/**
	 * Creates an instance of {@link ETALoadRepeatFamilyListTrack}
	 */
	public ETALoadRepeatFamilyListTrack() {
		super(RepeatFamilyListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Repeat Track", defaultDirectory, Utils.getReadableRepeatFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}


	@Override
	protected RepeatFamilyList generateList() throws Exception {
		return ((RepeatFamilyListGenerator)extractor).toRepeatFamilyList();
	}


	@Override
	public void doAtTheEnd(RepeatFamilyList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			RepeatFamilyListTrack newTrack = new RepeatFamilyListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
}
