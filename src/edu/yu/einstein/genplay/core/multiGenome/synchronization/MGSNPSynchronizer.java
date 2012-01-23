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
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.display.MGAlleleForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGMultiGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

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
		if (genomes != null && genomes.size() > 0) {
			Map<VCFReader, List<String>> readers = getSNPReaders(genomes);
			MGMultiGenomeForDisplay multiGenomeForDisplay = ProjectManager.getInstance().getMultiGenome().getMultiGenomeForDisplay();

			for (VCFReader reader: readers.keySet()) {
				List<String> genomeNames = transformList(readers.get(reader));
				List<String> fields = getColumnNamesForQuery(genomeNames);
				List<Map<String, Object>> results = null;
				try {
					results = reader.query(chromosome.getName(), 0, chromosome.getLength(), fields);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (results != null) {
					for (Map<String, Object> result: results) {
						if (hasSNP(result.get("REF").toString(), result.get("ALT").toString())) {
							String[] alternatives = result.get("ALT").toString().split(",");
							for (String genomeName: readers.get(reader)) {
								String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
								String genoType = result.get(genomeRawName).toString().split(":")[0];
								for (AlleleType alleleType: genomes.get(genomeName)) {
									int pos = getAlternativePosition(genoType, alleleType);
									if (pos > 0) {
										String alternative = alternatives[pos - 1];
										if (alternative.length() == 1) {
											MGVariantListForDisplay variantListForDisplay = null;
											if (alleleType == AlleleType.PATERNAL) {
												variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleA().getVariantList(chromosome, VariantType.SNPS);
											} else if (alleleType == AlleleType.MATERNAL) {
												variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleB().getVariantList(chromosome, VariantType.SNPS);
											}
											if (variantListForDisplay != null) {
												int referenceGenomePosition = getIntFromString(result.get("POS").toString());
												float score = getFloatFromString(result.get("QUAL").toString());
												if (score == -1) {
													score = 50;	// default value...
												}
												if (referenceGenomePosition != -1) {
													VariantInterface variant = new SNPVariant(variantListForDisplay, referenceGenomePosition, score, 0);
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
					if (alleleType == AlleleType.PATERNAL) {
						variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleA().getVariantList(chromosome, VariantType.SNPS);
					} else if (alleleType == AlleleType.MATERNAL) {
						variantListForDisplay = multiGenomeForDisplay.getGenomeInformation(genomeName).getAlleleB().getVariantList(chromosome, VariantType.SNPS);
					}
					if (variantListForDisplay != null) {
						variantListForDisplay.sort();
					}
				}
			}
		}

	}


	private Map<VCFReader, List<String>> getSNPReaders (Map<String, List<AlleleType>> genomes) {
		List<VCFReader> allReaders = ProjectManager.getInstance().getMultiGenome().getAllReaders();
		Map<VCFReader, List<String>> readers = new HashMap<VCFReader, List<String>>(); 

		for (VCFReader reader: allReaders) {
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


	private List<String> transformList (List<String> genomeFullNames) {
		List<String> genomeRawNames = new ArrayList<String>();
		for (String genomeFullName: genomeFullNames) {
			genomeRawNames.add(FormattedMultiGenomeName.getRawName(genomeFullName));
		}
		return genomeRawNames;
	}


	/**
	 * Creates the list of the column names necessary for a query.
	 * Four fields are static: POS, REF, ALT and INFO for the position synchronization.
	 * Name of the genomes (raw name) must be added dynamically according to the content of the VCF file and the project requirements.
	 * @param genomeRawNames list of genome raw names to add to the static list
	 * @return the list of column names necessary for a query
	 */
	private List<String> getColumnNamesForQuery (List<String> genomeRawNames) {
		List<String> columnNames = new ArrayList<String>();
		columnNames.add("POS");
		columnNames.add("REF");
		columnNames.add("ALT");
		columnNames.add("QUAL");
		for (String genomeName: genomeRawNames) {
			columnNames.add(genomeName);
		}
		return columnNames;
	}


	private boolean hasSNP (String ref, String alt) {
		if (ref.length() == 1) {
			String[] altArray = alt.split(",");
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
			if (alleleType == AlleleType.PATERNAL) {
				result = format.substring(0, 1);
			} else if (alleleType == AlleleType.MATERNAL) {
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


	private int getIntFromString (String s) {
		int result = -1;
		try {
			result = Integer.parseInt(s);
		} catch (Exception e) {}
		return result;
	}


	private float getFloatFromString (String s) {
		float result = (float) -1.0;
		try {
			result = Float.parseFloat(s);
		} catch (Exception e) {}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////


	private void deleteSNP (Map<String, List<AlleleType>> genomes) {
		if (genomes != null && genomes.size() > 0) {
			MGMultiGenomeForDisplay multiGenome = ProjectManager.getInstance().getMultiGenome().getMultiGenomeForDisplay();

			List<AlleleType> alleleList = getAlleleTypeList();
			for (String genomeName: genomes.keySet()) {
				for (AlleleType alleleType: alleleList) {
					MGAlleleForDisplay allele = null;
					if (alleleType == AlleleType.PATERNAL) {
						allele = multiGenome.getGenomeInformation(genomeName).getAlleleA();
					} else if (alleleType == AlleleType.MATERNAL) {
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
		alleleList.add(AlleleType.PATERNAL);
		alleleList.add(AlleleType.MATERNAL);
		return alleleList;
	}

}
