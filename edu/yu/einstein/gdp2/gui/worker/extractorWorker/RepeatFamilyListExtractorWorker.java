/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import java.io.File;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.track.RepeatFamilyListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;

/**
 * A worker thread that loads a {@link RepeatFamilyList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListExtractorWorker extends ExtractorWorker<RepeatFamilyListGenerator, RepeatFamilyList> {

	/**
	 * Creates an instance of an {@link RepeatFamilyListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public RepeatFamilyListExtractorWorker(TrackList trackList, String logFile, File fileToExtract, ChromosomeManager chromosomeManager) {
		super(trackList, logFile, fileToExtract, chromosomeManager, RepeatFamilyListGenerator.class, "Loading Repeat Track");
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new RepeatFamilyListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), name, stripes);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the repeat track");
		}
	}
	
	
	@Override
	public RepeatFamilyList generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return ((RepeatFamilyListGenerator)extractor).toRepeatFamilyList();
	}
}
