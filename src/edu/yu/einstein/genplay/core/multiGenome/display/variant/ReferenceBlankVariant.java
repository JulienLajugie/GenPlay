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
package edu.yu.einstein.genplay.core.multiGenome.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.display.MGVariantListForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReferenceBlankVariant implements Serializable, VariantInterface {

	/** Generated serial version ID */
	private static final long serialVersionUID = -2068590198125427396L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private int 						referenceGenomePosition;
	private int 						length;
	private final int					chromosomeCode;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(referenceGenomePosition);
		out.writeInt(length);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		referenceGenomePosition = in.readInt();
		length = in.readInt();
	}


	/**
	 * Constructor of {@link ReferenceBlankVariant}
	 * @param referenceGenomePosition
	 * @param length
	 * @param chromosomeCode code of the chromosome (eg: 1 -> chr1)
	 */
	public ReferenceBlankVariant(int referenceGenomePosition, int length, int chromosomeCode) {
		this.referenceGenomePosition = referenceGenomePosition;
		this.length = length;
		this.chromosomeCode = chromosomeCode;
	}


	@Override
	public MGVariantListForDisplay getVariantListForDisplay() {
		return null;
	}


	@Override
	public int getReferenceGenomePosition() {
		return referenceGenomePosition;
	}


	@Override
	public int getLength() {
		return length;
	}


	@Override
	public float getScore() {
		return -1;
	}


	@Override
	public int phasedWithPos() {
		return -1;
	}


	@Override
	public VariantType getType() {
		return VariantType.BLANK;
	}


	@Override
	public void show() {
		String info = "[P:" + referenceGenomePosition + "; ";
		info += "T: " + getType() + "; ";
		info += "L: " + length + "; ";
		info += "St: " + getStart() + "; ";
		info += "Sp: " + getStop() + "; ";
		info += "P': " + phasedWithPos() + "]";
		System.out.println(info);
	}


	private Chromosome getChromosome () {
		return ProjectManager.getInstance().getProjectChromosome().get(chromosomeCode);
	}


	@Override
	public int getStart() {
		return ShiftCompute.getPosition(FormattedMultiGenomeName.REFERENCE_GENOME_NAME, getAlleleType(), referenceGenomePosition, getChromosome(), FormattedMultiGenomeName.META_GENOME_NAME) + 1;
		//return ShiftCompute.computeShiftForReferenceGenome(getChromosome(), referenceGenomePosition) + 1;
	}


	@Override
	public int getStop() {
		return ShiftCompute.getPosition(FormattedMultiGenomeName.REFERENCE_GENOME_NAME, getAlleleType(), referenceGenomePosition + 1, getChromosome(), FormattedMultiGenomeName.META_GENOME_NAME) - 1;
		//return ShiftCompute.computeShiftForReferenceGenome(getChromosome(), referenceGenomePosition + 1) - 1;
	}


	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}

		// object must be Test at this point
		ReferenceBlankVariant test = (ReferenceBlankVariant)obj;
		return (referenceGenomePosition == test.getReferenceGenomePosition()) &&
				(length == test.getLength());
	}


	@Override
	public AlleleType getAlleleType() {
		return null;
	}


	@Override
	public String getGenomeName() {
		return ProjectManager.getInstance().getAssembly().getDisplayName();
	}


	@Override
	public VCFLine getVCFLine() {
		return VCFLineUtility.getVCFLine(this);
	}


	@Override
	public String getVariantSequence() {
		return "-";
	}
}
