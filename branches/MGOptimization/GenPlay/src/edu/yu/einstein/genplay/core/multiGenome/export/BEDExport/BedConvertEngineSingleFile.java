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
package edu.yu.einstein.genplay.core.multiGenome.export.BEDExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.export.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.export.FileAlgorithmInterface;
import edu.yu.einstein.genplay.core.multiGenome.export.SingleFileAlgorithm;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BedConvertEngineSingleFile extends ExportEngine {

	private final MGSynchronizer 	synchronizer;

	private final String 					fullGenomeName;
	private final Track<?> 					firstAlleleTrack;
	private final Track<?> 					secondAlleleTrack;
	private final VCFHeaderType 			header;
	private final CoordinateSystemType 		coordinateSystem;
	private List<AlleleSettingsBedConvert> 	fullAlleleList;
	private List<AlleleSettingsBedConvert> 	alleleListToConvert;

	private int genomeIndex;


	/**
	 * Constructor of {@link BedConvertEngineSingleFile}
	 * @param fullGenomeName the full genome name of the genome to export
	 * @param firstAlleleTrack track to export the first allele
	 * @param secondAlleleTrack track to export the second allele
	 * @param header the header to use as a score
	 */
	public BedConvertEngineSingleFile (String fullGenomeName, Track<?> firstAlleleTrack, Track<?> secondAlleleTrack, VCFHeaderType header) {
		this.synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();
		this.fullGenomeName = fullGenomeName;
		this.firstAlleleTrack = firstAlleleTrack;
		this.secondAlleleTrack = secondAlleleTrack;
		this.header = header;
		this.isConversion = true;
		coordinateSystem = CoordinateSystemType.METAGENOME;
	}


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			if ((firstAlleleTrack != null) || (secondAlleleTrack != null)) {
				fileHandler = new SingleFileAlgorithm(this);
				initializeAlleleList();
				return true;
			} else {
				System.err.println("BedExportEngineSingleFile.canStart() one track has to be not null");
			}
		} else {
			System.err.println("BedExportEngineSingleFile.canStart() number of files invalid: " + fileList.size());
		}
		return false;
	}


	/**
	 * Initialize the list of allele
	 */
	private void initializeAlleleList () {
		fullAlleleList = new ArrayList<AlleleSettingsBedConvert>();
		fullAlleleList.add(new AlleleSettingsBedConvert(AlleleType.ALLELE01, coordinateSystem));
		fullAlleleList.add(new AlleleSettingsBedConvert(AlleleType.ALLELE02, coordinateSystem));

		alleleListToConvert = new ArrayList<AlleleSettingsBedConvert>();
		if ((firstAlleleTrack != null) && (secondAlleleTrack != null)) {
			alleleListToConvert.add(fullAlleleList.get(0));
			alleleListToConvert.add(fullAlleleList.get(1));
		} else if ((firstAlleleTrack != null) && (secondAlleleTrack == null)) {
			alleleListToConvert.add(fullAlleleList.get(0));
		} else if ((firstAlleleTrack == null) && (secondAlleleTrack != null)) {
			alleleListToConvert.add(fullAlleleList.get(1));
		}
	}


	@Override
	protected void process() throws Exception {
		// Retrieve the index of the column of the genome in the VCF
		genomeIndex = fileHandler.getCurrentVCFReader().getReader().getIndexFromGenome(fullGenomeName);

		// Compute the file scan algorithm
		fileHandler.compute();
	}


	@Override
	protected void processLine(FileAlgorithmInterface fileAlgorithm) throws IOException {
		VCFLine currentLine = fileAlgorithm.getCurrentLine();
		currentLine.processForAnalyse();

		String gt = currentLine.getFormatField(genomeIndex, 0).toString();
		if (gt.length() == 3) {
			Chromosome chromosome = currentLine.getChromosome();
			int[] lengths = synchronizer.getVariantLengths(currentLine.getREF(), Utils.splitWithTab(currentLine.getALT()), currentLine.getINFO());

			for (AlleleSettingsBedConvert alleleExport: fullAlleleList) {
				int altIndex = synchronizer.getAlleleIndex(gt.charAt(alleleExport.getCharIndex()));
				alleleExport.initializeCurrentInformation(chromosome, lengths, currentLine, altIndex);
			}

			AlleleSettingsBedConvert firstAllele = fullAlleleList.get(0);
			AlleleSettingsBedConvert secondAllele = fullAlleleList.get(1);
			firstAllele.updateCurrentInformation(secondAllele);
			secondAllele.updateCurrentInformation(firstAllele);


			for (AlleleSettingsBedConvert alleleExport: alleleListToConvert) {
				Object score = getScore(currentLine, alleleExport);
				if (score != null) {
					alleleExport.addCurrentInformation(chromosome, score);
				} else {
					System.err.println("The line could not be exported. It seems the ID '" + header.getId() + "' has not been found in the line: " + currentLine.toString());
				}
			}
		}
	}


	private Object getScore (VCFLine currentLine, AlleleSettingsBedConvert alleleExport) {
		Object value = currentLine.getHeaderField(header, genomeIndex);
		Object result = null;
		if (value != null) {
			Object[] values = Utils.split(value.toString(), ',');
			int valueIndex = 0;

			if (values.length > 1) {
				if (header instanceof VCFHeaderAdvancedType) {
					VCFHeaderAdvancedType advanced = (VCFHeaderAdvancedType) header;
					if (!advanced.getNumber().equals("1")) {
						valueIndex = alleleExport.getCurrentAltIndex();
						if (advanced.getColumnCategory() == VCFColumnName.FORMAT) {
							if (header.getId().equals("AD")) {
								valueIndex++;
							}
						}
					}
				}
			}

			if (valueIndex < values.length) {
				result = values[valueIndex];
			} else {
				result = null;
			}
		}

		return result;
	}


	/**
	 * @return the scored chromosome window list for the first track, null if not required
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ScoredChromosomeWindowList getFirstList () throws InvalidChromosomeException, InterruptedException, ExecutionException {
		if (firstAlleleTrack != null) {
			AlleleSettingsBedConvert alleleSettings = alleleListToConvert.get(0);
			return getList(alleleSettings);
		}
		return null;
	}


	/**
	 * @return the scored chromosome window list for the second track, null if not required
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ScoredChromosomeWindowList getSecondList () throws InvalidChromosomeException, InterruptedException, ExecutionException {
		if (secondAlleleTrack != null) {
			AlleleSettingsBedConvert alleleSettings = alleleListToConvert.get(0);;
			if (alleleListToConvert.size() == 2) {
				alleleSettings = alleleListToConvert.get(1);
			}
			return getList(alleleSettings);
		}
		return null;
	}


	private ScoredChromosomeWindowList getList (AlleleSettingsBedConvert alleleSettings) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new SimpleScoredChromosomeWindowList(alleleSettings.getStartList(), alleleSettings.getStopList(), alleleSettings.getScoreList(), ScoreCalculationMethod.AVERAGE);
	}

}
