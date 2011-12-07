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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * SNPs can be enabled or disabled according to a genome.
 * When SNPs are enabled for a genome, SNP variants are added to {@link MGMultiGenome} lists.
 * When SNPs are disabled for a genome, SNP variants are deleted from {@link MGMultiGenome} lists.
 * Those modifications are made according to the chromosomes present in {@link MGMultiGenome}.
 * It means it is sensitive to the CHROMOSOME_LOADING_OPTION.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SNPSynchroniser implements Serializable {

	private static final long serialVersionUID = -4204806185089675978L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private 	 	MGMultiGenome 			genomes;			// Instance of the MGMultiGenome
	private 		Map<String, VCFReader> 	SNPReaders;			// Mapping between files and their readers.
	private			Map<String, Boolean> 	activeGenome; 		// Mapping list of enable/disable genomes
	private			Map<String, Integer> 	genomeCounter; 		// Mapping list of enable/disable genomes
	private			Chromosome				currentChromosome;	// The current chromosome


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomes);
		out.writeObject(SNPReaders);
		out.writeObject(activeGenome);
		out.writeObject(genomeCounter);
		out.writeObject(currentChromosome);
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
		genomes = (MGMultiGenome) in.readObject();
		SNPReaders = (Map<String, VCFReader>) in.readObject();
		activeGenome = (Map<String, Boolean>) in.readObject();
		genomeCounter = (Map<String, Integer>) in.readObject();
		currentChromosome = (Chromosome) in.readObject();
	}


	/**
	 * Constructor of {@link SNPSynchroniser}
	 */
	protected SNPSynchroniser () {}


	/**
	 * Initializes the list of enabled/disabled genome.
	 * By default, all genome are set to false.
	 * @param chromosome 
	 * @param genomesInformation 	the genome information
	 * @param SNPReaders			the list of reader indexed by genome name required for SNPs synchronization
	 */
	protected void initializesSNPSynchroniser (Chromosome chromosome, MGMultiGenome genomesInformation, Map<String, VCFReader> SNPReaders) {
		this.currentChromosome = chromosome;
		this.genomes = genomesInformation;
		this.SNPReaders = SNPReaders;
		activeGenome = new HashMap<String, Boolean>();
		genomeCounter = new HashMap<String, Integer>();
		for (String genomeName: this.SNPReaders.keySet()) {
			activeGenome.put(genomeName, false);
			genomeCounter.put(genomeName, 0);
		}
	}


	/**
	 * Refreshes the current chromosome.
	 * After synchronization, length of the current chromosome may has changed.
	 * This method set the new current chromosome according to its name. 
	 */
	protected void refreshCurrentChromosome (List<Chromosome> chromosomeList) {
		for (Chromosome chromosome: chromosomeList) {
			if (chromosome.getName().equals(currentChromosome.getName())) {
				currentChromosome = chromosome;
				break;
			}
		}
	}


	/**
	 * Adds SNPs information to a genome.
	 * @param genomeName a genome name
	 */
	private void addSNP (List<Chromosome> chromosomeList, String genomeName) {

		// Gets the reader
		VCFReader reader = SNPReaders.get(genomeName);

		// The reader must exists
		if (reader != null) {

			// Gets other genome names present in the same VCF file (in order to get the MGPosition if already created)
			List<String> otherGenomeNames = getOtherGenomeNames(genomeName);

			// For each chromosome of the multi genome object
			for (Chromosome chromosome: chromosomeList) {
				List<Map<String, Object>> result = null;
				try {
					result = reader.query(chromosome.getName(), 0, chromosome.getLength());
				} catch (IOException e) {
					e.printStackTrace();
				}

				// If results exist
				if (result != null) {

					// This block creates and adds the new variant
					for (Map<String, Object> info: result) {												// Scans every result lines
						int position = Integer.parseInt(info.get("POS").toString());						// Gets the reference genome position
						MGPosition positionInformation = null;												// Declares the MGPosition,
						// It is the VCF line information who can already exist for other genomes from the same VCF file,
						// if they have been required in the project and already processed
						for (String name: otherGenomeNames) {												// Scan for the other genomes
							positionInformation = genomes.getMGPosition(name, chromosome, position);		// Tries to get the MGPosition
							if (positionInformation != null) {												// If it is not null it exists
								break;																		// and the loop can be quit to do not scan the other genome (obviously same MGPosition object)
							}
						}
						if (positionInformation == null) {													// If no MGPosition has been got, it means it does not exist 
							positionInformation = new MGPosition(chromosome, info, reader);					// and it has to be instanced
						}
						Variant variant = new VCFSNP(genomeName, chromosome, positionInformation);			// Creates the SNP variant
						genomes.addVariant(genomeName, chromosome, variant);								// Adds the variant
					}

					// This block updates all SNPs,
					// it consists to initializes the reference and meta genome offset.
					MGChromosome chromosomeInformation = genomes.getChromosomeInformation(genomeName, chromosome);
					chromosomeInformation.resetIndexList();													// Many position have just been added, the list has to be reinitialized
					int[] indexes = chromosomeInformation.getPositionIndex();
					int cptIndex = 0;
					int cptInstance = 0;

					chromosomeInformation.setCurrentPosition(indexes[0]);
					Variant current = chromosomeInformation.getCurrentVariant();
					if (current instanceof VCFSNP) {
						int refPosition = current.getPositionInformation().getPos();
						current.setGenomePosition(refPosition);												// Sets the relative genome position
						current.setInitialReferenceOffset(0);												// Sets the initial reference genome offset
						current.setInitialMetaGenomeOffset(0);												// Sets the initial meta genome offset
						//System.out.println("refPosition: " + refPosition);
					}

					for (int i = 1; i < indexes.length; i++) {
						cptIndex++;
						chromosomeInformation.setCurrentPosition(indexes[i]);								// Sets the current position
						current = chromosomeInformation.getCurrentVariant();								// Gets the current variant
						if (current instanceof VCFSNP) {													// If it is a SNP (it has just been added and has to be updated)
							cptInstance++;
							chromosomeInformation.updatePreviousPosition(indexes[i-1]);						// Sets the previous position: value of indexes[i-1]
							Variant previous = chromosomeInformation.getPreviousPosition();					// Gets the previous variant
							//current.setGenomePosition(10);

							int refPosition = current.getPositionInformation().getPos();
							current.setGenomePosition(														// Sets the relative genome position
									refPosition -
									previous.getNextReferencePositionOffset());
							current.setInitialReferenceOffset(previous.getNextReferencePositionOffset());	// Sets the initial reference genome offset
							current.setInitialMetaGenomeOffset(previous.getNextMetaGenomePositionOffset());	// Sets the initial meta genome offset
							/*if (i <= 50) {
								show(current);
							}*/

						}
					}
				}
			}

			performGC();
		}
	}

	
	@SuppressWarnings("unused") // use for development only
	private void show (Variant variant) {
		String info = variant.getGenomePosition() + " Ref (";
		info += variant.getInitialReferenceOffset() + ", ";
		info += variant.getReferenceGenomePosition() + ", ";
		info += variant.getNextReferencePositionOffset() + ", ";
		info += variant.getNextReferenceGenomePosition() + ") MG (";
		info += variant.getInitialMetaGenomeOffset() + ", ";
		info += variant.getMetaGenomePosition() + ", ";
		info += variant.getNextMetaGenomePositionOffset() + ", ";
		info += variant.getNextMetaGenomePosition() + ")";
		System.out.println(info);
	}


	/**
	 * Removes SNPs information from a genome.
	 * @param genomeName a genome name
	 */
	private void removeSNP (List<Chromosome> chromosomeList, String genomeName) {

		// Gets the multi genome chromosome list
		List<Chromosome> chromosomes = new ArrayList<Chromosome>(chromosomeList);

		// Gets the genome information
		MGGenome genome = genomes.getGenomeInformation(genomeName);

		// For each chromosome of the multi genome object
		for (Chromosome chromosome: chromosomes) {

			// Gets the variant list
			Map<Integer, Variant> variantList = genome.getGenomeInformation().get(chromosome).getPositionInformationList();

			// Gets the list of all reference genome position
			List<Integer> indexList = new ArrayList<Integer>(variantList.keySet());

			// Scans the variant list to find the ones instanced as VCFSNP in order to remove them
			for (int i: indexList) {
				if (variantList.get(i) instanceof VCFSNP) {
					variantList.remove(i);
				}
			}
		}

		performGC();
	}


	/**
	 * Removes SNPs data from a chromosome of all activated genome.
	 * Then, it deactivates them but does not modify counters.
	 * Compute method must be called in order to load new SNPs data for the new chromosome.
	 * @param chromosome	the chromosome
	 */
	public void removeChromosomeSNPs (Chromosome chromosome) {
		if (!currentChromosome.equals(chromosome)) {
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>();
			chromosomeList.add(currentChromosome);

			List<String> genomeToDeactivate = new ArrayList<String>();

			for (String genomeName: activeGenome.keySet()) {
				boolean isActive = activeGenome.get(genomeName);
				if (isActive) {
					removeSNP(chromosomeList, genomeName);
					genomeToDeactivate.add(genomeName);
				}
			}

			for (String genomeName: genomeToDeactivate) {
				activeGenome.put(genomeName, false);
			}

			currentChromosome = chromosome;
		}
	}


	/**
	 * Compute the SNPs synchronization.
	 * SNPs are loaded/released in/out of GenPlay according to settings. 
	 */
	public void compute () {

		List<Chromosome> chromosomeList = new ArrayList<Chromosome>();
		chromosomeList.add(currentChromosome);

		for (String genomeName: activeGenome.keySet()) {
			boolean isActive = activeGenome.get(genomeName);
			int counter = genomeCounter.get(genomeName);

			// if the genome is not active but its counter is positive
			if (!isActive && counter > 0) {
				addSNP(chromosomeList, genomeName);			// its SNPs must be added
				activeGenome.put(genomeName, true);			// it must be marked as "activated"
			}
			// if the genome is active but its counter is null
			else if (isActive && counter == 0) {
				removeSNP(chromosomeList, genomeName);		// its SNPs must be removed
				activeGenome.put(genomeName, false);		// it must be marked as "deactivated"
			}
		}

	}


	/**
	 * Increases SNPs counter for a genome.
	 * @param genomeName a genome name
	 */
	private void increaseCounter (String genomeName) {
		int counter = genomeCounter.get(genomeName);
		counter++;
		genomeCounter.put(genomeName, counter);
	}


	/**
	 * Decreases SNPs counter for a genome.
	 * @param genomeName a genome name
	 */
	private void decreaseCounter (String genomeName) {
		int counter = genomeCounter.get(genomeName);
		if (counter > 0) {
			counter--;
			genomeCounter.put(genomeName, counter);
		}
	}


	/**
	 * Compares previous and new multi genome stripe in order to update lists of enabled genome SNPs.
	 * Enable a genome for SNP will load its SNP information.
	 * Disable a gnome for SNP will delete its SNP information.
	 * @param previousGenomes 	the previous required genomes list
	 * @param nextGenomes		the new required genomes list
	 */
	public void updateCounters (List<String> previousGenomes, List<String> nextGenomes) {
		if (previousGenomes != null) {

			// If genomes were present in the last multi genome stripe settings but not in the new one,
			// they have to be disabled.
			for (String name: previousGenomes) {
				if (!nextGenomes.contains(name)) {
					decreaseCounter(name);
				}
			}

			// If genomes are present in the new mutli genome stripe settings but not in the previous one,
			// they have to be enable.
			for (String name: nextGenomes) {
				if (!previousGenomes.contains(name)) {
					increaseCounter(name);
				}
			}
		} else {
			// If there is no previous multi genome stripe settings,
			// every genome of the new one have to be enabled.
			for (String name: nextGenomes) {
				increaseCounter(name);
			}
		}
	}
	

	/**
	 * Gathers genome names required in the project and present in the VCF file.
	 * In order to do that, it uses a genome name but do not include it in the returned list.
	 * @param genomeName	a genome name
	 * @return				the list of genome names
	 */
	private List<String> getOtherGenomeNames (String genomeName) {
		final List<String> projectGenomeNames = new ArrayList<String>(SNPReaders.keySet());
		final VCFReader reader = SNPReaders.get(genomeName);
		final List<String> vcfGenomeNames = reader.getRawGenomesNames();
		List<String> genomeNames = new ArrayList<String>();
		for (String fullName: projectGenomeNames) {
			if (activeGenome.get(fullName) &&
					vcfGenomeNames.contains(FormattedMultiGenomeName.getRawName(fullName))) {
				if (!fullName.equals(genomeName)) {
					genomeNames.add(fullName);
				}
			}
		}
		return genomeNames;
	}


	/**
	 * @return true if SNPs synchronization has been requested, false otherwise.
	 */
	public boolean hasBeenRequested () {
		for (boolean b: activeGenome.values()) {
			if (b) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * @return the currentChromosome
	 */
	public Chromosome getCurrentChromosome() {
		return currentChromosome;
	}


	/**
	 * Runs the garbage collector
	 */
	private void performGC () {
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
	}
}
