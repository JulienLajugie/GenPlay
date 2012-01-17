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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.arrayList.IntArrayAsOffsetList;
import edu.yu.einstein.genplay.core.manager.project.MultiGenome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.IndelVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSynchronizer {

	private MultiGenome multiGenome;


	/**
	 * Constructor of {@link MGSynchronizer}
	 * @param multiGenome the multi genome instance
	 */
	public MGSynchronizer (MultiGenome multiGenome) {
		this.multiGenome = multiGenome;
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	Variant insertion section


	/**
	 * Reads the VCF files in order to store the position where a variation happen.
	 * Does not take into account SNP and variation equal to the reference (0/0)
	 * @throws IOException
	 */
	public void insertVariantposition () throws IOException {
		List<Chromosome> chromosomeList = ProjectManager.getInstance().getProjectChromosome().getChromosomeList();	// get the chromosome list
		List<VCFReader> readerList = multiGenome.getAllReaders();													// get all vcf readers

		List<AlleleType> alleleTypeList = new ArrayList<AlleleType>();
		alleleTypeList.add(AlleleType.PATERNAL);
		alleleTypeList.add(AlleleType.MATERNAL);

		for (Chromosome chromosome: chromosomeList) {																// loop on every chromosome
			for (VCFReader reader: readerList) {																	// loop on every vcf reader
				List<String> genomeRawNames = getRequiredGenomeNamesFromAReader(reader);							// get the genome raw names list
				List<String> columnNames = getColumnNamesForQuery(genomeRawNames);									// get the list of column to perform the query on the current vcf
				
				List<Map<String, Object>> result = reader.query(chromosome.getName(), 0, chromosome.getLength(), columnNames);	// perform the query on the current vcf: chromosome name, from 0 to its length
				
				for (Map<String, Object> VCFLine: result) {															// loop on every line of the result of the query

					int referencePosition = Integer.parseInt(VCFLine.get("POS").toString());						// get the reference genome position (POS field)
					String reference = VCFLine.get("REF").toString();												// get the reference value (REF field)
					String[] alternatives = VCFLine.get("ALT").toString().split(",");								// get the alternative values and split them into an array (comma separated)
					String info = VCFLine.get("INFO").toString();													// get the information of the variation (INFO field). Needs it if the variation is SV coded.
					float score;
					try {
						score = Float.parseFloat(VCFLine.get("QUAL").toString());
					} catch (Exception e) {
						score = 0;
					}

					for (String genomeRawName: genomeRawNames) {													// loop on every genome raw name

						String genotype = VCFLine.get(genomeRawName).toString().trim().split(":")[0];				// get the related format value, split it (colon separated) into an array, get the first value: the genotype 
						if (genotype.length() == 3) {																// the genotype must have 3 characters (eg: 0/0 0/1 1/0 1/1 0|0 0|1 1|0 1|1)

							for (AlleleType alleleType: alleleTypeList) {
								char alleleChar;
								if (alleleType == AlleleType.PATERNAL) {													// if we are processing the paternal allele
									alleleChar = VCFLine.get(genomeRawName).toString().charAt(0);							// we look at the first character
								} else {																					// if not, it means we are looking to the maternal allele
									alleleChar = VCFLine.get(genomeRawName).toString().charAt(2);							// and we look at the third character
								}

								if (alleleChar != '.' && alleleChar != '0') {												// if we have a variation
									int alleleLength = retrieveVariantLength(reference, alternatives, info, alleleChar);	// we retrieve its length
									VariantType variantType = getVariantType(alleleLength);									// get the type of variant according to the length of the variation
									reader.addVariantType(genomeRawName, variantType);										// notice the reader of the variant type
									if (variantType != VariantType.SNPS) {													// if it is not a SNP
										List<MGOffset> offsetList;
										if (alleleType == AlleleType.PATERNAL) {											// if we are processing the paternal allele
											offsetList = multiGenome.getMultiGenome().getGenomeInformation(genomeRawName).getAlleleA().getOffsetList().get(chromosome);	// we get the list of offset of the allele A
										} else {																			// if not, it means we are looking to the maternal allele
											offsetList = multiGenome.getMultiGenome().getGenomeInformation(genomeRawName).getAlleleB().getOffsetList().get(chromosome);	// we get the list of offset of the allele B
										}
										offsetList.add(new MGOffset(referencePosition, alleleLength));						// we insert the new offset

										if (variantType == VariantType.INSERTION) {											// if we are processing an insertion
											multiGenome.getMultiGenome().getReferenceGenome().getAllele().getOffsetList().get(chromosome).add(new MGOffset(referencePosition, alleleLength));				// we insert it into the reference genome allele
											MGVariantListForDisplay variantListForDisplay = multiGenome.getMultiGenomeForDisplay().getGenomeInformation(genomeRawName).getAlleleA().getVariantList(chromosome, VariantType.INSERTION); // we also insert it to the display data structure
											VariantInterface variant = new IndelVariant(variantListForDisplay, referencePosition, alleleLength, score, 0);
											variantListForDisplay.getVariantList().add(variant);
										} else {																			// if it is not an insertion it is a deletion
											MGVariantListForDisplay variantListForDisplay = multiGenome.getMultiGenomeForDisplay().getGenomeInformation(genomeRawName).getAlleleA().getVariantList(chromosome, VariantType.DELETION); // we only insert it to the display data structure
											VariantInterface variant = new IndelVariant(variantListForDisplay, referencePosition, alleleLength, score, 0);
											variantListForDisplay.getVariantList().add(variant);
										}
									}
								}
							}
						} else {
							System.err.println("FORMAT field for the genome " + genomeRawName + " (" + chromosome.getName() + ") at the position " + referencePosition + " of the reference is invalid: GT = " + genotype + " (length: " + genotype.length() + ")");
						}
					}
				}
			}
		}
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
		columnNames.add("INFO");
		for (String genomeRawName: genomeRawNames) {
			columnNames.add(genomeRawName);
		}
		return columnNames;
	}


	/**
	 * Retrieves the name of the genomes required in the project that are present in a VCF file.
	 * A project does not necessary require all genomes present in a VCF file.
	 * @param reader	VCF reader of the VCF file
	 * @return			the list of genome names required for the project and present in the VCF file
	 */
	private List<String> getRequiredGenomeNamesFromAReader (VCFReader reader) {
		List<String> requiredGenomeNames = new ArrayList<String>();
		List<String> allGenomeRawNames = multiGenome.getAllGenomeRawNames(); 
		List<String> readerGenomeRawNames = reader.getRawGenomesNames();

		for (String readerGenomeRawName: readerGenomeRawNames) {
			if (allGenomeRawNames.contains(readerGenomeRawName)) {
				requiredGenomeNames.add(readerGenomeRawName);
			}
		}

		return requiredGenomeNames;
	}


	/**
	 * Retrieves the length of a variation using the reference and the alternative.
	 * The index of the alternative is given by the FORMAT field of the genome.
	 * If the alternative is a structural variant, the length is given by the SVLEN INFO attributes 
	 * @param reference		REF field
	 * @param alternatives	array of alternatives
	 * @param info			INFO field
	 * @param indexChar		character from the FORMAT field of a genome
	 * @return	the length of the variation
	 */
	private int retrieveVariantLength (String reference, String[] alternatives, String info, char indexChar) {
		int length = 0;

		if (indexChar != '.' && indexChar != '0') {
			int index = Integer.parseInt(indexChar + "") - 1;
			String value = alternatives[index];
			if (value.charAt(0) == '<') {
				String lengthPattern = "SVLEN=";
				int lengthPatternIndex = info.indexOf(lengthPattern) + lengthPattern.length();
				int nextCommaIndex = info.indexOf(";", lengthPatternIndex);
				if (nextCommaIndex == -1) {
					length = Integer.parseInt(info.substring(lengthPatternIndex));
				} else {
					length = Integer.parseInt(info.substring(lengthPatternIndex, nextCommaIndex));
				}
			} else {
				length = value.length() - reference.length();
			}
		}

		return length;
	}


	private VariantType getVariantType (int variationLength) {
		if (variationLength < 0) {
			return VariantType.DELETION;
		} else if (variationLength > 0) {
			return VariantType.INSERTION;
		} else if (variationLength == 0) {
			return VariantType.SNPS;
		} else {
			return null;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Synchronization section


	/**
	 * Performs the synchronization for every genome of the project.
	 */
	public void performPositionSynchronization () {
		synchronizeToGenomesLevel();
		multiGenome.getMultiGenome().getReferenceGenome().synchronizePosition();
	}


	/**
	 * This method manages the position synchronization for every genome.
	 * It handles the genomes loop in order to process the synchronization for both alleles of each of them.
	 */
	private void synchronizeToGenomesLevel () {
		for (String genomeName: multiGenome.getGenomeNames()) {																// scan on every genome
			MGGenome genomeInformation = multiGenome.getMultiGenome().getGenomeInformation(genomeName);						// current genome information 

			ChromosomeListOfLists<MGOffset> chromosomeAlleleAOffsetList = synchronizeToAlleleLevel(genomeInformation.getAlleleA().getOffsetList());		// get the synchronized chromosome list of list for the allele A
			ChromosomeListOfLists<MGOffset> chromosomeAlleleBOffsetList = synchronizeToAlleleLevel(genomeInformation.getAlleleB().getOffsetList());		// get the synchronized chromosome list of list for the allele B

			genomeInformation.getAlleleA().setOffsetList(chromosomeAlleleAOffsetList);						// set the current chromosome list of list of the allele A with the synchronized one
			genomeInformation.getAlleleB().setOffsetList(chromosomeAlleleBOffsetList);						// set the current chromosome list of list of the allele B with the synchronized one
		}
	}


	/**
	 * This method manages the synchronization for the chromosome list of list of an allele.
	 * It manages the chromosome loop in order to process the synchronization for every chromosome of this list.
	 * @param chromosomeAlleleOffsetList chromosome list of list of an allele
	 * @return a synchronized chromosome list of list
	 */
	private ChromosomeListOfLists<MGOffset> synchronizeToAlleleLevel (ChromosomeListOfLists<MGOffset> chromosomeAlleleOffsetList) {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();						// get the number of chromosome

		ChromosomeListOfLists<MGOffset> chromosomeReferenceListOfList = multiGenome.getMultiGenome().getReferenceGenome().getAllele().getOffsetList();	// get the chromosome list of list of the reference genome
		ChromosomeListOfLists<MGOffset> list = new ChromosomeArrayListOfLists<MGOffset>();												// instantiate a new chromosome list of list (to insert the synchronized list of offset)

		for (int i = 0; i < chromosomeListSize; i++) {																					// scan on the number of chromosome (loop on ever chromosome)
			List<MGOffset> referenceOffsetList = chromosomeReferenceListOfList.get(i);													// get the list of offset from the reference genome for the current chromosome
			List<MGOffset> alleleOffsetList = chromosomeAlleleOffsetList.get(i);														// get the list of offset from the current genome for the current chromosome
			List<MGOffset> alleleOffsetListTmp = synchronizeToChromosomeLevel(referenceOffsetList, alleleOffsetList);					// get the synchronized offset list
			list.add(alleleOffsetListTmp);																								// add the synchronized offset list to the chromosome list of list
		}
		return list;																													// return the chromosome list of list recently created
	}


	/**
	 * This method performs the synchronization of a list of offset using the offset list from the reference genome.
	 * @param referenceOffsetList	offset list from the reference genome
	 * @param alleleOffsetList		offset list to synchronize
	 * @return a synchronized list of offset
	 */
	private List<MGOffset> synchronizeToChromosomeLevel (List<MGOffset> referenceOffsetList, List<MGOffset> alleleOffsetList) {
		List<MGOffset> list = new IntArrayAsOffsetList();

		int referenceOffsetIndex = 0;				// index of the current offset from the reference genome
		int alleleOffsetIndex = 0;					// index of the current offset from the genome

		int lastRefPosition = 0;					// last genome reference position of a variation
		int length = 0;								// total length of all insertion between two deletion

		boolean valid = true;

		while (valid) {
			MGOffset currentReferenceOffset = getOffset(referenceOffsetList, referenceOffsetIndex);										// get the current reference offset
			MGOffset currentAlleleOffset = getOffset(alleleOffsetList, alleleOffsetIndex);												// get the current offset from the current genome
			MGOffset newOffset = null;																									// declare a new offset

			if (currentReferenceOffset != null && currentAlleleOffset != null) {														// if both offset exist

				if (currentAlleleOffset.getPosition() == currentReferenceOffset.getPosition()) {										// if both position are similar
					if (currentReferenceOffset.getValue() > currentAlleleOffset.getValue()) {											// if the value from the reference offset is higher than the one from the current allele, it means that an other genome of the project contains a bigger insertion.
						newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, currentAlleleOffset.getValue());	// get the new offset
						lastRefPosition = currentReferenceOffset.getPosition() + 1;														// the last reference position is the current reference position + 1 
						length = 0;																										// reset the length counter
					} else if (currentReferenceOffset.getValue() == currentAlleleOffset.getValue()) {									// if both value (length) are equal, there is no new offset
						length += currentReferenceOffset.getValue();																	// but the length must be taken into account
					}
					referenceOffsetIndex++;																								// increase the current reference offset
					alleleOffsetIndex++;																								// increase the current genome offset
				} else if(currentAlleleOffset.getPosition() < currentReferenceOffset.getPosition()) {
					if (currentAlleleOffset.getValue() < 0) {																			// if the current offset is related to a deletion
						newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, 0);	// get the new offset
						lastRefPosition = currentAlleleOffset.getPosition() + Math.abs(currentAlleleOffset.getValue()) + 1;
						length = 0;																										// reset the length counter
					} else if (currentAlleleOffset.getValue() > 0) {																	// if the current offset is related to an insertion
						length += currentAlleleOffset.getValue();																		// the length must be taken into account
					}
					alleleOffsetIndex++;																								// increase the current genome offset only
				} else if(currentReferenceOffset.getPosition() < currentAlleleOffset.getPosition()) {
					newOffset = getNewOffset(list, currentReferenceOffset, lastRefPosition, length, 0);									// get the new offset
					lastRefPosition = currentReferenceOffset.getPosition() + 1;
					length = 0;																											// reset the length counter
					referenceOffsetIndex++;																								// increase the current reference offset only
				}

			} else if (currentReferenceOffset == null && currentAlleleOffset != null) {													// if only the offset from the current genome exists

				if (currentAlleleOffset.getValue() < 0) {																				// if the current offset is related to a deletion
					newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, 0);									// get the new offset
					lastRefPosition = currentAlleleOffset.getPosition() + Math.abs(currentAlleleOffset.getValue()) + 1;
					length = 0;																											// reset the length counter
				} else if (currentAlleleOffset.getValue() > 0) {																		// if the current offset is related to an insertion
					length += currentAlleleOffset.getValue();																			// the length must be taken into account
				}
				alleleOffsetIndex++;																									// increase the current genome offset

			} else if (currentReferenceOffset != null && currentAlleleOffset == null) {													// if only the current offset from the reference exists

				newOffset = getNewOffset(list, currentReferenceOffset, lastRefPosition, length, 0);										// get the new offset
				lastRefPosition = currentReferenceOffset.getPosition() + 1;
				length = 0;																												// reset the length counter
				referenceOffsetIndex++;																									// increase the current reference offset only

			} else {
				valid = false;
			}

			if (newOffset != null) {
				list.add(newOffset);
			}
		}

		return list;
	}


	private MGOffset getNewOffset (List<MGOffset> offsetList, MGOffset currentOffset, int lastRefPosition, int additionalLength, int additionalValue) {
		MGOffset offset = null;
		int newGenomePosition = 0;
		int newOffsetValue = 0;

		if (offsetList.size() == 0) {
			newGenomePosition = currentOffset.getPosition();
		} else {
			newGenomePosition = (currentOffset.getPosition() - lastRefPosition) + getOffset(offsetList).getPosition() + additionalLength;
			newOffsetValue = getOffset(offsetList).getValue();
		}
		newOffsetValue += Math.abs(currentOffset.getValue());
		newGenomePosition += additionalValue + 1;

		offset = new MGOffset(newGenomePosition, newOffsetValue);

		return offset;
	}


	/**
	 * Look for an offset in an offset list according to an index.
	 * Checks if the index is valid and return the offset.
	 * @param offsetList 	the list of offset
	 * @param index			the index of the offset in the list
	 * @return				the offset if the index is valid, null otherwise
	 */
	private MGOffset getOffset (List<MGOffset> offsetList) {
		if (offsetList.size() > 0) {
			return offsetList.get(offsetList.size() - 1);
		}
		return null;
	}


	/**
	 * Look for an offset in an offset list according to an index.
	 * Checks if the index is valid and return the offset.
	 * @param offsetList 	the list of offset
	 * @param index			the index of the offset in the list
	 * @return				the offset if the index is valid, null otherwise
	 */
	private MGOffset getOffset (List<MGOffset> offsetList, int index) {
		if (index < offsetList.size()) {
			return offsetList.get(index);
		}
		return null;
	}

}
