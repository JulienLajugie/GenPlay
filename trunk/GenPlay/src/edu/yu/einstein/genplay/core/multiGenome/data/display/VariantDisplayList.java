/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.data.display;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGFileContentManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;

/**
 * A {@link VariantDisplayList} handles the list of {@link Variant} for each allele.
 * There is as many {@link Variant} lists as alleles.
 * It also handle the display policy of each {@link Variant}.
 * A {@link Variant} can be:
 * - shown
 * - filtered but shown (the "hide filtered variants" feature is off)
 * - hidden because filtered (the "hide filtered variants" feature is on)
 * - hidden because it is a homozygote reference (the "hide references" feature is on)
 * - hidden because filtered AND because it is a homozygote reference (both feature mentionned above are on)
 * 
 * The {@link VariantDisplayList} is specific of one genome and knows which {@link VariantType} it handles.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayList implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = 2664998644351746289L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 1;		// saved format version
	/** When a variant is shown */
	public static byte SHOW = 1;
	/** When a variant is filtered but shown */
	public static byte SHOW_FILTER = 2;

	/** When a variant is hidden because of all options */
	public static byte HIDE_ALL = -1;
	/** When a variant is hidden because filtered variants must be hidden */
	public static byte HIDE_FILTER = -2;
	/** When a variant is hidden because references must be hidden (case of a filtered homozygote reference) */
	public static byte HIDE_REFERENCE = -3;


	private List<List<Variant>> variants;	// The lists of variants for all alleles.
	private byte[][] display;				// The display policy bytes for all variants within all alleles (as array of bytes for memory usage).
	private String genomeName;				// The name of the genome.
	private List<VariantType> types;		// The list of variant type to handle.


	/**
	 * Constructor of {@link VariantDisplayList}
	 */
	public VariantDisplayList () {
		initialize(null, null);
	}


	/**
	 * Generate the lists of variants
	 */
	public void generateLists () {
		VariantDisplayListBuilder builder = new VariantDisplayListBuilder();
		variants = builder.getList(genomeName, types);
		builder = null;
	}


	/**
	 * @return the display
	 */
	public byte[][] getDisplay() {
		return display;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * Recursive function. Returns the index where the value is found or -1 if the exact value is not found.
	 * @param list
	 * @param value	a position on the meta genome
	 * @return the index where the start value of the window is found or -1 if the value is not found
	 */
	public int getIndex (List<Variant> list, int value) {
		int index = getIndex(list, value, 0, list.size() - 1);
		int start = list.get(index).getStart();
		int stop = list.get(index).getStop();
		if ((value >= start) && (value < stop)) {
			return index;
		}
		return -1;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @param indexStart	start index (in the data array)
	 * @param indexStop		stop index (in the data array)
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int getIndex (List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else {
			int start = list.get(indexStart + middle).getStart();
			int stop = list.get(indexStart + middle).getStop();
			if ((value >= start) && (value < stop)) {
				return indexStart + middle;
			} else if (value > start) {
				return getIndex(list, value, indexStart + middle + 1, indexStop);
			} else {
				return getIndex(list, value, indexStart, indexStart + middle);
			}
		}
	}


	/**
	 * @param alleleIndex the index of the allele to iterate
	 * @return a specific iterator for {@link VariantDisplayListIterator}
	 */
	public VariantDisplayListIterator getIterator (int alleleIndex) {
		return new VariantDisplayListIterator(this, alleleIndex);
	}


	/**
	 * @return the variants
	 */
	public List<List<Variant>> getVariants() {
		return variants;
	}


	/**
	 * @param alleleIndex			the index of an allele
	 * @param metaGenomePosition	a meta genome position
	 * @return the list of variants at the given allele including the given meta genome position, null if not found
	 */
	public List<Variant> getVariantsInArea (int alleleIndex, int metaGenomePosition) {
		List<Variant> result = new ArrayList<Variant>();
		int index = getIndex(variants.get(alleleIndex), metaGenomePosition);
		if (index > -1) {
			result.add(variants.get(alleleIndex).get(index));
			result.addAll(lookAfterIndex(alleleIndex, index, metaGenomePosition));
			result.addAll(lookBeforeIndex(alleleIndex, index, metaGenomePosition));
		}
		return result;
	}



	/**
	 * Initializes the {@link VariantDisplayList} input parameters
	 * @param genomeName the name of a genome
	 * @param types a list of {@link VariantType}
	 */
	public void initialize (String genomeName, List<VariantType> types) {
		this.genomeName = genomeName;
		this.types = types;
		display = null;
	}


	private void initialyzeDisplay () {
		display = new byte[variants.size()][];
		for (int i = 0; i < variants.size(); i++) {
			List<Variant> currentVariantList = variants.get(i);
			display[i] = new byte[currentVariantList.size()];
		}
	}


	/**
	 * Look for all {@link Variant} after a specific index (within a specific allele) which include a meta genome position.
	 * @param alleleIndex			the index of the allele
	 * @param index					the index where to start
	 * @param metaGenomePosition	the meta genome position to use
	 * @return	the list of {@link Variant} including the meta genome position after the given index, an empty list otherwise
	 */
	private List<Variant> lookAfterIndex (int alleleIndex, int index, int metaGenomePosition) {
		List<Variant> result = new ArrayList<Variant>();
		int nextIndex = index + 1;
		boolean includePosition = true;
		List<Variant> variantList = variants.get(alleleIndex);
		int size = variantList.size();

		while (includePosition && (nextIndex < size)) {
			Variant current = variantList.get(nextIndex);
			if ((metaGenomePosition >= current.getStart()) && (metaGenomePosition < current.getStop())) {
				result.add(current);
				nextIndex++;
			} else {
				includePosition = false;
			}
		}
		return result;
	}


	/**
	 * Look for all {@link Variant} before a specific index (within a specific allele) which include a meta genome position.
	 * @param alleleIndex			the index of the allele
	 * @param index					the index where to start
	 * @param metaGenomePosition	the meta genome position to use
	 * @return	the list of {@link Variant} including the meta genome position before the given index, an empty list otherwise
	 */
	private List<Variant> lookBeforeIndex (int alleleIndex, int index, int metaGenomePosition) {
		List<Variant> result = new ArrayList<Variant>();
		int previousIndex = index - 1;
		boolean includePosition = true;
		List<Variant> variantList = variants.get(alleleIndex);

		while (includePosition && (previousIndex > -1)) {
			Variant current = variantList.get(previousIndex);
			if ((metaGenomePosition >= current.getStart()) && (metaGenomePosition < current.getStop())) {
				result.add(current);
				previousIndex--;
			} else {
				includePosition = false;
			}
		}
		return result;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int savedVersion = in.readInt();
		if (savedVersion == 0) { // after version 0 we don't load the variant list anymore, we regenerate it instead
			variants = (List<List<Variant>>) in.readObject();
		}
		display = (byte[][]) in.readObject();
		genomeName = (String) in.readObject();
		types = (List<VariantType>) in.readObject();
		if (savedVersion > 0) { // regenerate the list instead of loading it
			generateLists();
		}
	}


	/**
	 * Update the display policy using the list of filters
	 * @param filters list of {@link MGFilter}
	 * @param showFilter true if filtered {@link Variant} have to be shown, false otherwise
	 */
	public void updateDisplay (List<MGFilter> filters, boolean showFilter) {
		initialyzeDisplay();
		updateDisplayForFilters(filters, showFilter);
	}


	/**
	 * Updates the display policy arrays according to the filters and the filter option
	 * @param filters		the list of {@link MGFilter}
	 * @param showFilter	the filter option (true: filters are shown, false: filters are hidden)
	 */
	private void updateDisplayForFilters (List<MGFilter> filters, boolean showFilter) {
		MGFileContentManager contentManager = ProjectManager.getInstance().getMultiGenomeProject().getFileContentManager();
		Chromosome currentChromosome = ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getChromosome();
		for (int i = 0; i < variants.size(); i++) {
			List<Variant> currentVariantList = variants.get(i);
			for (int j = 0; j < currentVariantList.size(); j++) {
				Variant currentVariant = currentVariantList.get(j);
				boolean valid = true;
				VCFFile file = contentManager.getFile(currentChromosome, currentVariant.getChromosomeContent());
				for (MGFilter filter: filters) {
					if (filter instanceof VCFFilter) {
						VCFFilter currentFilter = (VCFFilter) filter;
						if (currentFilter.getVCFFile().equals(file)) {
							valid = currentFilter.isVariantValid(currentVariant.getReferencePositionIndex());
							if (!valid) {
								break;
							}
						}
					}
				}
				byte display = SHOW;
				if (!valid) {
					if (showFilter) {
						display = SHOW_FILTER;
					} else {
						display = HIDE_FILTER;
					}
				}
				this.display[i][j] = display;
			}
		}
	}


	/**
	 * Update the whole display policy array of the current list of variant to match defined settings about:
	 * - showing the references
	 * - showing the filtered variants
	 * @param showReference true if the homozygote references have to be shown, false otherwise
	 * @param showFilter	true if the filtered variants have to be shown, false otherwise
	 */
	public void updateDisplayForOption (boolean showReference, boolean showFilter) {
		if ((variants.size() > 0) && (variants.get(0).size() > 0)) {
			int alleleNumber = variants.size();
			int variantNumber = variants.get(0).size();

			// For the reference option
			for (int i = 0; i < variantNumber; i++) {
				boolean isFullReference = true;
				for (int j = 0; j < alleleNumber; j++) {
					if (!(variants.get(j).get(i) instanceof ReferenceVariant)) {
						isFullReference = false;
						break;
					}
				}
				if (isFullReference) {
					for (int j = 0; j < alleleNumber; j++) {
						byte currentDisplay = display[j][i];
						byte newDisplay = currentDisplay;
						if (showReference) {
							if (currentDisplay == HIDE_ALL) {
								newDisplay = HIDE_FILTER;
							} else if (currentDisplay == HIDE_REFERENCE) {
								newDisplay = SHOW;
							}
						} else {
							if (currentDisplay == SHOW) {
								newDisplay = HIDE_REFERENCE;
							} else if (currentDisplay == HIDE_FILTER) {
								newDisplay = HIDE_ALL;
							}
						}
						display[j][i] = newDisplay;
					}
				}
			}


			// For the filter option
			for (int allele = 0; allele < display.length; allele++) {
				for (int pos = 0; pos < display[allele].length; pos++) {
					byte currentDisplay = display[allele][pos];
					byte newDisplay = currentDisplay;
					if (showFilter) {
						if (currentDisplay == HIDE_ALL) {
							newDisplay = HIDE_REFERENCE;
						} else if (currentDisplay == HIDE_FILTER) {
							newDisplay = SHOW_FILTER;
						}
					} else {
						if (currentDisplay == SHOW_FILTER) {
							newDisplay = HIDE_FILTER;
						}/* else if (currentDisplay == HIDE_REFERENCE) {
							newDisplay = HIDE_ALL;
						}*/
					}
					display[allele][pos] = newDisplay;
				}
			}
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(display);
		out.writeObject(genomeName);
		out.writeObject(types);
	}
}
