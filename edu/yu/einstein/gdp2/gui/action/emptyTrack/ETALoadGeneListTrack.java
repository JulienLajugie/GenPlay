/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.generator.GeneListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link GeneListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadGeneListTrack extends TrackListActionExtractorWorker<GeneList> {

	private static final long serialVersionUID = -6264760599336397028L;	// generated ID
	private static final String 	ACTION_NAME = "Load Gene Track";	// action name
	private static final String 	DESCRIPTION = "Load a track showing the genes";	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadGeneListTrack";


	/**
	 * Creates an instance of {@link ETALoadGeneListTrack}
	 */
	public ETALoadGeneListTrack() {
		super(GeneListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Gene Track", defaultDirectory, Utils.getReadableGeneFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}

	
	@Override
	protected GeneList generateList() throws Exception {
		return ((GeneListGenerator)extractor).toGeneList();
	}
	

	@Override
	public void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			Track newTrack = new GeneListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
}
