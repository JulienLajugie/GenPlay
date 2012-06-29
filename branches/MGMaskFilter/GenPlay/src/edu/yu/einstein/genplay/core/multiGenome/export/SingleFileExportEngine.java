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
package edu.yu.einstein.genplay.core.multiGenome.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SingleFileExportEngine extends ExportEngine {


	@Override
	protected boolean canStart() {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			return true;
		}
		System.err.println("SingleFileExportEngine.performExport() number of files invalid: " + fileList.size());
		return false;
	}


	@Override
	protected void performExport() throws IOException {
		// Initialize the reader
		VCFFile vcfFile = getFileList().get(0);
		BGZIPReader reader = new BGZIPReader(vcfFile);

		// Initialize the list of genomes
		List<String> genomeList = getGenomeList();

		// Initialize the output file
		File outputFile = new File(path);
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter out = new BufferedWriter(fw);

		// Gets the first line of data
		VCFLine currentLine = reader.getCurrentLine();

		// Gets the synchronizer object
		MGSynchronizer synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();

		// Scan the file line by line
		while (!currentLine.isLastLine()) {
			
			if (currentLine.isValid()) {																											// The line has to be a valid line to be processed
				int[] lengths = synchronizer.getVariantLengths(currentLine.getREF(), Utils.split(currentLine.getALT(), ','), currentLine.getINFO());		// Retrieves the length of all defined variations of the line
				VariantType[] variations = synchronizer.getVariantTypes(lengths);																	// Converts the lengths into variation types (insertion, deletion...)
				
				List<Integer> allValidIndex = new ArrayList<Integer>();																				// Initializes the array that will contain all valid alternative indexes of the line
				List<String> allValidGenome = new ArrayList<String>();																				// Initializes the array that will contain all valid genome names of the line
				
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
					data.writeObject(buildLine(reader, allValidIndex, genomeList, allValidGenome) + "\n");												// We have to add the line
					headerHandler.processLine(vcfFile, currentLine.getALT(), currentLine.getFILTER(), currentLine.getINFO(), currentLine.getFORMAT());	// Checks the line in order to update IDs for the header
				}
			}

			reader.goNextLine();
			currentLine = reader.getCurrentLine();
		}
		fw.close();
		out.close();
	}


	@Override
	protected void createHeader() throws IOException {
		// Initialize the reader
		BGZIPReader reader = new BGZIPReader(getFileList().get(0));

		// Initialize the list of genomes
		List<String> genomeList = getGenomeList();

		// Gets the meta data
		header = reader.getMetaDataHeader() + "\n";

		// Gets the fields data
		header += headerHandler.getFieldHeader() + "\n";

		// Gets the column names line
		header += reader.getFixedColumns();
		for (int i = 0; i < genomeList.size(); i++) {
			header += FormattedMultiGenomeName.getRawName(genomeList.get(i));
			if (i < (genomeList.size() - 1)) {
				header += "\t";
			}
		}
	}


	/**
	 * Retrieves the indexes of the alternatives defined by the genotype field of a genome
	 * @param genomeName	the name of the genome
	 * @param reader		the reader (to get the GT field)
	 * @param synchronizer	the multi genome synchronizer (to convert character as index)
	 * @return				an array of two integers containing the indexes
	 */
	private int[] getAlternativeIndexes (String genomeName, BGZIPReader reader, MGSynchronizer synchronizer) {
		int[] indexes = new int[2];
		int genomeIndex = reader.getIndexFromGenome(genomeName);
		String genotype = Utils.split(reader.getCurrentLine().getField(genomeIndex), ':')[0];

		indexes[0] = synchronizer.getAlleleIndex(genotype.charAt(0));
		indexes[1] = synchronizer.getAlleleIndex(genotype.charAt(2));

		return indexes;
	}


	/**
	 * Compares the required variations and the ones from the line in order to select the correct indexes.
	 * @param requiredVariation	the variations required for the export
	 * @param variations		the variations defined in the line
	 * @param indexes			an array of indexes referring to the variations of the line
	 * @return					the list of indexes related to required variations
	 */
	private List<Integer> getValidIndexes (List<VariantType> requiredVariation, VariantType[] variations, int[] indexes) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] >= 0) {
				if (requiredVariation.contains(variations[indexes[i]])) {
					if (!list.contains(indexes[i])) {
						list.add(indexes[i]);
					}
				}
			}
		}
		return list;
	}


	/**
	 * Tests the line with all required filters.
	 * @param line	the line to test
	 * @return		true if the line meet all filters requirements, false otherwise
	 */
	private boolean isValid (VCFLine line) {
		if (filterList != null) {
			Map<String, Object> map = line.toFullMap();
			for (VCFFilter filter: filterList) {
				if (!filter.getFilter().isValid(map)) {
					return false;
				}
			}
		}
		return true;
	}


	/**
	 * Builds the line to insert in the output.
	 * The ALT field contains information only about the required variations.
	 * The genome fields are native fields. Genomes that don't define any required variation will have a empty field: ./.
	 * All other fields are the ones from the native line. 
	 * @param reader			the file reader
	 * @param indexes			the indexes referring to the correct alternatives
	 * @param fullGenomesList	the list of all required genomes
	 * @param genomes			the list of genomes that have information matching requirements
	 * @return					the line to insert
	 */
	private String buildLine (BGZIPReader reader, List<Integer> indexes, List<String> fullGenomesList, List<String> genomes) {
		String result = "";
		VCFLine line = reader.getCurrentLine();
		result += line.getCHROM() + "\t";
		result += line.getPOS() + "\t";
		result += line.getID() + "\t";
		result += line.getREF() + "\t";
		result += buildALTField(indexes, line.getALT()) + "\t";
		result += line.getQUAL() + "\t";
		result += line.getFILTER() + "\t";
		result += line.getINFO() + "\t";
		result += line.getFORMAT() + "\t";
		for (int i = 0; i < fullGenomesList.size(); i++) {
			String currentGenome = fullGenomesList.get(i);

			if (genomes.contains(currentGenome)) {
				result += line.getField(reader.getIndexFromGenome(genomes.get(i)));
			} else {
				result += "./.";
			}
			if (i < fullGenomesList.size() - 1) {
				result += "\t";
			}
		}
		return result;
	}


	/**
	 * Build the ALT field.
	 * It contains only alternatives matching the requirements
	 * @param indexes	the list of indexes referring to the alternatives
	 * @param alt		the native ALT field
	 * @return			the ALT field to insert
	 */
	private String buildALTField (List<Integer> indexes, String alt) {
		Collections.sort(indexes);
		String[] alternatives = Utils.split(alt, ',');
		String result = "";
		for (int i = 0; i < indexes.size(); i++) {
			result += alternatives[indexes.get(i)];
			if (i < indexes.size() - 1) {
				result += ",";
			}
		}
		return result;
	}

}
