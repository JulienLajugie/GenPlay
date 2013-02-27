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
package edu.yu.einstein.genplay.core.enums;


/**
 * A type of indel for vcf
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum AlleleType {

	/**
	 * For a paternal allele
	 */
	ALLELE01 ("Maternal"),

	/**
	 * For a maternal allele
	 */
	ALLELE02 ("Paternal"),

	/**
	 * To mention both allele, paternal and maternal
	 */
	BOTH ("Both");


	private final String name; // String representing the indel


	/**
	 * Private constructor. Creates an instance of {@link AlleleType}
	 * @param name
	 */
	private AlleleType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}


	/**
	 * @param s	a string
	 * @return	the associated {@link AlleleType}
	 */
	public static AlleleType getAlleleType (String s) {
		if ((s != null) && !s.isEmpty()) {
			if (ALLELE01.toString().equals(s)) {
				return ALLELE01;
			} else if (ALLELE02.toString().equals(s)) {
				return ALLELE02;
			} else if (BOTH.toString().equals(s)) {
				return BOTH;
			}
		}
		return null;
	}


	/**
	 * The index 0 will return the first allele, 1 will return the second allele...
	 * @param alleleIndex the index of an allele
	 * @return the {@link AlleleType} of the index
	 */
	public static AlleleType getAlleleType (int alleleIndex) {
		if (alleleIndex == 0) {
			return ALLELE01;
		} else if (alleleIndex == 1) {
			return ALLELE02;
		}
		return null;
	}

}
