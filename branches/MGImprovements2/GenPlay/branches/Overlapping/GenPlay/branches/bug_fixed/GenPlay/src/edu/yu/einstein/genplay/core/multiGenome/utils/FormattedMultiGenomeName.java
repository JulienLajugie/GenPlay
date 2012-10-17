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


/**
 * This class manages the genome name display.
 * It is composed of:
 * - genome group name
 * - genome usual name
 * - genome raw name
 * This class can generate the formatted genome name or extract names from it.
 * 
 * A raw name is the name of the genome given in a VCF file.
 * A usual name is associated to a raw name in order to make it more understandable.
 * A group name gather raw name in a same group (will be used for operation).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FormattedMultiGenomeName {

	/** Meta Genome name for display */
	public static final String META_GENOME_NAME = "Current Meta Genome";
	/** Name of the reference genome */
	public static			String REFERENCE_GENOME_NAME = null;

	private static final 	String separator1 = " - ";	// First separator
	private static final 	String separator2 = " (";	// Second separator
	private static final 	String separator3 = ")";	// Third separator
	private static 			String elements[];			// Strings for the full name (group, genome, raw name)


	/**
	 * Concatenates names in order to make a formatted genome name.
	 * @param groupName		the genome group name
	 * @param usualName		the genome usual name
	 * @param rawName		the genome raw name
	 * @return				the formatted genome name
	 */
	public static String getFullFormattedGenomeName (String groupName, String usualName, String rawName) {
		setElements(groupName, usualName, rawName);
		return elements[0] + separator1 + elements[1] + separator2 + elements[2] + separator3;
	}


	/**
	 * Sets elements in a string array waiting for process.
	 * @param groupName		the genome group name
	 * @param usualName		the genome usual name
	 * @param rawName		the genome raw name
	 */
	private static void setElements (String groupName, String usualName, String rawName) {
		elements = new String[3];
		elements[0] = groupName;
		elements[1] = usualName;
		elements[2] = rawName;
	}


	/**
	 * Sets elements in a string array waiting for process.
	 * @param fullName	the formatted genome name
	 */
	private static void setElements (String fullName) {
		elements = new String[3];
		int separator1Index = fullName.indexOf(separator1);
		int separator2Index = fullName.indexOf(separator2);
		int separator3Index = fullName.indexOf(separator3);
		elements[0] = fullName.substring(0, separator1Index);
		elements[1] = fullName.substring((separator1Index + separator1.length()), separator2Index);
		elements[2] = fullName.substring((separator2Index + separator2.length()), separator3Index);
	}


	/**
	 * @param fullName	the formatted genome name
	 * @return			the genome group name, null if it is not a valid genome name
	 */
	public static String getGroupName (String fullName) {
		if (isValidGenomeName(fullName)) {
			setElements(fullName);
			return elements[0];
		}
		return null;
	}


	/**
	 * @param fullName	the formatted genome name
	 * @return			the genome usual name, null if it is not a valid genome name
	 */
	public static String getUsualName (String fullName) {
		if (isValidGenomeName(fullName)) {
			setElements(fullName);
			return elements[1];
		}
		return null;
	}


	/**
	 * @param fullName	the formatted genome name
	 * @return			the genome raw name, null if it is not a valid genome name
	 */
	public static String getRawName (String fullName) {
		if (isValidGenomeName(fullName)) {
			setElements(fullName);
			return elements[2];
		}
		return null;
	}


	/**
	 * @param fullName the full genome name
	 * @return true if it is not the name of the meta/reference genome
	 */
	public static boolean isValidGenomeName (String fullName) {
		if (fullName == null) {
			return false;
		}

		if (fullName.equals(META_GENOME_NAME)) {
			return false;
		}

		if ((REFERENCE_GENOME_NAME != null) && fullName.equals(REFERENCE_GENOME_NAME)) {
			return false;
		}

		return true;
	}

}
