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
package edu.yu.einstein.genplay.dataStructure.halfFloat;


/**
 * This class offers static methods to convert 32-bit floating values
 * into 16-bit floating values and conversely.
 * http://stackoverflow.com/questions/477750/primitive-type-short-casting-in-java
 * @author Julien Lajugie
 */
public class HalfFloat {

	/**
	 * Converts a float primitive (32-bit) into a half-precision
	 * floating-point format store in a char primitive (16-bit)
	 * @param fval a float value
	 * @return a char value
	 */
	public static char fromFloat(float fval) {
		int fbits = Float.floatToIntBits(fval);
		int sign = (fbits >>> 16) & 0x8000;
		// sign only
		int val = ( fbits & 0x7fffffff ) + 0x1000;
		// rounded value
		if (val >= 0x47800000) { // might be or become NaN/Inf
			// avoid Inf due to rounding
			if ((fbits & 0x7fffffff ) >= 0x47800000) {
				// is or must become NaN/Inf
				if( val < 0x7f800000 ) {
					// make it +/-Inf
					return (char) (sign | 0x7c00);
				}
				// remains +/-Inf or NaN, keep NaN (and Inf) bits
				return (char) (sign | 0x7c00 | (( fbits & 0x007fffff ) >>> 13));
			}
			// unrounded not quite Inf
			return (char) (sign | 0x7bff);
		}
		if (val >= 0x38800000) {
			return (char) (sign | ((val - 0x38000000) >>> 13)); // exp - 127 + 15
		}
		if (val < 0x33000000) {
			// becomes +/-0
			return (char) (sign);
		}
		// tmp exp for subnormal calc
		val = ( fbits & 0x7fffffff ) >>> 23;
		// add subnormal bit, round depending on cut off, div by 2^(1-(exp-127+15)) and >> 13 | exp=0
		return (char) (sign | ((((fbits & 0x7fffff) | 0x800000)	+ (0x800000 >>> (val - 102))) >>> (126 - val)));
	}


	/**
	 * Converts a half-precision floating-point format stored in a
	 * char primitive (16-bit) into a float primitive (32-bit).
	 * @param hbits a char value
	 * @return a float value
	 */
	public static float toFloat(char hbits) {
		// 10 bits mantissa
		int mant = hbits & 0x03ff;
		// 5 bits exponent
		int exp =  hbits & 0x7c00;
		// NaN/Inf
		if (exp == 0x7c00) {
			// -> NaN/Inf
			exp = 0x3fc00;
		} else if ( exp != 0 ) {	// normalized value
			// exp - 15 + 127
			exp += 0x1c000;
			/* if ((mant == 0) && (exp > 0x1c400)) {
				// smooth transition
				return Float.intBitsToFloat( (( hbits & 0x8000 ) << 16) | (exp << 13) | 0x3ff );
			} */
		}
		else if( mant != 0 ) { // && exp==0 -> subnormal
			// make it normal
			exp = 0x1c400;
			do {
				// mantissa * 2
				mant <<= 1;
				// decrease exp by 1
				exp -= 0x400;
			} while (( mant & 0x400 ) == 0); // while not normal
			// discard subnormal bit
			mant &= 0x3ff;
		} // else +/-0 -> +/-0
		// combine all parts: sign  << ( 31 - 15 ), value << ( 23 - 10 )
		return Float.intBitsToFloat((( hbits & 0x8000 ) << 16) | (( exp | mant ) << 13));
	}
}
