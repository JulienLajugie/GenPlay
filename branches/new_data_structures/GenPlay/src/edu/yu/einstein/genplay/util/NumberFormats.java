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
package edu.yu.einstein.genplay.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Class defining the different number formats used in GenPlay
 * @author EriJul
 */
public class NumberFormats {


	/**
	 * @return The Number format used to display positions (except by writers)
	 */
	public static NumberFormat getPositionFormat() {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getIntegerInstance();
		df.setGroupingUsed(true);
		return df;
	}


	/**
	 * @return The {@link NumberFormat} used to display scores (except by writers)
	 */
	public static NumberFormat getScoreFormat() {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance();
		df.setDecimalSeparatorAlwaysShown(false);
		df.setGroupingUsed(true);
		df.setMaximumFractionDigits(3);
		return df;
	}


	/**
	 * @return The NumberFormat used by writers to write scores.
	 * Groupings are not used because they can interfere with the writer separator character
	 */
	public static NumberFormat getWriterScoreFormat() {
		DecimalFormat df = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.US);
		df.setDecimalSeparatorAlwaysShown(false);
		df.setGroupingUsed(false);
		df.setMaximumFractionDigits(3);
		return df;
	}
}
