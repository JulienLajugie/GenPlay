/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.extractorWorker;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListGenerator;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.exception.InvalidChromosomeException;
import yu.einstein.gdp2.exception.ManagerDataNotLoadedException;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;

/**
 * A worker thread that loads a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListExtractorWorker extends ExtractorWorker<BinListGenerator, BinList> {

	private Number 					binSize = 0;		// Size of the bins of the BinList
	private ScoreCalculationMethod 	scoreCalculation = 
		ScoreCalculationMethod.AVERAGE;					// Method of calculation of the score of the BinList
	private DataPrecision 			precision = 
		DataPrecision.PRECISION_32BIT;					// Precision of the Data
	
	
	/**
	 * Creates an instance of an {@link BinListExtractorWorker}
	 * @param trackList a {@link TrackList}
	 * @param logFile a {@link File} for the log of the extraction
	 * @param fileToExtract file to extract
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
	public BinListExtractorWorker(TrackList trackList, String logFile, File fileToExtract, ChromosomeManager chromosomeManager) {
		super(trackList, logFile, fileToExtract, chromosomeManager, BinListGenerator.class, "Loading Fixed Window Track");
	}


	@Override
	public void doAtTheEnd() {
		try {
			BinList resultList = this.get(); 
			if (resultList != null) {
				final int selectedTrackIndex = trackList.getSelectedTrackIndex();
				final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
				BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, trackList.getChromosomeManager(), resultList);
				// write in the history
				String history = "Bin Size = " + resultList.getBinSize() + "bp, Precision = " + resultList.getPrecision();
				if (scoreCalculation != null) {
					history += ", Method of Calculation = " + scoreCalculation;
				}
				newTrack.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Color.GRAY);
				newTrack.getHistory().add(history, Color.GRAY);
				trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), name, stripes);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "Error while loading the fixed window track");
		}
	}


	@Override
	public BinList generateList() throws ManagerDataNotLoadedException, InvalidChromosomeException {
		if (((BinListGenerator)extractor).isBinSizeNeeded()) {
			notifyActionEnded("");
			binSize = NumberOptionPane.getValue(trackList.getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize == null) {
				return null;
			}
		}
		if (((BinListGenerator)extractor).isCriterionNeeded()) {	
			notifyActionEnded("");
			scoreCalculation = Utils.chooseScoreCalculation(trackList.getRootPane());
			if (scoreCalculation == null) {
				return null;
			}						
		} else {
			scoreCalculation = null;
		}
		if (((BinListGenerator)extractor).isPrecisionNeeded()) {	
			notifyActionEnded("");
			precision = Utils.choosePrecision(trackList.getRootPane());
			if (precision == null) {
				return null;
			}
		}
		notifyActionStarted("Loading Fixed Window Track");
		return ((BinListGenerator)extractor).toBinList(binSize.intValue(), precision, scoreCalculation);
	}
}
