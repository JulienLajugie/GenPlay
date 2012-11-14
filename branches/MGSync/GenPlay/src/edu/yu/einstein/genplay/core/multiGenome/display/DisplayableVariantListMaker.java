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
import edu.yu.einstein.genplay.core.comparator.VariantDisplayComparator;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.CacheTrack;
import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.project.MultiGenomeProject;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantDisplay;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.utils.VariantDisplayPolicy;
import edu.yu.einstein.genplay.core.multiGenome.utils.VariantIterator;

/**
 * This class creates the list of variant for a track (or half a track) and its filters. It is set with a list of variant list for display. Creating a list consist of: - gathering all lists of
 * variants from each variant list for display - adding and testing the variants to the main list according to the filters - sorting the list - adding all the blank of synchronization The full list of
 * variant is stored as well as the fitted list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DisplayableVariantListMaker implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -5236981711624610822L;
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0; // saved format version

	protected Chromosome fittedChromosome = null; // Chromosome with the adapted data
	protected Double fittedXRatio = null; // xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )

	private List<MGVariantListForDisplay> listOfVariantList; // The list of list of variant for display
	private List<VariantDisplay> variantDisplayList; // The full list of variant
	private List<VariantDisplay> fittedDataList; // List of data of the current chromosome adapted to the screen resolution
	private List<MGFilter> filtersList;

	private VariantDisplayPolicy displayPolicy;

	private CacheTrack<List<VariantDisplay>> cache;

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
		out.writeObject(variantDisplayList);
		out.writeObject(fittedDataList);
		out.writeObject(filtersList);
		out.writeObject(displayPolicy);
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
		variantDisplayList = (List<VariantDisplay>) in.readObject();
		fittedDataList = (List<VariantDisplay>) in.readObject();
		filtersList = (List<MGFilter>) in.readObject();
		displayPolicy = (VariantDisplayPolicy) in.readObject();

		cache = new CacheTrack<List<VariantDisplay>>();
	}

	/**
	 * Constructor of {@link DisplayableVariantListMaker}
	 * @param window the genome window
	 * @param xRatio the x ratio
	 */
	public DisplayableVariantListMaker(GenomeWindow window, double xRatio) {
		listOfVariantList = new ArrayList<MGVariantListForDisplay>();
		variantDisplayList = new ArrayList<VariantDisplay>();
		this.fittedChromosome = window.getChromosome();
		this.fittedXRatio = xRatio;
		displayPolicy = new VariantDisplayPolicy();
		cache = new CacheTrack<List<VariantDisplay>>();
	}

	/**
	 * Creates the full list of variant and sort it.
	 */
	private void computeVariantList() {
		variantDisplayList = new ArrayList<VariantDisplay>();
		cache.initialize();
		if (listOfVariantList.size() > 0) {
			for (MGVariantListForDisplay variantListForDisplay : listOfVariantList) { // loop on every variant list for display
				List<Variant> varianListTmp = variantListForDisplay.getVariantList(); // get the actual variant list
				for (Variant variant : varianListTmp) { // for every variant of the current list
					variantDisplayList.add(new VariantDisplay(variant));
				}
			}
			Collections.sort(variantDisplayList, new VariantDisplayComparator()); // sorts the list
			addReferences(); // adds the blank of synchronization
		}
	}

	/**
	 * This method scans the full variant list and the variant list from the reference genome (that contains all insertions). It adds one by one variant from both list according to their position.
	 * Insertions present in the reference genome allow the creation of the blank of synchronization.
	 */
	private void addReferences() {
		// if (MGDisplaySettings.getInstance().includeReferences()) {
		if (fittedChromosome != null) {
			Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
			List<Variant> referenceVariantList = ProjectManager.getInstance().getMultiGenomeProject().getMultiGenomeForDisplay().getReferenceGenome().getAllele().getVariantList(currentChromosome);
			List<VariantDisplay> newVariantList = new ArrayList<VariantDisplay>();
			int currentListIndex = 0;
			int referenceListIndex = 0;
			while ((currentListIndex < variantDisplayList.size()) && (referenceListIndex < referenceVariantList.size())) {
				VariantDisplay currentVariant = variantDisplayList.get(currentListIndex);
				Variant referenceVariant = referenceVariantList.get(referenceListIndex);

				if (currentVariant.getStart() < referenceVariant.getStart()) {
					newVariantList.add(currentVariant);
					currentListIndex++;
				} else if (referenceVariant.getStart() < currentVariant.getStart()) {
					newVariantList.add(new VariantDisplay(referenceVariant));
					referenceListIndex++;
				} else {
					newVariantList.add(currentVariant);
					currentListIndex++;
					referenceListIndex++;
				}
			}

			// Fill the new list with the rest of variant display
			for (int i = currentListIndex; i < variantDisplayList.size(); i++) {
				newVariantList.add(variantDisplayList.get(i));
			}

			// Fill the new list with the rest of reference variant
			for (int i = referenceListIndex; i < referenceVariantList.size(); i++) {
				newVariantList.add(new VariantDisplay(referenceVariantList.get(i)));
			}

			variantDisplayList = newVariantList;
		}
	}


	/**
	 * @param listOfVariantList the listOfVariantList to set
	 * @param filtersList list of filter to apply
	 * @param showReference true if reference variants have to be displayed, false otherwise
	 * @param showFiltered true if filtered variants have to be displayed, false otherwise
	 */
	public void setListOfVariantList(List<MGVariantListForDisplay> listOfVariantList, List<MGFilter> filtersList, boolean showReference, boolean showFiltered) {
		boolean hasChanged = false;
		if (filtersHaveChanged(filtersList) || (displayPolicy.displayReference() != showReference) || (displayPolicy.displayFilteredVariant() != showFiltered)) {
			hasChanged = true;
			this.filtersList = filtersList;
			displayPolicy.setShowReference(showReference);
			displayPolicy.setShowFiltered(showFiltered);
		}
		if (variantsHaveChanged(listOfVariantList)) {
			hasChanged = true;
			this.listOfVariantList = listOfVariantList;
			computeVariantList();
		}
		if (hasChanged) {
			updateDisplayableVariantList();
			fitToScreen();
		}
	}


	/**
	 * Reset all lists without control.
	 * Used after SNP loading.
	 */
	public void resetList() {
		computeVariantList();
		updateDisplayableVariantList();
		fitToScreen();
	}


	/**
	 * @return the variantList
	 */
	public List<VariantDisplay> getVariantList() {
		return variantDisplayList;
	}

	/**
	 * Compares the current list of variant list for display with another one
	 * @param listOfVariantList the other list of variant list for display
	 * @return true is lists are different, false otherwise
	 */
	private boolean variantsHaveChanged(List<MGVariantListForDisplay> listOfVariantList) {
		if ((this.listOfVariantList == null) || (listOfVariantList == null)) {
			return true;
		} else if (this.listOfVariantList.size() != listOfVariantList.size()) {
			return true;
		} else {
			for (MGVariantListForDisplay currentVariantList : this.listOfVariantList) {
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
	private boolean filtersHaveChanged(List<MGFilter> filtersList) {
		if ((this.filtersList == null) || (filtersList == null)) {
			return true;
		} else if (this.filtersList.size() != filtersList.size()) {
			return true;
		} else {
			for (MGFilter currentFilter : this.filtersList) {
				if (!filtersList.contains(currentFilter)) {
					return true;
				}
			}
		}
		return false;
	}

	private void updateDisplayableVariantList() {
		List<VariantType> variantTypes = getVariantTypeList();
		Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		MultiGenomeProject multiGenomeProject = ProjectManager.getInstance().getMultiGenomeProject();
		ByteArrayAsIntegerList displayList = new ByteArrayAsIntegerList(multiGenomeProject.getMultiGenomeForDisplay().getReferenceGenome().getAllele().getPositionIndexSize(currentChromosome));
		for (VariantDisplay currentDisplayableVariant : variantDisplayList) {
			int displayCode = VariantDisplayPolicy.DO_NOT_DISPLAY;
			Variant currentVariant = currentDisplayableVariant.getSource();
			int referenceGenomePosition = currentVariant.getReferenceGenomePosition();
			if (variantTypes.contains(currentVariant.getType())) {
				displayCode = VariantDisplayPolicy.DISPLAY;
				if (!(currentVariant instanceof ReferenceVariant)) {
					if (!isValid(currentVariant, filtersList)) {
						displayCode = VariantDisplayPolicy.FILTERED;
					}
				}
			}

			int positionIndex = multiGenomeProject.getReferencePositionIndex(currentChromosome, referenceGenomePosition);
			displayList.set(positionIndex, displayCode);
		}

		displayPolicy.setDisplayPolicyList(displayList);
	}

	/**
	 * @return the list of {@link VariantType} requested for display
	 */
	private List<VariantType> getVariantTypeList() {
		List<VariantType> list = new ArrayList<VariantType>();
		for (MGVariantListForDisplay currentListForDisplay : listOfVariantList) {
			VariantType currentType = currentListForDisplay.getType();
			if (!list.contains(currentType)) {
				list.add(currentType);
				// if (displayPolicy.displayReference()) {
				if (currentType == VariantType.INSERTION) {
					list.add(VariantType.REFERENCE_INSERTION);
				} else if (currentType == VariantType.DELETION) {
					list.add(VariantType.REFERENCE_DELETION);
				} else if (currentType == VariantType.SNPS) {
					list.add(VariantType.REFERENCE_SNP);
				}
				// }
			}
		}
		return list;
	}

	/**
	 * @param variant a variant
	 * @param filtersList a list of filters
	 * @return true is the variant passes all filters
	 */
	private boolean isValid(Variant variant, List<MGFilter> filtersList) {
		if (filtersList != null) {
			for (MGFilter filter : filtersList) { // loop on all filters
				if (!filter.isVariantValid(variant)) { // test the variant for the current filter
					return false; // if one is tested false, the variant does not pass
				}
			}
		}
		return true; // if all tests are correct, the variant passes
	}

	/**
	 * @param showReference true if reference variants have to be displayed, false otherwise
	 * @param showFiltered true if filtered variants have to be displayed, false otherwise
	 * @return true if major policy options have changed, false otherwise
	 */
	public boolean optionsHaveChanged(boolean showReference, boolean showFiltered) {
		if (filtersHaveChanged(filtersList) || (displayPolicy.displayReference() != showReference) || (displayPolicy.displayFilteredVariant() != showFiltered)) {
			return true;
		}
		return false;
	}

	// /////////////////////////////////////////////////////////////////// Interface methods

	/**
	 * @param window the genome window
	 * @param xRatio the x ratio
	 * @return the variant list that fits the screen
	 */
	public final List<VariantDisplay> getFittedData(GenomeWindow window, double xRatio) {
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

		List<VariantDisplay> result = getFittedData(window.getStart(), window.getStop());
		return result;
	}

	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	public void fitToScreen() {
		List<VariantDisplay> currentVariantList = variantDisplayList;

		fittedDataList = new ArrayList<VariantDisplay>();
		VariantIterator iterator = new VariantIterator(currentVariantList, displayPolicy);

		if (fittedXRatio > 1) {
			while (iterator.hasNext()) {
				fittedDataList.add(iterator.next());
			}
		} else {
			// Initialize the first position
			int start = -1;
			int stop = -1;
			VariantDisplay currentVariant = null;
			if (iterator.hasNext()) {
				currentVariant = iterator.next();
				start = currentVariant.getStart();
				stop = currentVariant.getStop();
			}

			// Process the next positions
			VariantDisplay previousVariant = null;
			boolean hasToMerge = false;
			while (iterator.hasNext()) {
				previousVariant = currentVariant;
				currentVariant = iterator.next(); // Get the next position
				double distance = (currentVariant.getStart() - stop) * fittedXRatio; // Compare its distance with the previous position
				if (distance < 1) {
					hasToMerge = true;
					stop = currentVariant.getStop();
				} else {
					// Insert previous information
					if (hasToMerge) {
						hasToMerge = false;
						fittedDataList.add(new VariantDisplay(start, stop));
					} else {
						fittedDataList.add(previousVariant);
					}

					// Update current information
					start = currentVariant.getStart();
					stop = currentVariant.getStop();
				}
			}

			// Handle the last case
			if (hasToMerge) {
				fittedDataList.add(new VariantDisplay(start, stop));
			} else {
				if (currentVariant != null) {
					fittedDataList.add(currentVariant);
				}
			}
		}
		cache.setData(fittedXRatio, fittedDataList);
	}

	protected List<VariantDisplay> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<VariantDisplay> resultList = new ArrayList<VariantDisplay>();
		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);

		if (indexStart > 0) {
			VariantDisplay variant = fittedDataList.get(indexStart - 1);
			if (variant.getStop() >= start) {
				resultList.add(variant);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			if (i == indexStop) {
				VariantDisplay variant = fittedDataList.get(indexStop);
				try {
					variant.getStart();
				} catch (Exception e) {
					System.out.println();
				}
				if (variant.getStart() <= stop) {
					resultList.add(variant);
				}
			} else {
				resultList.add(fittedDataList.get(i));
			}
		}
		if ((indexStop + 1) < fittedDataList.size()) {
			VariantDisplay variant = fittedDataList.get(indexStop + 1);
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
	private int findStart(List<VariantDisplay> list, int value, int indexStart, int indexStop) {
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
	private int findStop(List<VariantDisplay> list, int value, int indexStart, int indexStop) {
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

	// ///////////////////////////////////////////////////////////////////

	/**
	 * Prints a list of variant
	 * @param variantList the list of variant
	 */
	public static void printVariantList(List<Variant> variantList) {
		for (Variant variant : variantList) {
			variant.show();
		}
	}

	/**
	 * Prints a list of variant
	 * @param variantList the list of variant
	 */
	public static void printVariantDisplayList(List<VariantDisplay> variantList) {
		for (VariantDisplay variant : variantList) {
			System.out.println(variant.getStart() + " to " + variant.getStop() + " " + variant.getType());
		}
	}

	/**
	 * @return the displayPolicy
	 */
	public VariantDisplayPolicy getDisplayPolicy() {
		return displayPolicy;
	}

	/**
	 * @return the {@link VariantIterator} of the current fitted data list
	 */
	public VariantIterator getVariantIterator() {
		return new VariantIterator(fittedDataList, displayPolicy);
	}

}
