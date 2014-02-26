/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.enums;


/**
 * Enumeration of the different types of comparison operators
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public enum ComparisonOperators {


	/**
	 * "Equal" operator
	 */
	EQUAL ("="),

	/**
	 * "Not equal" operator
	 */
	DIFFERENT ("!="),

	/**
	 * "Greater than or equal to" operator
	 */
	GREATER_OR_EQUAL ("\u2265"),

	/**
	 * "Greater than" operator
	 */
	GREATER (">"),

	/**
	 * "Less than or equal to" operator
	 */
	LESS_OR_EQUAL ("\u2264"),

	/**
	 * "Less than" operator
	 */
	LESS ("<");


	/**
	 * Symbol of the operator
	 */
	private final String symbol;


	/**
	 * Private constructor. Creates an instance of {@link ComparisonOperators}
	 * @param name
	 */
	private ComparisonOperators(String symbol) {
		this.symbol = symbol;
	}


	/**
	 * @param leftOperand left operand
	 * @param rightOperand right operand
	 * @return the result of the comparison between the left and the right operand
	 */
	public <T extends Comparable<T>> boolean process(T leftOperand, T rightOperand) {
		switch (this) {
		case EQUAL:
			return leftOperand.compareTo(rightOperand) == 0;
		case DIFFERENT:
			return leftOperand.compareTo(rightOperand) != 0;
		case GREATER_OR_EQUAL:
			return leftOperand.compareTo(rightOperand) >= 0;
		case GREATER:
			return leftOperand.compareTo(rightOperand) > 0;
		case LESS_OR_EQUAL:
			return leftOperand.compareTo(rightOperand) <= 0;
		case LESS:
			return leftOperand.compareTo(rightOperand) < 0;
		default:
			throw new UnsupportedOperationException();
		}
	}


	@Override
	public String toString() {
		return symbol;
	}
}
