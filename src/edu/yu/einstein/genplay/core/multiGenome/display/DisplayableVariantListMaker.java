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
package edu.yu.einstein.genplay.core.multiGenome.display;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.VariantComparator;
import edu.yu.einstein.genplay.core.list.CacheTrack;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;

/**
 * This class creates the list of variant for a track (or half a track) and its filters.
 * It is set with a list of variant list for display.
 * Creating a list consist of:
 * - gathering all lists of variants from each variant list for display
 * - adding and testing the variants to the main list according to the filters
 * - sorting the list
 * - adding all the blank of synchronization
 * The full list of variant is stored as well as the fitted list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DisplayableVariantListMaker implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -5236981711624610822L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	protected Chromosome				fittedChromosome = null;		// Chromosome with the adapted data
	protected Double					fittedXRatio = null;			// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )

	private List<MGVariantListForDisplay> 	listOfVariantList;			// The list of list of variant for display
	private List<VariantInterface> 			variantList;				// The full list of variant
	private List<VariantInterface>		 	fittedDataList;				// List of data of the current chromosome adapted to the screen resolution
	private List<MGFilter> filtersList;

	private CacheTrack<List<VariantInterface>> cache;

	long time01;
	long time02;
	long time03;
	long time04;
	long time05;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(fittedChromosome);
		out.writeDouble(fittedXRatio);
		out.writeObject(listOfVariantList);
		out.writeObject(variantList);
		out.writeObject(fittedDataList);
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
		fittedChromosome = (Chromosome) in.readObject();
		fittedXRatio = in.readDouble();
		listOfVariantList = (List<MGVariantListForDisplay>) in.readObject();
		variantList = (List<VariantInterface>) in.readObject();
		fittedDataList = (List<VariantInterface>) in.readObject();
		cache = new CacheTrack<List<VariantInterface>>();
	}


	/**
	 * Constructor of {@link DisplayableVariantListMaker}
	 * @param window the genome window
	 * @param xRatio the x ratio
	 */
	public DisplayableVariantListMaker (GenomeWindow window, double xRatio) {
		listOfVariantList = new ArrayList<MGVariantListForDisplay>();
		variantList = new ArrayList<VariantInterface>();
		this.fittedChromosome = window.getChromosome();
		this.fittedXRatio = xRatio;
		cache = new CacheTrack<List<VariantInterface>>();
	}


	/**
	 * Creates the full list of variant and sort it.
	 */
	private void computeVariantList (List<MGFilter> filtersList) {
		variantList = new ArrayList<VariantInterface>();
		cache.initialize();
		if (listOfVariantList.size() > 0) {
			for (MGVariantListForDisplay variantListForDisplay: listOfVariantList) {			// loop on every variant list for display
				List<VariantInterface> varianListTmp = variantListForDisplay.getVariantList();	// get the actual variant list
				for (VariantInterface variant: varianListTmp) {									// for every variant of the current list
					if (isValid(variant, filtersList)) {										// it must passes all filters to be added
						variantList.add(variant);
					}
				}
			}

			Collections.sort(variantList, new VariantComparator());								// sorts the list
			synchronizationBlank();																// adds the blank of synchronization
		}
	}


	/**
	 * @param variant		a variant
	 * @param filtersList	a list of filters
	 * @return				true is the variant passes all filters
	 */
	private boolean isValid (VariantInterface variant, List<MGFilter> filtersList) {
		for (MGFilter filter: filtersList) {			// loop on all filters
			if (!filter.isVariantValid(variant)) {		// test the variant for the current filter
				return false;							// if one is tested false, the variant does not pass
			}
		}
		return true;									// if all tests are correct, the variant passes
	}


	/**
	 * This method scans the full variant list and the variant list from the reference genome (that contains all insertions).
	 * It adds one by one variant from both list according to their position.
	 * Insertions present in the reference genome allow the creation of the blank of synchronization.
	 */
	private void synchronizationBlank () {
		if (MGDisplaySettings.INCLUDE_BLANK_OPTION == MGDisplaySettings.YES_MG_OPTION) {
			if (fittedChromosome != null) {
				Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
				List<VariantInterface> referenceVariantList = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeForDisplay().getReferenceGenome().getAllele().getVariantList(currentChromosome);
				List<VariantInterface> newVariantList = new ArrayList<VariantInterface>();
				int currentListIndex = 0;
				int referenceListIndex = 0;
				while ((currentListIndex < variantList.size()) && (referenceListIndex < referenceVariantList.size())) {
					VariantInterface currentVariant = variantList.get(currentListIndex);
					VariantInterface referenceVariant = referenceVariantList.get(referenceListIndex);

					if (currentVariant.getStart() < referenceVariant.getStart()) {
						newVariantList.add(currentVariant);
						currentListIndex++;
					} else if (referenceVariant.getStart() < currentVariant.getStart()) {
						newVariantList.add(referenceVariant);
						referenceListIndex++;
					} else {
						newVariantList.add(currentVariant);
						currentListIndex++;
						referenceListIndex++;
					}
				}
				fillRestOfList(newVariantList, variantList, currentListIndex);
				fillRestOfList(newVariantList, referenceVariantList, referenceListIndex);
				variantList = newVariantList;
			}
		}
	}


	/**
	 * This method adds all the variant from a temporary list into a "full" list.
	 * This process start from a specific index given in parameter to the end of the temporary list.
	 * @param fullList		the full list
	 * @param tmpList		the temporary list
	 * @param startIndex	the index to start the filling
	 * @return				the full list containing the new variants
	 */
	private List<VariantInterface> fillRestOfList (List<VariantInterface> fullList, List<VariantInterface> tmpList, int startIndex) {
		for (int i = startIndex; i < tmpList.size(); i++) {
			fullList.add(tmpList.get(i));
		}
		return fullList;
	}


	/**
	 * @param listOfVariantList the listOfVariantList to set
	 * @param filtersList list of filter to apply
	 */
	public void setListOfVariantList(List<MGVariantListForDisplay> listOfVariantList, List<MGFilter> filtersList) {
		if (variantsHaveChanged(listOfVariantList) || filtersHaveChanged(filtersList)) {
			this.listOfVariantList = listOfVariantList;
			resetList(filtersList);
		}
	}


	/**
	 * Reset the variant list.
	 * @param filtersList list of filter to apply
	 */
	public void resetList (List<MGFilter> filtersList) {
		computeVariantList(filtersList);
		fitToScreen();
	}


	/**
	 * @return the variantList
	 */
	public List<VariantInterface> getVariantList() {
		return variantList;
	}


	/**
	 * Compares the current list of variant list for display with another one
	 * @param listOfVariantList the other list of variant list for display
	 * @return true is lists are different, false otherwise
	 */
	private boolean variantsHaveChanged (List<MGVariantListForDisplay> listOfVariantList) {
		if ((this.listOfVariantList == null) || (listOfVariantList == null)) {
			return true;
		} else if (this.listOfVariantList.size() != listOfVariantList.size()) {
			return true;
		} else {
			for (MGVariantListForDisplay currentVariantList: this.listOfVariantList) {
				if (!listOfVariantList.contains(currentVariantList)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Compares the current list of filters with another one
	 * @param filtersList the other list of filer
	 * @return true is lists are different, false otherwise
	 */
	private boolean filtersHaveChanged (List<MGFilter> filtersList) {
		if ((this.filtersList == null) || (filtersList == null)) {
			return true;
		} else if (this.filtersList.size() != filtersList.size()) {
			return true;
		} else {
			for (MGFilter currentFilter: this.filtersList) {
				if (!filtersList.contains(currentFilter)) {
					return true;
				}
			}
		}
		return false;
	}


	///////////////////////////////////////////////////////////////////// Interface methods

	/**
	 * @param window the genome window
	 * @param xRatio the x ratio
	 * @return the variant list that fits the screen
	 */
	public final List<VariantInterface> getFittedData(GenomeWindow window, double xRatio) {
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
				fittedDataList = cache.getData(xRatio);
			} else {
				fitToScreen();
			}
		}
		List<VariantInterface> result = getFittedData(window.getStart(), window.getStop());
		return result;
	}


	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	protected void fitToScreen() {
		List<VariantInterface> currentVariantList = variantList;

		if (fittedXRatio > 1) {
			fittedDataList = currentVariantList;
		} else {
			fittedDataList = new ArrayList<VariantInterface>();
			int variantListSize = currentVariantList.size();
			if (variantListSize == 1) {
				fittedDataList.add(currentVariantList.get(0));
			} else if (variantListSize > 1) {
				int currentIndex = 0;
				int nextIndex = 1;

				while (currentIndex < variantListSize) {
					VariantInterface currentVariant = currentVariantList.get(currentIndex);
					boolean hasToBeMerged = false;
					int start = currentVariant.getStart();
					int stop = currentVariant.getStop();

					if (nextIndex < variantListSize) {
						VariantInterface nextVariant = currentVariantList.get(nextIndex);
						double distance = (nextVariant.getStart() - stop) * fittedXRatio;

						while ((distance < 1) && (nextIndex < variantListSize)) {
							hasToBeMerged = true;
							currentIndex++;
							nextIndex++;
							if (nextIndex < variantListSize) {
								currentVariant = currentVariantList.get(currentIndex);
								nextVariant = currentVariantList.get(nextIndex);

								int stopTmp = currentVariant.getStop();
								distance = (nextVariant.getStart() - stopTmp) * fittedXRatio;
								if (distance < 1) {
									stop = nextVariant.getStop();
								}
							}
						}
					}
					VariantInterface newVariant;
					if (hasToBeMerged) {
						newVariant = new MixVariant(start, stop);
					} else {
						newVariant = currentVariant;
					}

					fittedDataList.add(newVariant);

					currentIndex++;
					nextIndex++;
				}
			}
		}
		cache.setData(fittedXRatio, fittedDataList);
	}


	protected List<VariantInterface> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<VariantInterface> resultList = new ArrayList<VariantInterface>();
		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);

		if (indexStart > 0) {
			VariantInterface variant = fittedDataList.get(indexStart - 1);
			if (variant.getStop() >= start) {
				resultList.add(variant);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			if (i == indexStop) {
				VariantInterface variant = fittedDataList.get(indexStop );
				if (variant.getStop() >= start) {
					resultList.add(variant);
				}
			} else {
				resultList.add(fittedDataList.get(i));
			}
		}
		if ((indexStop + 1) < fittedDataList.size()) {
			VariantInterface variant = fittedDataList.get(indexStop + 1);
			if (variant.getStart() <= stop) {
				resultList.add(variant);
			}
		}
		return resultList;
	}


	/**
	 * Recursive function. Returns the index where the start value of the window is found
	 * or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the start value of the window is found or the index right after if the exact value is not find
	 */
	private int findStart(List<VariantInterface> list, int value, int indexStart, int indexStop) {
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
	 * Recursive function. Returns the index where the stop value of the window is found
	 * or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the index where the stop value of the window is found or the index right before if the exact value is not find
	 */
	private int findStop(List<VariantInterface> list, int value, int indexStart, int indexStop) {
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

	/////////////////////////////////////////////////////////////////////


	/**
	 * Prints a list of variant
	 * @param variantList the list of variant
	 */
	public static void printVariantList (List<VariantInterface> variantList) {
		for (VariantInterface variant: variantList) {
			variant.show();
		}
	}

}
