/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;


import java.io.File;

import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * A worker thread that loads a {@link SCWListExtractorWorker}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWListExtractorWorker extends ExtractorWorker<ScoredChromosomeWindowList> {

	/**
	 * Creates an instance of an {@link SCWListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 */
	public SCWListExtractorWorker(TrackList trackList, String logFile, File fileToExtract) {
		super(trackList, logFile, fileToExtract, ScoredChromosomeWindowListGenerator.class, "Loading Variable Chromosome Window Track");
	}


	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new SCWListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
				//notifyActionEnded("Variable Window Track Loaded");
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the variable window track");
		}
	}
	
	
	@Override
	public ScoredChromosomeWindowList generateList() throws Exception {
		return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList();
	}
}
