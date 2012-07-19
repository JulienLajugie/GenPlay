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
package edu.yu.einstein.genplay.core.multiGenome.filter.utils;

import java.util.List;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.AltFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.FilterFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.FlagIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.GenotypeIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.QualFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.StringIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.StringIDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.VCFLineUtility;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class FilterUtility {


	/**
	 * The method to use to test a VCF line with a specific filter
	 * @param filter	the filter
	 * @param line		the VCF line (map String/Object)
	 * @return			true if the line verifies the filter, false otherwise
	 */
	public boolean isValid(IDFilterInterface filter, VCFLine line) {

		if (filter.getColumnName() == VCFColumnName.FORMAT) {
			List<String> genomeNames = filter.getGenomeNames();
			String[] results = new String[genomeNames.size()];

			for (int i = 0; i < genomeNames.size(); i++) {
				String rawName = FormattedMultiGenomeName.getRawName(genomeNames.get(i));
				results[i] = VCFLineUtility.getValue(line, filter.getHeaderType(), rawName);
			}

			if (results.length > 1) {
				FormatFilterOperatorType operator = filter.getOperator();

				if (operator == FormatFilterOperatorType.AND) {
					return passANDTest(filter, results);
				} else if (operator == FormatFilterOperatorType.OR) {
					return passORTest(filter, results);
				} else if (operator == FormatFilterOperatorType.SUM) {
					return passSUMTest(filter, results);
				} else if (operator == FormatFilterOperatorType.MEAN) {
					return passMEANTest(filter, results);
				}

				System.err.println("StringUtility.isValid() no operator found");
			} else {
				return passTest(filter, results[0]);
			}

			return false;
		}

		String value = line.getValueFromColumn(filter.getColumnName());
		return passTest(filter, value);
	}


	/**
	 * Algorithm for AND operator
	 * @param filter	the filter
	 * @param results	the array of results
	 * @return			true if the results verify the filter according to the filter
	 */
	private boolean passANDTest (IDFilterInterface filter, String[] results) {
		for (int i = 0; i < results.length; i++) {
			if (!passTest(filter, results[i])) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Algorithm for OR operator
	 * @param filter	the filter
	 * @param results	the array of results
	 * @return			true if the results verify the filter according to the filter
	 */
	private boolean passORTest (IDFilterInterface filter, String[] results) {
		for (int i = 0; i < results.length; i++) {
			if (passTest(filter, results[i])) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Algorithm for SUM operator
	 * @param filter	the filter
	 * @param results	the array of results
	 * @return			true if the results verify the filter according to the filter
	 */
	private boolean passSUMTest (IDFilterInterface filter, String[] results) {
		Float sum = (float) 0.0;
		for (int i = 0; i < results.length; i++) {
			Float f = toFloat(results[i]);
			if (f != null) {
				sum += f;
			}
		}
		return passTest(filter, sum.toString());
	}


	/**
	 * Algorithm for MEAN operator
	 * @param filter	the filter
	 * @param results	the array of results
	 * @return			true if the results verify the filter according to the filter
	 */
	private boolean passMEANTest (IDFilterInterface filter, String[] results) {
		Float mean = (float) 0.0;
		for (int i = 0; i < results.length; i++) {
			Float f = toFloat(results[i]);
			if (f != null) {
				mean += f;
			}
		}
		mean /= results.length;
		return passTest(filter, mean.toString());
	}


	/**
	 * Parses a string to a float
	 * @param s	the string
	 * @return	the float, null otherwise
	 */
	protected Float toFloat (String s) {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * Creates a description of the filter
	 * @param filter 	the filter
	 * @return			the description as a String
	 */
	public String toStringForDisplay (IDFilterInterface filter) {
		String text = "";

		if ((filter instanceof AltFilter) || (filter instanceof FilterFilter) || (filter instanceof StringIDFilter)) {
			StringIDFilterInterface current = (StringIDFilterInterface) filter;
			text += "Must ";
			if (current.isRequired()) {
				text += "contains ";
			} else {
				text += "not contains ";
			}
			text += current.getValue();

		} else if ((filter instanceof QualFilter) || (filter instanceof NumberIDFilter)) {
			NumberIDFilterInterface current = (NumberIDFilterInterface) filter;
			text += "x " + current.getInequation01() + " " + current.getValue01();
			if ((current.getInequation02() != null) && (current.getValue02() != null)) {
				if (current.isCumulative()) {
					text += " AND ";
				} else {
					text += " OR ";
				}
				text += "x " + current.getInequation02() + " " + current.getValue02();
			}

		} else if (filter instanceof FlagIDFilter) {
			FlagIDFilter current = (FlagIDFilter) filter;
			text += "Must be ";
			if (current.isRequired()) {
				text += "present";
			} else {
				text += "absent";
			}

		} else if (filter instanceof GenotypeIDFilter) {
			GenotypeIDFilter current = (GenotypeIDFilter) filter;
			text = "Must be ";
			if (current.getOption() == GenotypeIDFilter.HETEROZYGOTE_OPTION) {
				text += "heterozygote";
			} else if (current.getOption() == GenotypeIDFilter.HOMOZYGOTE_OPTION) {
				text += "homozygote";
			}

			text += " (";
			if (current.canBePhased() && current.canBeUnPhased()) {
				text += "phased & unphased";
			} else if (current.canBePhased() && !current.canBeUnPhased()) {
				text += "phased only";
			} else if (!current.canBePhased() && current.canBeUnPhased()) {
				text += "unphased only";
			}
			text += ")";

		}

		if (!text.isEmpty()) {
			List<String> genomeNames = filter.getGenomeNames();
			FormatFilterOperatorType operator = filter.getOperator();

			if (genomeNames != null) {
				if (genomeNames.size() == 1) {
					text += " for " + genomeNames.get(0);
				} else if (genomeNames.size() > 1) {
					text += " - " + operator.toString().toUpperCase() + " of ";
					for (int i = 0; i < genomeNames.size(); i++) {
						text += genomeNames.get(i);
						if (i < (genomeNames.size() - 1)) {
							text += ", ";
						}
					}
				}
			}
			text += ".";
		}

		return text;
	}


	/**
	 * @param filter 	the filter to use
	 * @param value		the value to test
	 * @return			true if the value passes the test, false otherwise
	 */
	protected abstract boolean passTest (IDFilterInterface filter, String value);


	/**
	 * Tests if both object are equals
	 * @param filter	the filter
	 * @param obj		the other filter
	 * @return			true if both filter are equals
	 */
	public abstract boolean equals(IDFilterInterface filter, Object obj);


	/**
	 * Looks for the error within the filter.
	 * Each error is separated with a new line.
	 * @param filter	the filter
	 * @return			the error(s), an empty string otherwise
	 */
	public abstract String getErrors(IDFilterInterface filter);

}
