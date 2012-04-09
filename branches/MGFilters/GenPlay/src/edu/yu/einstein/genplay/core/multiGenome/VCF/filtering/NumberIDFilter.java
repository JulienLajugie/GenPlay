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

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NumberIDFilter implements NumberIDFilterInterface {

	private VCFHeaderType 			ID;				// ID of the filter
	private String					category;		// category of the filter (ALT QUAL FILTER INFO FORMAT)
	private InequalityOperators		inequation01;
	private InequalityOperators 	inequation02;
	private Float 					value01;
	private Float 					value02;
	private boolean					cumulative;


	@Override
	public VCFHeaderType getID() {
		return ID;
	}


	@Override
	public void setID(VCFHeaderType id) {
		this.ID = id;
	}


	/**
	 * @return the inequation01
	 */
	public InequalityOperators getInequation01() {
		return inequation01;
	}


	/**
	 * @param inequation01 the inequation01 to set
	 */
	public void setInequation01(InequalityOperators inequation01) {
		this.inequation01 = inequation01;
	}


	/**
	 * @return the inequation02
	 */
	public InequalityOperators getInequation02() {
		return inequation02;
	}


	/**
	 * @param inequation02 the inequation02 to set
	 */
	public void setInequation02(InequalityOperators inequation02) {
		this.inequation02 = inequation02;
	}


	/**
	 * @return the value01
	 */
	public Float getValue01() {
		return value01;
	}


	/**
	 * @param value01 the value01 to set
	 */
	public void setValue01(Float value01) {
		this.value01 = value01;
	}


	/**
	 * @return the value02
	 */
	public Float getValue02() {
		return value02;
	}


	/**
	 * @param value02 the value02 to set
	 */
	public void setValue02(Float value02) {
		this.value02 = value02;
	}


	@Override
	public String toStringForDisplay() {
		String text = "";

		text += "x " + inequation01 + " " + value01;
		if (inequation02 != null && value02 != null) {
			if (cumulative) {
				text += " AND ";
			} else {
				text += " OR ";
			}
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


	@Override
	public void setCategory(String category) {
		this.category = category;
	}


	@Override
	public String getCategory() {
		return category;
	}


	@Override
	public boolean isValid(VariantInterface variant) {
		return false;
	}


	@Override
	public boolean isValid(Object o) {
		if (o != null) {
			String fullLine = o.toString();
			Float valueToCompare = FilterTester.getFloatValue(getColumnName(), fullLine, ID.getId());
			Boolean result01 = FilterTester.passInequation(inequation01, value01, valueToCompare);
			Boolean result02 = null;
			if (inequation02 != null) {
				result02 = FilterTester.passInequation(inequation02, value02, valueToCompare);
			}

			if (cumulative) {		// cumulative treatment
				if (result02 != null) {
					return (result01 & result02);
				}
				return result01;
			} else {				// non cumulative treatment
				if (result01 || result02) {
					return true;
				}
			}
		} else {
			System.out.println("NumberIDFilter.isValid()");
			System.out.println("value == null");
		}

		return false;
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
		NumberIDFilter test = (NumberIDFilter)obj;
		return ID.getId().equals(test.getID().getId()) && 
		category.equals(test.getCategory()) &&
		inequation01.toString().equals(test.getInequation01().toString()) &&
		inequation02.toString().equals(test.getInequation02().toString()) &&
		value01 == test.getValue01() &&
		value02 == test.getValue02();
	}


	@Override
	public VCFColumnName getColumnName() {
		return VCFColumnName.getColumnNameFromString(category);
	}


	@Override
	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}


	@Override
	public boolean isCumulative() {
		return cumulative;
	}

}
