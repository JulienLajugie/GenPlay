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
package edu.yu.einstein.genplay.core.multiGenome.data.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.VariantDisplayList;

/**
 * A {@link VariantDisplay} is exclusively for display purposes,
 * it contains the minimum amount of information to retrieve quickly and safely its specific display information.
 * A {@link Variant} itself can be involved in different layers/tracks but its way to display it can also be different every time.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -3704675535072054858L;
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0; // saved format version

	private VariantDisplayList displayList;		// The variant display list.
	private Variant variant;					// The variant.
	private int allele;							// The allele where the variant is.
	private int index;							// The index of the variant in the variant display list.


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(displayList);
		out.writeObject(variant);
		out.writeInt(allele);
		out.writeInt(index);
	}

	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		displayList = (VariantDisplayList) in.readObject();
		variant = (Variant) in.readObject();
		allele = in.readInt();
		index = in.readInt();
	}


	/**
	 * Constructor of {@link VariantDisplay}
	 * @param displayList the {@link VariantDisplayList}
	 * @param variant	the {@link Variant}
	 * @param allele	the index on which allele the {@link Variant} is
	 * @param index	the index of the {@link Variant} within its own {@link VariantDisplayList}
	 */
	public VariantDisplay (VariantDisplayList displayList, Variant variant, int allele, int index) {
		this.displayList = displayList;
		this.variant = variant;
		this.allele = allele;
		this.index = index;
	}


	/**
	 * @return the {@link Variant}
	 */
	public Variant getVariant () {
		return variant;
	}


	/**
	 * @return the display information of the {@link VariantDisplay}
	 */
	public byte getDisplay () {
		if (displayList != null) {
			return displayList.getDisplay()[allele][index];
		}
		return VariantDisplayList.SHOW;
	}


	/**
	 * @return the genome name where the {@link Variant} is
	 */
	public String getGenomeName () {
		if (displayList != null) {
			return displayList.getGenomeName();
		}
		return null;
	}


	/**
	 * @return the {@link AlleleType} of the {@link Variant}
	 */
	public AlleleType getAlleleType () {
		return AlleleType.getAlleleType(allele);
	}


	/**
	 * @return the sequence of nucleotide for the variant
	 */
	public String getVariantSequence() {
		if ((variant instanceof InsertionVariant) || (variant instanceof SNPVariant)) {
			return variant.getVariantSequence(getGenomeName(), AlleleType.getAlleleType(allele));
		} else {
			return variant.getVariantSequence();
		}
	}
}
