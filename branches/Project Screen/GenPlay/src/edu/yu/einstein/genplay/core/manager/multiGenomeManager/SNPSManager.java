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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.manager.multiGenomeManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFMultiGenomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFSNPInformation;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;


/**
 * This class manages SNPs information.
 * The main functionality is to create the list of SNPs in order to display them.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SNPSManager {

	private static 	SNPSManager 				instance;				// The instance of the class

	private 		boolean 					genomeChanged;			// Says if genome has changed compare to previous scan
	private 		boolean 					genomeWindowChanged;	// Says if genome window has changed compare to previous scan
	private 		double 						ratioThreshold;			// Ratio threshold to do not show up SNPs when zoom is not important enough
	
	//Dynamic variables
	private 		List<VCFSNPInformation> 	list;					// List of SNP position
	private 		List<String> 				fields;					// List of header for VCF file query
	private 		GenomeWindow 				genomeWindow;			// The current genome window
	private 		String 						fullGenomeName;			// The current full genome name
	private 		String 						groupName;				// The current group name
	private 		String 						rawName;				// The current genome raw name
	private 		VCFReader 					reader;					// The current VCF reader


	/**
	 * Constructor of {@link SNPSManager}
	 */
	private SNPSManager () {
		genomeChanged = false;
		genomeWindowChanged = false;
		list = new ArrayList<VCFSNPInformation>();
		ratioThreshold = 0.05;
		fullGenomeName = null;
		groupName = null;
		rawName = null;
		reader = null;
	}


	/**
	 * @return the instance of the singleton {@link SNPSManager}.
	 */
	public static SNPSManager getInstance () {
		if (instance == null) {
			instance = new SNPSManager();
		}
		return instance;
	}
	
	
	/**
	 * Initializes headers for VCF file queries
	 * @param genome the genome raw name
	 */
	private void initFields (String genome) {
		fields = new ArrayList<String>();
		fields.add("CHROM");
		fields.add("POS");
		fields.add("REF");
		fields.add("ALT");
		fields.add("FORMAT");
		if (genome != null) {
			fields.add(genome);
		}
	}


	/**
	 * @param fullGenomeName	the full current genome name
	 * @param genomeWindow		the current genome window
	 * @param xFactor			the current x ratio
	 * @return					the list of SNPs
	 */
	public List<VCFSNPInformation> getSNPSList (String fullGenomeName, GenomeWindow genomeWindow, double xFactor) {

		if (xFactor > ratioThreshold) {
			initChangements(fullGenomeName, genomeWindow);
			initNames();
			initReader();

			makeList();
			
			genomeChanged = false;
			genomeWindowChanged = false;
		} else {
			list = new ArrayList<VCFSNPInformation>();
		}

		return list;
	}


	/**
	 * Checks if genome and screen information has been changed in order to do not repeat some operation.
	 * @param fullGenomeName	the full current genome name
	 * @param genomeWindow		the current genome window
	 */
	private void initChangements(String fullGenomeName, GenomeWindow genomeWindow) {
		if (this.fullGenomeName == null || !this.fullGenomeName.equals(fullGenomeName)) {
			genomeChanged = true;
			this.fullGenomeName = fullGenomeName;
		}

		if (this.genomeWindow == null ||
				this.genomeWindow.getStart() != genomeWindow.getStart() ||
				this.genomeWindow.getStop() != genomeWindow.getStop()) {
			genomeWindowChanged = true;
			this.genomeWindow = genomeWindow;
		}
	}


	/**
	 * Initializes genome names for scanning process 
	 */
	private void initNames () {
		if (genomeChanged) {
			groupName = null;
			rawName = null;
			try {
				groupName = FormattedMultiGenomeName.getGroupName(fullGenomeName);
				rawName = FormattedMultiGenomeName.getRawName(fullGenomeName);
			} catch (Exception e) {}
			initFields(rawName);
		}
	}


	/**
	 * Initializes the VCF file reader 
	 */
	private void initReader () {
		if (genomeWindowChanged || genomeChanged) {
			VCFMultiGenomeInformation genomeInformation = MultiGenomeManager.getInstance().getMultiGenomeInformation();
			List<File> fileList = genomeInformation.getGenomeFilesAssociation().get(groupName);
			reader = null;
			if (fileList != null) {
				for (File file: fileList) {
					VCFType type = genomeInformation.getTypeFromVCF(file);
					if (type.equals(VCFType.SNPS)) {
						reader = MultiGenomeManager.getInstance().getReader(file);
					}
				}
			}
		}
	}


	/**
	 * Main algorithm for making the list of SNPs.
	 */
	private void makeList () {
		if (genomeWindowChanged || genomeChanged) {
			if (reader != null) {
				list = new ArrayList<VCFSNPInformation>();
				Chromosome chromosome = genomeWindow.getChromosome();
				int start = genomeWindow.getStart();
				int stop = genomeWindow.getStop();
				
				if (start < 0) {
					start = 0;
				} else {
					start = ShiftCompute.computeReversedShift(rawName, chromosome, start);
				}
				
				if (stop > MetaGenomeManager.getInstance().getGenomeLength()) {
					stop = (int) MetaGenomeManager.getInstance().getGenomeLength();
				} else {
					stop = ShiftCompute.computeReversedShift(rawName, chromosome, stop);
				}

				List<Map<String, Object>> results = null;

				try {
					results = reader.query(chromosome.getName(), start, stop, fields);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (results != null) {
					for (Map<String, Object> resultLine: results) {
						int genomePosition = Integer.parseInt(resultLine.get("POS").toString());
						int metaGenomePosition;
						try {
							metaGenomePosition = ShiftCompute.computeShift(rawName, chromosome, genomePosition);
						} catch (Exception e) {
							metaGenomePosition = genomePosition;
						}
						Nucleotide nReference = getAssociatedNucleotide(resultLine.get("REF").toString());
						Nucleotide nAlternative = getAssociatedNucleotide(resultLine.get("ALT").toString());

						Map<String, String> format = new HashMap<String, String>();
						String titles[] = resultLine.get("FORMAT").toString().split(":");
							String values[] = resultLine.get(rawName).toString().split(":");
							for (int i = 0; i < titles.length; i++) {
								format.put(titles[i], values[i]);
							}
						
						VCFSNPInformation info = new VCFSNPInformation(genomePosition, metaGenomePosition, nReference, nAlternative);
						
						info.setInfo(format);
						
						int[] gt = info.getGT();
						if (gt[0] == 0) {
							info.setOnFirstAllele(false);
						} else {
							info.setOnFirstAllele(true);
						}
						if (gt[1] == 0) {
							info.setOnSecondAllele(false);
						} else {
							info.setOnSecondAllele(true);
						}
						
						list.add(info);
					}
				}
			}
		}
	}


	/**
	 * @param n the raw nucleotide information
	 * @return	the associated nucleotide
	 */
	private Nucleotide getAssociatedNucleotide (String n) {
		Nucleotide nucleotide = null;
		if (n.equals("A")) {
			nucleotide = Nucleotide.ADENINE;
		} else if (n.equals("T")) {
			nucleotide = Nucleotide.THYMINE;
		} else if (n.equals("G")) {
			nucleotide = Nucleotide.GUANINE;
		} else if (n.equals("C")) {
			nucleotide = Nucleotide.CYTOSINE;
		}
		return nucleotide;
	}

}