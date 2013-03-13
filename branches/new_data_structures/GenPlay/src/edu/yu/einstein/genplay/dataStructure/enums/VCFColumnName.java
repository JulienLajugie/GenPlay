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
package edu.yu.einstein.genplay.dataStructure.enums;


/**
 * A type of indel for vcf
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum VCFColumnName {
	
	/**
	 * For the CHROM column name
	 */
	CHROM ("CHROM"),
	/**
	 * For the POS column name
	 */
	POS ("POS"),
	/**
	 * For the ID column name
	 */
	ID ("ID"),
	/**
	 * For the REF column name
	 */
	REF ("REF"),
	/**
	 * For the ALT column name
	 */
	ALT ("ALT"),
	/**
	 * For the QUAL column name
	 */
	QUAL ("QUAL"),
	/**
	 * For the FILTER column name
	 */
	FILTER ("FILTER"),
	/**
	 * For the INFO column name
	 */
	INFO ("INFO"),
	/**
	 * For the FORMAT column name
	 */
	FORMAT ("FORMAT");
	
	
	private final String name; // String representing the indel 
	
	
	/**
	 * Private constructor. Creates an instance of {@link VCFColumnName}
	 * @param name
	 */
	private VCFColumnName(String name) {
		this.name = name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	
	
	/**
	 * @param s the string
	 * @return the {@link VCFColumnName} associated to the string, null otherwise
	 */
	public static VCFColumnName getColumnNameFromString (String s) {
		if (s.equals(CHROM.name)) {
			return CHROM;
		} else if (s.equals(POS.name)) {
			return POS;
		} else if (s.equals(ID.name)) {
			return ID;
		} else if (s.equals(REF.name)) {
			return REF;
		} else if (s.equals(ALT.name)) {
			return ALT;
		} else if (s.equals(QUAL.name)) {
			return QUAL;
		} else if (s.equals(FILTER.name)) {
			return FILTER;
		} else if (s.equals(INFO.name)) {
			return INFO;
		} else if (s.equals(FORMAT.name)) {
			return FORMAT;
		}
		return null;
	}
	
}
