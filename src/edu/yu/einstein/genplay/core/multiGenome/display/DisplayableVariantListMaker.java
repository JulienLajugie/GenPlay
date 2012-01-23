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
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MixVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantComparator;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DisplayableVariantListMaker implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -5236981711624610822L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	protected Chromosome				fittedChromosome = null;		// Chromosome with the adapted data
	protected Double					fittedXRatio = null;			// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )

	private List<MGVariantListForDisplay> 	listOfVariantList;
	private List<VariantInterface> 			variantList;
	private List<VariantInterface>		 	fittedDataList;				// List of data of the current chromosome adapted to the screen resolution

	
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
	}
	

	/**
	 * Constructor of {@link DisplayableVariantListMaker}
	 * @param window the genome window
	 * @param xRatio the x ratio
	 */
	public DisplayableVariantListMaker (GenomeWindow window, double xRatio) {
		listOfVariantList = null;
		this.fittedChromosome = window.getChromosome();
		this.fittedXRatio = xRatio;
	}


	/**
	 * Creates the full list of variant and sort it.
	 */
	private void computeVariantList () {
		variantList = new ArrayList<VariantInterface>();
		for (MGVariantListForDisplay variantListForDisplay: listOfVariantList) {
			List<VariantInterface> varianListTmp = variantListForDisplay.getVariantList();
			for (VariantInterface variant: varianListTmp) {
				variantList.add(variant);
			}
		}
		Collections.sort(variantList, new VariantComparator());
	}


	/**
	 * @param listOfVariantList the listOfVariantList to set
	 */
	public void setListOfVariantList(List<MGVariantListForDisplay> listOfVariantList) {
		if (listOfVariantList == null) {
			variantList = null;
			this.listOfVariantList = null;
		} else {
			if (hasChanged(listOfVariantList)) {
				this.listOfVariantList = listOfVariantList;
				resetList();
			}
		}
	}


	/**
	 * Reset the variant list.
	 */
	public void resetList () {
		if (listOfVariantList != null) {
			computeVariantList();
			fitToScreen();
		}
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
	private boolean hasChanged (List<MGVariantListForDisplay> listOfVariantList) {
		if (this.listOfVariantList == null || listOfVariantList == null) {
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


	///////////////////////////////////////////////////////////////////// Interface methods


	/**
	 * @param window the genome window
	 * @param xRatio the x ratio
	 * @return the variant list that fits the screen
	 */
	public final List<VariantInterface> getFittedData(GenomeWindow window, double xRatio) {
		if ((fittedChromosome == null) || (!fittedChromosome.equals(window.getChromosome()))) {
			fittedChromosome = window.getChromosome();
			if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
				fittedXRatio = xRatio;
			}
			fitToScreen();
		} else if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
			fittedXRatio = xRatio;
			fitToScreen();
		}
		return getFittedData(window.getStart(), window.getStop());
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

				while (nextIndex < currentVariantList.size()) {
					VariantInterface currentVariant = currentVariantList.get(currentIndex);
					VariantInterface nextVariant = currentVariantList.get(nextIndex);
					int start = currentVariant.getStart();
					int stop = currentVariant.getStop();
					double distance = (nextVariant.getStart() - stop) * fittedXRatio;

					boolean hasToBeMerged = false;
					while ((distance < 1) && (nextIndex < currentVariantList.size())) {
						hasToBeMerged = true;
						currentIndex++;
						nextIndex++;
						if (nextIndex < currentVariantList.size()) {
							currentVariant = currentVariantList.get(currentIndex);
							stop = currentVariant.getStop();
							nextVariant = currentVariantList.get(nextIndex);
							distance = (nextVariant.getStart() - stop) * fittedXRatio;
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
			resultList.add(fittedDataList.get(i));
		}
		if (indexStop + 1 < fittedDataList.size()) {
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
