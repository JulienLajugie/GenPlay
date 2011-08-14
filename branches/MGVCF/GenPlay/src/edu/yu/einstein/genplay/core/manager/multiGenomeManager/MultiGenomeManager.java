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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFBlank;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFIndel;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSV;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.GenomePositionCalculation;


/**
 * This class manages a multi genome project.
 * It reads vcf files, analyzes them and perform synchronization algorithms to display information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeManager {

	/**
	 * FULL value for CHROMOSOME_LOADING_OPTION option involves the loading of every chromosomes when multi genome project starts
	 */
	public 	static final 	int 						FULL					 	= 1;
	
	/**
	 * SEQUENTIAL value for CHROMOSOME_LOADING_OPTION option involves the sequential loading (one by one) of chromosomes during a multi genome project (low memory cost)
	 */
	public static final 	int 						SEQUENTIAL 					= 0;
	
	/**
	 * CHROMOSOME_LOADING_OPTION to choose the chromosome loading mode
	 * It must be used only for development, some functionalities cannot work in a SEQUENTIAL mode.
	 * The loading of some type of file requires to perform operation on every chromosome,
	 * the SEQUENTIAL mode loading only one chromosome, it can lead to a null pointer exception error. 
	 */
	public static final		int							CHROMOSOME_LOADING_OPTION 	= SEQUENTIAL;

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

	
	private static 			MultiGenomeManager 			instance = null;		// unique instance of the singleton
	private 				Map<File, VCFReader> 		fileReaders;			// Mapping between files and their readers.
	private					MGMultiGenome				genomesInformation;		// Genomes information
	private					MetaGenomeManager			metaGenomeManager;		// Meta genome manager instance
	private					ReferenceGenomeManager		referenceGenomeManager;	// Reference genome manager instance
	private					CoordinateSystemType 		cst;
	private					boolean						hasBeenInitialized;		// Uses when multi genome manager has been initialized
	private					boolean						dataComputed = false;	// Uses after every multi genome process


	/**
	 * @return an instance of a {@link MultiGenomeManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static MultiGenomeManager getInstance() {
		if (instance == null) {
			synchronized(MultiGenomeManager.class) {
				if (instance == null) {
					instance = new MultiGenomeManager();
				}
			}
		}
		return instance;
	}


	/**
	 * Constructor of {@link MultiGenomeManager}
	 */
	private MultiGenomeManager () {
		this.genomesInformation = new MGMultiGenome();
		this.metaGenomeManager = MetaGenomeManager.getInstance();
		this.referenceGenomeManager = ReferenceGenomeManager.getInstance();
		this.metaGenomeManager.initChromosomeList();
		ProjectManager.getInstance().setMultiGenomeProject(true);
		this.metaGenomeManager.initializeChromosomeLength();
		cst = CoordinateSystemType.METAGENOME;
		hasBeenInitialized = false;
		dataComputed = false;
	}


	/**
	 * Initializes the multi genome manager
	 * @param fileReaders				mapping between files and their readers.
	 * @param genomeFileAssociation		mapping between genome names and their files
	 */
	public void init (Map<File, VCFReader> fileReaders, Map<String, List<File>> genomeFileAssociation) {
		this.fileReaders = fileReaders;
		genomesInformation.init(genomeFileAssociation);
	}


	/**
	 * Initializes genomes information.
	 */
	public void initMultiGenomeInformation () {
		genomesInformation.initMultiGenomeInformation();
	}


	/**
	 * Computes all operations in order to synchronize positions.
	 * @throws IOException
	 */
	public void compute () throws IOException {
		dataComputed = false;
		if (initialyzeData()) {
			compileData();
			MetaGenomeManager.getInstance().computeGenomeSize();
			MetaGenomeManager.getInstance().updateChromosomeList();
			//showData();
		}
	}


	/**
	 * @return true if data has been computed
	 */
	public boolean dataHasBeenComputed() {
		return dataComputed;
	}


	/**
	 * Initializes positions according to a genome and a chromosome.
	 * Each VCF are scanned in order to find insertions and deletions.
	 * The offset position is unknown and is sets to 0 at this point.
	 * @throws IOException
	 */
	private boolean initialyzeData () throws IOException {
		boolean valid = false;
		Map<String, Chromosome> chromosomeList = ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList();
		for (VCFReader reader: fileReaders.values()) {

			VCFType vcfType = reader.getVcfType();
			if (vcfType != VCFType.SNPS) {
				valid = true;
				for (final Chromosome chromosome: chromosomeList.values()) {
					//Adds the chromosome to the reference genome chromosome list
					referenceGenomeManager.addChromosome(chromosome.getName());

					//Performs query on the current VCF to get all data regarding the chromosome
					List<Map<String, Object>> result = reader.query(chromosome.getName(),
							0,
							chromosome.getLength());

					final List<String> genomeNames = genomesInformation.getGenomeNameList();

					//Analyse query results
					createPositions(chromosome, genomeNames, result, vcfType, reader);
					result = null;
				}
			}
		}
		return valid;
	}


	/**
	 * Creates insertion and deletion positions from the query results.
	 * @param chromosome	current chromosome
	 * @param genomeNames	genome raw names list
	 * @param result		query result
	 * @param reader 
	 */
	private void createPositions (Chromosome chromosome, List<String> genomeNames, List<Map<String, Object>> result, VCFType vcfType, VCFReader reader) {
		if (result != null) {
			for (Map<String, Object> info: result) {	// Scans every result lines
				referenceGenomeManager.addPosition(chromosome.getName(), Integer.parseInt(info.get("POS").toString().trim()));
				MGPosition positionInformation = new MGPosition(chromosome, info, reader);

				for (String genomeName: genomeNames) {

					Variant variant = null;
					if (vcfType == VCFType.INDELS) {
						variant = new VCFIndel(genomeName, chromosome, positionInformation);
					} else if (vcfType == VCFType.SV) {
						variant = new VCFSV(genomeName, chromosome, positionInformation);
					} else if (vcfType == VCFType.SNPS) {
						// would probably never happen
						variant = new VCFSNP(genomeName, chromosome, positionInformation);
					}

					genomesInformation.addVariant(genomeName, chromosome, variant);
				}
			}
		}
	}


	/**
	 * Scans every positions in order to cross data from VCF files.
	 * Deletion does not involve modification in other tracks.
	 * Insertion involves modification in other tracks creating "Blank" positions.
	 */
	private void compileData () {
		Map<String, Chromosome> chromosomeList = ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList();
		for (Chromosome chromosome: chromosomeList.values()) {														// Scan by chromosome
			List<MGChromosome> currentChromosomeList =
				genomesInformation.getChromosomeInformationList(chromosome);										// List of all existing chromosome in VCF files
			referenceGenomeManager.setList(chromosome.getName());

			for (MGChromosome chromosomeInformation: currentChromosomeList) {							// Resets all index lists
				chromosomeInformation.resetIndexList();
			}

			while (referenceGenomeManager.isValidIndex()) {															// Scan by position
				List<Integer> insertPositions = new ArrayList<Integer>();											// List of all length insertion position. Used at the end to update all tracks.
				int currentRefPosition = referenceGenomeManager.getCurrentPosition();								// Current position of the reference genome
				for (MGChromosome chromosomeInformation: currentChromosomeList) {						// Scan VCF content by chromosome
					chromosomeInformation.setCurrentPosition(currentRefPosition);
					Variant currentInformation =
						chromosomeInformation.getCurrentVariant();										// Current position information according to a specific VCF file
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
						if (GenomePositionCalculation.isInsertion(currentInformation)) {									// If the current position is an insertion
							insertPositions.add(currentInformation.getLength());									// It is necessary to store its length in order to update other tracks
						}
					}

				}

				if (insertPositions.size() > 0 ) {
					int maxLength = getLongestLength(insertPositions);												// Maximum length of every insertion positions
					updateInsert(currentChromosomeList, currentRefPosition, maxLength);								// The other tracks must be updated
					updateReferenceGenome(chromosome, currentRefPosition, maxLength);
					updateMetaGenome(chromosome, maxLength);														// Update the meta genome length
				}
				updatePreviousPosition (currentChromosomeList, currentRefPosition);									// The previous position is set with the current position
				referenceGenomeManager.nextIndex();																	// Increases the current index
			}
			for (MGChromosome chromosomeInformation: currentChromosomeList) {							// Resets all index lists
				chromosomeInformation.resetIndexList();
			}
			genomesInformation.getChromosomeInformation(ReferenceGenomeManager.getInstance().getReferenceName(), chromosome).resetIndexList();
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
		for (MGChromosome chromosomeInformation: chromosomeList) {								// Scan VCF content by chromosome
			Variant position = chromosomeInformation.getVariant(refPosition);							// Gets the current position in a new variable
			if (position != null) {																			// If an information exists at this position
				if (position.getLength() < maxLength) {														// If the current event length is smaller than the maximum length found
					position.addExtraOffset(maxLength - position.getLength());								// The difference is added into the meta genome "extra" offset
				}
			} else { 																						// If there is no information, needs to add a "blank" position
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
	private void updateReferenceGenome (Chromosome chromosome, int refPosition, int maxLength) {
		MGChromosome chromosomeInformation = genomesInformation.getChromosomeInformation(ReferenceGenomeManager.getInstance().getReferenceName(), chromosome);
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
			metaGenomeManager.updateChromosomeLength(chromosome, length);
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
	 * @return genome names association array
	 */
	public Object[] getFormattedGenomeArray () {
		return genomesInformation.getFormattedGenomeArray();
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
	 * @return the hasBeenInitialized
	 */
	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}


	/**
	 * set the hasBeenInitialized to true
	 */
	public void setHasBeenInitialized() {
		this.hasBeenInitialized = true;
	}


	////////////////////////////// Show methods

	/**
	 * Shows genomes information.
	 */
	public void showData () {
		genomesInformation.showData();
	}

}