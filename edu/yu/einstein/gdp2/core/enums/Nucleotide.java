/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Enumeration representing the different nucleotides (or bases) used in bioinformatics
 * @author Julien Lajugie
 * @version 0.1
 */
public enum Nucleotide {
	
	THYMINE 	((byte)0, 	'T', "Thymine", 			"T"),
	URACIL 		((byte)0, 	'U', "Uracil", 				"U"),
	CYTOSINE 	((byte)1, 	'C', "Cytosine", 			"C"),
	ADENINE 	((byte)2, 	'A', "Adenine", 			"A"),
	GUANINE 	((byte)3, 	'G', "Guanine", 			"G"),
	ANY 		((byte)4, 	'N', "Any Nucleotide", 		"A or G or C or T"),
	PURINE 		((byte)5, 	'R', "Purine", 				"A or G"),
	PYRIMIDINE 	((byte)6, 	'Y', "Pyrimidine", 			"C or T"),
	WEAK 		((byte)7, 	'W', "Weak Interaction", 	"A or T"),
	STRONG 		((byte)8, 	'S', "Strong Interaction", 	"C or G"),
	AMINO 		((byte)9, 	'M', "Amino", 				"A or C"),
	KETO 		((byte)10, 	'K', "Keto", 				"C or G"),
	NOT_A 		((byte)11,	'B', "Not A", 				"C or G or T"),
	NOT_G 		((byte)12, 	'H', "Not G", 				"A or C or T"),
	NOT_C 		((byte)13, 	'D', "Not C", 				"A or G or T"),
	NOT_T 		((byte)14,	'V', "Not T", 				"A or C or G"),	
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
