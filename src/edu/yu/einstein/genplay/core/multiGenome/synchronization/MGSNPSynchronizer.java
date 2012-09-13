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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGMultiGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSNPSynchronizer implements Serializable {


	/** Generated serial version ID */
	private static final long serialVersionUID = 2617311727908279221L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private Map<String, List<AlleleType>> genomeNames;		// List of genome required for SNP display
	private Chromosome chromosome;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeNames);
		out.writeObject(chromosome);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		genomeNames = (Map<String, List<AlleleType>>) in.readObject();
		chromosome = (Chromosome) in.readObject();
	}


	/**
	 * Constructor of {@link MGSNPSynchronizer}
	 */
	public MGSNPSynchronizer () {
		this.genomeNames = new HashMap<String, List<AlleleType>>();
		this.chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
	}


	/**
	 * Main method of the SNP synchronizer.
	 * It computes the SNP information.
	 * @param genomeNames list of genome name with their required allele types
	 */
	public void compute (Map<String, List<AlleleType>> genomeNames) {
		Map<String, List<AlleleType>> genomesToAdd = new HashMap<String, List<AlleleType>>();
		Map<String, List<AlleleType>> genomesToDelete = new HashMap<String, List<AlleleType>>();

		Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();

		if (!this.chromosome.getName().equals(currentChromosome.getName())) {
			genomesToAdd = this.genomeNames;
			genomesToDelete = this.genomeNames;
		} else {
			List<AlleleType> alleleList = getAlleleTypeList();

			if (genomeNames.size() == 0) {
				genomesToDelete = this.genomeNames;
			} else {
				for (String genomeName: genomeNames.keySet()) {
					if (!this.genomeNames.containsKey(genomeName)) {
						genomesToAdd.put(genomeName, genomeNames.get(genomeName));
					} else {
						List<AlleleType> existingList = this.genomeNames.get(genomeName);
						List<AlleleType> currentList = genomeNames.get(genomeName);

						for (AlleleType alleleType: alleleList) {
							if (existingList.contains(alleleType) && !currentList.contains(alleleType)) {
								if (!genomesToDelete.containsKey(genomeName)) {
									genomesToDelete.put(genomeName, new ArrayList<AlleleType>());
								}
								genomesToDelete.get(genomeName).add(alleleType);
							} else if (!existingList.contains(alleleType) && currentList.contains(alleleType)) {
								if (!genomesToAdd.containsKey(genomeName)) {
									genomesToAdd.put(genomeName, new ArrayList<AlleleType>());
								}
								genomesToAdd.get(genomeName).add(alleleType);
							}
						}
					}
				}
			}
		}

		deleteSNP(genomesToDelete);

		this.chromosome = currentChromosome;

		addSNP(genomesToAdd);

		this.genomeNames = genomeNames;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////// Add SNPs methods


	private void addSNP (Map<String, List<AlleleType>> genomes) {
		if ((genomes != null) && (genomes.size() > 0)) {
			Map<VCFFile, List<String>> readers = getSNPReaders(genomes);
			MGMultiGenomeForDisplay multiGenomeForDisplay = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeForDisplay();

			for (VCFFile reader: readers.keySet()) {
				List<String> results = null;
				try {
					results = reader.getReader().query(chromosome.getName(), 0, chromosome.getLength());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (results != null) {
					VCFLine line = new VCFLine(null, null);
					for (String result: results) {
						line.initialize(result, reader.getHeader());
						if (hasSNP(line.getREF(), line.getALT())) {
							line.processForAnalyse();
							for (String genomeName: readers.get(reader)) {
								String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
								//String genoType = result.get(genomeRawName).toString().split(":")[0];
								String genoType = line.getGenotype(genomeRawName);
								for (AlleleType alleleType: genomes.get(genomeName)) {
									int pos = getAlternativePosition(genoType, alleleType);
									if (pos > 0) {
										String alternative = line.getAlternatives()[pos - 1];
										if (alternative.length() == 1) {
											MGVariantListForDisplay variantListForDisplay = null;
											if (alleleType == AlleleType.ALLELE01) {
												variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleA().getVariantList(chromosome, VariantType.SNPS);
											} else if (alleleType == AlleleType.ALLELE02) {
												variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleB().getVariantList(chromosome, VariantType.SNPS);
											}
											if (variantListForDisplay != null) {
												int referenceGenomePosition = line.getReferencePosition();
												float score = line.getQuality();
												if (score == -1) {
													score = 50;	// default value...
												}
												if (referenceGenomePosition != -1) {
													Variant variant = new SNPVariant(variantListForDisplay, referenceGenomePosition, score, 0);
													variantListForDisplay.getVariantList().add(variant);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			for (String genomeName: genomes.keySet()) {
				for (AlleleType alleleType: genomes.get(genomeName)) {
					MGVariantListForDisplay variantListForDisplay = null;
					if (alleleType == AlleleType.ALLELE01) {
						variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleA().getVariantList(chromosome, VariantType.SNPS);
					} else if (alleleType == AlleleType.ALLELE02) {
						variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleB().getVariantList(chromosome, VariantType.SNPS);
					}
					if (variantListForDisplay != null) {
						variantListForDisplay.sort();
					}
				}
			}
		}

	}


	private Map<VCFFile, List<String>> getSNPReaders (Map<String, List<AlleleType>> genomes) {
		List<VCFFile> allReaders = ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles();
		Map<VCFFile, List<String>> readers = new HashMap<VCFFile, List<String>>();

		for (VCFFile reader: allReaders) {
			for (String genomeName: genomes.keySet()) {
				if (reader.canManage(genomeName, VariantType.SNPS)) {
					if (!readers.containsKey(reader)) {
						readers.put(reader, new ArrayList<String>());
					}
					readers.get(reader).add(genomeName);
				}
			}
		}
		return readers;
	}


	private boolean hasSNP (String ref, String alt) {
		if (ref.length() == 1) {
			//String[] altArray = alt.split(",");
			String[] altArray = Utils.split(alt, ',');
			for (String element: altArray) {
				if (element.length() == 1) {
					return true;
				}
			}
		}
		return false;
	}


	private int getAlternativePosition (String format, AlleleType alleleType) {
		if (format.length() == 3) {
			String result = "";
			if (alleleType == AlleleType.ALLELE01) {
				result = format.substring(0, 1);
			} else if (alleleType == AlleleType.ALLELE02) {
				result = format.substring(2);
			}
			try {
				int pos = Integer.parseInt(result);
				return pos;
			} catch (Exception e) {
			}
		}
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////


	private void deleteSNP (Map<String, List<AlleleType>> genomes) {
		if ((genomes != null) && (genomes.size() > 0)) {
			MGMultiGenomeForDisplay multiGenome = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeForDisplay();

			List<AlleleType> alleleList = getAlleleTypeList();
			for (String genomeName: genomes.keySet()) {
				for (AlleleType alleleType: alleleList) {
					MGAlleleForDisplay allele = null;
					if (alleleType == AlleleType.ALLELE01) {
						allele = multiGenome.getGenomeInformation(genomeName).getAlleleA();
					} else if (alleleType == AlleleType.ALLELE02) {
						allele = multiGenome.getGenomeInformation(genomeName).getAlleleB();
					}
					if (allele != null) {
						allele.getVariantList(chromosome, VariantType.SNPS).clearVariantList();
					}
				}
			}
		}
	}


	private List<AlleleType> getAlleleTypeList () {
		List<AlleleType> alleleList = new ArrayList<AlleleType>();
		alleleList.add(AlleleType.ALLELE01);
		alleleList.add(AlleleType.ALLELE02);
		return alleleList;
	}


	/*private void printList (Map<String, List<AlleleType>> genomes) {
		String info = "";
		for (String genomeName: genomes.keySet()) {
			info += "genome: " + genomeName + " -> ";
			List<AlleleType> alleleList = genomes.get(genomeName);
			for (AlleleType alleleType: alleleList) {
				info += alleleType + "; ";
			}
		}
		System.out.println(info);
	}*/

}
