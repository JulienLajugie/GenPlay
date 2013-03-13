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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;


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
	private static final 	String separator4 = " - ";	// Fourth separator
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
		elements = new String[4];
		int separator1Index = fullName.indexOf(separator1);
		int separator2Index = fullName.indexOf(separator2);
		int separator3Index = fullName.indexOf(separator3);
		int separator4Index = fullName.indexOf(separator4, separator3Index);
		try {
			elements[0] = fullName.substring(0, separator1Index);
		} catch (Exception e) {}
		elements[0] = fullName.substring(0, separator1Index);
		elements[1] = fullName.substring((separator1Index + separator1.length()), separator2Index);
		elements[2] = fullName.substring((separator2Index + separator2.length()), separator3Index);
		elements[3] = null;
		if (separator4Index != -1) {
			elements[3] = fullName.substring((separator4Index + separator4.length()));
		}
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
	 * @param fullNameWithAllele	the formatted genome name containing the allele
	 * @return			the allele type, null if it is not a valid genome name
	 */
	public static AlleleType getAlleleName (String fullNameWithAllele) {
		if (isValidGenomeName(fullNameWithAllele)) {
			setElements(fullNameWithAllele);
			if (elements[3] != null) {
				return AlleleType.getAlleleType(elements[3]);
			}
		}
		return null;
	}


	/**
	 * @param genomeFullName 	a full genome name
	 * @param allele			an {@link AlleleType}
	 * @return	the formatted full genome name with allele
	 */
	public static String getFullNameWithAllele (String genomeFullName, AlleleType allele) {
		if (isValidGenomeName(genomeFullName)) {
			return genomeFullName + separator4 + allele.toString();
		}
		return genomeFullName;
	}


	/**
	 * @param genomeFullName 	a full genome name with allele
	 * @return	the formatted full genome name without the allele
	 */
	public static String getFullNameWithoutAllele (String genomeFullName) {
		if (isValidGenomeName(genomeFullName)) {
			setElements(genomeFullName);
			return elements[0] + separator1 + elements[1] + separator2 + elements[2] + separator3;
		}
		return genomeFullName;
	}


	/**
	 * @param fullName the full genome name
	 * @return true if it is not the name of the meta/reference genome
	 */
	public static boolean isValidGenomeName (String fullName) {
		if (fullName == null) {
			return false;
		}

		if (fullName.isEmpty()) {
			return false;
		}

		if (isMetaGenome(fullName)) {
			return false;
		}

		if (isReferenceGenome(fullName)) {
			return false;
		}

		return true;
	}


	/**
	 * @param genomeName a genome name
	 * @return	true if the genome name represents the meta genome, false otherwise
	 */
	public static boolean isMetaGenome (String genomeName) {
		if (genomeName == null) {
			return false;
		}
		return (genomeName.equals(FormattedMultiGenomeName.META_GENOME_NAME) || genomeName.equals(CoordinateSystemType.METAGENOME.toString()));
	}


	/**
	 * @param genomeName a genome name
	 * @return	true if the genome name represents the reference genome, false otherwise
	 */
	public static boolean isReferenceGenome (String genomeName) {
		boolean result = false;
		if (genomeName == null) {
			return result;
		}

		Assembly assembly = ProjectManager.getInstance().getAssembly();
		if (assembly != null) {
			result = genomeName.equals(ProjectManager.getInstance().getAssembly().getDisplayName());
		}
		if (!result) {
			result = (genomeName.equals(FormattedMultiGenomeName.REFERENCE_GENOME_NAME) || genomeName.equals(CoordinateSystemType.REFERENCE.toString()));
		}
		return result;
	}


	/**
	 * @param name01 first genome name
	 * @param name02 second genome name
	 * @return	true if the genome name represents the reference genome, false otherwise
	 */
	public static boolean isSameGenome (String name01, String name02) {
		boolean result = name01.equals(name02);
		if (!result) {
			if (isMetaGenome(name01) && isMetaGenome(name02)) {
				result = true;
			} else if (isReferenceGenome(name01) && isReferenceGenome(name02)) {
				result = true;
			}
		}
		return result;
	}
}
