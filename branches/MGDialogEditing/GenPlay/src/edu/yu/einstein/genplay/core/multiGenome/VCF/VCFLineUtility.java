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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.util.Map;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;

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
	public static String getValue (Map<String, Object> line, VCFHeaderType header, String genomeRawName) {
		VCFColumnName columnName = header.getColumnCategory();
		String fieldLine = line.get(columnName.toString()).toString();

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
	public static String getInfoValue (Map<String, Object> line, VCFHeaderType header) {
		String info = line.get(VCFColumnName.INFO.toString()).toString();
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
	public static String getFormatValue (Map<String, Object> line, VCFHeaderType header, String genomeRawName) {
		String[] format = line.get(VCFColumnName.FORMAT.toString()).toString().split(":");
		String result = null;

		int idIndex = -1;
		for (int i = 0; i < format.length; i++) {
			if (format[i].equals(header.getId())) {
				idIndex = i;
			}
		}

		if (idIndex != -1) {
			String[] genomeFormat = line.get(genomeRawName).toString().split(":");
			if (idIndex < genomeFormat.length) {
				result = genomeFormat[idIndex];
			}
		}

		return result;
	}
}
