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
package edu.yu.einstein.genplay.core.multiGenome.filter.VCFID;

import edu.yu.einstein.genplay.dataStructure.enums.ComparisonOperators;


/**
 * @author Nicolas Fourel
 */
public interface NumberIDFilterInterface extends IDFilterInterface {


	/**
	 * @return the inequation01
	 */
	public ComparisonOperators getInequation01();


	/**
	 * @return the inequation02
	 */
	public ComparisonOperators getInequation02();


	/**
	 * @return the value01
	 */
	public Float getValue01();


	/**
	 * @return the value02
	 */
	public Float getValue02();


	/**
	 * @return true if results of inequations must be cumulated, false otherwise (AND/OR operator)
	 */
	public boolean isCumulative();


	/**
	 * @param cumulative the boolean to set
	 */
	public void setCumulative (boolean cumulative);


	/**
	 * @param inequation01 the inequation01 to set
	 */
	public void setInequation01(ComparisonOperators inequation01);


	/**
	 * @param inequation02 the inequation02 to set
	 */
	public void setInequation02(ComparisonOperators inequation02);


	/**
	 * @param value01 the value01 to set
	 */
	public void setValue01(Float value01);


	/**
	 * @param value02 the value02 to set
	 */
	public void setValue02(Float value02);

}
