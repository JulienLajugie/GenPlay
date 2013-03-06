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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.track;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.IO.extractor.ReadLengthAndShiftHandler;
import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.core.generator.ScoredChromosomeWindowListGenerator;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog.NewCurveLayerDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a {@link SCWLayer} to the selected track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TAAddSCWLayer extends TrackListActionExtractorWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -7836987725953057426L;								// generated ID
	private static final String ACTION_NAME = "Add Variable Window Layer";							// action name
	private static final String DESCRIPTION = "Add a layer displaying windows of variable sizes"; 	// tooltip
	private ScoreCalculationMethod 	scoreCalculation = null;										// method of calculation for the score
	private Strand					strand = null;													// strand to extract
	private int						strandShift = 0;												// position shift on a strand
	private int 					readLength = 0;													// user specified length of the reads (0 to keep the original length)


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddSCWLayer.class.getName();


	/**
	 * Creates an instance of {@link TAAddSCWLayer}
	 */
	public TAAddSCWLayer() {
		super(ScoredChromosomeWindowListGenerator.class);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Variable Window Layer", defaultDirectory, Utils.getReadableSCWFileFilters(), true);
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}


	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		boolean isStrandNeeded = extractor instanceof StrandedExtractor;
		NewCurveLayerDialog ncld = new NewCurveLayerDialog(null, false, false, false, false, isStrandNeeded, true, true);
		if (ncld.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
			selectedChromo = ncld.getSelectedChromosomes();
			// if not all the chromosomes are selected we need
			// to ask the user if the file is sorted or not
			if (!Utils.allChromosomeSelected(selectedChromo)) {
				int dialogResult = JOptionPane.showConfirmDialog(getRootPane(), "GenPlay can accelerate the loading if you know that your file is sorted by chromosome." +
						"Press yes only if you know that your file is sorted.\n" +
						"If you press yes and your file is not sorted, the file may load incompletely, leading to a loss of valuable information.\n" +
						"The chromosomes must be ordered the same way it is ordered in the chromosome selection combo-box.\n\n" +
						"Is your file sorted?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (dialogResult == JOptionPane.YES_OPTION) {
					extractor.setFileSorted(true);
				} else if (dialogResult == JOptionPane.NO_OPTION) {
					extractor.setFileSorted(false);
				} else if (dialogResult == JOptionPane.CLOSED_OPTION) {
					throw new InterruptedException();
				}
			}
			extractor.setSelectedChromosomes(selectedChromo);
			if (isStrandNeeded) {
				strand = ncld.getStrandToExtract();
				strandShift = ncld.getStrandShiftValue();
				readLength = ncld.getReadLengthValue();
				((StrandedExtractor) extractor).selectStrand(strand);
				((StrandedExtractor) extractor).setReadLengthAndShiftHandler(new ReadLengthAndShiftHandler(strandShift, readLength));
			}
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				genomeName = ncld.getGenomeName();
				alleleType = ncld.getAlleleType();
				extractor.setGenomeName(genomeName);
				extractor.setAlleleType(alleleType);
			}
		} else {
			throw new InterruptedException();
		}
	}


	@Override
	protected ScoredChromosomeWindowList generateList() throws Exception {
		notifyActionStop();
		if (((ScoredChromosomeWindowListGenerator)extractor).overlapped()){
			NewCurveLayerDialog nctd = new NewCurveLayerDialog(name, true, false, false, true, false, false,  false);
			if (nctd.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
				name = nctd.getLayerName();
				scoreCalculation = nctd.getScoreCalculationMethod();
				notifyActionStart("Generating Layer", SimpleScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(scoreCalculation);
			}
		} else {
			NewCurveLayerDialog nctd = new NewCurveLayerDialog(name, true, false, false, false, false, false,  false);
			if (nctd.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
				name = nctd.getLayerName();
				notifyActionStart("Generating Layer", SimpleScoredChromosomeWindowList.getCreationStepCount(), true);
				return ((ScoredChromosomeWindowListGenerator)extractor).toScoredChromosomeWindowList(null);
			}
		}
		throw new InterruptedException();
	}


	@Override
	public void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			SCWLayer newLayer = new SCWLayer(selectedTrack, actionResult, fileToExtract.getName());
			newLayer.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Colors.GREY);
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
			if (readLength != 0) {
				history += ", Read Length = " + readLength +"bp";
			}
			if (!history.isEmpty()) {
				newLayer.getHistory().add(history, Colors.GREY);
			}
			selectedTrack.getLayers().add(newLayer);
			selectedTrack.setActiveLayer(newLayer);
		}
	}
}

