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
public enum VariantType {

	// From indel type VCF file
	/**
	 * For an insertion
	 */
	INSERTION ("Insertion"),
	/**
	 * For a deletion
	 */
	DELETION ("Deletion"),


	// From SNPs type VCF file
	/**
	 * For SNPs
	 */
	SNPS ("SNPs"),


	// From SV type VCF file
	/*
	 * For a SV deletion
	 */
	//DEL ("Deletion (SV)"),
	/*
	 * For a SV insertion
	 */
	//INS ("Insertion (SV)"),
	/**
	 * For a SV duplication
	 */
	DUP ("Duplication (SV)"),
	/**
	 * For a SV inversion
	 */
	INV ("Inversion (SV)"),
	/**
	 * For a SV copy number variation
	 */
	CNV ("Copy Number Variation (SV)"),


	// For display
	/**
	 * To display the reference insertion
	 */
	REFERENCE_INSERTION ("Reference insertion"),
	/**
	 * To display the reference deletion
	 */
	REFERENCE_DELETION ("Reference deletion"),
	/**
	 * To display the reference SNP
	 */
	REFERENCE_SNP ("Reference SNP"),
	/**
	 * To display the reference SNP
	 */
	REFERENCE_NO_CALL ("Reference no call"),
	/**
	 * To display the no call
	 */
	NO_CALL ("No Call"),
	/**
	 * When type are mixed during the fitting screen data process
	 */
	MIX ("Mix");


	private final String name; // String representing the indel


	/**
	 * Private constructor. Creates an instance of {@link VariantType}
	 * @param name
	 */
	private VariantType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
