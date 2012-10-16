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
package edu.yu.einstein.genplay.core.multiGenome.utils;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.list.arrayList.ByteArrayAsIntegerList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantDisplay;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplayPolicy {

	/** Code policy to hide a variant */
	public static final int DO_NOT_DISPLAY = 0;
	/** Code policy to show a variant */
	public static final int DISPLAY = 1;
	/** Code policy to show a variant as a filtered variant */
	public static final int FILTERED = 2;

	private ByteArrayAsIntegerList displayPolicy;						// list of boolean meaning whether variants can be displayed or not
	private Boolean showReference;
	private Boolean showFiltered;


	/**
	 * Constructor of {@link VariantDisplayPolicy}
	 */
	public VariantDisplayPolicy () {
		initialize(null, null, null);
	}


	/**
	 * Constructor of {@link VariantDisplayPolicy}
	 * @param displayPolicy	the display policy array
	 * @param showReference	to show the reference variants
	 * @param showFiltered	to show the filtered variants
	 */
	public VariantDisplayPolicy (ByteArrayAsIntegerList displayPolicy, Boolean showReference, Boolean showFiltered) {
		initialize(displayPolicy, showReference, showFiltered);
	}


	/**
	 * Initializes the {@link VariantDisplayPolicy}
	 * @param displayPolicy	the display policy array
	 * @param showReference	to show the reference variants
	 * @param showFiltered	to show the filtered variants
	 */
	public void initialize (ByteArrayAsIntegerList displayPolicy, Boolean showReference, Boolean showFiltered) {
		this.displayPolicy = displayPolicy;
		this.showReference = showReference;
		this.showFiltered = showFiltered;
	}


	/**
	 * @param variant a variant
	 * @return the display code of the variant
	 */
	public int getVariantDisplayPolicy (VariantDisplay variant) {
		if (variant.getSource() == null) {
			return DISPLAY;
		}
		int positionIndex = getVariantIndex(variant);
		int policy = displayPolicy.get(positionIndex);
		return policy;
	}


	/**
	 * @param variant a variant
	 * @return true if the variant has to be shown, false otherwise
	 */
	public boolean isShown (VariantDisplay variant) {
		return getVariantDisplayPolicy(variant) > 0;
	}


	/**
	 * @param variant a variant
	 * @return true if the variant has to be shown, false otherwise
	 */
	public boolean isFiltered (VariantDisplay variant) {
		return getVariantDisplayPolicy(variant) == FILTERED;
	}


	/**
	 * @return the displayPolicy
	 */
	public ByteArrayAsIntegerList getDisplayPolicyList() {
		return displayPolicy;
	}


	/**
	 * @param displayPolicy the displayList to set
	 */
	public void setDisplayPolicyList(ByteArrayAsIntegerList displayPolicy) {
		this.displayPolicy = displayPolicy;
	}


	/**
	 * @return the showReference
	 */
	public Boolean displayReference() {
		if (showReference == null) {
			return true;
		}
		return showReference;
	}


	/**
	 * @param showReference the showReference to set
	 */
	public void setShowReference(Boolean showReference) {
		this.showReference = showReference;
	}


	/**
	 * @return the showFiltered
	 */
	public boolean displayFilteredVariant() {
		if (showFiltered == null) {
			return true;
		}
		return showFiltered;
	}


	/**
	 * @param showFiltered the showFiltered to set
	 */
	public void setShowFiltered(Boolean showFiltered) {
		this.showFiltered = showFiltered;
	}


	/**
	 * @param variant a variant
	 * @return the index of a variant
	 */
	public int getVariantIndex (VariantDisplay variant) {
		int genomePosition = -1;
		Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		if (variant.getSource() == null) {
			genomePosition = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, null, variant.getStart(), chromosome, FormattedMultiGenomeName.REFERENCE_GENOME_NAME);
		} else {
			genomePosition = variant.getSource().getReferenceGenomePosition();
		}
		int positionIndex = -1;
		if (genomePosition != -1) {
			positionIndex = ProjectManager.getInstance().getMultiGenomeProject().getReferencePositionIndex(chromosome, genomePosition);
		}
		return positionIndex;
	}

}
