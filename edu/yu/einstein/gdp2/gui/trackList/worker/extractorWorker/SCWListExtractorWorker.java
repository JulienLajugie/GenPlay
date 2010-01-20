/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.worker.extractorWorker;

import java.io.File;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;

/**
 * A worker thread that loads a {@link SCWListExtractorWorker}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListExtractorWorker extends ExtractorWorker<ScoredChromosomeWindowListGenerator, ScoredChromosomeWindowList> {

	/**
	 * Creates an instance of an {@link SCWListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public SCWListExtractorWorker(TrackList trackList, String logFile, File fileToExtract, ChromosomeManager chromosomeManager) {
		super(trackList, logFile, fileToExtract, chromosomeManager, ScoredChromosomeWindowListGenerator.class);
	}


	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new SCWListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), name, stripes);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the variable window track");
		}
	}
	
	
	@Override
	public ScoredChromosomeWindowList generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList();
	}
}
