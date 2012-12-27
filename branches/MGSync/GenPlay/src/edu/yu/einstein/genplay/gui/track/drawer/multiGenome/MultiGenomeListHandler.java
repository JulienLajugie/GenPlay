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
package edu.yu.einstein.genplay.gui.track.drawer.multiGenome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.list.CacheTrack;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayList;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayMultiListScanner;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeListHandler {

	private List<VariantDisplayList> variantList;
	private List<List<Variant>> fullList;
	private List<List<Variant>> fittedList;
	private final CacheTrack<List<List<Variant>>> cache;
	private Chromosome fittedChromosome; // Chromosome with the adapted data
	private Double fittedXRatio; // xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )


	/**
	 * Constructor of {@link MultiGenomeListHandler}
	 */
	public MultiGenomeListHandler () {
		cache = new CacheTrack<List<List<Variant>>>();
		fittedChromosome = null;
		fittedXRatio = null;
	}


	/**
	 * Initializes the list of {@link Variant}
	 * @param variantList
	 */
	public void initialize (List<VariantDisplayList> variantList) {
		cache.initialize();
		this.variantList = variantList;
		fullList = new ArrayList<List<Variant>>();
		fullList.add(new ArrayList<Variant>());
		fullList.add(new ArrayList<Variant>());

		for (VariantDisplayList current: variantList) {
			fullList.get(0).addAll(current.getVariants().get(0));
			fullList.get(1).addAll(current.getVariants().get(1));
		}

		Collections.sort(fullList.get(0), new VariantComparator());
		Collections.sort(fullList.get(1), new VariantComparator());
	}


	// /////////////////////////////////////////////////////////////////// Interface methods

	/**
	 * @param window the genome window
	 * @param xRatio the x ratio
	 * @param allele the allele index
	 * @return the variant list that fits the screen
	 */
	public final List<Variant> getFittedData(GenomeWindow window, double xRatio, int allele) {
		boolean hasToFit = false;
		if ((fittedChromosome == null) || (!fittedChromosome.equals(window.getChromosome()))) {
			fittedChromosome = window.getChromosome();
			if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
				fittedXRatio = xRatio;
			}
			hasToFit = true;
		} else if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
			fittedXRatio = xRatio;
			hasToFit = true;
		}

		if (hasToFit) {
			if (cache.hasData(xRatio)) {
				fittedList = cache.getData(xRatio);
			} else {
				fitToScreen();
			}
		}

		List<Variant> result = getFittedData(window.getStart(), window.getStop(), allele);
		return result;
	}

	protected List<Variant> getFittedData(int start, int stop, int allele) {
		if ((fittedList == null) || (fittedList.size() == 0) || (fittedList.get(allele).size() == 0)) {
			return null;
		}

		ArrayList<Variant> resultList = new ArrayList<Variant>();
		int indexStart = findStart(fittedList.get(allele), start, 0, fittedList.get(allele).size() - 1);
		int indexStop = findStop(fittedList.get(allele), stop, 0, fittedList.get(allele).size() - 1);

		if (indexStart > 0) {
			Variant variant = fittedList.get(allele).get(indexStart - 1);
			if (variant.getStop() >= start) {
				resultList.add(variant);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			if (i == indexStop) {
				Variant variant = fittedList.get(allele).get(indexStop);
				if (variant.getStart() <= stop) {
					resultList.add(variant);
				}
			} else {
				resultList.add(fittedList.get(allele).get(i));
			}
		}
		if ((indexStop + 1) < fittedList.get(allele).size()) {
			Variant variant = fittedList.get(allele).get(indexStop + 1);
			if (variant.getStart() <= stop) {
				resultList.add(variant);
			}
		}
		return resultList;
	}

	/**
	 * Recursive function. Returns the index where the start value of the window is found or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the start value of the window is found or the index right after if the exact value is not find
	 */
	private int findStart(List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}

	/**
	 * Recursive function. Returns the index where the stop value of the window is found or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the stop value of the window is found or the index right before if the exact value is not find
	 */
	private int findStop(List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}



	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	public void fitToScreen() {
		fittedList = new ArrayList<List<Variant>>();
		fittedList.add(new ArrayList<Variant>());
		fittedList.add(new ArrayList<Variant>());
		fitToScreen(0);
		fitToScreen(1);
		cache.setData(fittedXRatio, fittedList);
	}


	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	private void fitToScreen (int allele) {
		VariantDisplayMultiListScanner scanner = new VariantDisplayMultiListScanner(variantList);
		scanner.initialize(allele);

		if (fittedXRatio > 1) {
			while (scanner.hasNext()) {
				List<Variant> variants = scanner.next();
				fittedList.get(allele).add(variants.get(0));
			}
		} else {
			// Initialize the first position
			int start = -1;
			int stop = -1;
			Variant currentVariant = null;
			if (scanner.hasNext()) {
				currentVariant = scanner.next().get(0);
				start = currentVariant.getStart();
				stop = currentVariant.getStop();
			}

			// Process the next positions
			Variant previousVariant = null;
			boolean hasToMerge = false;
			while (scanner.hasNext()) {
				previousVariant = currentVariant;
				currentVariant = scanner.next().get(0); // Get the next position
				double distance = (currentVariant.getStart() - stop) * fittedXRatio; // Compare its distance with the previous position
				if (distance < 1) {
					hasToMerge = true;
					stop = currentVariant.getStop();
				} else {
					// Insert previous information
					if (hasToMerge) {
						hasToMerge = false;
						fittedList.get(allele).add(new MixVariant(start, stop));
					} else {
						fittedList.get(allele).add(previousVariant);
					}

					// Update current information
					start = currentVariant.getStart();
					stop = currentVariant.getStop();
				}
			}

			// Handle the last case
			if (hasToMerge) {
				fittedList.get(allele).add(new MixVariant(start, stop));
			} else {
				if (currentVariant != null) {
					fittedList.get(allele).add(currentVariant);
				}
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////


}
