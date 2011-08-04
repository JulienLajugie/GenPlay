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
package edu.yu.einstein.genplay.core.multiGenome.stripeManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosomeInformation;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * This class adapts VCF variant information to displayable variant.
 * It mostly creates list of variants displayable by the GUI layer.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ListCreator implements DisplayableDataList<List<DisplayableVariant>> {

	// Graphic variables
	private GenomeWindow					currentGenomeWindow;		// Chromosome with the adapted data
	private Double							currentXRatio;				// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )


	// Filter variables
	private Map<String, List<VariantType>>	genomes;				// Raw genome names list
	private Double							quality;					// Variant quality threshold (only equal and greater variants will be selected)


	private List<DisplayableVariant> 		list;						// List of displayable variants
	private boolean							hasBeenChanged;				// Is true is any information has been modified


	/**
	 * Constructor of {@link ListCreator}
	 */
	public ListCreator () {
		currentGenomeWindow = null;
		currentXRatio = null;
		genomes = new HashMap<String, List<VariantType>>();
		list = new ArrayList<DisplayableVariant>();
		hasBeenChanged = false;
	}


	@Override
	public List<DisplayableVariant> getFittedData(GenomeWindow genomeWindow, double xRatio) {
		if ((currentGenomeWindow == null) ||
				(!currentGenomeWindow.getChromosome().equals(genomeWindow.getChromosome())) ||
				(currentGenomeWindow.getStart() != genomeWindow.getStart()) ||
				(currentGenomeWindow.getStop() != genomeWindow.getStop())) {
			currentGenomeWindow = genomeWindow;
			hasBeenChanged = true;
		}
		if ((currentXRatio == null) || (currentXRatio != xRatio)) {
			currentXRatio = xRatio;
			hasBeenChanged = true;
		}

		buildList();

		return list;
	}


	/**
	 * Builds the list of displayable variants
	 */
	private void buildList () {
		if (hasBeenChanged) {
			list = new ArrayList<DisplayableVariant>();

			// Creates the list of involved variants
			List<Variant> newVariantList = new ArrayList<Variant>();
			
			// Scan for every required genomes
			for (String rawGenomeName: genomes.keySet()) {
				// Gets parameters
				MGChromosomeInformation chromosomeInformation = MultiGenomeManager.getInstance().getChromosomeInformation(rawGenomeName, currentGenomeWindow.getChromosome());
				Map<Integer, Variant> variants = chromosomeInformation.getPositionInformationList();
				int[] indexes = chromosomeInformation.getPositionIndex();

				// Gets variant boundaries indexes according to the screen position
				int start = findStart(variants, indexes);
				int stop = findStop(variants, indexes);

				// Scan the full variant list with the right indexes
				for (int i = start; i <= stop; i++) {
					Variant current = variants.get(indexes[i]);
					
					// The variant is added if pertinent
					if (passFilter(current)) {
						newVariantList.add(current);
					}
				}

			}
			
			// Sorts variant list according to the meta genome position
			Collections.sort(newVariantList, new VariantMGPositionComparator());
			
			
			// Creates the list of graphical variants
			
			if (currentXRatio > 1) {
				for (Variant variant: newVariantList) {
					addVariant(variant);
				}
			} else {
				
			}

			hasBeenChanged = false;
		}
	}

	
	private void addVariant (Variant variant) {
		ChromosomeWindow chromosome = new ChromosomeWindow(variant.getMetaGenomePosition(), variant.getNextMetaGenomePosition());
		DisplayableVariant displayableVariant = new DisplayableVariant(variant.getType(), chromosome, variant);

		if (variant.getExtraOffset() > 0) {
			ChromosomeWindow extraChromosome = new ChromosomeWindow(variant.getNextMetaGenomePosition() - variant.getExtraOffset(), variant.getNextMetaGenomePosition());
			displayableVariant.setDeadZone(extraChromosome);
		}

		list.add(displayableVariant);
	}
	
	
	/**
	 * @param variants list of variants
	 * @param indexes list of indexes of the list of variants
	 * @return the start index
	 */
	private int findStart (Map<Integer, Variant> variants, int[] indexes) {
		int start = getIndex(variants, indexes, currentGenomeWindow.getStart(), 0, indexes.length - 1); 

		if ((start - 1) > 0) {
			start--;
		}

		return start;
	}


	/**
	 * @param variants list of variants
	 * @param indexes list of indexes of the list of variants
	 * @return the stop index
	 */
	private int findStop (Map<Integer, Variant> variants, int[] indexes) {
		int stop = getIndex(variants, indexes, currentGenomeWindow.getStop(), 0, indexes.length - 1); 

		if ((stop + 1) < indexes.length) {
			stop++;
		}

		return stop;
	}


	/**
	 * @param variants list of variants
	 * @param indexes index list of genome reference position of the variants
	 * @param value meta genome position
	 * @param indexStart index to start
	 * @param indexStop index to stop
	 * @return the index
	 */
	private int getIndex(Map<Integer, Variant>	variants, int[] indexes, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == variants.get(indexes[indexStart + middle]).getMetaGenomePosition()) {
			return indexStart + middle;
		} else if (value > variants.get(indexes[indexStart + middle]).getMetaGenomePosition()) {
			return getIndex(variants, indexes, value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(variants, indexes, value, indexStart, indexStart + middle);
		}
	}



	/**
	 * Checks if the variant passes filters:
	 * - variant type
	 * - quality
	 * @param variant the variant
	 * @return	true if the variant is correct, false if not
	 */
	private boolean passFilter (Variant variant) {
		boolean result = true;

		// Variant type filter
		
		//try {
			if (!genomes.get(variant.getRawGenomeName()).contains(variant.getType())) {
			result = false;
		}
		/*} catch (Exception e) {
			System.out.println("raw: " + variant.getRawGenomeName());
			System.out.println("full: " + variant.getFullGenomeName());
			System.out.println("mg pos: " + variant.getMetaGenomePosition());
		}*/
		

		// Quality filter
		if (variant.getQuality() < quality) {
			result = false;
		}

		return result;
	}





	/**
	 * Sets the raw genome names and checks if they are different.
	 * @param rawGenomeNames the raw genome names to set
	 */
	public void setRawGenomeNames(Map<String, List<VariantType>> genomes) {
		if (setsAreDifferents(this.genomes.keySet(), genomes.keySet())) {
			hasBeenChanged = true;
		} else {
			for (String genomeName: this.genomes.keySet()) {
				if (listAreDifferents(this.genomes.get(genomeName), genomes.get(genomeName))) {
					hasBeenChanged = true;
					break;
				}
			}
		}
		this.genomes = genomes;

	}


	/**
	 * Checks if two sets of String are strictly similar or not 
	 * @param set1	the first set of String
	 * @param set2	the second set of String
	 * @return		true if they are not similar, false if they are
	 */
	private boolean setsAreDifferents (Set<String> set1, Set<String> set2) {
		if (set1.size() != set2.size()) {
			return true;
		} else {
			for (String name: set1) {
				if (!set2.contains(name)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Checks if two lists of VCFType are strictly similar or not 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private boolean listAreDifferents (List<VariantType> list1, List<VariantType> list2) {
		if (list1.size() != list2.size()) {
			return true;
		} else {
			for (VariantType type: list1) {
				if (!list2.contains(type)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Sets the quality and checks if it is still the same.
	 * @param quality the quality to set
	 */
	public void setQuality(Double quality) {
		if (this.quality != quality) {
			hasBeenChanged = true;
		}
		this.quality = quality;
	}





}
