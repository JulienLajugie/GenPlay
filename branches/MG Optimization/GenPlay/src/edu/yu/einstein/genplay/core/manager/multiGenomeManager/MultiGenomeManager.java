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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFBlank;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGMultiGenomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPositionInformation;
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
	public 	static final 	int 						SEQUENTIAL 					= 0;
	/**
	 * CHROMOSOME_LOADING_OPTION to choose the chromosome loading mode
	 */
	public 	static			int							CHROMOSOME_LOADING_OPTION 	= SEQUENTIAL;

	private static final 	Color 						DEFAULT_COLOR 				= Color.black;
	private static final 	Color 						INSERTION_DEFAULT_COLOR 	= Color.green;
	private static final 	Color 						DELETION_DEFAULT_COLOR 		= Color.red;
	private static final 	Color 						SNPS_DEFAULT_COLOR			= Color.cyan;
	private static final 	Color 						SV_DEFAULT_COLOR 			= Color.magenta;

	private static 			MultiGenomeManager 			instance = null;		// unique instance of the singleton
	private 				Map<File, VCFReader> 		fileReaders;			// VCF Readers for every VCF files
	private					MGMultiGenomeInformation	genomesInformation;		// Genomes information
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
		this.genomesInformation = new MGMultiGenomeInformation();
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
	 * Sets genome information.
	 * @param genomeGroupAssociation	association between groups and genome names
	 * @param genomeFilesAssociation	association between groups and VCF files
	 * @param genomeNamesAssociation	association between genome raw names and explicit names
	 * @param filesTypeAssociation 		association between VCF types and VCF files.
	 */
	public void setGenomes (Map<String, List<String>> genomeGroupAssociation, 
			Map<String,List<File>> genomeFilesAssociation,
			Map<String, String> genomeNamesAssociation,
			Map<VCFType, List<File>> filesTypeAssociation) {
		genomesInformation.setGenomes(genomeGroupAssociation, genomeFilesAssociation, genomeNamesAssociation, filesTypeAssociation);
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
		initialyzeVCFReaders();
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
	 * Initializes VCF readers for every VCF file.
	 * Each VCF file has is own reader.
	 * @throws IOException
	 */
	private void initialyzeVCFReaders () throws IOException {
		fileReaders = new HashMap<File, VCFReader>();
		List<File> list = genomesInformation.getVCFFiles();
		for (File vcf: list) {
			fileReaders.put(vcf, new VCFReader(vcf));
		}
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
		for (final File vcf: fileReaders.keySet()) {

			VCFType vcfType = genomesInformation.getTypeFromVCF(vcf);
			if (vcfType != VCFType.SNPS) {
				VCFReader reader = fileReaders.get(vcf);
				valid = true;
				for (final Chromosome chromosome: chromosomeList.values()) {
					//Adds the chromosome to the reference genome chromosome list
					referenceGenomeManager.addChromosome(chromosome.getName());

					//Performs query on the current VCF to get all data regarding the chromosome
					List<Map<String, Object>> result = reader.query(chromosome.getName(),
							0,
							chromosome.getLength());

					
					final List<String> genomeNames = getRawGenomeNames(reader);

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
				//System.out.println();
				MGPositionInformation positionInformation = new MGPositionInformation(chromosome, info, reader);
				for (String genomeName: genomeNames) {
					genomesInformation.addInformation(	genomeName,
							chromosome,
							Integer.parseInt(info.get("POS").toString()),
							info,
							positionInformation,
							vcfType);
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
			List<MGChromosomeInformation> currentChromosomeList =
				genomesInformation.getCurrentChromosomeInformation(chromosome);										// List of all existing chromosome in VCF files
			referenceGenomeManager.setList(chromosome.getName());

			for (MGChromosomeInformation chromosomeInformation: currentChromosomeList) {							// Resets all index lists
				chromosomeInformation.resetIndexList();
			}

			while (referenceGenomeManager.isValidIndex()) {															// Scan by position
				List<Integer> insertPositions = new ArrayList<Integer>();											// List of all length insertion position. Used at the end to update all tracks.
				int currentRefPosition = referenceGenomeManager.getCurrentPosition();								// Current position of the reference genome
				for (MGChromosomeInformation chromosomeInformation: currentChromosomeList) {						// Scan VCF content by chromosome
					chromosomeInformation.setCurrentPosition(currentRefPosition);
					Variant currentInformation =
						chromosomeInformation.getCurrentPositionInformation();										// Current position information according to a specific VCF file
					Variant previousInformation =
						chromosomeInformation.getPreviousPosition();												// Previous position information according to a specific VCF file

					if (currentInformation != null) {																// The chromosome can have an information at this position, or not.
						int genomePosition = chromosomeInformation.getGenomePosition();
						currentInformation.setGenomePosition(genomePosition);
						if (chromosomeInformation.isFirstPosition()) {												// If the current position is the first of the position list
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
			for (MGChromosomeInformation chromosomeInformation: currentChromosomeList) {							// Resets all index lists
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
	private void updateInsert (List<MGChromosomeInformation> chromosomeList, int refPosition, int maxLength) {
		for (MGChromosomeInformation chromosomeInformation: chromosomeList) {								// Scan VCF content by chromosome
			Variant position = chromosomeInformation.getPosition(refPosition);							// Gets the current position in a new variable
			if (position != null) {																			// If an information exists at this position
				if (position.getLength() < maxLength) {														// If the current event length is smaller than the maximum length found
					position.addExtraOffset(maxLength - position.getLength());								// The difference is added into the meta genome "extra" offset
				}
			} else { 																						// If there is no information, needs to add a "blank" position
				Variant previousPosition = chromosomeInformation.getPreviousPosition();
				Variant blank = new VCFBlank(chromosomeInformation.getGenomeInformation().getGenomeFullName(), chromosomeInformation.getChromosome(), maxLength);
				chromosomeInformation.addBlank(refPosition, blank);
				position = chromosomeInformation.getPosition(refPosition);
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
		MGChromosomeInformation chromosomeInformation = genomesInformation.getChromosomeInformation(ReferenceGenomeManager.getInstance().getReferenceName(), chromosome);
		Variant blank = new VCFBlank(ReferenceGenomeManager.getInstance().getReferenceName(), chromosome, maxLength);
		chromosomeInformation.addBlank(refPosition, blank);

		Variant position = chromosomeInformation.getPosition(refPosition);
		if (chromosomeInformation.getPositionInformationList().size() > 1) {
			Variant previousPosition = chromosomeInformation.getPreviousPosition();
			position.setGenomePosition(																	// Sets the relative genome position
					refPosition -
					previousPosition.getNextReferencePositionOffset());
			position.setInitialReferenceOffset(previousPosition.getNextReferencePositionOffset());		// Sets the initial reference genome offset
			position.setInitialMetaGenomeOffset(previousPosition.getNextMetaGenomePositionOffset());	//Sets the initial meta genome offset
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
	private void updatePreviousPosition (List<MGChromosomeInformation> currentChromosomeList, int currentRefPosition) {
		for (MGChromosomeInformation chromosomeInformation: currentChromosomeList) {	// Scan by chromosome
			chromosomeInformation.updatePreviousPosition(currentRefPosition);			// Update the last index of the current chromosome information list
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
	 * Creates a raw genome names according to the ones required and specific to a VCF file
	 * @param reader the VCF file reader
	 * @return the list containing the required raw genome name(s) from the VCF file 
	 */
	private List<String> getRawGenomeNames (VCFReader reader) {
		final List<String> allRawGenomeNames = genomesInformation.getAllRawGenomeNames();
		final List<String> VCFRawGenomeNames = reader.getRawGenomesNames();
		List<String> names = new ArrayList<String>();
		for (String name: VCFRawGenomeNames) {
			if (allRawGenomeNames.contains(name)) {
				names.add(name);
			}
		}
		return names;
	}


	/**
	 * @return the multiGenomeInformation
	 */
	public MGMultiGenomeInformation getMultiGenomeInformation() {
		return genomesInformation;
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
	 * @param usualName	the understandable genome name
	 * @return	the raw genome name
	 */
	public String getRawGenomeName (String usualName) {
		return genomesInformation.getRawGenomeName(usualName);
	}


	/**
	 * @param vcf 	a VCF file
	 * @return		the associated VCF file
	 */
	public VCFReader getReader (File vcf) {
		return fileReaders.get(vcf);
	}


	/**
	 * @param genome 		the raw genome name
	 * @param chromosome 	the chromosome
	 * @return 				the chromosome information object
	 */
	public MGChromosomeInformation getChromosomeInformation(String genome, Chromosome chromosome) {
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


	//////////////////////////// getColor methods

	/**
	 * @return the defaultColor
	 */
	public static Color getDefaultColor() {
		return DEFAULT_COLOR;
	}


	/**
	 * @return the insertionDefaultColor
	 */
	public static Color getInsertionDefaultColor() {
		return INSERTION_DEFAULT_COLOR;
	}


	/**
	 * @return the deletionDefaultColor
	 */
	public static Color getDeletionDefaultColor() {
		return DELETION_DEFAULT_COLOR;
	}


	/**
	 * @return the snpsDefaultColor
	 */
	public static Color getSnpsDefaultColor() {
		return SNPS_DEFAULT_COLOR;
	}


	/**
	 * @return the svDefaultColor
	 */
	public static Color getSvDefaultColor() {
		return SV_DEFAULT_COLOR;
	}


	////////////////////////////// Show methods

	/**
	 * Shows genomes information.
	 */
	public void showData () {
		genomesInformation.showData();
	}


	/**
	 * Shows all genome association information
	 */
	public void showAllAssociation () {
		genomesInformation.showAllAssociation();
	}


	/**
	 * Shows VCF information.
	 * Mostly used for development.
	 * @param vcf				VCF file
	 * @param showHeader		to display the header
	 * @param showAttributes	to display the attributes
	 * @param showColumns		to display the column names
	 */
	@SuppressWarnings("unused")
	private void showInformation (	File vcf,
			boolean showHeader,
			boolean showAttributes,
			boolean showColumns) {
		System.out.println("***** VCF Information: " + vcf.getName());
		if (showHeader){
			fileReaders.get(vcf).showHeaderInfo();
		}
		if (showAttributes){
			//fileReaders.get(vcf).showHeaderAttributes();
		}
		if (showColumns){
			fileReaders.get(vcf).showColumnNames();
		}
	}

}