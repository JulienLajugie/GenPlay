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
package edu.yu.einstein.genplay.core.IO.utils;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.Strand;

/**
 * This class contains methods that verifies the content of a line in order to determine if the line is correct or not.
 * Methods are separated in three different parts:
 * - multiple controls: these methods are public and are supposed to be used from the other classes. They control a bench of parameters.
 * - single control: these methods are private and are used by the multiple controls methods. They control as few parameter as possible.
 * - utils methods: help the development.
 * 
 * Multiple controls methods return a string containing errors.
 * That string is empty if no error has been found.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DataLineValidator {


	///////////////////////////////////////////////////// Methods for multiple controls
	
	/**
	 * @param chromosome	the chromosome
	 * @param start			the start position
	 * @param stop			the stop position
	 * @return				the errors
	 */
	public static String getErrors (Chromosome chromosome, int start, int stop){
		String errors = "";

		errors = addError(errors, getErrors(chromosome, start));
		//errors = addError(errors, getErrors(chromosome, stop));
		errors = addError(errors, getErrors(start, stop));

		return errors;
	}


	/**
	 * @param chromosome	the chromosome
	 * @param start			the start position
	 * @param stop			the stop position
	 * @param score			the score
	 * @return				the errors
	 */
	public static String getErrors (Chromosome chromosome, int start, int stop, Double score) {
		String errors = "";

		errors = addError(errors, getErrors(chromosome, start, stop));
		errors = addError(errors, getErrors(score));

		return errors;
	}


	/**
	 * @param chromosome	the chromosome
	 * @param start			the start position
	 * @param stop			the stop position
	 * @param score			the score
	 * @param name 			the name
	 * @param strand 		the strand
	 * @return				the errors
	 */
	public static String getErrors (Chromosome chromosome, int start, int stop, Double score, String name, Strand strand) {
		String errors = "";

		errors = addError(errors, getErrors(chromosome, start, stop, score));
		errors = addError(errors, getErrors(name));
		errors = addError(errors, getErrors(strand));

		return errors;
	}


	/**
	 * @param chromosome	the chromosome
	 * @param start			the start position
	 * @param stop			the stop position
	 * @param exonStart 	list of the start position of exons
	 * @param exonStop 		list of the stop position of exons
	 * @return				the errors
	 */
	public static String getErrors (Chromosome chromosome, int start, int stop, int[] exonStart, int[] exonStop){
		String errors = "";

		errors = addError(errors, getErrors(chromosome, start, stop));
		errors = addError(errors, getErrors(chromosome, exonStart));
		errors = addError(errors, getErrors(chromosome, exonStop));

		return errors;
	}
	
	
	/**
	 * @param chromosome	the chromosome
	 * @param start			the start position
	 * @param stop			the stop position
	 * @param exonStart 	list of the start position of exons
	 * @param exonStop 		list of the stop position of exons
	 * @param name 			the name
	 * @param strand 		the strand
	 * @return				the errors
	 */
	public static String getErrors (Chromosome chromosome, int start, int stop, int[] exonStart, int[] exonStop, String name, Strand strand){
		String errors = "";

		errors = addError(errors, getErrors(chromosome, start, stop, exonStart, exonStop));
		errors = addError(errors, getErrors(name));
		errors = addError(errors, getErrors(strand));

		return errors;
	}

	/////////////////////////////////////////////////////



	///////////////////////////////////////////////////// Methods for single control

	/**
	 * @param name
	 * @return the error
	 */
	private static String getErrors (String name) {
		String errors = "";

		if (name == null ) {
			errors += "The name is invalid (null value found)";
		} else if (name.isEmpty()) {
			errors += "The name is empty";
		}

		return errors;
	}


	/**
	 * @param score
	 * @return the error
	 */
	private static String getErrors (Double score) {
		String errors = "";

		if (score == null) {
			errors += "The score is invalid (null value found)";
		}/* else {
			if (score == 0) {
				errors += "Score (" + score + ") is equal to zero";
			}
		}*/

		return errors;
	}


	/**
	 * @param strand
	 * @return the error
	 */
	private static String getErrors (Strand strand) {
		String errors = "";

		if (strand == null ) {
			errors += "The strand is invalid (null value found)";
		}

		return errors;
	}


	/**
	 * @param start
	 * @param stop
	 * @return the error
	 */
	private static String getErrors (int start, int stop) {
		String errors = "";

		if (start > stop) {
			errors += "Start position (" + start + ") higher than the stop position (" + stop + ")";
		}

		return errors;
	}


	/**
	 * @param chromosome
	 * @param position
	 * @return the error
	 */
	public static String getErrors (Chromosome chromosome, int position){
		String errors = "";

		if (position < 0) {
			errors += "Position (" + position + ") lower than 0";
		} else if (position > chromosome.getLength()) {
			errors += "Position (" + position + ") higher than the chromosome (" + chromosome.getName() + ") length (" + chromosome.getLength() + ")";
		}

		return errors;
	}


	/**
	 * @param chromosome
	 * @param positions
	 * @return the error
	 */
	private static String getErrors (Chromosome chromosome, int[] positions){
		String errors = "";

		for (int i = 0; i < positions.length; i++) {
			errors = addError(errors, getErrors(chromosome, positions[i]));
		}

		return errors;
	}

	/////////////////////////////////////////////////////



	///////////////////////////////////////////////////// Utils

	/**
	 * Adds an error to other errors and return the full error message.
	 * The aim of this method is to add easily a message without taking care of:
	 * - checking the content of the error (no insertion if empty)
	 * - adding a new line
	 * 
	 * Therefore, multiple controls methods code is simplified.
	 */
	private static String addError (String errors, String error) {
		if (error.length() > 0) {
			if (errors.length() > 0) {
				errors += "\n";
			}
			errors += error;
		}
		return errors;
	}

	/////////////////////////////////////////////////////

}
