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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.operation.UpdateEngine;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.FileScannerInterface;
import edu.yu.einstein.genplay.core.multiGenome.operation.fileScanner.UpdateFileScanner;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class create an new VCF file based on a VCF file, using the data of a VCF track.
 * The update here is about the genotype field.
 * It will use the genotype of variation from a track to change the one of another VCF.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOApplyVCFGenotype extends UpdateEngine {

	protected BufferedWriter 	data;			// The output file stream
	private List<String> 		destGenome;		// The names of the genomes to export
	private boolean 			canBeInserted;	// If the current line can be inserted


	@Override
	protected boolean canStart() throws Exception {
		List<VCFFile> fileList = getFileList();
		if (fileList.size() == 1) {
			if (fileList.get(0).getFile().exists() && fileToUpdate.getFile().exists()) {
				fileScanner = new UpdateFileScanner(this, fileList.get(0), fileToUpdate);
				if (genomeNameMap.size() > 0) {
					return true;
				}
				System.err.println("MGOApplyVCFGenotype.canStart() Files does not have any genome name in common.");
			}
			else {
				System.err.println("MGOApplyVCFGenotype.canStart() At least one of the file does not exist.");
			}
		}
		System.err.println("MGOApplyVCFGenotype.canStart() number of files invalid: " + fileList.size());
		return false;
	}


	@Override
	protected void process() throws Exception {
		File dataFile = new File(path);
		FileWriter fw = new FileWriter(dataFile);
		data = new BufferedWriter(fw);

		String header = ((UpdateFileScanner) fileScanner).getDestinationReader().getFullHeader();
		header += "\n";

		data.write(header);

		fileScanner.compute();

		data.close();
		data.close();
	}


	@Override
	public void processLine(VCFLine src, VCFLine dest) throws IOException {
		canBeInserted = false;

		destGenome = fileToUpdate.getHeader().getRawGenomesNames();

		List<String> gtList = getNewGtList(src, dest);

		if (canBeInserted) {									// set a cascade of canBeInserted to be sure not to process data if there is no need
			String format = getFormatString(dest, gtList);

			String line = getFullLine(dest, format);
			line += "\n";

			if (canBeInserted) {
				data.write(line);
			}
		}
	}


	/**
	 * @param src	the line to use as a model
	 * @param dest	the line to update
	 * @return the list of all genotypes
	 */
	private List<String> getNewGtList (VCFLine src, VCFLine dest) {
		List<String> gtList = new ArrayList<String>();

		for (String destGenomeName: destGenome) {
			String gtDest = dest.getGenotype(destGenomeName);
			String newGtDest = gtDest;

			if (genomeNameMap.containsKey(destGenomeName)) {
				String srcGenomeName = genomeNameMap.get(destGenomeName);
				String gtSrc = src.getGenotype(srcGenomeName);

				if (hasAlternative(gtSrc)) {
					canBeInserted = true;
					if (isGenotypePhased(gtSrc)) {
						String altSrc01 = getFormattedAlt(src.getAlternativeFromRawName(srcGenomeName, AlleleType.ALLELE01));
						String altSrc02 = getFormattedAlt(src.getAlternativeFromRawName(srcGenomeName, AlleleType.ALLELE02));

						int alleleDest01 = getAltIndex(dest.getAlternatives(), altSrc01);
						int alleleDest02 = getAltIndex(dest.getAlternatives(), altSrc02);

						if ((alleleDest01 == -1) || (alleleDest02 == -1)) {
							canBeInserted = false;
							System.err.println(src.getCHROM() + " " + src.getPOS() + ": Alternative '" + altSrc01 + "' and/or '" + altSrc02 + "' has not been found in alternatives field '" + dest.getAlternatives().toString() + "'.");
						} else {
							newGtDest = alleleDest01 + "|" + alleleDest02;
						}
					}
				}
			}
			gtList.add(newGtDest);
		}

		return gtList;
	}


	/**
	 * @param gt	a genotype
	 * @return true if the genotype codes for an alternative
	 */
	private boolean hasAlternative (String gt) {
		char allele01 = gt.charAt(0);
		char allele02 = gt.charAt(2);

		if (((allele01 == '.') || (allele01 == '0')) && ((allele02 == '.') || (allele02 == '0'))) {
			return false;
		}
		return true;
	}


	/**
	 * @param gt a genotype
	 * @return true if phased, false otherwise
	 */
	private boolean isGenotypePhased (String gt) {
		if (gt.charAt(1) == '|') {
			return true;
		}
		return false;
	}


	/**
	 * This method formats an unvalid alternative into an empty string.
	 * It does not change the alternative if it is correct.
	 * @param alt an alternative
	 * @return the formatted alternative
	 */
	private String getFormattedAlt (String alt) {
		if (alt == null) {
			alt = "";
		}
		return alt;
	}


	/**
	 * @param alternatives 	an array of alternatives
	 * @param alternative	an alternative
	 * @return the index of an alternative among an array of alternatives
	 */
	private int getAltIndex (String[] alternatives, String alternative) {
		int allele = -1;

		if (!alternative.isEmpty()) {
			for (int i = 0; i < alternatives.length; i++) {
				if (alternative.equals(alternatives[i])) {
					allele = i + 1;
					break;
				}
			}
		} else {
			allele = 0;
		}

		return allele;
	}


	/**
	 * @param dest		the line to update
	 * @param gtList	the list of new genotypes
	 * @return the line with all format fields for all genomes
	 */
	private String getFormatString (VCFLine dest, List<String> gtList) {
		String format = "";
		int plIndex = getPLIndex(dest);
		for (int i = 0; i < destGenome.size(); i++) {
			String genome = destGenome.get(i);
			String[] formatValues = dest.getFormatValues(genome);
			String formatChunk = gtList.get(i);

			boolean excludePL = false;
			if (!gtList.get(i).equals(dest.getGenotype(genome))){
				excludePL = true;
			}

			for (int j = 1; j < formatValues.length; j++) {
				if (excludePL && (j == plIndex)) {
					int num = Utils.split(formatValues[j], ',').length;
					String tmp = ":0";
					for (int k = 1; k < num; k++) {
						tmp += ",0";
					}
					formatChunk += tmp;
				} else {
					formatChunk += ":" + formatValues[j];
				}
			}
			format += formatChunk;

			if (i < (destGenome.size() - 1)) {
				format += "\t";
			}
		}

		return format;
	}


	/**
	 * @param dest the line to update
	 * @return the index of the PL field in the format field, -1 if it does not exist
	 */
	private int getPLIndex (VCFLine dest) {
		String[] format = dest.getFormat();
		for (int i = 0; i < format.length; i++) {
			if (format[i].equals("PL")){
				return i;
			}
		}
		return -1;
	}


	/**
	 * @param dest 		the line to update
	 * @param format	the format part of the line
	 * @return the complete new line to insert
	 */
	private String getFullLine (VCFLine dest, String format) {
		String line = "";

		for (int i = 0; i < 9; i++) {
			line += dest.getField(i) + "\t";
		}

		line += format;

		return line;
	}


	@Override
	public void processLine(FileScannerInterface fileAlgorithm)	throws IOException {}

}
