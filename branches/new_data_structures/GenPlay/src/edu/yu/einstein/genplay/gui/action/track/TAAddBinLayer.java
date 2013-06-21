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
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListFactory;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListActionExtractorWorker;
import edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog.NewCurveLayerDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Adds a {@link BinLayer} to the specified track
 * @author Julien Lajugie
 */
public final class TAAddBinLayer extends TrackListActionExtractorWorker<BinList> {

	private static final long serialVersionUID = -3974211916629578143L;	// generated ID
	private static final String 	ACTION_NAME = "Add Fixed Window Layer"; 						// action name
	private static final String 	DESCRIPTION = "Add a layer displaying bins with a fixed size"; 	// tooltip
	private int 					binSize = 0;													// Size of the bins of the BinList
	private ScoreOperation 			scoreCalculation = null;										// Method of calculation of the score of the BinList
	private Strand					strand = null;													// strand to extract
	private int						fragmentLength = 0;												// user specified length of the fragments
	private int 					readLength = 0;													// user specified length of the reads (0 to keep the original length)
	private String 					samHistory = null;												// history line for sam extraction

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddBinLayer.class.getName();


	/**
	 * Creates an instance of {@link TAAddBinLayer}
	 */
	public TAAddBinLayer() {
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			BinLayer newLayer = new BinLayer(selectedTrack, actionResult, name);
			// add the history to the layer
			String history = "Bin Size = " + actionResult.getBinSize() + "bp";
			history += ", Method of Calculation = " + scoreCalculation;
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
			newLayer.getHistory().add("Load " + fileToExtract.getAbsolutePath(), Colors.GREY);
			newLayer.getHistory().add(history, Colors.GREY);
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
		NewCurveLayerDialog ncld = NewCurveLayerDialog.createNewBinLayerDialog(extractor);
		if (ncld.showDialog(getRootPane()) == NewCurveLayerDialog.APPROVE_OPTION) {
			name = ncld.getLayerName();
			binSize = ncld.getBinSize();
			scoreCalculation = ncld.getScoreCalculationMethod();
			selectedChromo = ncld.getSelectedChromosomes();
			// if not all the chromosomes are selected we need
			// to ask the user if the file is sorted or not
			extractor.setChromosomeSelector(new ChromosomesSelector(selectedChromo));
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
					samHistory += "Read Group: " + ncld.getReadGroup() + ", ";
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
	protected BinList generateList() throws Exception {
		try {
			BinList binList;
			if (((fragmentLength != 0) || (readLength != 0)) && (strand == null)) {
				/* if we extract both strand and the strands are shifted we need to use the strand safe
				 * factory since reads on the 3' strand are shifted toward 5' which can change the order
				 * of reads and cause the file to be no longer sorted
				 */
				notifyActionStart("Generating Fixed-Window Layer", (BinList.getCreationStepCount(SCWListType.BIN) * 3) + 2, true);
				binList = SCWListFactory.createStrandSafeBinList((SCWReader) extractor, binSize, scoreCalculation);
			} else {
				// if the binSize is known we can find out how many steps will be used
				notifyActionStart("Generating Fixed-Window Layer", BinList.getCreationStepCount(SCWListType.BIN) + 1, true);
				binList = SCWListFactory.createBinList((SCWReader) extractor, binSize, scoreCalculation);
			}
			return binList;
		} catch (ClassCastException e) {
			throw new InvalidFileTypeException();
		}
	}


	@Override
	protected File retrieveFileToExtract() {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Fixed Window Layer", defaultDirectory, Utils.getReadableBinListFileFilters(), true);
		if (selectedFile != null) {
			return selectedFile;
		} else {
			return null;
		}
	}
}
