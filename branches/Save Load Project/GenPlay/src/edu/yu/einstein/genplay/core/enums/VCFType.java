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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.enums;


/**
 * A type of vcf
 * @author Nicolas Fourel
 * @version 0.1
 */
public enum VCFType {
	
	/**
	 * For indels
	 */
	INDELS ("Indels"),
	/**
	 * For SNPs
	 */
	SNPS ("SNPs"),
	/**
	 * For structural variation
	 */
	SV ("SV");

	
	private final String name; // String representing the indel 
	
	
	/**
	 * Private constructor. Creates an instance of {@link VCFType}
	 * @param name
	 */
	private VCFType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	public static VCFType getTypeFromString (String s) {
		if (s.equals(INDELS.name)) {
			return INDELS;
		} else if (s.equals(SNPS.name)) {
			return SNPS;
		} else if (s.equals(SV.name)) {
			return SV;
		} else {
			return null;
		}
	}
}
