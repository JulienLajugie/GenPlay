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

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.core.IO.utils.ChromosomesSelector;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWListFactory;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog.NewCurveLayerDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.GenericSCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a {@link GenericSCWLayer} to the selected track
 * @author Julien Lajugie
 */
public final class TAAddSCWLayer extends TrackListActionExtractorWorker<SCWList> {

	private static final long serialVersionUID = -7836987725953057426L;								// generated ID
	private static final String ACTION_NAME = "Add Variable Window Layer";							// action name
	private static final String DESCRIPTION = "Add a layer displaying windows of variable sizes"; 	// tooltip
	private ScoreOperation 			scoreCalculation = null;										// method of calculation for the score
	private Strand					strand = null;													// strand to extract
	private ScorePrecision			scorePrecision = null;											// precision of the score
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
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void doAtTheEnd(SCWList actionResult) {
		if (actionResult != null) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			GenericSCWLayer newLayer = new GenericSCWLayer(selectedTrack, actionResult, fileToExtract.getName());
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


	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		boolean isStrandNeeded = extractor instanceof StrandedExtractor;
		NewCurveLayerDialog ncld = new NewCurveLayerDialog(null, false, false, false, false, isStrandNeeded, true, true);
		if (ncld.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
			selectedChromo = ncld.getSelectedChromosomes();
			// if not all the chromosomes are selected we need
			// to ask the user if the file is sorted or not
			extractor.setChromosomeSelector(new ChromosomesSelector(selectedChromo));
			if (isStrandNeeded) {
				strand = ncld.getStrandToExtract();
				strandShift = ncld.getStrandShiftValue();
				readLength = ncld.getReadLengthValue();
				StrandedExtractorOptions strandedExtractorOptions = new StrandedExtractorOptions(strand, strandShift, readLength);
				((StrandedExtractor) extractor).setStrandedExtractorOptions(strandedExtractorOptions);
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
	protected SCWList generateList() throws Exception {
		notifyActionStop();
		NewCurveLayerDialog nctd = new NewCurveLayerDialog(name, true, false, true, true, false, false,  false);
		if (nctd.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
			name = nctd.getLayerName();
			scoreCalculation = nctd.getScoreCalculationMethod();
			scorePrecision = nctd.getDataPrecision();
			notifyActionStart("Generating Layer", SimpleSCWList.getCreationStepCount(), true);
			SCWList scwList = SimpleSCWListFactory.createGenericSCWArrayList((SCWReader) extractor, scorePrecision, scoreCalculation);
			return scwList;
		}
		throw new InterruptedException();
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
}

