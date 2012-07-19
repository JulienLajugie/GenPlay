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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.IndelVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.ReferenceVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.SNPVariant;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
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
	public static VCFLine getVCFLine (VariantInterface variant) {
		if ((variant instanceof IndelVariant) || (variant instanceof SNPVariant)) {
			List<VCFFile> vcfFileList = ProjectManager.getInstance().getMultiGenomeProject().getVCFFiles(variant.getVariantListForDisplay().getAlleleForDisplay().getGenomeInformation().getName(), variant.getVariantListForDisplay().getType());
			int start = variant.getReferenceGenomePosition();
			List<String> results = new ArrayList<String>();
			List<VCFFile> requiredFiles = new ArrayList<VCFFile>();
			for (VCFFile vcfFile: vcfFileList) {
				List<String> resultsTmp = null;
				try {
					resultsTmp = vcfFile.getReader().query(variant.getVariantListForDisplay().getChromosome().getName(), start - 1, start);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (resultsTmp.size() > 0) {
					for (String resultTmp: resultsTmp) {
						results.add(resultTmp);
						requiredFiles.add(vcfFile);
					}
				}
			}

			VCFLine line = null;
			int size = results.size();
			switch (size) {
			case 1:
				line = new VCFLine(results.get(0), requiredFiles.get(0).getHeader());
			case 0:
				//System.err.println("MGVariantListForDisplay.getFullVariantInformation: No variant found");
				break;
			default:
				//System.err.println("MGVariantListForDisplay.getFullVariantInformation: Many variant found: " + size);
				line = getRightInformation(variant, results, requiredFiles);
				break;
			}
			return line;
		} else if (variant instanceof ReferenceVariant) {
			VCFFile file = ((ReferenceVariant) variant).getVCFFile();
			Chromosome chromosome = ((ReferenceVariant) variant).getChromosome();
			List<String> resultsTmp = null;
			try {
				resultsTmp = file.getReader().query(chromosome.getName(), variant.getReferenceGenomePosition() - 1, variant.getReferenceGenomePosition());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (resultsTmp.size() > 0) {
				return new VCFLine(resultsTmp.get(0), file.getHeader());
			}
		}
		return null;
	}


	private static VCFLine getRightInformation (VariantInterface variant, List<String> results, List<VCFFile> vcfFiles) {
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
	}


	/**
	 * Retrieves the QUAL field from a result line
	 * @param result	the result
	 * @return			the quality as a float or 0 is the QUAL field is not valid (eg: '.')
	 */
	private static float getQUALFromResult (String result) {
		String[] array = Utils.splitWithTab(result);
		float qual = 0;
		try {
			qual = Float.parseFloat(array[5].toString());
		} catch (Exception e) {}
		return qual;
	}

}
