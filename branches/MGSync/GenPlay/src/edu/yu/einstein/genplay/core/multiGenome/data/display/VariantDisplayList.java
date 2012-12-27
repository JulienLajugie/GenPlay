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
package edu.yu.einstein.genplay.core.multiGenome.data.display;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGFileContentManager;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayList implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = 2664998644351746289L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	/** When a variant is shown */
	public static byte SHOW = 0;
	/** When a variant is hidden */
	public static byte HIDE = -1;
	/** When a variant is filtered */
	public static byte FILTER = 1;

	private List<List<Variant>> variants;
	private byte[][] display;
	private String genomeName;
	private List<VariantType> types;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(variants);
		out.writeObject(display);
		out.writeObject(genomeName);
		out.writeObject(types);
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
		variants = (List<List<Variant>>) in.readObject();
		display = (byte[][]) in.readObject();
		genomeName = (String) in.readObject();
		types = (List<VariantType>) in.readObject();
	}


	/**
	 * Constructor of {@link VariantDisplayList}
	 */
	public VariantDisplayList () {
		initialize(null, null);
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


	/**
	 * Generate the lists of variants
	 */
	public void generateLists () {
		VariantDisplayListBuilder builder = new VariantDisplayListBuilder();
		variants = builder.getList(genomeName, types);
		builder = null;
	}


	/**
	 * Update the display policy using the list of filters
	 * @param filters list of {@link MGFilter}
	 */
	public void updateDisplay (List<MGFilter> filters) {
		initialyzeDisplay();
		MGFileContentManager contentManager = ProjectManager.getInstance().getMultiGenomeProject().getFileContentManager();
		Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		for (int i = 0; i < variants.size(); i++) {
			List<Variant> currentVariantList = variants.get(i);
			for (int j = 0; j < currentVariantList.size(); j++) {
				Variant currentVariant = currentVariantList.get(j);
				VCFFile file = contentManager.getFile(currentChromosome, currentVariant.getChromosomeContent());

				boolean valid = true;
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
					display = FILTER;
				}
				this.display[i][j] = display;
			}
		}
	}


	private void initialyzeDisplay () {
		display = new byte[variants.size()][];
		for (int i = 0; i < variants.size(); i++) {
			List<Variant> currentVariantList = variants.get(i);
			display[i] = new byte[currentVariantList.size()];
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
	 * @param alleleIndex			the index of an allele
	 * @param metaGenomePosition	a meta genome position
	 * @return the variant at the given allele and meta genome position, null if not found
	 */
	public Variant getVariant (int alleleIndex, int metaGenomePosition) {
		Variant variant = null;
		int index = getIndex(variants.get(alleleIndex), metaGenomePosition);
		if (index > -1) {
			variant = variants.get(0).get(index);
		}
		return variant;
	}


	/**
	 * Recursive function. Returns the index where the value is found or -1 if the exact value is not found.
	 * @param list
	 * @param value	value
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
	 * @return the variants
	 */
	public List<List<Variant>> getVariants() {
		return variants;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return the display
	 */
	public byte[][] getDisplay() {
		return display;
	}
}
