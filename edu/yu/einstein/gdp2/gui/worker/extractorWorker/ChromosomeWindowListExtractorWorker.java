/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import generator.ChromosomeWindowListGenerator;

import java.io.File;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * A worker thread that loads a {@link ChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeWindowListExtractorWorker extends ExtractorWorker<ChromosomeWindowList> {

	/**
	 * Creates an instance of an {@link ChromosomeWindowListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 */
	public ChromosomeWindowListExtractorWorker(TrackList trackList,	String logFile, File fileToExtract) {
		super(trackList, logFile, fileToExtract, ChromosomeWindowListGenerator.class, "Loading Stripes");
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				trackList.getSelectedTrack().setStripes(this.get());
				notifyActionEnded("Stripes Loaded");
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the stripe");
		}
	}
	

	@Override
	public ChromosomeWindowList generateList() throws InvalidChromosomeException {
		return ((ChromosomeWindowListGenerator)extractor).toChromosomeWindowList();
	}
}
