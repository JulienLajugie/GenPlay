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
package edu.yu.einstein.genplay.core.manager.project;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFBlank;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFIndel;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSV;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;


/**
 * This class manages a multi genome project.
 * It reads vcf files, analyzes them and perform synchronization algorithms to display information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeSynchronizer implements Serializable {

	private static final long serialVersionUID = 5101409095108321375L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	/**
	 * The default color for a stripe
	 */
	public static final 	Color 						DEFAULT_COLOR 				= Color.black;
	/**
	 * The default color for a insertion stripe
	 */
	public static final 	Color 						INSERTION_DEFAULT_COLOR 	= Color.green;
	/**
	 * The default color for a deletion stripe
	 */
	public static final 	Color 						DELETION_DEFAULT_COLOR 		= Color.red;
	/**
	 * The default color for a SNP stripe
	 */
	public static final 	Color 						SNPS_DEFAULT_COLOR			= Color.cyan;
	/**
	 * The default color for a SV stripe
	 */
	public static final 	Color 						SV_DEFAULT_COLOR 			= Color.magenta;


	private		MGMultiGenome					genomesInformation;				// Genomes information
	private		MetaGenomeSynchroniser			metaGenomeSynchroniser;			// Instance of the Meta Genome Synchroniser
	private		ReferenceGenomeSynchroniser		referenceGenomeSynchroniser;	// Instance of the Reference Genome Synchroniser
	private		SNPSynchroniser					snpSynchroniser;				// Instance of the SNP Synchroniser
	private 	Map<String, List<VCFReader>> 	genomeFileAssociation;			// Mapping between genome names and their reader.
	private		List<String>					genomeNames;					//
	private		CoordinateSystemType 			cst;
	private		boolean							dataComputed = false;			// Uses after every multi genome process


	private int[] stats = {0, 0, 0, 0, 0, 0, 0, 0};
	

	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomesInformation);
		out.writeObject(metaGenomeSynchroniser);
		out.writeObject(referenceGenomeSynchroniser);
		out.writeObject(snpSynchroniser);
		out.writeObject(genomeFileAssociation);
		out.writeObject(cst);
		out.writeBoolean(dataComputed);
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
		genomesInformation = (MGMultiGenome) in.readObject();
		metaGenomeSynchroniser = (MetaGenomeSynchroniser) in.readObject();
		referenceGenomeSynchroniser = (ReferenceGenomeSynchroniser) in.readObject();
		snpSynchroniser = (SNPSynchroniser) in.readObject();
		genomeFileAssociation = (Map<String, List<VCFReader>>) in.readObject();
		cst = (CoordinateSystemType) in.readObject();
		dataComputed = in.readBoolean();
	}


	/**
	 * Constructor of {@link GenomeSynchronizer}
	 */
	protected GenomeSynchronizer (List<Chromosome> chromosomeList) {
		this.genomesInformation = new MGMultiGenome();
		this.referenceGenomeSynchroniser = new ReferenceGenomeSynchroniser();
		this.metaGenomeSynchroniser = new MetaGenomeSynchroniser(chromosomeList);
		this.snpSynchroniser = new SNPSynchroniser();
		cst = CoordinateSystemType.METAGENOME;
		dataComputed = false;
	}


	/**
	 * Initializes the multi genome objects:
	 * - genome information
	 * - SNPs synchronizer
	 * @param chromosome 
	 */
	public void initializesGenomeSynchronizer (Chromosome chromosome) {
		genomesInformation.initMultiGenomeInformation(genomeNames);
		//snpSynchroniser.initializesSNPSynchroniser(chromosome, genomesInformation, null);
	}


	/**
	 * Sets the mapping table between vcf file and their related genomes.
	 * Initializes genomes information hierarchy (genomes > chromosomes).
	 * Initializes genomes for SNP management.
	 * @param genomeFileAssociation mapping between genome names and their files.
	 */
	public void setGenomeFileAssociation(Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
		genomeNames = new ArrayList<String>(this.genomeFileAssociation.keySet());
		Collections.sort(genomeNames);
	}


	/**
	 * Computes all operations in order to synchronize positions.
	 * @param referenceGenomeName the genome reference name (display name)
	 * @param chromosomeList the current chromosome list for multi genome project
	 * @throws IOException
	 */
	public void compute (String referenceGenomeName, List<Chromosome> chromosomeList) throws IOException {
		dataComputed = false;
		if (initialyzeData(chromosomeList)) {
			compileData(referenceGenomeName, chromosomeList);
			metaGenomeSynchroniser.computeGenomeSize();
			metaGenomeSynchroniser.refreshChromosomeReferences();
			//snpSynchroniser.refreshCurrentChromosome(metaGenomeSynchroniser.getChromosomeList());
			//showData();
			String info = "";
			info += "Indel:     " + stats[0] + "\n";
			info += "SV:        " + stats[1] + "\n";
			info += "SNPs:      " + stats[2] + "\n";
			info += "Breakends: " + stats[3] + "\n";
			info += "Valid alternative:      " + stats[7] + "\n";
			info += "Allele error (1/2):     " + stats[4] + "\n";
			info += "No variation (0/0):     " + stats[5] + "\n";
			info += "Reference error (-1/0): " + stats[6] + "\n";
			//System.out.println(info);
		}
	}


	/**
	 * Refreshes chromosome references from a chromosome list information.
	 * @param chromosomeList the chromosome list
	 */
	public void refreshChromosomeReferences (List<Chromosome> chromosomeList) {
		genomesInformation.refreshChromosomeReferences(chromosomeList);
	}


	/**
	 * @return true if data has been computed
	 */
	public boolean dataHasBeenComputed() {
		return dataComputed;
	}


	/**
	 * This method is called when SNPs have been synchronized.
	 */
	public void SNPhaveBeenComputed() {
		this.dataComputed = true;
	}


	/**
	 * Initializes positions according to a genome and a chromosome.
	 * Each VCF are scanned in order to find insertions and deletions.
	 * The offset position is unknown and is sets to 0 at this point.
	 * @throws IOException
	 */
	private boolean initialyzeData (List<Chromosome> chromosomeList) throws IOException {
		boolean valid = false;
		List<VCFReader> fileReaders = getReaderList();
		for (VCFReader reader: fileReaders) {
			valid = true;
			for (final Chromosome chromosome: chromosomeList) {
				//Adds the chromosome to the reference genome chromosome list
				referenceGenomeSynchroniser.addChromosome(chromosome.getName());

				//Performs query on the current VCF to get all data regarding the chromosome
				List<Map<String, Object>> result = reader.query(chromosome.getName(),
						0,
						chromosome.getLength());
				
				final List<String> genomeNames = getRequiredGenomeNamesInVCFFile(reader);

				//Analyze query results
				createPositions(chromosome, genomeNames, result, reader);
				result = null;
			}
		}
		return valid;
	}


	/**
	 * This method compares the list of genome names in the project and the genome names from a VCF file.
	 * It returns the list of genome from a VCF file only if they have been required.
	 * @param reader	the reader of the VCF file
	 * @return 			the list of genome names
	 */
	private List<String> getRequiredGenomeNamesInVCFFile (VCFReader reader) {
		final List<String> vcfGenomeNames = reader.getRawGenomesNames();
		List<String> requiredGenomeNames = new ArrayList<String>();
		for (String fullName: genomeNames) {
			if (vcfGenomeNames.contains(FormattedMultiGenomeName.getRawName(fullName))) {
				requiredGenomeNames.add(fullName);
			}
		}
		return requiredGenomeNames;
	}


	/**
	 * Creates insertion and deletion positions from the query results.
	 * @param chromosome	current chromosome
	 * @param genomeNames	genome raw names list
	 * @param result		query result
	 * @param reader 
	 */
	private void createPositions (Chromosome chromosome, List<String> genomeNames, List<Map<String, Object>> result, VCFReader reader) {
		if (result != null) {
			for (Map<String, Object> info: result) {	// Scans every result lines
				referenceGenomeSynchroniser.addPosition(chromosome.getName(), Integer.parseInt(info.get("POS").toString().trim()));
				MGPosition positionInformation = new MGPosition(chromosome, info, reader);
				
				for (String genomeName: genomeNames) {
					String alternative = null;
					String gt = positionInformation.getFormatValue(FormattedMultiGenomeName.getRawName(genomeName), "GT").toString();
					int alleleA = Integer.parseInt("" + gt.charAt(0));
					int alleleB;
					if (gt.length() == 3) {
						alleleB = Integer.parseInt("" + gt.charAt(2));
					} else {
						alleleB = alleleA;
					}

					if (alleleA != alleleB && alleleA != 0 && alleleB != 0) {
						//System.err.println("allele erreur");
						stats[4]++;
					} else {
						int pos = alleleA;
						if (pos == 0) {
							pos = alleleB;
						}
						pos--;
						if (pos >= 0) {
							String alt[] = positionInformation.getAlternative().split(",");
							if (pos < alt.length) {
								alternative = alt[pos];
								stats[7]++;
							} else {
								//System.out.println("the line contains an error: refer to an alternative index that does not exist");
								stats[6]++;
							}
						} else {
							//System.out.println("no variation for this genome");
							stats[5]++;
						}
					}

					if (alternative != null) {
						Variant variant = null;
						if (alternative.charAt(0) == '<') {
							variant = new VCFSV(genomeName, chromosome, positionInformation);
							stats[1]++;
						} else if (alternative.contains("[") || alternative.contains("]")) {
							//System.out.println("GenPlay does not support: Complex Rearrangements with Breakends");
							stats[3]++;
						} else {
							int refLength = positionInformation.getReference().length();
							int altLength = alternative.length();
							if (refLength == altLength && refLength == 1) {
								// SNP
								//variant = new VCFSNP(genomeName, chromosome, positionInformation);
								stats[2]++;
							} else {
								variant = new VCFIndel(genomeName, chromosome, positionInformation);
								stats[0]++;
							}

						}

						if (variant != null) {
							genomesInformation.addVariant(genomeName, chromosome, variant);
						}
					}
				}
			}
		}
	}


	/**
	 * Scans every positions in order to cross data from VCF files.
	 * Deletion does not involve modification in other tracks.
	 * Insertion involves modification in other tracks creating "Blank" positions.
	 */
	private void compileData (String referenceGenomeName, List<Chromosome> chromosomeList) {
		for (Chromosome chromosome: chromosomeList) {																// Scan by chromosome
			List<MGChromosome> currentChromosomeList =
				genomesInformation.getChromosomeInformationList(chromosome);										// List of all existing chromosome in VCF files
			referenceGenomeSynchroniser.setList(chromosome.getName());

			for (MGChromosome chromosomeInformation: currentChromosomeList) {										// Resets all index lists
				chromosomeInformation.resetIndexList();
			}

			while (referenceGenomeSynchroniser.isValidIndex()) {													// Scan by position
				List<Integer> insertPositions = new ArrayList<Integer>();											// List of all length insertion position. Used at the end to update all tracks.
				int currentRefPosition = referenceGenomeSynchroniser.getCurrentPosition();								// Current position of the reference genome
				for (MGChromosome chromosomeInformation: currentChromosomeList) {									// Scan VCF content by chromosome
					chromosomeInformation.setCurrentPosition(currentRefPosition);
					Variant currentInformation =
						chromosomeInformation.getCurrentVariant();													// Current position information according to a specific VCF file
					Variant previousInformation =
						chromosomeInformation.getPreviousPosition();												// Previous position information according to a specific VCF file

					if (currentInformation != null) {																// The chromosome can have an information at this position, or not.
						int genomePosition = chromosomeInformation.getGenomePosition();
						currentInformation.setGenomePosition(genomePosition);
						if (chromosomeInformation.isFirstVariant()) {												// If the current position is the first of the position list
							currentInformation.setInitialMetaGenomeOffset(0);										// Initial meta genome offset is zero
							currentInformation.setInitialReferenceOffset(0);										// Initial reference genome offset is zero
						} else {																					// If not
							currentInformation.setInitialMetaGenomeOffset(previousInformation.getNextMetaGenomePositionOffset());	// Initial meta genome offset must be set according to the previous position
							currentInformation.setInitialReferenceOffset(previousInformation.getNextReferencePositionOffset());		// Initial reference genome offset must be set according to the previous position
						}
						if (currentInformation.getType() == VariantType.INSERTION){									// If the current position is an insertion
							insertPositions.add(currentInformation.getLength());									// It is necessary to store its length in order to update other tracks
						}
					}

				}

				if (insertPositions.size() > 0 ) {
					int maxLength = getLongestLength(insertPositions);												// Maximum length of every insertion positions
					updateInsert(currentChromosomeList, currentRefPosition, maxLength);								// The other tracks must be updated
					updateReferenceGenome(referenceGenomeName, chromosome, currentRefPosition, maxLength);
					updateMetaGenome(chromosome, maxLength);														// Update the meta genome length
				}
				updatePreviousPosition (currentChromosomeList, currentRefPosition);									// The previous position is set with the current position
				referenceGenomeSynchroniser.nextIndex();															// Increases the current index
			}
			for (MGChromosome chromosomeInformation: currentChromosomeList) {										// Resets all index lists
				chromosomeInformation.resetIndexList();
			}
			genomesInformation.getChromosomeInformation(referenceGenomeName, chromosome).resetIndexList();
		}
		dataComputed = true;
	}


	/**
	 * Updates all positions in all tracks when an insertion happens.
	 * For a same reference position, more than one insertion can happen and their length is not necessary the same.
	 * @param chromosomeList	list of all concerned chromosome track
	 * @param refPosition		reference genome position
	 * @param maxLength			maximum length found in all insertion positions
	 */
	private void updateInsert (List<MGChromosome> chromosomeList, int refPosition, int maxLength) {
		for (MGChromosome chromosomeInformation: chromosomeList) {												// Scan VCF content by chromosome
			Variant position = chromosomeInformation.getVariant(refPosition);									// Gets the current position in a new variable
			if (position != null) {																				// If an information exists at this position
				if (position.getLength() < maxLength) {															// If the current event length is smaller than the maximum length found
					position.addExtraOffset(maxLength - position.getLength());									// The difference is added into the meta genome "extra" offset
				}
			} else { 																							// If there is no information, needs to add a "blank" position
				Variant previousPosition = chromosomeInformation.getPreviousPosition();

				String genomeName = chromosomeInformation.getGenomeInformation().getGenomeName();
				Chromosome chromosome = chromosomeInformation.getChromosome();
				Variant variant = new VCFBlank(genomeName, chromosome, maxLength);
				genomesInformation.addBlank(genomeName, chromosome, refPosition, variant);

				position = chromosomeInformation.getVariant(refPosition);
				if (previousPosition == null) {
					position.setGenomePosition(refPosition);													// Sets the relative genome position
					position.setInitialReferenceOffset(0);														// Sets the initial reference genome offset
					position.setInitialMetaGenomeOffset(0);														//Sets the initial meta genome offset
				} else {
					position.setGenomePosition(																	// Sets the relative genome position
							refPosition -
							previousPosition.getNextReferencePositionOffset());
					position.setInitialReferenceOffset(previousPosition.getNextReferencePositionOffset());		// Sets the initial reference genome offset
					position.setInitialMetaGenomeOffset(previousPosition.getNextMetaGenomePositionOffset());	//Sets the initial meta genome offset
				}
			}
		}
	}


	/**
	 * Updates reference genome positions, insertions in other genomes involve synchronization in the reference genome.
	 * @param chromosome		list of all concerned chromosome track
	 * @param refPosition		reference genome position
	 * @param maxLength			maximum length found in all insertion positions
	 */
	private void updateReferenceGenome (String referenceGenomeName, Chromosome chromosome, int refPosition, int maxLength) {
		MGChromosome chromosomeInformation = genomesInformation.getChromosomeInformation(referenceGenomeName, chromosome);
		String genomeName = chromosomeInformation.getGenomeInformation().getGenomeName();
		Variant variant = new VCFBlank(genomeName, chromosome, maxLength);
		genomesInformation.addBlank(genomeName, chromosome, refPosition, variant);

		Variant position = chromosomeInformation.getVariant(refPosition);
		if (chromosomeInformation.getPositionInformationList().size() > 1) {
			Variant previousPosition = chromosomeInformation.getPreviousPosition();
			position.setGenomePosition(																	// Sets the relative genome position
					refPosition -
					previousPosition.getNextReferencePositionOffset());
			position.setInitialReferenceOffset(previousPosition.getNextReferencePositionOffset());		// Sets the initial reference genome offset
			position.setInitialMetaGenomeOffset(previousPosition.getNextMetaGenomePositionOffset());	// Sets the initial meta genome offset
		} else {
			position.setGenomePosition(refPosition);													// Sets the relative genome position
			position.setInitialMetaGenomeOffset(0);														// Initial meta genome offset is zero
			position.setInitialReferenceOffset(0);
		}
		chromosomeInformation.updatePreviousPosition(refPosition);
	}


	/**
	 * Set previous position of every concerned chromosome using the current position
	 * @param currentChromosomeList	list of concerned chromosome
	 * @param currentRefPosition	current position (from the reference chromosome)
	 */
	private void updatePreviousPosition (List<MGChromosome> currentChromosomeList, int currentRefPosition) {
		for (MGChromosome chromosomeInformation: currentChromosomeList) {			// Scan by chromosome
			chromosomeInformation.updatePreviousPosition(currentRefPosition);		// Update the last index of the current chromosome information list
		}
	}


	/**
	 * Updates the length of the meta genome according to a chromosome
	 * @param chromosome	the chromosome
	 * @param length		the length to add
	 */
	private void updateMetaGenome (Chromosome chromosome, int length) {
		if (length > 0) {
			metaGenomeSynchroniser.updateChromosomeLength(chromosome, length);
		}
	}


	/**
	 * @param insertPositions 	list of insertion position
	 * @return 					the longest length of the position list
	 */
	private int getLongestLength (List<Integer> insertPositions) {
		int max = insertPositions.get(0);
		for (Integer currentLenght: insertPositions) {
			if (currentLenght > max) {
				max = currentLenght;
			} 
		}
		return max;
	}


	/**
	 * @return the list of readers
	 */
	public List<VCFReader> getReaderList () {
		List<VCFReader> list = new ArrayList<VCFReader>();
		for (List<VCFReader> readerList: genomeFileAssociation.values()) {
			for (VCFReader reader: readerList) {
				if (!list.contains(reader)) {
					list.add(reader);
				}
			}
		}
		return list;
	}


	/**
	 * Get a vcf reader object with a vcf file name.
	 * @param fileName 	the name of the vcf file
	 * @return			the reader
	 */
	public VCFReader getReaderFromName (String fileName) {
		List<VCFReader> list = getReaderList();
		for (VCFReader reader: list) {
			if (reader.getFile().getName().equals(fileName)) {
				return reader;
			}
		}
		return null;
	}


	/**
	 * @return the list of genome names
	 */
	public List<String> getGenomeNameList () {
		return genomeNames;
	}

	/**
	 * @return the total number of genome
	 */
	private int getGenomeNumber () {
		return genomeFileAssociation.size();
	}


	/**
	 * Creates an array with all genome names association.
	 * Used for display.
	 * @return	genome names association array
	 */
	public Object[] getFormattedGenomeArray () {
		String[] names = new String[getGenomeNumber() + 1];
		names[0] = ProjectManager.getInstance().getAssembly().getDisplayName();
		int index = 1;
		for (String name: genomeNames) {
			names[index] = name;
			index++;
		}
		return names;
	}


	/**
	 * @return the cst
	 */
	public CoordinateSystemType getCst() {
		return cst;
	}


	/**
	 * @param cst the cst to set
	 */
	public void setCst(CoordinateSystemType cst) {
		this.cst = cst;
	}


	/**
	 * @param genome 		the raw genome name
	 * @param chromosome 	the chromosome
	 * @return 				the chromosome information object
	 */
	public MGChromosome getChromosomeInformation(String genome, Chromosome chromosome) {
		return genomesInformation.getChromosomeInformation(genome, chromosome);
	}


	/**
	 * @param genome 		the raw genome name
	 * @return 				the genome information
	 */
	public MGGenome getGenomeInformation(String genome) {
		return genomesInformation.getGenomeInformation(genome);
	}


	/**
	 * @return the genomesInformation
	 */
	public MGMultiGenome getGenomesInformation() {
		return genomesInformation;
	}


	/**
	 * @return the genomeFileAssociation
	 */
	public Map<String, List<VCFReader>> getGenomeFileAssociation() {
		return genomeFileAssociation;
	}


	/**
	 * @return the metaGenomeSynchroniser
	 */
	public MetaGenomeSynchroniser getMetaGenomeSynchroniser() {
		return metaGenomeSynchroniser;
	}


	/**
	 * @return the referenceGenomeSynchroniser
	 */
	public ReferenceGenomeSynchroniser getReferenceGenomeSynchroniser() {
		return referenceGenomeSynchroniser;
	}


	/**
	 * @return the snpSynchroniser
	 */
	public SNPSynchroniser getSnpSynchroniser() {
		return snpSynchroniser;
	}


	//////////////////////////////Show methods

	/**
	 * Shows genomes information.
	 */
	public void showData () {
		genomesInformation.showData();
	}


	/**
	 * Shows the genome associations.
	 */
	public void showGenomesAssociation () {
		String result = "";
		for (String genomeName: genomeFileAssociation.keySet()) {
			result += genomeName + ":\n";
			for (VCFReader reader: genomeFileAssociation.get(genomeName)) {
				result += "\t" + reader.getFile().getPath() + "\n";
			}
		}
		System.out.println(result);
	}

}
