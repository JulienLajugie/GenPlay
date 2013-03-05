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
package edu.yu.einstein.genplay.core.SAM;

import java.util.List;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecord.SAMTagAndValue;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMSegment {


	/**
	 * @return true if a {@link SAMRecord} has been set, false otherwise
	 */
	private static boolean recordCheck (SAMRecord record) {
		if (record == null) {
			System.err.println("Invalid use of the SAMRecord class methods: no SAMRecord set.");
			return false;
		}
		return true;
	}


	/**
	 * TEST
	 * @param record a {@link SAMRecord}
	 */
	public static void doSomething (SAMRecord record) {
		if (recordCheck(record)) {
			System.out.println("SAMSegment.doSomething()");
		}
	}



	/**
	 * Print the {@link SAMRecord} information
	 * @param record a {@link SAMRecord}
	 */
	public static void printRecord (SAMRecord record) {
		if (recordCheck(record)) {
			System.out.println(getTextDescription(record));
		}
	}

	/**
	 * @param record a {@link SAMRecord}
	 * @return the description of the {@link SAMRecord}, null otherwise
	 */
	public static String getTextDescription (SAMRecord record) {
		return getDescription(record, "\n");
	}


	/**
	 * @param record a {@link SAMRecord}
	 * @return the description of the {@link SAMRecord}, null otherwise
	 */
	public static String getHTMLDescription (SAMRecord record) {
		String info = "";
		info += "<html>";
		info += getDescription(record, "<br />");
		info += "</html>";
		return info;
	}


	/**
	 * @param record a {@link SAMRecord}
	 * @return the description of the {@link SAMRecord}, null otherwise
	 */
	private static String getDescription (SAMRecord record, String balise) {
		if (recordCheck(record)) {
			String info = "";

			info += "Read:" + balise;
			info += "Name: " + getString(record.getReadName()) + balise;
			info += "Aligment Start: " + getString("" + record.getAlignmentStart()) + balise;
			info += "Aligment End: " + getString("" + record.getAlignmentEnd()) + balise;
			info += "Length: " + getString("" + record.getReadLength()) + balise;
			info += "Strand: " + getStrand(record.getReadNegativeStrandFlag()) + balise;
			info += "Mapped: " + !record.getReadUnmappedFlag() + balise;
			info += "Paired: " + record.getReadPairedFlag() + balise;
			info += "Base Quality String: " + getString(record.getBaseQualityString()) + balise;

			List<SAMTagAndValue> tags = record.getAttributes();
			if ((tags != null) && !tags.isEmpty()) {
				info += "Tags & Values:" + balise;
				for (SAMTagAndValue tag: tags) {
					info += tag.tag + ": " + tag.value + balise;
				}
			}

			if (record.getReadPairedFlag()) {
				info += "Pair:" + balise;
				info += "Reference Name: " + getString(record.getMateReferenceName()) + balise;
				info += "Reference Index: " + getString("" + record.getMateReferenceIndex()) + balise;
				info += "Aligment Start: " + getString("" + record.getMateAlignmentStart()) + balise;
				info += "Strand: " + getStrand(record.getMateNegativeStrandFlag()) + balise;
				info += "Mapped: " + !record.getMateUnmappedFlag() + balise;
			}

			return info;
		}
		return null;
	}


	/*private static String getDescription (SAMRecord record, String balise) {
		if (recordCheck(record)) {
			String info = "";

			info += "Read:\n";
			info += "Name: " + getString(record.getReadName()) + "\n";
			info += "Aligment Start: " + getString("" + record.getAlignmentStart()) + "\n";
			info += "Aligment End: " + getString("" + record.getAlignmentEnd()) + "\n";
			info += "Length: " + getString("" + record.getReadLength()) + "\n";
			info += "Strand: " + getStrand(record.getReadNegativeStrandFlag()) + "\n";
			info += "Mapped: " + !record.getReadUnmappedFlag() + "\n";
			info += "Paired: " + record.getReadPairedFlag() + "\n";
			info += "Base Quality String: " + getString(record.getBaseQualityString()) + "\n";

			List<SAMTagAndValue> tags = record.getAttributes();
			if ((tags != null) && !tags.isEmpty()) {
				info += "Tags & Values:\n";
				for (SAMTagAndValue tag: tags) {
					info += tag.tag + ": " + tag.value + "\n";
				}
			}

			if (record.getReadPairedFlag()) {
				info += "Pair:\n";
				info += "Reference Name: " + getString(record.getMateReferenceName()) + "\n";
				info += "Reference Index: " + getString("" + record.getMateReferenceIndex()) + "\n";
				info += "Aligment Start: " + getString("" + record.getMateAlignmentStart()) + "\n";
				info += "Strand: " + getStrand(record.getMateNegativeStrandFlag()) + "\n";
				info += "Mapped: " + !record.getMateUnmappedFlag() + "\n";
			}

			return info;
		}
		return null;
	}*/






	private static String getString (String s) {
		if ((s == null) || s.isEmpty()) {
			return " - ";
		}
		return s;
	}


	/**
	 * @param b the NegativeStrandFlag
	 * @return "reverse" if true, "forward" if false
	 */
	public static String getStrand (boolean b) {
		if (b) {
			return "reverse";
		}
		return "forward";
	}


	/**
	 * @param record a {@link SAMRecord}
	 * @return true if the {@link SAMRecord} passes all the {@link SAMFilters} options
	 */
	public static boolean passFilters (SAMRecord record) {
		boolean valid = true;
		if (recordCheck(record)) {
			if ((SAMFilters.mustBeMapped == SAMFilters.YES_OPTION) && record.getReadUnmappedFlag()) {
				valid = false;
			}
		}
		return valid;
	}

}
