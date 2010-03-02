/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import java.io.File;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowListGenerator;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;

/**
 * A worker thread that loads a {@link ChromosomeWindowList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ChromosomeWindowListExtractorWorker extends ExtractorWorker<ChromosomeWindowListGenerator, ChromosomeWindowList> {

	/**
	 * Creates an instance of an {@link ChromosomeWindowListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public ChromosomeWindowListExtractorWorker(TrackList trackList,	String logFile, File fileToExtract,	ChromosomeManager chromosomeManager) {
		super(trackList, logFile, fileToExtract, chromosomeManager,	ChromosomeWindowListGenerator.class);
	}

	
	@Override
	public void doAtTheEnd() {
		try {
			if (this.get() != null) {
				trackList.getSelectedTrack().setStripes(this.get());
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the stripe");
		}
	}
	

	@Override
	public ChromosomeWindowList generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		return ((ChromosomeWindowListGenerator)extractor).toChromosomeWindowList();
	}

}
