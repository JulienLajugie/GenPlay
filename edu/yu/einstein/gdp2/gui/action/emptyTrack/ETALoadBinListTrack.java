/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.Color;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.generator.BinListGenerator;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.dialog.newCurveTrackDialog.NewCurveTrackDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link BinListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadBinListTrack extends TrackListActionExtractorWorker<BinList> {

	private static final long serialVersionUID = -3974211916629578143L;	// generated ID
	private static final String 	ACTION_NAME = "Load Fixed Window Track"; 				// action name
	private static final String 	DESCRIPTION = "Load a track with a fixed window size"; 	// tooltip
	private int 					binSize = 0;											// Size of the bins of the BinList
	private ScoreCalculationMethod 	scoreCalculation = ScoreCalculationMethod.AVERAGE;		// Method of calculation of the score of the BinList
	private DataPrecision 			precision = DataPrecision.PRECISION_32BIT;				// Precision of the Data
	private BinListGenerator 		binListGenerator;										// BinList Generator
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadBinListTrack";


	/**
	 * Creates an instance of {@link ETALoadBinListTrack}
	 */
	public ETALoadBinListTrack() {
		super(BinListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Fixed Window Track", defaultDirectory, Utils.getReadableBinListFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}


	@Override
	protected BinList generateList() throws Exception {
		notifyActionStop();		
		binListGenerator = (BinListGenerator)extractor;
		NewCurveTrackDialog nctd = new NewCurveTrackDialog(name, binListGenerator.isBinSizeNeeded(), binListGenerator.isPrecisionNeeded(), binListGenerator.isCriterionNeeded(), true);
		if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
			name = nctd.getTrackName();
			binSize = nctd.getBinSize();
			scoreCalculation = nctd.getScoreCalculationMethod();
			precision = nctd.getDataPrecision();		
			// if the binSize is known we can find out how many steps will be used
			if (binListGenerator.isBinSizeNeeded()) {
				notifyActionStart("Generating Track", 1 + BinList.getCreationStepCount(binSize), true);
			} else {
				notifyActionStart("Generating Track", 1, true);
			}
			return ((BinListGenerator) extractor).toBinList(binSize, precision, scoreCalculation);
		}
		throw new InterruptedException();
	}


	@Override
	public void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			BinListTrack newTrack = new BinListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			// write in the history
			String history = "Bin Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision();
			if (binListGenerator.isCriterionNeeded()) {
				history += ", Method of Calculation = " + scoreCalculation;
			}
			newTrack.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Color.GRAY);
			newTrack.getHistory().add(history, Color.GRAY);
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
}
