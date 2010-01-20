/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.worker.extractorWorker;

import java.io.File;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.GeneListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;

/**
 * A worker thread that loads a {@link GeneListExtractorWorker}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneListExtractorWorker extends ExtractorWorker<GeneListGenerator, GeneList> {
	
	/**
	 * Creates an instance of an {@link GeneListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public GeneListExtractorWorker(TrackList trackList, String logFile, File fileToExtract, ChromosomeManager chromosomeManager) {
		super(trackList, logFile, fileToExtract, chromosomeManager, GeneListGenerator.class);
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new GeneListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), name, stripes);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the gene track");
		}
	}
	

	@Override
	public GeneList generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return ((GeneListGenerator)extractor).toGeneList();
	}

}
