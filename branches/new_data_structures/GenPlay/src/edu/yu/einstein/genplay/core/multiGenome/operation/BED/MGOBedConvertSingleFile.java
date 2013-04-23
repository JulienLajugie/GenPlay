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
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.operation.ExportEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.FileScannerInterface;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.SingleFileScanner;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Operation to convert a VCF track as a variable window track.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOBedConvertSingleFile extends ExportEngine {

	private final String 					fullGenomeName;			// The genome to convert.
	private final Track 					firstAlleleTrack;		// The track where the data of the first allele are.
	private final Track 					secondAlleleTrack;		// The track where the data of the second allele are.
	private final Double					dotValue;				// The value to give for "." in genotype (omit => null)
	private final VCFHeaderType 			header;					// The header field to use as a score.
	private final CoordinateSystemType 		coordinateSystem;		// The coordinate system to export the positions.
	private List<AlleleSettingsBedConvert> 	fullAlleleList;			// The full list of allele settings helper.
	private List<AlleleSettingsBedConvert> 	alleleListToConvert;	// The list of allele settings helper to use.

	private int genomeIndex;										// The index of the genome in the file to export.


	/**
	 * Constructor of {@link MGOBedConvertSingleFile}
	 * @param fullGenomeName the full genome name of the genome to export
	 * @param firstAlleleTrack track to export the first allele
	 * @param secondAlleleTrack track to export the second allele
	 * @param dotValue value to give for "." in genotype (omit => null)
	 * @param header the header to use as a score
	 */
	public MGOBedConvertSingleFile (String fullGenomeName, Track firstAlleleTrack, Track secondAlleleTrack, Double dotValue, VCFHeaderType header) {
		this.fullGenomeName = fullGenomeName;
		this.firstAlleleTrack = firstAlleleTrack;
		this.secondAlleleTrack = secondAlleleTrack;
		this.dotValue = dotValue;
		this.header = header;
		this.isConversion = true;
		coordinateSystem = CoordinateSystemType.METAGENOME;
	}


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			if ((firstAlleleTrack != null) || (secondAlleleTrack != null)) {
				fileScanner = new SingleFileScanner(this);
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
		genomeIndex = fileScanner.getCurrentVCFReader().getReader().getIndexFromGenome(fullGenomeName);

		// Compute the file scan algorithm
		fileScanner.compute();
	}

	int cpt = 0;
	@Override
	public void processLine(FileScannerInterface fileAlgorithm) throws IOException {
		cpt++;
		VCFLine currentLine = fileAlgorithm.getCurrentLine();
		currentLine.processForAnalyse();
		String gt = currentLine.getFormatField(genomeIndex, 0).toString();
		if (gt.length() == 3) {
			Chromosome chromosome = currentLine.getChromosome();
			int[] lengths = VCFLineUtility.getVariantLengths(currentLine.getREF(), Utils.split(currentLine.getALT(), ','), currentLine.getINFO());

			for (AlleleSettingsBedConvert alleleExport: fullAlleleList) {
				int altIndex = VCFLineUtility.getAlleleIndex(gt.charAt(alleleExport.getCharIndex()));
				alleleExport.initializeCurrentInformation(lengths, currentLine, altIndex);
			}

			AlleleSettingsBedConvert firstAllele = fullAlleleList.get(0);
			AlleleSettingsBedConvert secondAllele = fullAlleleList.get(1);
			firstAllele.updateCurrentInformation(secondAllele, chromosome);
			secondAllele.updateCurrentInformation(firstAllele, chromosome);

			firstAllele.finalizePosition();
			secondAllele.finalizePosition();

			for (AlleleSettingsBedConvert alleleExport: alleleListToConvert) {
				Object score = getScore(currentLine, alleleExport);
				if (score != null) {
					alleleExport.addCurrentInformation(chromosome, score, includeReferences, includeNoCall);
				} else {
					//System.err.println("The line could not be exported. It seems the ID '" + header.getId() + "' has not been found in the line: " + currentLine.toString());
				}
			}
		}
	}


	/**
	 * @param currentLine	the current line in process
	 * @param alleleExport	the allele setting helper to use
	 * @return the score to use, null otherwise
	 */
	private Object getScore (VCFLine currentLine, AlleleSettingsBedConvert alleleExport) {
		Object result = null;

		if (alleleExport.isKnown()) {
			Object value = currentLine.getHeaderField(header, genomeIndex);
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

				if ((valueIndex > -1) && (valueIndex < values.length)) {
					result = values[valueIndex];
				} else {
					result = null;
				}
			}
		} else {
			if (dotValue != null) {
				result = dotValue;
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
	public SCWList getFirstList () throws InvalidChromosomeException, InterruptedException, ExecutionException {
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
	public SCWList getSecondList () throws InvalidChromosomeException, InterruptedException, ExecutionException {
		if (secondAlleleTrack != null) {
			AlleleSettingsBedConvert alleleSettings = alleleListToConvert.get(0);;
			if (alleleListToConvert.size() == 2) {
				alleleSettings = alleleListToConvert.get(1);
			}
			return getList(alleleSettings);
		}
		return null;
	}


	/**
	 * @param alleleSettings	the allele settings helper
	 * @return the {@link SCWList}
	 * @throws InvalidChromosomeException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private SCWList getList (AlleleSettingsBedConvert alleleSettings) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		return new SimpleSCWList(alleleSettings.getStartList(), alleleSettings.getStopList(), alleleSettings.getScoreList(), ScoreOperation.AVERAGE);
	}


	@Override
	public void processLine(VCFLine src, VCFLine dest) throws IOException {}

}
