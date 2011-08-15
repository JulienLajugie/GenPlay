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

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType.VCFSNPtmp;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGChromosome;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;

/**
 * This class adapts VCF variant information to displayable variant.
 * It mostly creates list of variants displayable by the GUI layer.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DisplayableVariantListCreator implements DisplayableDataList<List<DisplayableVariant>> {



	// Graphic variables
	private GenomeWindow					currentGenomeWindow;			// Chromosome with the adapted data
	private Double							currentXRatio;					// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )

	// Filter variables
	private Map<String, List<VariantType>>	genomes;						// Genome names list
	private Double							quality;						// Variant quality threshold (only equal and greater variants will be selected)
	//private double 							ratioThreshold;					// Ratio threshold to do not show up SNPs when zoom is not important enough

	private List<DisplayableVariant> 		fittedDisplayableVariantList;	// Complete list of the displayable variant
	private boolean							hasBeenChanged;					// Is true is any information has been modified


	/**
	 * Constructor of {@link DisplayableVariantListCreator}
	 */
	public DisplayableVariantListCreator () {
		currentGenomeWindow = null;
		currentXRatio = null;
		genomes = new HashMap<String, List<VariantType>>();
		//ratioThreshold = 0.05;
		hasBeenChanged = false;
	}


	@Override
	public List<DisplayableVariant> getFittedData(GenomeWindow genomeWindow, double xRatio) {
		if ((currentGenomeWindow == null) ||
				(!currentGenomeWindow.getChromosome().equals(genomeWindow.getChromosome()))) {
			hasBeenChanged = true;
		}
		currentGenomeWindow = genomeWindow;
		if ((currentXRatio == null) || (!currentXRatio.equals(xRatio))) {
			currentXRatio = xRatio;
			hasBeenChanged = true;
		}

		if (hasBeenChanged) {
			List<Variant> fittedVariantList = getFittedVariantList();
			createDisplayableVariantList(fittedVariantList);
			hasBeenChanged = false;
		}

		return getFittedDisplayableVariantList();
	}


	/**
	 * Builds the list of fitted variants.
	 * They are selected according to:
	 * - the requested genomes
	 * - the requested variant type
	 * - the quality
	 * @return the list of fitted variant
	 */
	private List<Variant> getFittedVariantList () {

		// Creates the list of involved variants
		List<Variant> fittedVariantList = new ArrayList<Variant>();

		// Scan for every required genomes
		for (String genomeFullName: genomes.keySet()) {
			
			// Gets parameters
			MGChromosome chromosomeInformation = MultiGenomeManager.getInstance().getChromosomeInformation(genomeFullName, currentGenomeWindow.getChromosome());
			Map<Integer, Variant> variants = chromosomeInformation.getPositionInformationList();
			int[] indexes = chromosomeInformation.getPositionIndex();

			// Scan the full variant list with the right indexes
			for (int i = 0; i < indexes.length; i++) {
				Variant current = variants.get(indexes[i]);

				// The variant is added if pertinent
				if (passFilter(current) && (current.getType() != VariantType.SNPS)) {
					fittedVariantList.add(current);
				}
			}

			
			
			////////////////////////// SNPs
			/*System.out.println("fittedVariantList size: " + fittedVariantList.size());

			System.out.println("=== " + rawGenomeName);
			System.out.println(currentXRatio);
			if (genomes.get(rawGenomeName).contains(VariantType.SNPS) && (currentXRatio > ratioThreshold)) {
				System.out.println("Contains SNPs");

				// Gets the VCF reader
				MGMultiGenomeInformation genomeInformation = MultiGenomeManager.getInstance().getMultiGenomeInformation();
				String groupName = genomeInformation.getGroupNameFromRawName(rawGenomeName);
				List<File> fileList = genomeInformation.getGenomeFilesAssociation().get(groupName);
				VCFReader reader = null;
				if (fileList != null) {
					for (File file: fileList) {
						VCFType type = genomeInformation.getTypeFromVCF(file);
						if (type.equals(VCFType.SNPS)) {
							reader = MultiGenomeManager.getInstance().getReader(file);
						}
					}
				}

				if (reader != null) {
					System.out.println("vcf name: " + reader.getVcf().getName());
					List<Map<String, Object>> result;
					try {
						int start = Math.max(0, currentGenomeWindow.getStart());
						int stop = Math.min(currentGenomeWindow.getChromosome().getLength(), currentGenomeWindow.getStop());
						//result = reader.query(currentGenomeWindow.getChromosome().getName(), start, stop);
						result = reader.query(currentGenomeWindow.getChromosome().getName(), 0, currentGenomeWindow.getChromosome().getLength());
						System.out.println("result size: " + result.size());
						for (Map<String, Object> line: result) {
							int genomePosition = Integer.parseInt(line.get("POS").toString());
							int metaGenomePosition = ShiftCompute.computeShift(rawGenomeName, currentGenomeWindow.getChromosome(), genomePosition);
							Variant variant = new VCFSNPtmp(genomePosition, metaGenomePosition);
							//fittedVariantList.add(variant);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}*/
		}
		/////////////////////////////////////////////////////////////////////////////////////////

		// Sorts the list using the start position on the meta genome coordinates
		Collections.sort(fittedVariantList, new VariantMGPositionComparator());

		return fittedVariantList;
	}



	/**
	 * Creates a complete list of displayable variant according to the X ratio.
	 * @param fittedVariantList list of fitted variant
	 */
	private void createDisplayableVariantList (List<Variant> fittedVariantList) {

		fittedDisplayableVariantList = new ArrayList<DisplayableVariant>();

		if (fittedVariantList.size() > 0) {

			// if it is not necessary to merge stripes
			if (currentXRatio > 1) {
				for (int i = 0; i < fittedVariantList.size(); i++) {
					Variant current = fittedVariantList.get(i);
					fittedDisplayableVariantList.add(new RegularDisplayableVariant(fittedVariantList.get(i), current.getMetaGenomePosition(), current.getNextMetaGenomePosition()));
				}
			} else {

				boolean isValid = true;
				int index = 0;
				int stopIndex = fittedVariantList.size() - 1;

				while (isValid) {

					// Gets the current variant
					Variant current = fittedVariantList.get(index);

					// Gets the start
					int start = current.getMetaGenomePosition();

					// Gets the stop
					int stop = current.getNextMetaGenomePosition() - 1;
					int nextIndex = index + 1;

					// Checks if it is necessary to merge variants
					boolean hasBeenMerged = false;
					if (nextIndex <= stopIndex) {
						boolean merging = false;												// By default the merging is not necessary.
						double distance = (fittedVariantList.get(nextIndex).getMetaGenomePosition() - stop) * currentXRatio; // Distance between the start position of the next variant and the current stop.

						if (distance < 1) {														// If the distance is smaller than 1 pixel,
							merging = true;														// the merging is required.
							hasBeenMerged = true;
						}

						while (merging) {														// While the merging is required,
							stop = fittedVariantList.get(nextIndex).getNextMetaGenomePosition();	// the new stop becomes the stop of the next variant,
							nextIndex++;
							if (nextIndex <= stopIndex) {										// if a next variant exists,
								distance = (fittedVariantList.get(nextIndex).getMetaGenomePosition() - stop) * currentXRatio; // the new distance is calculated,
								if (distance >= 1) {											// if the distance is greater/equal than 1 pixel,
									merging = false;											// the merging is not necessary,
								}
							} else {															// if there is no next variant,
								isValid = false;												// the loop is not valid anymore,
								merging = false;												// and the merging cannot be performed.
							}
						}

					} else {
						isValid = false;
					}

					// Creates the displayable variant
					DisplayableVariant displayableVariant;
					if (hasBeenMerged) {
						displayableVariant = new MIXDisplayableVariant(start, stop);
					} else {
						if (current instanceof VCFSNPtmp) {
							displayableVariant = new SNPDisplayableVariant(current.getGenomePosition());
						} else {
							displayableVariant = new RegularDisplayableVariant(current, start, stop);
						}
					}

					// Add the displayable variant
					fittedDisplayableVariantList.add(displayableVariant);

					// Goes to the next index if it is valid
					if (nextIndex > stopIndex) {
						isValid = false;
					} else {
						index = nextIndex;
					}
				}
			}
		}

	}


	/**
	 * @return the fitted list of displayable variant
	 */
	private List<DisplayableVariant> getFittedDisplayableVariantList () {
		List<DisplayableVariant> variantList = new ArrayList<DisplayableVariant>();

		if (fittedDisplayableVariantList.size() > 0) {

			// Start and stop meta genome position on the screen
			final int screenStart = currentGenomeWindow.getStart();
			final int screenStop = currentGenomeWindow.getStop();

			// Gets variant boundaries indexes according to the screen position
			int startIndex = findStart();
			int stopIndex = findStop();

			// Loops to add displayable variants from the full list to the fitted list
			for (int i = startIndex; i <= stopIndex; i++) {
				DisplayableVariant current = fittedDisplayableVariantList.get(i);

				// Gets the start
				int currentStart = current.getStart();
				int currentStop = current.getStop();

				// Solve bug for displaying stripes at the edge of the screen
				boolean edgeBug = false;
				if (currentStart < screenStart && currentStop > screenStart) {
					edgeBug = true;
					currentStart = screenStart;
					if (currentStop > screenStop) {
						currentStop = screenStop;
					}
				} else if (currentStart < screenStop && currentStop > screenStop) {
					edgeBug = true;
					currentStop = screenStop;
					if (currentStart < screenStart) {
						currentStart = screenStart;
					}
				}

				// If bug appeared
				if (edgeBug) {
					DisplayableVariant newDisplayableVariant = null;
					if (current instanceof RegularDisplayableVariant) {
						newDisplayableVariant = new RegularDisplayableVariant(current.getNativeVariant(), currentStart, currentStop);
					} else if (current instanceof MIXDisplayableVariant) {
						newDisplayableVariant = new MIXDisplayableVariant(currentStart, currentStop);
					} else if (current instanceof SNPDisplayableVariant) {
						newDisplayableVariant = new SNPDisplayableVariant(currentStart);
					}
					variantList.add(newDisplayableVariant);	// new start/stop have to be taken in account
				} else { // if not
					variantList.add(current); //the current displayable variant is used
				}
			}
		}

		return variantList;
	}



	/**
	 * @param variants list of variants
	 * @param indexes list of indexes of the list of variants
	 * @return the start index
	 */
	private int findStart () {
		int start = getIndex(currentGenomeWindow.getStart(), 0, fittedDisplayableVariantList.size()); 
		start--;
		if (start < 0) {
			start = 0;
		}
		return start;
	}


	/**
	 * @param variants list of variants
	 * @param indexes list of indexes of the list of variants
	 * @return the stop index
	 */
	private int findStop () {
		int stop = getIndex(currentGenomeWindow.getStop(), 0, fittedDisplayableVariantList.size());
		if (stop >= fittedDisplayableVariantList.size()) {
			stop = fittedDisplayableVariantList.size() - 1;
		}
		return stop;
	}


	/**
	 * @param value meta genome position
	 * @param indexStart index to start
	 * @param indexStop index to stop
	 * @return the index
	 */
	private int getIndex(int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == fittedDisplayableVariantList.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > fittedDisplayableVariantList.get(indexStart + middle).getStart()) {
			return getIndex(value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(value, indexStart, indexStart + middle);
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
		if (!genomes.get(variant.getFullGenomeName()).contains(variant.getType())) {
			result = false;
		}

		// Quality filter
		if (variant.getQuality() < quality) {
			result = false;
		}

		return result;
	}


	/**
	 * Sets the raw genome names and checks if they are different.
	 * @param genomes the raw genome names to set
	 */
	public void setGenomeNames(Map<String, List<VariantType>> genomes) {
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
		if (this.quality == null || !this.quality.equals(quality)) {
			hasBeenChanged = true;
		}
		this.quality = quality;
	}


	/**
	 * @return the full fitted list of displayable variant
	 */
	public List<DisplayableVariant> getFullDisplayableVariantList () {
		return fittedDisplayableVariantList;
	}

}