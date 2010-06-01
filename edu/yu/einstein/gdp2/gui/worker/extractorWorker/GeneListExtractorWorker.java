/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import generator.GeneListGenerator;

import java.io.File;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * A worker thread that loads a {@link GeneListExtractorWorker}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListExtractorWorker extends ExtractorWorker<GeneList> {
	
	/**
	 * Creates an instance of an {@link GeneListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 */
	public GeneListExtractorWorker(TrackList trackList, String logFile, File fileToExtract) {
		super(trackList, logFile, fileToExtract, GeneListGenerator.class, "Loading Gene Track");
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new GeneListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
				notifyActionEnded("Gene Track Loaded");
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the gene track");
		}
	}
	

	@Override
	public GeneList generateList() throws InvalidChromosomeException {
		return ((GeneListGenerator)extractor).toGeneList();
	}

}
