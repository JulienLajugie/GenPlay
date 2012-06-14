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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SingleFileExportEngine extends ExportEngine {

	@Override
	protected void performExport() throws IOException {
		//showInformation();
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			//System.out.println("SingleFileExportEngine.performExport()");

			// Initialize the reader
			File file = fileList.get(0).getFile();
			BGZIPReader reader = new BGZIPReader(file);

			// Initialize the output
			String output = "";

			// Add the header (with columns) to the output
			output += reader.getFulleHeader() + "\n";

			VCFLine currentLine = reader.getCurrentLine();

			MGSynchronizer synchronizer = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeSynchronizer();

			while (!currentLine.isLastLine()) {

				if (currentLine.isValid()) {
					int[] lengths = synchronizer.getVariantLengths(currentLine.getREF(), currentLine.getALT().split(","), currentLine.getINFO());
					VariantType[] variations = synchronizer.getVariantTypes(lengths);
					List<Integer> allValidIndex = new ArrayList<Integer>();
					List<String> allValidGenome = new ArrayList<String>();
					for (String genomeName: variationMap.keySet()) {
						int[] altIndexes = getAlternativeIndexes(genomeName, reader, synchronizer);
						//VariantType[] variationsFound = getVariantTypes(variations, altIndexes);
						//List<Integer> validIndex = getValidIndexes(variationMap.get(genomeName), variationsFound);
						//List<Integer> validIndex = getValidIndexes(variationMap.get(genomeName), variations, );
						/*if (validIndex.size() > 0) {
							allValidGenome.add(genomeName);
							for (int index: validIndex) {
								if (!allValidIndex.contains(index)) {
									allValidIndex.add(index);
								}
							}
						}*/
					}
					if (allValidGenome.size() > 0) {
						System.out.println(allValidIndex.toString());
						output += buildLine(reader, allValidIndex, allValidGenome) + "\n";
					}
					
				}

				reader.goNextLine();
				currentLine = reader.getCurrentLine();
			}

			//reader.printAllFile();
			//reader.printFileAsElements();

			System.out.println("OUTPUT:\n" + output);
		} else {
			System.err.println("SingleFileExportEngine.performExport() number of files invalid: " + fileList.size());
		}
	}


	private int[] getAlternativeIndexes (String genomeName, BGZIPReader reader, MGSynchronizer synchronizer) {
		int[] indexes = new int[2];

		int genomeIndex = reader.getGenomeIndex(genomeName);
		String genotype = reader.getCurrentLine().getField(genomeIndex).split(":")[0];

		indexes[0] = synchronizer.getAlleleIndex(genotype.charAt(0));
		indexes[1] = synchronizer.getAlleleIndex(genotype.charAt(2));

		return indexes;
	}


	private VariantType[] getVariantTypes (VariantType[] variations, int[] altIndexes) {
		VariantType[] result = new VariantType[2];
		for (int i = 0; i < altIndexes.length; i++) {
			if (altIndexes[i] >= 0) {
				result[i] = variations[altIndexes[i]];
			} else {
				result[i] = null;
			}
		}
		return result;
	}


	/*private List<Integer> getValidIndexes (List<VariantType> requiredVariation, VariantType[] foundVariation) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < foundVariation.length; i++) {
			if (requiredVariation.contains(foundVariation[i])) {
				if () {
					list.add(i);
				}
			}
		}
		return list;
	}*/
	
	private List<Integer> getValidIndexes (List<VariantType> requiredVariation, VariantType[] variations, int[] indexes) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < indexes.length; i++) {
			if (requiredVariation.contains(variations[indexes[i]])) {
				if (!list.contains(indexes[i])) {
					list.add(indexes[i]);
				}
			}
		}
		return list;
	}


	private String buildLine (BGZIPReader reader, List<Integer> indexes, List<String> genomes) {
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
		for (int i = 0; i < genomes.size(); i++) {
			result += line.getField(reader.getGenomeIndex(genomes.get(i)));
			if (i < genomes.size() - 1) {
				result += "\t";
			}
		}

		return result;
	}


	private String buildALTField (List<Integer> indexes, String alt) {
		String[] alternatives = alt.split(",");
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
