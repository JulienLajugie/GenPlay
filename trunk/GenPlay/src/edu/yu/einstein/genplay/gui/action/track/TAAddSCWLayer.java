/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.action.track;

import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.extractor.SAMExtractor;
import edu.yu.einstein.genplay.core.IO.extractor.StrandedExtractor;
import edu.yu.einstein.genplay.core.IO.utils.ChromosomesSelector;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.DuplicateSAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.InvalidSAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.MappingQualitySAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.NotUniqueNorPrimarySAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.NotUniqueSAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.ReadGroupsSAMRecordFilter;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.UnpairedSAMRecordFilter;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog.NewCurveLayerDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a {@link SimpleSCWLayer} to the selected track
 * @author Julien Lajugie
 */
public final class TAAddSCWLayer extends TrackListActionExtractorWorker<SCWList> {

	private static final long serialVersionUID = -7836987725953057426L;								// generated ID
	private static final String ACTION_NAME = "Add Window Layer";									// action name
	private static final String DESCRIPTION = "Add a layer displaying windows with a score"; 		// tooltip
	private ScoreOperation 			scoreCalculation = null;										// method of calculation for the score
	private Strand					strand = null;													// strand to extract
	private int						fragmentLength = 0;												// user specified length of the fragments
	private int 					readLength = 0;													// user specified length of the reads
	private String 					samHistory = null;												// history line for sam extraction
	private boolean 				isBinList = false;												// true if the result should be a bin list
	private int 					binSize = 0;													// Size of the bins of the BinList


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
			AbstractSCWLayer<?> newLayer;
			String history = new String();
			if (isBinList) {
				newLayer = new BinLayer(selectedTrack, (BinList) actionResult, name);
				// add the history to the layer
				history = "Bin Size = " + binSize + "bp, ";
			} else {
				newLayer = new SimpleSCWLayer(selectedTrack, actionResult, name);
			}
			newLayer.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Colors.GREY);
			history += "Method of Calculation = " + scoreCalculation;
			if (strand != null) {
				history += ", Strand = ";
				if (strand == Strand.FIVE) {
					history += "5'";
				} else {
					history += "3'";
				}
			}
			if (fragmentLength != 0) {
				history += ", Fragment Length = " + fragmentLength + "bp";
			}
			if (readLength != 0) {
				history += ", Read Length = " + readLength + "bp";
			}
			if (!history.isEmpty()) {
				newLayer.getHistory().add(history, Colors.GREY);
			}
			if (samHistory != null) {
				newLayer.getHistory().add(samHistory, Colors.GREY);
			}
			selectedTrack.getLayers().add(newLayer);
			selectedTrack.setActiveLayer(newLayer);
		}
	}


	@Override
	protected void doBeforeExtraction() throws InterruptedException {
		boolean isStrandNeeded = extractor instanceof StrandedExtractor;
		boolean isSAMExtractor = extractor instanceof SAMExtractor;
		boolean isMultiGenome = ProjectManager.getInstance().isMultiGenomeProject();
		NewCurveLayerDialog ncld = new NewCurveLayerDialog(extractor);
		if (ncld.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
			selectedChromo = ncld.getSelectedChromosomes();
			// if not all the chromosomes are selected we need
			// to ask the user if the file is sorted or not
			extractor.setChromosomeSelector(new ChromosomesSelector(selectedChromo));
			name = ncld.getLayerName();
			scoreCalculation = ncld.getScoreCalculationMethod();
			isBinList = ncld.isCreateBinListSelected();
			binSize = ncld.getBinSize();
			// set strand options
			if (isStrandNeeded) {
				strand = ncld.getStrandToExtract();
				fragmentLength = ncld.getFragmentLengthValue();
				readLength = ncld.getReadLengthValue();
				StrandedExtractorOptions strandedExtractorOptions = new StrandedExtractorOptions(strand, fragmentLength, readLength);
				((StrandedExtractor) extractor).setStrandedExtractorOptions(strandedExtractorOptions);
			}
			// set SAM options
			if (isSAMExtractor) {
				samHistory = "";
				SAMExtractor samExtractor = (SAMExtractor) extractor;
				// always remove invalid reads
				samExtractor.addFilter(new InvalidSAMRecordFilter());
				if (ncld.getReadGroup() != null) {
					samExtractor.addFilter(new ReadGroupsSAMRecordFilter(ncld.getReadGroup()));
					samHistory += "Read Group: " + ncld.getReadGroup().getId() + ", ";
				}
				if (ncld.isRemoveDuplicatesSelected()) {
					samExtractor.addFilter(new DuplicateSAMRecordFilter());
					samHistory += "Duplicates Removed, ";
				}
				if (ncld.isPairedEndSelected()) {
					samExtractor.setPairedMode(true);
					samExtractor.addFilter(new UnpairedSAMRecordFilter());
					samHistory += "Paired-End Mode";
				} else {
					samExtractor.setPairedMode(false);
					samHistory += "Single-End Mode";
					if (ncld.getMappingQuality() > 0) {
						samExtractor.addFilter(new MappingQualitySAMRecordFilter(ncld.getMappingQuality()));
						samHistory += ", Mapping Quality ≥ " + ncld.getMappingQuality();
					}
					if (ncld.isUniqueSelected()) {
						samExtractor.addFilter(new NotUniqueSAMRecordFilter());
						samHistory += ", Unique Alignments Only";
					} else if (ncld.isPrimaryAligmentSelected()) {
						samExtractor.addFilter(new NotUniqueNorPrimarySAMRecordFilter());
						samHistory += ", Unique and Primary Alignments";
					}
				}
			}
			// set multi-genome options
			if (isMultiGenome) {
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
		try {
			SCWList scwList;
			if (((fragmentLength != 0) || (readLength != 0)) && (strand == null)) {
				/* if we extract both strand and the strands are shifted we need to use the strand safe
				 * factory since reads on the 3' strand are shifted toward 5' which can change the order
				 * of reads and cause the file to be no longer sorted
				 */
				if (isBinList) {
					notifyActionStart("Generating Fixed-Window Layer", (BinList.getCreationStepCount(SCWListType.BIN) * 3) + 2, true);
					scwList = SCWListFactory.createStrandSafeBinList((SCWReader) extractor, binSize, scoreCalculation);
				} else {
					notifyActionStart("Generating Sequencing/Microarray Layer", (SimpleSCWList.getCreationStepCount(SCWListType.GENERIC) * 3) + 2, true);
					scwList = SCWListFactory.createStrandSafeDenseSCWList((SCWReader) extractor, scoreCalculation);
				}
			} else {
				if (isBinList) {
					notifyActionStart("Generating Sequencing/Microarray Layer", BinList.getCreationStepCount(SCWListType.BIN) + 1, true);
					scwList = SCWListFactory.createBinList((SCWReader) extractor, binSize, scoreCalculation);
				} else {
					notifyActionStart("Generating Sequencing/Microarray Layer", SimpleSCWList.getCreationStepCount(SCWListType.GENERIC) + 1, true);
					scwList = SCWListFactory.createDenseSCWList((SCWReader) extractor, scoreCalculation);
				}
			}
			return scwList;
		} catch (ClassCastException e) {
			throw new InvalidFileTypeException();
		}
	}


	@Override
	protected File retrieveFileToExtract() {
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Sequencing/Microarray Layer", Utils.getReadableSCWFileFilters(), true);
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}
}

