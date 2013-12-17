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
package edu.yu.einstein.genplay.core.multiGenome.operation.VCF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileFullStatistic;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFSampleStatistics;
import edu.yu.einstein.genplay.core.multiGenome.operation.BasicEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.FileScannerInterface;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.SingleFileScanner;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;


/**
 * This method exports a VCF track into a VCF file.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOVCFStatisticsSingleFile extends BasicEngine {

	private VCFFileFullStatistic nativeStatistics;
	private VCFFileFullStatistic newStatistics;
	private List<String> genomeNames;
	private List<AlleleType> alleleTypeList;
	Map<AlleleType, Integer> alleleIndexMap;



	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			fileScanner = new SingleFileScanner(this);
			nativeStatistics = fileList.get(0).getStatistics();

			newStatistics = new VCFFileFullStatistic();
			genomeNames = getGenomeList();
			for (String genomeName: genomeNames) {
				newStatistics.addGenomeName(genomeName);
			}
			alleleTypeList = new ArrayList<AlleleType>();
			alleleTypeList.add(AlleleType.ALLELE01);
			alleleTypeList.add(AlleleType.ALLELE02);

			alleleIndexMap = new HashMap<AlleleType, Integer>();

			return true;
		}
		System.err.println("VCFExportEngineSingleFile.canStart() number of files invalid: " + fileList.size());
		return false;
	}


	@Override
	public void processLine(FileScannerInterface fileAlgorithm) throws IOException {
		VCFLine currentLine = fileAlgorithm.getCurrentLine();
		updateFileStatistics(newStatistics, currentLine.getAlternativesTypes(), currentLine.getAlternatives());

		for (String genomeName: genomeNames) {
			String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
			String genotype = currentLine.getGenotype(genomeRawName);
			if (genotype.length() == 3) {
				int alleleIndex01 = getAlleleIndex(genotype.charAt(0));
				int alleleIndex02 = getAlleleIndex(genotype.charAt(2));
				updateGenotypeSampleStatistics(newStatistics.getSampleStatistics(genomeName), currentLine.getAlternativesTypes(), alleleIndex01, alleleIndex02);

				for (AlleleType alleleType: alleleTypeList) {
					int currentAlleleIndex = alleleIndex01;
					if (alleleType == AlleleType.ALLELE02) {
						currentAlleleIndex = alleleIndex02;
					}
					if (currentAlleleIndex >= 0) {
						VariantType variantType = currentLine.getAlternativesTypes()[currentAlleleIndex];
						updateVariationSampleStatistics(newStatistics.getSampleStatistics(genomeName), variantType, currentLine.getAlternatives()[currentAlleleIndex]);
					}
				}
			}
		}
	}


	@Override
	protected void process() throws Exception {
		fileScanner.compute();
	}


	@Override
	public void processLine(VCFLine src, VCFLine dest) throws IOException {}


	/**
	 * @return the generated statistics
	 */
	public VCFFileStatistics getNewStatistics() {
		return newStatistics;
	}


	/**
	 * @return the statistics of the file
	 */
	public VCFFileStatistics getNativeStatistics() {
		return nativeStatistics;
	}


	/**
	 * Updates statistics related to the file
	 * @param statistic		file statistics
	 * @param variantTypes	variant type array
	 * @param alternatives	alternatives array
	 */
	private void updateFileStatistics (VCFFileStatistics statistic, VariantType[] variantTypes, String[] alternatives) {
		statistic.incrementNumberOfLines();
		for (int i = 0; i < variantTypes.length; i++) {
			if (variantTypes[i] == VariantType.SNPS) {
				statistic.incrementNumberOfSNPs();
			} else if (variantTypes[i] == VariantType.INSERTION) {
				if (isStructuralVariant(alternatives[i])) {
					statistic.incrementNumberOfLongInsertions();
				} else {
					statistic.incrementNumberOfShortInsertions();
				}
			} else if (variantTypes[i] == VariantType.DELETION) {
				if (isStructuralVariant(alternatives[i])) {
					statistic.incrementNumberOfLongDeletions();
				} else {
					statistic.incrementNumberOfShortDeletions();
				}
			}
		}
	}


	/**
	 * @param statistic				sample statistics
	 * @param variantTypes			array of variant types
	 * @param firstAlleleNumber		number of the first allele
	 * @param secondAlleleNumber	number of the second allele
	 */
	private void updateGenotypeSampleStatistics (VCFSampleStatistics statistic, VariantType[] variantTypes, int firstAlleleIndex, int secondAlleleIndex) {
		boolean homozygote = isVariantHomozygote(firstAlleleIndex, secondAlleleIndex);
		boolean heterozygote = isVariantHeterozygote(firstAlleleIndex, secondAlleleIndex);

		for (VariantType variantType: variantTypes) {
			if (homozygote) {
				if (firstAlleleIndex > -1) {
					if (variantType == VariantType.SNPS) {
						statistic.incrementNumberOfHomozygoteSNPs();
					} else if (variantType == VariantType.INSERTION) {
						statistic.incrementNumberOfHomozygoteInsertions();
					} else if (variantType == VariantType.DELETION) {
						statistic.incrementNumberOfHomozygoteDeletions();
					}
				}
			} else if (heterozygote) {
				if (variantType == VariantType.SNPS) {
					statistic.incrementNumberOfHeterozygoteSNPs();
				} else if (variantType == VariantType.INSERTION) {
					statistic.incrementNumberOfHeterozygoteInsertions();
				} else if (variantType == VariantType.DELETION) {
					statistic.incrementNumberOfHeterozygoteDeletions();
				}
			}
		}
	}


	/**
	 * Defines if a variant is homozygote according to its genotype.
	 * @param firstAlleleNumber		number related to the "first" allele
	 * @param secondAlleleNumber	number related to the "second" allele
	 * @return	true if the variant is homozygote, false otherwise
	 */
	private boolean isVariantHomozygote (int firstAlleleNumber, int secondAlleleNumber) {
		if ((firstAlleleNumber == secondAlleleNumber) && (firstAlleleNumber >= 0)) {
			return true;
		}
		return false;
	}


	/**
	 * Defines if a variant is heterozygote according to its genotype.
	 * @param firstAlleleNumber		number related to the "first" allele
	 * @param secondAlleleNumber	number related to the "second" allele
	 * @return	true if the variant is heterozygote, false otherwise
	 */
	private boolean isVariantHeterozygote (int firstAlleleNumber, int secondAlleleNumber) {
		if (firstAlleleNumber != secondAlleleNumber) {
			if ((firstAlleleNumber >= 0) || (secondAlleleNumber >= 0)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Updates statistics related to the sample
	 * @param statistic		sample statistics
	 * @param variantType	variant type
	 * @param alternative	alternative
	 */
	private void updateVariationSampleStatistics (VCFSampleStatistics statistic, VariantType variantType, String alternative) {
		if (variantType == VariantType.SNPS) {
			statistic.incrementNumberOfSNPs();
		} else if (variantType == VariantType.INSERTION) {
			if (isStructuralVariant(alternative)) {
				statistic.incrementNumberOfLongInsertions();
			} else {
				statistic.incrementNumberOfShortInsertions();
			}
		} else if (variantType == VariantType.DELETION) {
			if (isStructuralVariant(alternative)) {
				statistic.incrementNumberOfLongDeletions();
			} else {
				statistic.incrementNumberOfShortDeletions();
			}
		}
	}


	/**
	 * @param alternative ALT field (or part of it)
	 * @return true if the given alternative is coded as an SV
	 */
	private boolean isStructuralVariant (String alternative) {
		if (alternative.charAt(0) == '<') {
			return true;
		}
		return false;
	}


	/**
	 * Transforms a character into its allele index.
	 * The char 1 will refer to the first alternative located at the index 0 of any arrays.
	 * The char 0 returns -1 and the char '.' returns -2 and don't refer to any alternatives.
	 * @param alleleChar the character
	 * @return the associated code (char - 1)
	 */
	public int getAlleleIndex (char alleleChar) {
		int alleleIndex = -1;
		if (alleleChar == '.') {
			alleleIndex = -2;
		} else if (alleleChar == '0') {
			alleleIndex = -1;
		} else {
			try {
				alleleIndex = Integer.parseInt(alleleChar + "") - 1;
			} catch (Exception e) {}
		}
		return alleleIndex;
	}
}
