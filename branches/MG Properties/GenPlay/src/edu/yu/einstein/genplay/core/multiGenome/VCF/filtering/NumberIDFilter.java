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
package edu.yu.einstein.genplay.core.multiGenome.VCF.filtering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NumberIDFilter implements NumberIDFilterInterface {

	/** Generated default serial ID*/
	private static final long serialVersionUID = 6939178664986604958L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private VCFHeaderType 			ID;				// ID of the filter
	private String					category;		// category of the filter (ALT QUAL FILTER INFO FORMAT)
	private InequalityOperators		inequation01;
	private InequalityOperators 	inequation02;
	private Float 					value01;
	private Float 					value02;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(ID);
		out.writeObject(category);
		out.writeObject(inequation01);
		if (inequation02 != null) {
			out.writeObject(inequation02);
		}
		out.writeFloat(value01);
		if (value02 != null) {
			out.writeFloat(value02);
		}
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		ID = (VCFHeaderType) in.readObject();
		category = (String) in.readObject();
		inequation01 = (InequalityOperators) in.readObject();
		try {
			inequation02 = (InequalityOperators) in.readObject();
		} catch (Exception e) {
			inequation02 = null;
		}
		value01 = in.readFloat();
		try {
			value02 = in.readFloat();
		} catch (Exception e) {
			value02 = null;
		}
	}


	@Override
	public boolean passFilter(String genomeFullName, Variant variant) {
		Float value = getValue(genomeFullName, variant);
		boolean result01 = false;
		boolean result02 = false;

		if (value01 != null) {
			result01 = isValid(inequation01, value01, value);
		}

		if (value02 != null) {
			result02 = isValid(inequation02, value02, value);
		}

		// non cumulative treatment
		if (result01 || result02) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public VCFHeaderType getID() {
		return ID;
	}


	@Override
	public void setID(VCFHeaderType id) {
		this.ID = id;
	}


	@Override
	public InequalityOperators getInequation01() {
		return inequation01;
	}


	@Override
	public void setInequation01(InequalityOperators inequation01) {
		this.inequation01 = inequation01;
	}


	@Override
	public InequalityOperators getInequation02() {
		return inequation02;
	}


	@Override
	public void setInequation02(InequalityOperators inequation02) {
		this.inequation02 = inequation02;
	}


	@Override
	public Float getValue01() {
		return value01;
	}


	@Override
	public void setValue01(Float value01) {
		this.value01 = value01;
	}


	@Override
	public Float getValue02() {
		return value02;
	}


	@Override
	public void setValue02(Float value02) {
		this.value02 = value02;
	}


	@Override
	public String toStringForDisplay() {
		String text = "";

		text += "x " + inequation01 + " " + value01;
		if (inequation02 != null && value02 != null) {
			text += " and ";
			text += "x " + inequation02 + " " + value02;
		}

		return text;
	}


	@Override
	public String getErrors() {
		String error = "";

		if (ID == null) {
			error += "ID missing;";
		}

		if (inequation01 == null || inequation01.equals(" ")) {
			error += "First inequation invalid;";
		}

		if (value01 == null) {
			error += "First value invalid;";
		}

		if (inequation02 != null && inequation02.equals(" ") && value02 != null) {
			error += "Second sign inequation missing";
		}

		if (inequation02 != null && !inequation02.equals(" ") && value02 == null) {
			error += "Second value missing";
		}

		if (error.equals("")) {
			return null;
		} else {
			return error;
		}
	}


	/**
	 * Get the value associated to the ID in the variant information.
	 * @param genomeFullName full genome name
	 * @param variant			variant for retrieving information
	 * @return					the value of the ID for specific variant and genome (if apply) or null if not found
	 */
	private Float getValue (String genomeFullName, Variant variant) {
		Object result = null;
		if (category.equals("ALT")) {
			System.out.println("NumberIDFilter getValue ALT not supported");

		} else if (category.equals("QUAL")) {
			result = variant.getPositionInformation().getQuality();

		} else if (category.equals("FILTER")) {
			System.out.println("NumberIDFilter getValue FILTER not supported");

		} else if (category.equals("INFO")) {
			result = variant.getPositionInformation().getInfoValue(ID.getId());

		} else if (category.equals("FORMAT")) {
			String rawName = FormattedMultiGenomeName.getRawName(genomeFullName);
			result = variant.getPositionInformation().getFormatValue(rawName, ID.getId());
		}

		if (result != null) {
			return Float.parseFloat(result.toString());
		}

		return null;
	}


	/**
	 * Compare to float in order to define if they correlate the inequation.
	 * @param inequation		an inequation
	 * @param referenceValue	a first value
	 * @param valueToCompare	a second value
	 * @return					true if both values correlate the inequation, false otherwise.
	 */
	private boolean isValid (InequalityOperators inequation, Float referenceValue, Float valueToCompare) {
		boolean valid = false;

		if (valueToCompare < 0) {
			valueToCompare = valueToCompare * -1;
		}

		int result = valueToCompare.compareTo(referenceValue);

		if (inequation == InequalityOperators.EQUAL) {
			if (result == 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR) {
			if (result > 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR_OR_EQUAL) {
			if (result >= 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR) {
			if (result < 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR_OR_EQUAL) {
			if (result <= 0) {
				valid = true;
			}
		}

		return valid;
	}



	@Override
	public void setCategory(String category) {
		this.category = category;
	}


	@Override
	public String getCategory () {
		return category;
	}

}
