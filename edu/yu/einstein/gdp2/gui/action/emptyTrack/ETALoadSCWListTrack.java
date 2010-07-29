/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.Color;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.dialog.newCurveTrackDialog.NewCurveTrackDialog;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link SCWListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadSCWListTrack extends TrackListActionExtractorWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -7836987725953057426L;	// generated ID
	private static final String ACTION_NAME = "Load Variable Window Track";	// action name
	private static final String DESCRIPTION = "Load a track with variable window sizes"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadSCWListTrack";


	/**
	 * Creates an instance of {@link ETALoadSCWListTrack}
	 */
	public ETALoadSCWListTrack() {
		super(ScoredChromosomeWindowListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Variable Window Track", defaultDirectory, Utils.getReadableSCWFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}


	@Override
	protected ScoredChromosomeWindowList generateList() throws Exception {
		notifyActionStop();
		if (((ScoredChromosomeWindowListGenerator)extractor).overlapped()){
			NewCurveTrackDialog nctd = new NewCurveTrackDialog(name, false, false, true, false);
			if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
				name = nctd.getTrackName();
				notifyActionStart("Generating Track", ScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(nctd.getScoreCalculationMethod());
			}
		} else {
			NewCurveTrackDialog nctd = new NewCurveTrackDialog(name, false, false, true, false);
			if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
				name = nctd.getTrackName();
				notifyActionStart("Generating Track", ScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(null);
			}
		}
		return null;
	}


	@Override
	public void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			SCWListTrack newTrack = new SCWListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			newTrack.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Color.GRAY);
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
}

