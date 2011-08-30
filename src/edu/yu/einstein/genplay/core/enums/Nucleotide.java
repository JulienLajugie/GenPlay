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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Enumeration representing the different nucleotides (or bases) used in bioinformatics
 * @author Julien Lajugie
 * @version 0.1
 */
public enum Nucleotide {
	
	/**
	 * Thymine
	 */
	THYMINE 	((byte)0, 	'T', "Thymine", 			"T"),
	/**
	 * Uracil
	 */
	URACIL 		((byte)0, 	'U', "Uracil", 				"U"),
	/**
	 * Cytosine
	 */
	CYTOSINE 	((byte)1, 	'C', "Cytosine", 			"C"),
	/**
	 * Adenine
	 */
	ADENINE 	((byte)2, 	'A', "Adenine", 			"A"),
	/**
	 * Guanine
	 */
	GUANINE 	((byte)3, 	'G', "Guanine", 			"G"),
	/**
	 * Any Nucleotide (A or G or C or T)
	 */
	ANY 		((byte)4, 	'N', "Any Nucleotide", 		"A or G or C or T"),
	/**
	 * Purine (A or G)
	 */
	PURINE 		((byte)5, 	'R', "Purine", 				"A or G"),
	/**
	 * Pyrimidine (C or T)
	 */
	PYRIMIDINE 	((byte)6, 	'Y', "Pyrimidine", 			"C or T"),
	/**
	 * Weak Interaction (A or T)
	 */
	WEAK 		((byte)7, 	'W', "Weak Interaction", 	"A or T"),
	/**
	 * Strong Interaction (C or G)
	 */
	STRONG 		((byte)8, 	'S', "Strong Interaction", 	"C or G"),
	/**
	 * Amino (A or C)
	 */
	AMINO 		((byte)9, 	'M', "Amino", 				"A or C"),
	/**
	 * Keto (C or G)
	 */
	KETO 		((byte)10, 	'K', "Keto", 				"C or G"),
	/**
	 * Not A (C or G or T)
	 */
	NOT_A 		((byte)11,	'B', "Not A", 				"C or G or T"),
	/**
	 * Not G (A or C or T)
	 */
	NOT_G 		((byte)12, 	'H', "Not G", 				"A or C or T"),
	/**
	 * Not C (A or G or T)
	 */
	NOT_C 		((byte)13, 	'D', "Not C", 				"A or G or T"),
	/**
	 * Not T (A or C or G)
	 */
	NOT_T 		((byte)14,	'V', "Not T", 				"A or C or G"),	
	/**
	 * Gap (Gap of indeterminate length)
	 */
	GAP 		((byte)15, 	'-', "Gap", 				"Gap of indeterminate length");

	
	private final byte 								value;			// byte value of a nucleotide
	private final char 								code;			// code of a nucleotide 
	private final String 							name;			// name of a nucleotide
	private final String 							description;	// description of a nucleotide
	private Nucleotide 								complement;		// base complement
	private static final Map<Byte, Nucleotide> 		LOOKUP_VALUE = 
		new HashMap<Byte, Nucleotide>();							// map of the byte values for lookup
	private static final Map<Character, Nucleotide>	LOOKUP_CODE = 
		new HashMap<Character, Nucleotide>();						// map of the codes for lookup
	
	
	/**
	 * Private constructor. Creates an instance of {@link Nucleotide}
	 * @param value byte value of the nucleotide
	 * @param code code of the nucleotide
	 * @param name name of the nucleotide
	 * @param description description of the nucleotide
	 */
	private Nucleotide(byte value, char code, String name, String description) {
		this.value = value;
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	
	// fill the complement field with the complement base
	static {
		ADENINE.complement = THYMINE;
		GUANINE.complement = CYTOSINE;
		CYTOSINE.complement = GUANINE;
		THYMINE.complement = ADENINE;
		URACIL.complement = ADENINE;
		PURINE.complement = PYRIMIDINE;
		PYRIMIDINE.complement = PURINE;
		WEAK.complement = WEAK;
		STRONG.complement = STRONG;
		AMINO.complement = KETO;
		KETO.complement = AMINO;
		NOT_A.complement = NOT_T;
		NOT_G.complement = NOT_C;
		NOT_C.complement = NOT_G;
		NOT_T.complement = NOT_A;
		ANY.complement = ANY;
		GAP.complement = null;
	}
	
	
	// fill the maps for lookup 
	static {
		for(Nucleotide currentNucleotide : EnumSet.allOf(Nucleotide.class)) {
			// don't fill with the byte value of URACIL because it's the same value as the one for THYMINE
			if (currentNucleotide != URACIL) {
				LOOKUP_VALUE.put(currentNucleotide.value, currentNucleotide);
			}
			LOOKUP_CODE.put(currentNucleotide.code, currentNucleotide);
		}
	}

	
	/**
	 * Returns the string representation of a {@link Nucleotide} which is also its code
	 */
	@Override
	public String toString() {
		return String.valueOf(code);
	}

	/**
	 * @return the byte value of the {@link Nucleotide}
	 */
	public final byte getValue() {
		return value;
	}


	/**
	 * @return the code of the {@link Nucleotide}
	 */
	public final char getCode() {
		return code;
	}


	/**
	 * @return the name of the {@link Nucleotide}
	 */
	public final String getName() {
		return name;
	}


	/**
	 * @return the description of the {@link Nucleotide}
	 */
	public final String getDescription() {
		return description;
	}


	/**
	 * @return the complement of the {@link Nucleotide}
	 */
	public final Nucleotide getComplement() {
		return complement;
	}


	/**
	 * @param value a byte value of a nucleotide
	 * @return a {@link Nucleotide} associated to the specified value
	 */
	public static Nucleotide get(byte value) {
		return LOOKUP_VALUE.get(value);
	}


	/**
	 * @param code a code of a {@link Nucleotide}
	 * @return a {@link Nucleotide} associated to the specified code
	 */
	public static Nucleotide get(char code) {
		return LOOKUP_CODE.get(code);
	}
}
