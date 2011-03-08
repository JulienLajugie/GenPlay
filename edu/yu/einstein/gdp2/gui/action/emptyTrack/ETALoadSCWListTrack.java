/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.Color;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.extractor.StrandedExtractor;
import yu.einstein.gdp2.core.generator.ScoredChromosomeWindowListGenerator;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionExtractorWorker;
import yu.einstein.gdp2.gui.dialog.newCurveTrackDialog.NewCurveTrackDialog;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.TrackColor;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link SCWListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ETALoadSCWListTrack extends TrackListActionExtractorWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -7836987725953057426L;						// generated ID
	private static final String ACTION_NAME = "Load Variable Window Track";					// action name
	private static final String DESCRIPTION = "Load a track with variable window sizes"; 	// tooltip
	private ScoreCalculationMethod 	scoreCalculation = null;								// method of calculation for the score
	private Strand					strand = null;											// strand to extract
	private int						strandShift = 0;										// position shift on a strand


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadSCWListTrack";


	/**
	 * Creates an instance of {@link ETALoadSCWListTrack}
	 */
	public ETALoadSCWListTrack() {
		super(ScoredChromosomeWindowListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Variable Window Track", defaultDirectory, Utils.getReadableSCWFileFilters());
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}
	
	
	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		boolean isStrandNeeded = extractor instanceof StrandedExtractor;
		NewCurveTrackDialog nctd = new NewCurveTrackDialog(null, false, false, false, false, isStrandNeeded, true);
		if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
			extractor.setSelectedChromosomes(nctd.getSelectedChromosomes());
			if (isStrandNeeded) {
				strand = nctd.getStrandToExtract();
				strandShift = nctd.getStrandShiftValue();
				((StrandedExtractor) extractor).selectStrand(strand);
				((StrandedExtractor) extractor).setStrandShift(strandShift);
			}
		} else {
			throw new InterruptedException();
		}
	}


	@Override
	protected ScoredChromosomeWindowList generateList() throws Exception {
		notifyActionStop();
		if (((ScoredChromosomeWindowListGenerator)extractor).overlapped()){
			NewCurveTrackDialog nctd = new NewCurveTrackDialog(name, true, false, false, true, false, false);
			if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
				name = nctd.getTrackName();
				scoreCalculation = nctd.getScoreCalculationMethod();
				notifyActionStart("Generating Track", ScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(scoreCalculation);
			}
		} else {
			NewCurveTrackDialog nctd = new NewCurveTrackDialog(name, true, false, false, false, false, false);
			if (nctd.showDialog(getRootPane()) == NewCurveTrackDialog.APPROVE_OPTION) {
				name = nctd.getTrackName();
				notifyActionStart("Generating Track", ScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(null);
			}
		}
		throw new InterruptedException();
	}


	@Override
	public void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			TrackList trackList = getTrackList();
			int selectedTrackIndex = trackList.getSelectedTrackIndex();
			ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
			SCWListTrack newTrack = new SCWListTrack(trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			newTrack.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Color.GRAY);
			String history = new String(); 
			if (scoreCalculation != null) {
				history += "Method of Calculation = " + scoreCalculation;
			}	
			if (strand != null) {
				history += ", Strand = ";
				if (strand == Strand.FIVE) {
					history += "5'";
				} else {
					history += "3'";
				}
			}
			if (strandShift != 0) {
				history += ", Strand Shift = " + strandShift +"bp";
			}
			if (!history.isEmpty()) {
				newTrack.getHistory().add(history, Color.GRAY);
			}
			newTrack.setTrackColor(TrackColor.getTrackColor());
			trackList.setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), name, stripes);
		}
	}
}

