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
import java.io.Serializable;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;

/**
 * This class is supposed to be used to display variant in a very efficient way.
 * It contains minimal information such as start and stop position on the current coordinate system.
 * It contains also a reference to the native variant that has more information for more operations.
 * 
 * This class solves the issue of recalculating start and stop position for display.
 * The additional required memory is very insignificant compared to the speed of the display.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariantDisplay implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = 6787479396400757243L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version

	private Variant source;		// The native variant.
	private int start;			// The start position.
	private int stop;			// The stop position.


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(source);
		out.writeInt(start);
		out.writeInt(stop);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		source = (Variant) in.readObject();
		start = in.readInt();
		stop = in.readInt();
	}


	/**
	 * Constructor of {@link VariantDisplay}
	 * @param source the native variant
	 */
	public VariantDisplay (Variant source) {
		this.source = source;
		start = source.getStart();
		stop = source.getStop();
	}


	/**
	 * Constructor of {@link VariantDisplay}
	 * @param start the start position
	 * @param stop	the stop position
	 */
	public VariantDisplay (int start, int stop) {
		this.source = null;
		this.start = start;
		this.stop = stop;
	}


	/**
	 * @return the source
	 */
	public Variant getSource() {
		return source;
	}


	/**
	 * @param source the source to set
	 */
	public void setSource(Variant source) {
		this.source = source;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}


	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @return the stop
	 */
	public int getStop() {
		return stop;
	}


	/**
	 * @param stop the stop to set
	 */
	public void setStop(int stop) {
		this.stop = stop;
	}


	/**
	 * @return the type of the variation
	 */
	public VariantType getType() {
		if (source == null) {
			return VariantType.MIX;
		}
		return source.getType();
	}


	/**
	 * @return the score of the variation
	 */
	public float getScore() {
		if (source == null) {
			return 1000;
		}
		return source.getScore();
	}


	/**
	 * @return the length of the variation
	 */
	public int getLength() {
		if (source == null) {
			return stop - start;
		}
		return source.getLength();
	}


	/**
	 * @return the string of the nucleotide of the variation
	 */
	public String getVariantSequence() {
		if (source == null) {
			return "-";
		}
		return source.getVariantSequence();
	}


	/**
	 * @return the VCF line related to the variant
	 */
	public VCFLine getVCFLine() {
		if (source == null) {
			return null;
		}
		return source.getVCFLine();
	}


	/**
	 * @return true if the variant source is a reference variant
	 */
	public boolean isReference () {
		return (source != null) && (source instanceof ReferenceVariant);
	}


	/**
	 * @return true if the variant source is a no call
	 */
	public boolean isNoCall () {
		return (source != null) && (source instanceof NoCallVariantOld);
	}


	/**
	 * @return true if the variant display is a mixed variant
	 */
	public boolean isMix () {
		return source == null;
	}


	/**
	 * @return true if the variant is an alternative, false otherwise
	 */
	public boolean isAlternative () {
		return (source != null) && ((source instanceof IndelVariant) || (source instanceof SNPVariant));
	}


	/**
	 * A variant is dominant according to the following order of importance:
	 * - is an alternative
	 * - is a reference
	 * - is a no call
	 * - is a mix of variants
	 * 
	 * If the given variant is null, the current variant is dominant.
	 * If both, current and given, variants are not at the same position, none of them is dominant.
	 * If both, current and given, variants have the same type, both of them are dominants.
	 * 
	 * @param variant the variant to compare
	 * @return	true if the current variant is dominant compare to the given variant, false otherwise
	 */
	public boolean isDominant (VariantDisplay variant) {
		Boolean result = false;

		if (variant == null) {
			result = true;
		} else if (getStart() == variant.getStart()) {
			if (	(isAlternative() && variant.isAlternative()) ||
					(isReference() && variant.isReference()) ||
					(isNoCall() && variant.isNoCall()) ||
					(isMix() && variant.isMix())){
				result = true;
			} else if (isAlternative() && (variant.isMix() || variant.isReference()  || variant.isNoCall())) {
				result = true;
			} else if (isReference() && (variant.isNoCall() || variant.isMix())) {
				result = true;
			} else if (isNoCall() && variant.isMix()) {
				result = true;
			}
		}

		return result;
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
		VariantDisplay test = (VariantDisplay)obj;

		if ((source != null) && (test.getSource()!= null)) {
			return source.equals(test.getSource());
		}

		return ((start == test.getStart())) &&
				(stop == test.getStop()) &&
				(getScore() == test.getScore());
	}

}
