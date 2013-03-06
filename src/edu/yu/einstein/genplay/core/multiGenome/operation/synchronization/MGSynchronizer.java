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
package edu.yu.einstein.genplay.core.multiGenome.operation.synchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.MultiGenomeProject;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFScanner.VCFGenomeScanner;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFScanner.VCFScanner;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFScanner.VCFScannerReceiver;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFSampleStatistics;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGLineContent;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSAllele;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSGenome;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSOffset;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsOffsetList;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGSynchronizer implements VCFScannerReceiver, Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = -6135675382773268961L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	/** Index for reference */
	public final static int REFERENCE = -1;
	/** Index for no call */
	public final static int NO_CALL = -2;


	private final MultiGenomeProject multiGenomeProject;

	// Attributes used for the synchronization
	private Map<Chromosome, Integer> chromosomeIndexes;
	private ChromosomeListOfLists<MGSOffset> referenceOffsetList;
	private VCFFile currentFile;
	private VCFFileStatistics currentStatistics;
	private List<String> currentGenomes;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosomeIndexes);
		out.writeObject(referenceOffsetList);
		out.writeObject(currentFile);
		out.writeObject(currentStatistics);
		out.writeObject(currentGenomes);
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
		chromosomeIndexes = (Map<Chromosome, Integer>) in.readObject();
		referenceOffsetList = (ChromosomeListOfLists<MGSOffset>) in.readObject();
		currentFile = (VCFFile) in.readObject();
		currentStatistics = (VCFFileStatistics) in.readObject();
		currentGenomes = (List<String>) in.readObject();
	}


	/**
	 * Constructor of {@link MGSynchronizer}
	 * @param multiGenomeProject the multi genome instance
	 */
	public MGSynchronizer (MultiGenomeProject multiGenomeProject) {
		this.multiGenomeProject = multiGenomeProject;
		referenceOffsetList = multiGenomeProject.getMultiGenome().getReferenceGenome().getAllele().getOffsetList();
	}



	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	Read files & insert data

	/**
	 * Processes all files and insert required synchronization data into memory.
	 * @param genomes
	 * @param variations
	 * @param filters
	 * @throws IOException
	 */
	public void processFiles (List<String> genomes, List<VariantType> variations, List<MGFilter> filters) throws IOException {
		List<VCFFile> VCFFileList = multiGenomeProject.getAllVCFFiles();													// get all vcf readers

		for (VCFFile vcfFile: VCFFileList) {
			chromosomeIndexes = new HashMap<Chromosome, Integer>();
			currentFile = vcfFile;
			currentStatistics = vcfFile.getStatistics();
			currentGenomes = getRequiredGenomeNamesFromAReader(vcfFile);
			VCFScanner scanner = new VCFGenomeScanner(this, vcfFile);
			scanner.setGenomes(genomes);
			scanner.setVariations(variations);
			scanner.setFilters(filters);
			scanner.compute();
			currentStatistics.processStatistics();
		}
		multiGenomeProject.getFileContentManager().compact();
	}


	@Override
	public void processLine(VCFLine line) {
		// Set global information
		Chromosome chromosome = line.getChromosome();
		int referencePosition = line.getReferencePosition();								// get the reference genome position (POS field)

		updateFileStatistics(currentStatistics, line.getAlternativesTypes(), line.getAlternatives());

		// Set position information
		MGLineContent position = new MGLineContent();
		position.setReferenceGenomePosition(referencePosition);
		position.setScore(line.getQuality());
		position.setAlternatives(line.getAlternativesLength());
		Map<String, byte[]> genotypes = new HashMap<String, byte[]>();

		// Start genome scanning
		for (String genomeName: currentGenomes) {															// loop on every genome raw name
			MGSGenome genome = multiGenomeProject.getMultiGenome().getGenomeInformation(genomeName);
			String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
			String genotype = line.getGenotype(genomeRawName);											// get the related format value, split it (colon separated) into an array, get the first value: the genotype

			genotype = genotype.replace('|', '/');
			String[] currentAltIndexes = genotype.split("/");
			byte[] byteGenotypeArray = new byte[currentAltIndexes.length];
			for (int i = 0; i < currentAltIndexes.length; i++) {
				int currentAltIndex = VCFLineUtility.getAlleleIndex(currentAltIndexes[i]);

				switch (currentAltIndex) {
				case NO_CALL:
					byteGenotypeArray[i] = NO_CALL;
					break;
				case REFERENCE:
					byteGenotypeArray[i] = REFERENCE;
					break;
				default:
					byteGenotypeArray[i] = (byte) currentAltIndex;
					int alternativeLength = line.getAlternativesLength()[currentAltIndex];				// we retrieve its length
					VariantType variantType = line.getAlternativesTypes()[currentAltIndex];				// get the type of variant according to the length of the variation
					updateVariationSampleStatistics(currentStatistics.getSampleStatistics(genomeName), variantType, line.getAlternatives()[currentAltIndex]);
					currentFile.addVariantType(genomeName, variantType);								// notice the reader of the variant type

					if (variantType != VariantType.SNPS) {
						genome.getAllele(i).getOffsetList().get(chromosome).add(new MGSOffset(referencePosition, alternativeLength));

						if (variantType == VariantType.INSERTION) {
							referenceOffsetList.get(chromosome).add(new MGSOffset(referencePosition, alternativeLength));				// add the offset to the reference genome allele if it is an insertion
						}
					}
					break;
				}
			}

			updateGenotypeSampleStatistics(currentStatistics.getSampleStatistics(genomeName), line.getAlternativesTypes(), byteGenotypeArray);
			genotypes.put(genomeName, byteGenotypeArray);
		}

		// Finish position creation
		position.setGenotypes(genotypes);
		if (chromosomeIndexes.get(chromosome) == null) {
			chromosomeIndexes.put(chromosome, 0);
		}
		int currentIndex = chromosomeIndexes.get(chromosome);
		multiGenomeProject.getFileContentManager().getContent(currentFile, chromosome).addPosition(currentIndex, position);
		chromosomeIndexes.put(chromosome, currentIndex + 1);
	}


	/**
	 * Retrieves the name of the genomes required in the project that are present in a VCF file.
	 * A project does not necessary require all genomes present in a VCF file.
	 * @param vcfFile	VCF file
	 * @return			the list of genome names required for the project and present in the VCF file
	 */
	private List<String> getRequiredGenomeNamesFromAReader (VCFFile vcfFile) {
		List<String> requiredGenomeNames = new ArrayList<String>();
		List<String> allGenomeNames = multiGenomeProject.getGenomeNames();
		List<String> readerGenomeNames = vcfFile.getHeader().getGenomeNames();

		for (String readerGenomeName: readerGenomeNames) {
			if (allGenomeNames.contains(readerGenomeName)) {
				requiredGenomeNames.add(readerGenomeName);
			}
		}

		return requiredGenomeNames;
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
	private void updateGenotypeSampleStatistics (VCFSampleStatistics statistic, VariantType[] variantTypes, byte[] alleleIndexes) {
		boolean hemizygote = false;
		boolean homozygote = false;
		boolean heterozygote = false;
		if (alleleIndexes.length > 0) {
			if (alleleIndexes.length == 1) {
				hemizygote = true;
			} else {
				homozygote = isVariantHomozygote(alleleIndexes[0], alleleIndexes[1]);
				heterozygote = isVariantHeterozygote(alleleIndexes[0], alleleIndexes[1]);
			}

			for (VariantType variantType: variantTypes) {
				if (homozygote) {
					if (alleleIndexes[0] > -1) {
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
				} else if (hemizygote) {
					if (variantType == VariantType.SNPS) {
						statistic.incrementNumberOfHemizygoteSNPs();
					} else if (variantType == VariantType.INSERTION) {
						statistic.incrementNumberOfHemizygoteInsertions();
					} else if (variantType == VariantType.DELETION) {
						statistic.incrementNumberOfHemizygoteDeletions();
					}
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


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	Synchronizes the data


	/**
	 * Performs the synchronization for every genome of the project.
	 * The process is separated on 3 levels:
	 * - genomes
	 * - alleles
	 * - chromosomes
	 * (Makes the reading easier)
	 */
	public void performPositionSynchronization () {
		referenceOffsetList = multiGenomeProject.getMultiGenome().getReferenceGenome().getAllele().getOffsetList();
		synchronizeToGenomesLevel();
		multiGenomeProject.getMultiGenome().getReferenceGenome().synchronizePosition();
	}


	/**
	 * This method manages the position synchronization for every genome.
	 * It handles the genomes loop in order to process the synchronization for both alleles of each of them.
	 */
	private void synchronizeToGenomesLevel () {
		for (String genomeName: multiGenomeProject.getGenomeNames()) {																// scan on every genome
			MGSGenome genomeInformation = multiGenomeProject.getMultiGenome().getGenomeInformation(genomeName);						// current genome information
			List<MGSAllele> alleles = genomeInformation.getAlleles();

			for (MGSAllele allele: alleles) {
				ChromosomeListOfLists<MGSOffset> chromosomeAlleleAOffsetList = synchronizeToAlleleLevel(allele.getOffsetList());		// get the synchronized chromosome list of list for the current allele
				allele.setOffsetList(chromosomeAlleleAOffsetList);																	// set the current chromosome list of list of the current allele with the synchronized one
			}
		}
	}


	/**
	 * This method manages the synchronization for the chromosome list of list of an allele.
	 * It manages the chromosome loop in order to process the synchronization for every chromosome of this list.
	 * @param chromosomeAlleleOffsetList chromosome list of list of an allele
	 * @return a synchronized chromosome list of list
	 */
	private ChromosomeListOfLists<MGSOffset> synchronizeToAlleleLevel (ChromosomeListOfLists<MGSOffset> chromosomeAlleleOffsetList) {
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();						// get the number of chromosome
		ChromosomeListOfLists<MGSOffset> list = new ChromosomeArrayListOfLists<MGSOffset>();												// instantiate a new chromosome list of list (to insert the synchronized list of offset)

		for (int i = 0; i < chromosomeListSize; i++) {																					// scan on the number of chromosome (loop on ever chromosome)
			List<MGSOffset> referenceOffsetList = this.referenceOffsetList.get(i);														// get the list of offset from the reference genome for the current chromosome
			List<MGSOffset> alleleOffsetList = chromosomeAlleleOffsetList.get(i);														// get the list of offset from the current genome for the current chromosome
			List<MGSOffset> alleleOffsetListTmp = synchronizeToChromosomeLevel(referenceOffsetList, alleleOffsetList);					// get the synchronized offset list
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
	private List<MGSOffset> synchronizeToChromosomeLevel (List<MGSOffset> referenceOffsetList, List<MGSOffset> alleleOffsetList) {
		List<MGSOffset> list = new IntArrayAsOffsetList();

		int referenceOffsetIndex = 0;				// index of the current offset from the reference genome
		int alleleOffsetIndex = 0;					// index of the current offset from the genome

		int lastRefPosition = 0;					// last genome reference position of a variation
		int length = 0;								// total length of all insertion between two deletion

		boolean valid = true;

		while (valid) {

			MGSOffset currentReferenceOffset = getOffset(referenceOffsetList, referenceOffsetIndex);										// get the current reference offset
			MGSOffset currentAlleleOffset = getOffset(alleleOffsetList, alleleOffsetIndex);												// get the current offset from the current genome
			MGSOffset newOffset = null;																									// declare a new offset



			if ((currentReferenceOffset != null) && (currentAlleleOffset != null)) {													// if both offset exist
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

			} else if ((currentReferenceOffset == null) && (currentAlleleOffset != null)) {													// if only the offset from the current genome exists

				if (currentAlleleOffset.getValue() < 0) {																				// if the current offset is related to a deletion
					newOffset = getNewOffset(list, currentAlleleOffset, lastRefPosition, length, 0);									// get the new offset
					lastRefPosition = currentAlleleOffset.getPosition() + Math.abs(currentAlleleOffset.getValue()) + 1;
					length = 0;																											// reset the length counter
				} else if (currentAlleleOffset.getValue() > 0) {																		// if the current offset is related to an insertion
					length += currentAlleleOffset.getValue();																			// the length must be taken into account
				}
				alleleOffsetIndex++;																									// increase the current genome offset

			} else if ((currentReferenceOffset != null) && (currentAlleleOffset == null)) {													// if only the current offset from the reference exists

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


	/**
	 * Creates an offset
	 * @param offsetList		the full list of offsets
	 * @param currentOffset		the offset being processed
	 * @param lastRefPosition	the last reference genome position
	 * @param additionalLength	the total length of all insertion between two deletion
	 * @param additionalValue	only in the case where two or more insertion happen at the same position and the one of the actual offset is not the longest one
	 * @return					a new offset corresponding to the current process
	 */
	private MGSOffset getNewOffset (List<MGSOffset> offsetList, MGSOffset currentOffset, int lastRefPosition, int additionalLength, int additionalValue) {
		MGSOffset offset = null;
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

		offset = new MGSOffset(newGenomePosition, newOffsetValue);

		return offset;
	}


	/**
	 * Look for an offset in an offset list according to an index.
	 * Checks if the index is valid and return the offset.
	 * @param offsetList 	the list of offset
	 * @param index			the index of the offset in the list
	 * @return				the offset if the index is valid, null otherwise
	 */
	private MGSOffset getOffset (List<MGSOffset> offsetList) {
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
	private MGSOffset getOffset (List<MGSOffset> offsetList, int index) {
		if (index < offsetList.size()) {
			return offsetList.get(index);
		}
		return null;
	}

}
