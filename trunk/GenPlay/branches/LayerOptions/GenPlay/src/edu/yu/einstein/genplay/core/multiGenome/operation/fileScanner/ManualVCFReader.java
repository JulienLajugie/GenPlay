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
package edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.BGZIPReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.operation.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class reads a VCF file (as a gz) and can process some operations on the lines.
 * It checks if the line is valid according to the requirements (variations and filters).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ManualVCFReader {

	private final boolean includeReferences;
	private final boolean includeNoCall;

	private final VCFFile							vcfFile; 		// The vcf file (.gz)
	private final BGZIPReader 						reader;			// The gz reader
	private final List<String> 						genomeList;		// The list of required genomes

	private final Map<String, List<VariantType>> 	variationMap;	// map between genome names and their required variation
	private final List<MGFilter> 					filterList;		// list of filter

	private VCFLine 								currentLine;	// The current VCF line
	private List<Integer> 							allValidIndex;	// The array that will contain all valid alternative indexes of the line
	private List<String> 							allValidGenome;	// The array that will contain all valid genome names of the line

	private final MGSynchronizer synchronizer;						// The Multi Genome Synchronizer


	/**
	 * Constructor of {@link ManualVCFReader}
	 * @param vcfFile		the vcf file
	 * @param genomeList	the list of genome
	 * @param variationMap 	the map of variations
	 * @param filterList 	the list of filters
	 * @param includeReferences include the references (0)
	 * @param includeNoCall 	include the no call (.)
	 * @throws Exception
	 */
	public ManualVCFReader (VCFFile vcfFile, List<String> genomeList, Map<String, List<VariantType>> variationMap, List<MGFilter> filterList, boolean includeReferences, boolean includeNoCall) throws Exception {
		this.vcfFile = vcfFile;
		this.genomeList = genomeList;
		this.variationMap = variationMap;
		this.filterList = filterList;
		this.includeReferences = includeReferences;
		this.includeNoCall = includeNoCall;

		synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();
		reader = new BGZIPReader(vcfFile);
	}


	/**
	 * Goes to the next line in the file.
	 * @throws IOException
	 */
	public void goNextLine () throws IOException {
		reader.goNextLine();
		currentLine = reader.getCurrentLine();
	}


	/**
	 * Goes to the next line in the file.
	 * It processes the line in order to know whether it passes the constraints or not.
	 * The constraints are variations types and filters.
	 * When the line passes the constraints, two lists are created:
	 * - allValidGenome: the list of genome names their variations verify the constraints
	 * - allValidIndex: the list of alternative indexes that verify the constraints
	 * 
	 * @return the current VCF line
	 * @throws IOException
	 */
	public VCFLine getNextValidLine () throws IOException {
		goNextLine();
		return getCurrentValidLine();
	}


	/**
	 * Goes to the next line in the file.
	 * Does not process anything, just return the next line
	 * 
	 * @return the next line in the VCF file
	 * @throws IOException
	 */
	public VCFLine getNextLine () throws IOException {
		goNextLine();
		return reader.getCurrentLine();
	}


	/**
	 * Gets and processes the current line in the file.
	 * It processes the line in order to know whether it passes the constraints or not.
	 * The constraints are variations types and filters.
	 * When the line passes the constraints, two lists are created:
	 * - allValidGenome: the list of genome names their variations verify the constraints
	 * - allValidIndex: the list of alternative indexes that verify the constraints
	 * 
	 * @return the current line
	 */
	public VCFLine getCurrentValidLine () {
		currentLine = reader.getCurrentLine();
		passValidation(currentLine);
		return currentLine;
	}


	/**
	 * It processes the line in order to know whether it passes the constraints or not.
	 * The constraints are variations types and filters.
	 * When the line passes the constraints, two lists are created:
	 * - allValidGenome: the list of genome names their variations verify the constraints
	 * - allValidIndex: the list of alternative indexes that verify the constraints
	 * 
	 * @param currentLine a vcf line
	 */
	private void passValidation (VCFLine currentLine) {
		boolean hasPassed = false;

		if (!currentLine.isLastLine() && currentLine.isValid()) {																											// The line has to be a valid line to be processed
			currentLine.processForAnalyse();
			int[] lengths = VCFLineUtility.getVariantLengths(currentLine.getREF(), Utils.split(currentLine.getALT(), ','), currentLine.getINFO());		// Retrieves the length of all defined variations of the line
			VariantType[] variations = VCFLineUtility.getVariantTypes(lengths);																	// Converts the lengths into variation types (insertion, deletion...)

			allValidIndex = new ArrayList<Integer>();																				// Initializes the array that will contain all valid alternative indexes of the line
			allValidGenome = new ArrayList<String>();																				// Initializes the array that will contain all valid genome names of the line

			for (int i = 0; i < genomeList.size(); i++) {																						// Will scan information for all genomes of the line
				int[] altIndexes = getAlternativeIndexes(genomeList.get(i), reader, synchronizer);												// Gets indexes defined by the GT type ('.' is converted as -1)
				List<Integer> validIndex = getValidIndexes(variationMap.get(genomeList.get(i)), variations, altIndexes);						// Only keeps the valid ones (excludes the ones referring to the reference)
				if (validIndex.size() > 0) {																									// If we have found at least one valid index (one variant matching the variation requirements)
					if (isValid(currentLine)) {
						allValidGenome.add(genomeList.get(i));																					// If the process comes here, it means information has been found for the current genome
						for (int index: validIndex) {																							// For all found indexes
							if (!allValidIndex.contains(index)) {																				// If it does not have been stored yet
								allValidIndex.add(index);																						// We store it
							}
						}
					}
				}
			}

			if (allValidGenome.size() > 0) {																										// If information has been found for at least one genome
				hasPassed = true;
			}
		}

		currentLine.setHasData(hasPassed);
	}


	/**
	 * Retrieves the indexes of the alternatives defined by the genotype field of a genome
	 * @param genomeName	the name of the genome
	 * @param reader		the reader (to get the GT field)
	 * @param synchronizer	the multi genome synchronizer (to convert character as index)
	 * @return				an array of two integers containing the indexes
	 */
	private int[] getAlternativeIndexes (String genomeName, BGZIPReader reader, MGSynchronizer synchronizer) {

		int genomeIndex = reader.getIndexFromGenome(genomeName);
		String genotype = Utils.split(reader.getCurrentLine().getField(genomeIndex), ':')[0];
		int size = (int) Math.floor(genotype.length() / 2);
		int[] indexes = new int[size];
		int charIndex = 0;
		for (int i = 0; i < size; i++) {
			indexes[i] = VCFLineUtility.getAlleleIndex(genotype.charAt(charIndex) + "");
			charIndex += 2;
		}

		return indexes;
	}


	/**
	 * Compares the required variations and the ones from the line in order to select the correct indexes.
	 * If it has to include the 0 genotype (refers to reference), the index will be -1.
	 * @param requiredVariation	the variations required for the export
	 * @param variations		the variations defined in the line
	 * @param indexes			an array of indexes referring to the variations of the line
	 * @return					the list of indexes related to required variations
	 */
	private List<Integer> getValidIndexes (List<VariantType> requiredVariation, VariantType[] variations, int[] indexes) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < indexes.length; i++) {
			int currentIndex = indexes[i];
			boolean insert = false;

			if (currentIndex >= 0) {
				insert = requiredVariation.contains(variations[indexes[i]]);
			}

			if (insert && !list.contains(currentIndex)) {
				list.add(currentIndex);
			}
		}

		if (list.size() == 0) {
			if (isReferenceValid(requiredVariation, variations) && (includeReferences || includeNoCall)) {
				list.add(-1);
			}
		}

		return list;
	}


	/**
	 * @param requiredVariation	the required variation
	 * @param variations		the variation defined in the line
	 * @return true if the line defines at least one of the required variation
	 */
	private boolean isReferenceValid (List<VariantType> requiredVariation, VariantType[] variations) {
		boolean result = false;
		for (VariantType type: variations) {
			if (requiredVariation.contains(type)) {
				result = true;
				break;
			}
		}
		return result;
	}


	/**
	 * Tests the line with all required filters.
	 * @param line	the line to test
	 * @return		true if the line meet all filters requirements, false otherwise
	 */
	private boolean isValid (VCFLine line) {
		if (filterList != null) {
			for (MGFilter filter: filterList) {
				if (!filter.getFilter().isValid(line)) {
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * @return the vcfFile
	 */
	public VCFFile getVcfFile() {
		return vcfFile;
	}


	/**
	 * @return the reader
	 */
	public BGZIPReader getReader() {
		return reader;
	}


	/**
	 * @return the allValidIndex
	 */
	public List<Integer> getAllValidIndex() {
		return allValidIndex;
	}


	/**
	 * @return the allValidGenome
	 */
	public List<String> getAllValidGenome() {
		return allValidGenome;
	}
}
