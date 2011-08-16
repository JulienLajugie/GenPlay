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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNP;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripe;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.action.project.PAMultiGenome;

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
public class SNPManager {

	private static 	SNPManager 				instance;			// The instance of the class
	private final	MultiGenomeManager 		multiGenome;		// Instance of the Multi Genome Manager
	private final 	MGMultiGenome 			genomes;			// Instance of the MGMultiGenome
	private			Map<String, Boolean> 	activeGenome; 		// Mapping list of enable/disable genomes
	private			Map<String, Integer> 	genomeCounter; 		// Mapping list of enable/disable genomes


	/**
	 * Constructor of {@link SNPManager}
	 */
	private SNPManager () {
		multiGenome = MultiGenomeManager.getInstance();
		genomes = multiGenome.getGenomesInformation();
		initializesGenomeList();
	}


	/**
	 * @return the instance of the singleton {@link SNPManager}.
	 */
	public static SNPManager getInstance () {
		if (instance == null) {
			instance = new SNPManager();
		}
		return instance;
	}


	/**
	 * Initializes the list of enabled/disabled genome.
	 * By default, all genome are set to false.
	 */
	private void initializesGenomeList () {
		activeGenome = new HashMap<String, Boolean>();
		genomeCounter = new HashMap<String, Integer>();
		for (String genomeName: multiGenome.getGenomesInformation().getGenomeNameList()) {
			activeGenome.put(genomeName, false);
			genomeCounter.put(genomeName, 0);
		}
	}
	
	
	/**
	 * Reinitializes SNP after each {@link PAMultiGenome} action.
	 */
	public void reinit () {
		for (String name: activeGenome.keySet()) {
			if (activeGenome.get(name)) {
				addSNP(name);
			}
		}
	}


	/**
	 * Adds SNPs information to a genome.
	 * @param genomeName a genome name
	 */
	private void addSNP (String genomeName) {
		
		// Gets the reader
		VCFReader reader = multiGenome.getReader(genomeName, VCFType.SNPS);

		if (reader != null) {

			// Gets the multi genome chromosome list
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>(ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values());

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

				if (result != null) {
					for (Map<String, Object> info: result) {	// Scans every result lines
						int position = Integer.parseInt(info.get("POS").toString());
						MGPosition positionInformation = null;
						for (String name: otherGenomeNames) {
							positionInformation = genomes.getMGPosition(name, chromosome, position);
							if (positionInformation != null) {
								break;
							}
						}
						if (positionInformation == null) {
							positionInformation = new MGPosition(chromosome, info, reader);
						}
						Variant variant = new VCFSNP(genomeName, chromosome, positionInformation);
						genomes.addVariant(genomeName, chromosome, variant);
					}
				}
			}
			
			
			

			activeGenome.put(genomeName, true);
			
			performGC();
		}
	}


	/**
	 * Removes SNPs information from a genome.
	 * @param genomeName a genome name
	 */
	private void removeSNP (String genomeName) {
		
		// Gets the multi genome chromosome list
		List<Chromosome> chromosomeList = new ArrayList<Chromosome>(ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values());

		// Gets the genome information
		MGGenome genome = genomes.getGenomeInformation(genomeName);

		// For each chromosome of the multi genome object
		for (Chromosome chromosome: chromosomeList) {

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

		activeGenome.put(genomeName, false);

		performGC();
	}


	/**
	 * Enables SNPs information for a genome.
	 * @param genomeName a genome name
	 */
	public void enableGenome (String genomeName) {
		int counter = genomeCounter.get(genomeName);
		if (counter == 0) {
			addSNP(genomeName);
		}
		counter++;
		genomeCounter.put(genomeName, counter);
	}


	/**
	 * Disables SNPs information for a genome.
	 * @param genomeName a genome name
	 */
	public void disableGenome (String genomeName) {
		int counter = genomeCounter.get(genomeName);
		if (counter > 0) {
			counter--;
			if (counter == 0) {
				removeSNP(genomeName);
			}
			genomeCounter.put(genomeName, counter);
		}
	}


	/**
	 * Compares previous and new multi genome stripe and updates lists.
	 * @param previous 	the previous {@link MultiGenomeStripe} object
	 * @param next		the new {@link MultiGenomeStripe} object
	 */
	public void updateSNP (MultiGenomeStripe previous, MultiGenomeStripe next) {
		List<String> nextGenomes = getGenomeNamesForSNP(next.getRequiredGenomes());
		if (previous != null) {
			List<String> previousGenomes = getGenomeNamesForSNP(previous.getRequiredGenomes());

			for (String name: previousGenomes) {
				if (!nextGenomes.contains(name)) {
					disableGenome(name);
				}
			}

			for (String name: nextGenomes) {
				if (!previousGenomes.contains(name)) {
					enableGenome(name);
				}
			}
		} else {
			for (String name: nextGenomes) {
				enableGenome(name);
			}
		}
	}


	/**
	 * Gathers genome names require for a SNP display
	 * @param list association of genome name/variant type list
	 * @return the list of genome names
	 */
	private List<String> getGenomeNamesForSNP (Map<String, List<VariantType>> list) {
		List<String> names = new ArrayList<String>();
		for (String name: list.keySet()) {
			List<VariantType> variantList = list.get(name);
			if (variantList.contains(VariantType.SNPS)) {
				names.add(name);
			}
		}
		return names;
	}


	/**
	 * Gathers genome names required in the project and present in the VCF file.
	 * In order to do that, it uses a genome name but do not include it in the returned list.
	 * @param genomeName	a genome name
	 * @return				the list of genome names
	 */
	private List<String> getOtherGenomeNames (String genomeName) {
		final List<String> projectGenomeNames = genomes.getGenomeNameList();
		final VCFReader reader = multiGenome.getReader(genomeName, VCFType.SNPS);
		final List<String> vcfGenomeNames = reader.getRawGenomesNames();
		List<String> genomeNames = new ArrayList<String>();
		for (String fullName: projectGenomeNames) {
			if (activeGenome.get(fullName) && vcfGenomeNames.contains(FormattedMultiGenomeName.getRawName(fullName))) {
				if (!fullName.equals(genomeName)) {
					genomeNames.add(fullName);
				}
			}
		}
		return genomeNames;
	}


	/**
	 * Runs the garbage collector
	 */
	private void performGC () {
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
	}
}