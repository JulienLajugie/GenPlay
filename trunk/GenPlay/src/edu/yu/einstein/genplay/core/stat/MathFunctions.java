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
package edu.yu.einstein.genplay.core.stat;

import java.io.Serializable;

/**
 * Poisson
 * This class gather some methods to calculate mathematical functions.
 * These methods are developed for IslandFinder class.
 * @author Nicolas Fourel
 */
public class MathFunctions implements Serializable {

	private static final long serialVersionUID = 8391340544698147680L;

	/**
	 * This method find the n first digits of a value
	 * 
	 * @param value	value containing digits (or not)
	 * @param n		number of digits wanted
	 * @return		the n first digits
	 */
	public static Double getDigits (double value, int n) {
		Double digits = Math.round(value * Math.pow(10, n)) - (Math.round(value - 0.5d) * Math.pow(10, n));
		digits /= Math.pow(10, n);
		return digits;
	}

	/**
	 * This method says if the value is an integer seeking the 10 first digits.
	 * 
	 * @param value	digital value
	 * @return		true or false
	 */
	public static boolean isInteger (double value) {
		if (getDigits(value, 10) == 0.0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method implements the linear interpolation.
	 * 
	 * @param	xa	x coordinate of the point A
	 * @param	ya	y coordinate of the point A
	 * @param	xb	x coordinate of the point B
	 * @param	yb	y coordinate of the point B
	 * @param	x	x coordinate of the sought point
	 * @return	y	y coordinate of the sought point
	 */
	public static double linearInterpolation (double xa, double ya, double xb, double yb, double x) {
		double y;
		y = (ya - yb) / (xa-xb);
		y *= x;
		y += ((xa * yb) - (xb * ya)) / (xa - xb);
		return y;
	}

	/**
	 * unlogValue method
	 * This method return the decimal value from a logarithm value
	 * 
	 * @param value	the logarithm value
	 * @return		the decimal value
	 */
	public static double unlogValue (double value) {
		return Math.pow(10.0, value);
	}

}
