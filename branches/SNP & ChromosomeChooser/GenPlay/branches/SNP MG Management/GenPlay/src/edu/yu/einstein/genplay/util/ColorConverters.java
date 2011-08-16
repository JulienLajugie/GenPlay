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
package edu.yu.einstein.genplay.util;

import java.awt.Color;

import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.enums.Strand;



/**
 * Class containing only static method that associate a color to specified parameters
 * @author Julien Lajugie
 * @version 0.1
 */
public class ColorConverters {

	
	/**
	 * Associates a color to a {@link Nucleotide}
	 * @param nucleotide {@link Nucleotide}
	 * @return a {@link Color}
	 */
	public static Color nucleotideToColor(Nucleotide nucleotide) {
		switch (nucleotide) {
		case ADENINE:
			return new Color(200, 0, 0);
		case CYTOSINE:
			return new Color(0, 0, 200);
		case GUANINE:
			return new Color(255, 200, 0);
		case THYMINE: 
			return new Color(0, 200, 0);
		case ANY:
			return Color.black;
		default:
			return null;
		}
	}


	/**
	 * Associates a color to a gene depending on the strand of the gene and if the gene is highlighted
	 * @param strand a {@link Strand}
	 * @param isHighlighted true if the gene is highlighted
	 * @return a {@link Color}
	 */
	public static Color geneToColor(Strand strand, boolean isHighlighted) {
		if (strand == Strand.FIVE) {
			if (isHighlighted) {
				return new Color(255, 0, 0);
			} else {
				return new Color(180, 0, 0);
			}
		} else {
			if (isHighlighted) {
				return new Color(0, 100, 255);
			} else {
				return new Color(0, 0, 200);
			}			
		}		
	}


	/**
	 * Returns a color associated to a score. 
	 * High intensities are red. Medium are green. Low are blue.
	 * @param score A score indexed between min and max.
	 * @param min minimum intensity value
	 * @param max maximum intensity value
	 * @return A color
	 */
	public static Color scoreToColor(double score, double min, double max) {
		// set the score to min if the score is smaller than min
		score = Math.max(min, score);
		// set the score to max if the score is greater than max
		score = Math.min(max, score);
		double distance = max - min;
		double newScore = score - min;
		double distanceQuarter = distance / 4;
		int r = 0;
		int v = 0;
		int b = 0;

		if ((newScore >= 0) && (newScore <= distanceQuarter)) {
			r = 0;
			v = (int)(newScore * 255 / distanceQuarter);
			b = 255;			
		} else if ((newScore > distanceQuarter) && (newScore <= 2 * distanceQuarter)) {
			r = 0;
			v = 255;
			b = (int)(255 - (newScore - distanceQuarter) * 255 / distanceQuarter);			
		} else if ((newScore > 2 * distanceQuarter) && (newScore <= 3 * distanceQuarter)) {
			r = (int)((newScore - 2 * distanceQuarter) * 255 / distanceQuarter);
			v = 255;
			b = 0;			
		} else if ((newScore > 3 * distanceQuarter) && (newScore <= distance)) {
			r = 255;
			v = (int)(255 - (newScore - 3 * distanceQuarter) * 255 / distanceQuarter);
			b = 0;			
		}		
		return new Color(r, v, b);
	}
}
