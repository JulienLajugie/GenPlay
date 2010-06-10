/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;


import java.io.File;

import yu.einstein.gdp2.core.generator.RepeatFamilyListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.track.RepeatFamilyListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * A worker thread that loads a {@link RepeatFamilyList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListExtractorWorker extends ExtractorWorker<RepeatFamilyList> {

	/**
	 * Creates an instance of an {@link RepeatFamilyListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 */
	public RepeatFamilyListExtractorWorker(TrackList trackList, String logFile, File fileToExtract) {
		super(trackList, logFile, fileToExtract, RepeatFamilyListGenerator.class, "Loading Repeat Track");
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				Track newTrack = new RepeatFamilyListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, this.get());
				trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
				//notifyActionEnded("Repeat Track Loaded");
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the repeat track");
		}
	}
	
	
	@Override
	public RepeatFamilyList generateList() throws Exception {
		return ((RepeatFamilyListGenerator)extractor).toRepeatFamilyList();
	}
}
