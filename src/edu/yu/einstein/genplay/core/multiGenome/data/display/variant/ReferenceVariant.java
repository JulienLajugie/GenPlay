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
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReferenceVariant extends MultiNucleotideVariant {

	/** Default serial version ID */
	private static final long serialVersionUID = 2296711907593637593L;
	private VariantType type;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	@Override
	protected void writeObject(ObjectOutputStream out) throws IOException {
		super.writeObject(out);
		out.writeObject(type);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		super.readObject(in);
		type = (VariantType) in.readObject();
	}


	/**
	 * Constructor of {@link ReferenceVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param type the type of the reference variant
	 */
	public ReferenceVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex, VariantType type) {
		super(chromosomeContent, referencePositionIndex);
		this.type = type;
	}


	/**
	 * Constructor of {@link ReferenceVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 * @param stop stop position
	 * @param type the type of the reference variant
	 */
	public ReferenceVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex, int start, int stop, VariantType type) {
		super(chromosomeContent, referencePositionIndex, start, stop);
		this.type = type;
	}


	@Override
	public VariantType getType() {
		return type;
	}


	/**
	 * @return a description of the {@link Variant}
	 */
	@Override
	public String getDescription () {
		String description = super.getDescription();
		description += " TYPE: " + type.name() + ";";
		return description;
	}


	@Override
	public String getVariantSequence() {
		VCFLine line = getVCFLine();
		if (line != null) {
			String chain = "-";
			String ref = line.getREF();
			int length = getLength();
			if (length == 1) {
				chain = ref;
				//} else if (length < 0) {
			} else {
				chain = ref.substring(1);
			}
			return chain;
		}
		return super.getVariantSequence();
	}
}
