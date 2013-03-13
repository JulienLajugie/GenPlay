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
package edu.yu.einstein.genplay.core.multiGenome.utils;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.core.multiGenome.operation.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLineUtility {


	/**
	 * Looks in a line for an ID in order to return its value
	 * @param line	part of a VCF line
	 * @param ID	ID name
	 * @return		the value of the ID
	 */
	public static Object getIDValue (String line, String ID) {
		int indexID;
		try {
			indexID = line.indexOf(ID);
		} catch (Exception e) {
			System.out.println("line: " + line + "; ID: " + ID);
		}
		indexID = line.indexOf(ID);
		//int indexID = line.indexOf(ID);
		int indexValue = indexID + ID.length() + 1;
		int indexEnd = line.indexOf(";", indexValue);
		if (indexEnd == -1) {
			indexEnd = line.length();
		}
		return line.substring(indexValue, indexEnd);
	}


	/**
	 * Retrieves a String value within a string.
	 * According to the column, the value can be the full line associated to the current column, or part of it.
	 * @param line			the VCF line (as a map String/Object)
	 * @param header		the header that contains the ID to look for
	 * @param genomeRawName	the line associated to a genome ONLY FOR FORMAT FILTER (can be null otherwise)
	 * @return				the string value of the ID, null otherwise
	 */
	public static String getValue (VCFLine line, VCFHeaderType header, String genomeRawName) {
		VCFColumnName columnName = header.getColumnCategory();
		String fieldLine = line.getValueFromColumn(columnName);

		String result = null;
		if (columnName == VCFColumnName.ALT) {						// Columns ALT, QUAL, FILTER are not composed of different ID
			result = fieldLine;										// the value to get is necessary the full line!
		} else if (columnName == VCFColumnName.QUAL) {
			result = fieldLine;
		} else if (columnName == VCFColumnName.FILTER) {
			result = fieldLine;
		} else if (columnName == VCFColumnName.INFO) {				// Columns INFO and FORMAT gather different ID (; or : delimited)
			result = getInfoValue(line, header);					// a more complex process is used to locate and retrieve the ID value
		} else if (columnName == VCFColumnName.FORMAT) {
			result = getFormatValue(line, header, genomeRawName);	// a more complex process is used to locate and retrieve the ID value
		}

		return result;
	}


	/**
	 * Gets the value according to the INFO field and a specific field
	 * @param line		the VCF line
	 * @param header	the header (containing the ID field)
	 * @return		the value of the specific field of the INFO field
	 */
	public static String getInfoValue (VCFLine line, VCFHeaderType header) {
		String info = line.getINFO();
		return getInfoValue(info, header);
	}


	/**
	 * Gets the value according to the INFO field and a specific field
	 * @param info		the info part of the line
	 * @param header	the header (containing the ID field)
	 * @return		the value of the specific field of the INFO field
	 */
	public static String getInfoValue (String info, VCFHeaderType header) {
		String result = null;

		int indexStart = info.indexOf(header.getId());
		if (indexStart != -1) {
			indexStart += header.getId().length() + 1;
			int indexStop = info.indexOf(";", indexStart);
			if (indexStop == -1) {
				indexStop = info.length();
			}
			result = info.substring(indexStart, indexStop);
		}

		return result;
	}


	/**
	 * Gets the value according to the FORMAT field and a specific field
	 * @param line			the VCF line
	 * @param header		the header (containing the ID field)
	 * @param genomeRawName the genome raw name
	 * @return		the value of the specific field of the FORMAT field
	 */
	public static String getFormatValue (VCFLine line, VCFHeaderType header, String genomeRawName) {
		return getFormatValue(line.getFORMAT(), line.getFormatValues(genomeRawName), header);
	}


	/**
	 * Gets the value according to the FORMAT field and a specific field
	 * @param lineFormat	the FORMAT line
	 * @param genomeFormat	the format field of the related genome
	 * @param header		the header
	 * @return				the value of the field, null otherwise
	 */
	public static String getFormatValue (String lineFormat, String genomeFormat, VCFHeaderType header) {
		String[] genomeFormats = Utils.split(genomeFormat, ':');
		return getFormatValue(lineFormat, genomeFormats, header);
	}


	/**
	 * Gets the value according to the FORMAT field and a specific field
	 * @param lineFormat	the FORMAT line
	 * @param genomeFormat	the parsed format field of the related genome
	 * @param header		the header
	 * @return				the value of the field, null otherwise
	 */
	public static String getFormatValue (String lineFormat, String[] genomeFormat, VCFHeaderType header) {
		String[] format = Utils.split(lineFormat, ':');
		int idIndex = -1;
		for (int i = 0; i < format.length; i++) {
			if (format[i].equals(header.getId())) {
				idIndex = i;
			}
		}

		String result = null;

		if (idIndex != -1) {
			if (idIndex < genomeFormat.length) {
				result = genomeFormat[idIndex];
			}
		}

		return result;
	}


	/**
	 * @param variant a variant
	 * @return the VCF line of the variant
	 */
	public static VCFLine getVCFLine (Variant variant) {
		return null;
	}


	/*private static VCFLine getRightInformation (Variant variant, List<String> results, List<VCFFile> vcfFiles) {
		if (results.size() > 0) {
			float variantScore = variant.getScore();
			for (int i = 0; i < results.size(); i++) {
				String result = results.get(i);
				float currentScore = getQUALFromResult(result);
				if (variantScore == currentScore) {
					return new VCFLine(result, vcfFiles.get(i).getHeader());
				}
			}
		}
		return null;
	}*/


	/**
	 * Retrieves the QUAL field from a result line
	 * @param result	the result
	 * @return			the quality as a float or 0 is the QUAL field is not valid (eg: '.')
	 */
	/*private static float getQUALFromResult (String result) {
		String[] array = Utils.splitWithTab(result);
		float qual = 0;
		try {
			qual = Float.parseFloat(array[5].toString());
		} catch (Exception e) {}
		return qual;
	}*/


	/**
	 * Transforms a character into its allele index.
	 * The char 1 will refer to the first alternative located at the index 0 of any arrays.
	 * The char 0 returns -1 and the char '.' returns -2 and don't refer to any alternatives.
	 * @param alleleChar the character
	 * @return the associated code (char - 1)
	 */
	public static int getAlleleIndex (char alleleChar) {
		return getAlleleIndex(alleleChar + "");
	}


	/**
	 * Transforms a character into its allele index.
	 * The char 1 will refer to the first alternative located at the index 0 of any arrays.
	 * The char 0 returns -1 and the char '.' returns -2 and don't refer to any alternatives.
	 * @param alleleChar the character
	 * @return the associated code (char - 1)
	 */
	public static int getAlleleIndex (String alleleChar) {
		int alleleIndex = -1;
		if (alleleChar.equals(".")) {
			alleleIndex = MGSynchronizer.NO_CALL;
		} else if (alleleChar.equals("0")) {
			alleleIndex = MGSynchronizer.REFERENCE;
		} else {
			try {
				alleleIndex = Integer.parseInt(alleleChar) - 1;
			} catch (Exception e) {}
		}
		return alleleIndex;
	}


	/**
	 * Retrieves the length of all defined alternatives
	 * If an alternative is SV coded, the info field is required
	 * @param reference		the REF field
	 * @param alternatives	the parsed ALT field
	 * @param info			the INFO field
	 * @return				an array of integer as lengths
	 */
	public static int[] getVariantLengths(String reference, String[] alternatives, String info) {
		int[] lengths = new int[alternatives.length];

		for (int i = 0; i < alternatives.length; i++) {
			lengths[i] = retrieveVariantLength(reference, alternatives[i], info);
		}

		return lengths;
	}


	/**
	 * Defines the variant type according to several lengths
	 * @param length 	array of length
	 * @return			an array of variant types
	 */
	public static VariantType[] getVariantTypes (int[] length) {
		VariantType[] variantTypes = new VariantType[length.length];

		for (int i = 0; i < length.length; i++) {
			variantTypes[i] = getVariantType(length[i]);
		}

		return variantTypes;
	}


	/**
	 * Retrieves the length of a variation using the reference and the alternative.
	 * If the alternative is a structural variant, the length is given by the SVLEN INFO attributes
	 * @param reference		REF field
	 * @param alternative	ALT field
	 * @param info			INFO field
	 * @return	the length of the variation
	 */
	public static int retrieveVariantLength (String reference, String alternative, String info) {
		int length = 0;

		if (isStructuralVariant(alternative)) {
			String lengthPattern = "SVLEN=";
			int lengthPatternIndex = info.indexOf(lengthPattern) + lengthPattern.length();
			int nextCommaIndex = info.indexOf(";", lengthPatternIndex);
			if (nextCommaIndex == -1) {
				length = Integer.parseInt(info.substring(lengthPatternIndex));
			} else {
				length = Integer.parseInt(info.substring(lengthPatternIndex, nextCommaIndex));
			}
		} else {
			length = alternative.length() - reference.length();
		}

		return length;
	}


	/**
	 * Tests the length of a variation to find its type out.
	 * @param variationLength 	length of the variation
	 * @return					the variation type {@link VariantType}
	 */
	public static VariantType getVariantType (int variationLength) {
		if (variationLength < 0) {
			return VariantType.DELETION;
		} else if (variationLength > 0) {
			return VariantType.INSERTION;
		} else if (variationLength == 0) {
			return VariantType.SNPS;
		} else {
			return null;
		}
	}


	/**
	 * @param alternative ALT field (or part of it)
	 * @return true if the given alternative is coded as an SV
	 */
	public static boolean isStructuralVariant (String alternative) {
		if (alternative.charAt(0) == '<') {
			return true;
		}
		return false;
	}
}
