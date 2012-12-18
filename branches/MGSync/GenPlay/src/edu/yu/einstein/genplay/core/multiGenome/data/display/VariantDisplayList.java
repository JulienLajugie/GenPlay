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
public class VariantDisplayList {

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
	}


	/**
	 * @return the variants
	 */
	public List<List<Variant>> getVariants() {
		return variants;
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
	 * @return the display
	 */
	public byte[][] getDisplay() {
		return display;
	}


	/**
	 * @param alleleIndex the index of the allele to iterate
	 * @return a specific iterator for {@link VariantDisplayListIterator}
	 */
	public VariantDisplayListIterator getIterator (int alleleIndex) {
		return new VariantDisplayListIterator(this, alleleIndex);
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}

}
