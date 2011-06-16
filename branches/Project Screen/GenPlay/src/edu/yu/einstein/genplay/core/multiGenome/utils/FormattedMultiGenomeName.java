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
package edu.yu.einstein.genplay.core.multiGenome.utils;

/**
 * This class manages the genome name display.
 * It is composed of:
 * - genome group name
 * - genome usual name
 * - genome raw name
 * This class can generate the formatted genome name or extract information.
 * 
 * @author Nicolas
 */
public class FormattedMultiGenomeName {

	private static final String separator1 = " - ";
	private static final String separator2 = " (";
	private static final String separator3 = ")";
	private static String elements[];
	
	
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
	 * @return			the genome group name
	 */
	public static String getGroupName (String fullName) {
		setElements(fullName);
		return elements[0];
	}
	
	
	/**
	 * @param fullName	the formatted genome name
	 * @return			the genome usual name
	 */
	public static String getUsualName (String fullName) {
		setElements(fullName);
		return elements[1];
	}
	
	
	/**
	 * @param fullName	the formatted genome name
	 * @return			the genome raw name
	 */
	public static String getRawName (String fullName) {
		setElements(fullName);
		return elements[2];
	}
	
	
}