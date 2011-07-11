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
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFChromosomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFMultiGenomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFPositionInformation;
import edu.yu.einstein.genplay.core.multiGenome.VCFFile.VCFReader;


/**
 * This class manages a multi genome project.
 * It reads vcf files, analyzes them and perform synchronization algorithms to display information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeManager {

	public 	static final 	int 						FULL					 	= 1;	// Algorithm computed for every loaded chromosome when project is created
	public 	static final 	int 						SEQUENTIAL 					= 0;	// Algorithm computed at every changement of chromosome
	public 	static			int							CHROMOSOME_LOADING_OPTION 	= SEQUENTIAL;

	private static final 	Color 						DEFAULT_COLOR 				= Color.black;
	private static final 	Color 						INSERTION_DEFAULT_COLOR 	= Color.green;
	private static final 	Color 						DELETION_DEFAULT_COLOR 		= Color.red;
	private static final 	Color 						SNPS_DEFAULT_COLOR			= Color.cyan;
	private static final 	Color 						SV_DEFAULT_COLOR 			= Color.magenta;

	private static 			MultiGenomeManager 			instance = null;		// unique instance of the singleton
	private					List<String> 				fields;					// VCF column Filter
	private 				Map<File, VCFReader> 		fileReaders;			// VCF Readers for every VCF files
	private					VCFMultiGenomeInformation	genomesInformation;		// Genomes information
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
		this.genomesInformation = new VCFMultiGenomeInformation();
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
		}
	}


	/**
	 * @return true if data has been computed
	 */
	public boolean dataHasBeenComputed() {
		return dataComputed;
	}


	/**
	 * Initializes vcf filter.
	 * Uses to select vcf columns in order to build genomes information. 
	 */
	private void initialyzeVCFFilter (List<String> names, VCFType type) {
		fields = new ArrayList<String>();
		fields.add("CHROM");
		fields.add("POS");
		fields.add("ID");
		fields.add("REF");
		fields.add("ALT");
		fields.add("QUAL");
		fields.add("FILTER");
		fields.add("INFO");
		fields.add("FORMAT");
		for (String name: names) {
			fields.add(name);
		}
		
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
			final List<String> genomeNames = genomesInformation.getGenomeNamesFromVCF(vcf);
			VCFType vcfType = genomesInformation.getTypeFromVCF(vcf);
			if (vcfType != VCFType.SNPS) {
				valid = true;
				initialyzeVCFFilter(genomeNames, vcfType);
				for (final Chromosome chromosome: chromosomeList.values()) {
					//Adds the chromosome to the reference genome chromosome list
					referenceGenomeManager.addChromosome(chromosome.getName());
					
					//Performs query on the current VCF to get all data regarding the chromosome
					/*List<Map<String, Object>> result = fileReaders.get(vcf).query(chromosome.getName(),
							0,
							chromosome.getLength(),
							fields);*/
					List<Map<String, Object>> result = fileReaders.get(vcf).query(chromosome.getName(),
							0,
							chromosome.getLength());

					//Analyse query results
					createPositions(chromosome, genomeNames, result, vcfType);
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
	 */
	private void createPositions (Chromosome chromosome, List<String> genomeNames, List<Map<String, Object>> result, VCFType vcfType) {
		if (result != null) {

			for (Map<String, Object> info: result) {	// Scans every result lines
				boolean isSNP = false;
				VariantType type = null;
				int length = -1;
				if (vcfType == VCFType.INDELS) {
					int refLength = info.get("REF").toString().length();
					int altLength = info.get("ALT").toString().length();
					length = Math.abs(refLength - altLength);
					if (refLength > altLength) {				// Deletion: reference value length > new value length 
						type = VariantType.DELETION;
					} else if (refLength < altLength){			// Insertion: reference value length < new value length
						type = VariantType.INSERTION;
					} else {
						isSNP = true;
					}
				} else if (vcfType == VCFType.SV) {
					String formatInfo = info.get("INFO").toString();
					String typeResult = (String) formatParser(formatInfo, "SVTYPE");
					String lengthResult = (String) formatParser(formatInfo, "SVLEN");
					if (typeResult.equals("DEL")) {
						type = VariantType.DELETION;
					} else if (typeResult.equals("INS")) {
						type = VariantType.INSERTION; 
					} else {
						type = VariantType.SV;
					}
					if (lengthResult.charAt(0) == '-') {
						lengthResult = lengthResult.substring(1);
					}
					length = Integer.parseInt(lengthResult);
				} else if (vcfType == VCFType.SNPS) {
					isSNP = true;
				}

				if (!isSNP) {
					referenceGenomeManager.addPosition(chromosome.getName(), Integer.parseInt(info.get("POS").toString()));
					Map<String, String> format;
					//System.out.println(info.toString());
					String titles[] = info.get("FORMAT").toString().split(":");
					for (String genomeName: genomeNames) {
						format = new HashMap<String, String>();
						//System.out.println(genomeName);
						String values[] = info.get(genomeName).toString().split(":");
						for (int i = 0; i < titles.length; i++) {
							format.put(titles[i], values[i]);
						}

						genomesInformation.addInformation(	genomeName,
								chromosome,
								Integer.parseInt(info.get("POS").toString()),
								type,
								length,
								format);
					}
				}
			}

		}

	}


	/**
	 * Gets information from format field
	 * @param format	the format string field
	 * @param element	the information title
	 * @return			the value
	 */
	private Object formatParser (String format, String element) {
		Object o = null;
		int elementIndex = format.indexOf(element);
		if (elementIndex != -1) {
			int startIndex = elementIndex + element.length() + 1;
			int stopIndex = format.indexOf(";", startIndex);
			if (stopIndex == -1) {
				stopIndex = format.length();
			}
			o = format.substring(startIndex, stopIndex);
		}
		return o;
	}



	/**
	 * Scans every positions in order to cross data from VCF files.
	 * Deletion does not involve modification in other tracks.
	 * Insertion involves modification in other tracks creating "Blank" positions.
	 */
	private void compileData () {
		Map<String, Chromosome> chromosomeList = ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList();
		for (Chromosome chromosome: chromosomeList.values()) {														// Scan by chromosome
			List<VCFChromosomeInformation> currentChromosomeList =
				genomesInformation.getCurrentChromosomeInformation(chromosome);										// List of all existing chromosome in VCF files
			referenceGenomeManager.setList(chromosome.getName());

			for (VCFChromosomeInformation chromosomeInformation: currentChromosomeList) {							// Resets all index lists
				chromosomeInformation.resetIndexList();
			}

			while (referenceGenomeManager.isValidIndex()) {															// Scan by position
				List<Integer> insertPositions = new ArrayList<Integer>();											// List of all length insertion position. Used at the end to update all tracks.
				int currentRefPosition = referenceGenomeManager.getCurrentPosition();								// Current position of the reference genome
				for (VCFChromosomeInformation chromosomeInformation: currentChromosomeList) {						// Scan VCF content by chromosome
					chromosomeInformation.setCurrentPosition(currentRefPosition);
					VCFPositionInformation currentInformation =
						chromosomeInformation.getCurrentPositionInformation();										// Current position information according to a specific VCF file
					VCFPositionInformation previousInformation =
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
						if (currentInformation.getType() == VariantType.INSERTION) {								// If the current position is an insertion
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
			for (VCFChromosomeInformation chromosomeInformation: currentChromosomeList) {							// Resets all index lists
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
	private void updateInsert (List<VCFChromosomeInformation> chromosomeList, int refPosition, int maxLength) {
		for (VCFChromosomeInformation chromosomeInformation: chromosomeList) {								// Scan VCF content by chromosome
			VCFPositionInformation position = chromosomeInformation.getPositionInformation(refPosition);	// Gets the current position in a new variable
			if (position != null) {																			// If an information exists at this position
				if (position.getLength() < maxLength) {														// If the current event length is smaller than the maximum length found
					position.addExtraOffset(maxLength - position.getLength());								// The difference is added into the meta genome "extra" offset
				}
			} else { 																						// If there is no information, needs to add a "blank" position
				VCFPositionInformation previousPosition = chromosomeInformation.getPreviousPosition();
				chromosomeInformation.addInformation(refPosition,											// Adds a blank position
						VariantType.BLANK,
						maxLength,
						null);
				position = chromosomeInformation.getPositionInformation(refPosition);
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
		VCFChromosomeInformation chromosomeInformation = genomesInformation.getChromosomeInformation(ReferenceGenomeManager.getInstance().getReferenceName(), chromosome);
		chromosomeInformation.addInformation(refPosition, VariantType.BLANK, maxLength, null);
		VCFPositionInformation position = chromosomeInformation.getPositionInformation(refPosition);
		if (chromosomeInformation.getPositionInformationList().size() > 1) {
			VCFPositionInformation previousPosition = chromosomeInformation.getPreviousPosition();
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
	private void updatePreviousPosition (List<VCFChromosomeInformation> currentChromosomeList, int currentRefPosition) {
		for (VCFChromosomeInformation chromosomeInformation: currentChromosomeList) {	// Scan by chromosome
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
	 * @return the multiGenomeInformation
	 */
	public VCFMultiGenomeInformation getMultiGenomeInformation() {
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
	 * @return the genomesInformation
	 */
	public VCFChromosomeInformation getChromosomeInformation(String genome, Chromosome chromosome) {
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
		//Development.showMax();
		//Development.showIndelCounts();
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
			fileReaders.get(vcf).showHeaderAttributes();
		}
		if (showColumns){
			fileReaders.get(vcf).showColumnNames();
		}
	}

}