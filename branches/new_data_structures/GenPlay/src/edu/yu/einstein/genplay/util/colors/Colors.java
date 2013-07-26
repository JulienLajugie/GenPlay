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
package edu.yu.einstein.genplay.util.colors;

import java.awt.Color;
import java.util.Random;

import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;

/**
 * Set of colors for GenPlay.
 * How it works:
 * - Every color has three variations: Regular, Light, Dark
 * - They are named: COLOR, LIGHT_COLOR, DARK_COLOR
 * - A name in the description correspond to the name of the color the current color is based on.
 * - The same color is used for the same group of three color, unless it is specified (must be specified for the three of them)
 * - The mentioned number is the value of brigthness, if it does not appear, the default one is used
 * 
 * List of colors this class is inspired of:
 * http://en.wikipedia.org/wiki/List_of_colors
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Colors {


	//////////////////////////////////////////////// Regular colors
	// Cyan / Magenta / Pink


	/** Blue color (Bleu de France) */
	public static final Color BLUE = new Color(0x318ce8);

	/** Light blue color (100) */
	public static final Color LIGHT_BLUE = new Color(0x369aff);

	/** Dark blue color (60) */
	public static final Color DARK_BLUE = new Color(0x205d99);



	/** Purple color (Palatinate purple) (50) */
	public static final Color PURPLE = new Color(0x803076);

	/** Light purple color (75) */
	public static final Color LIGHT_PURPLE = new Color(0xbf49b1);

	/** Dark purple color (35) */
	public static final Color DARK_PURPLE = new Color(0x592253);



	/** Red color (Cadmium red) */
	public static final Color RED = new Color(0xe80023);

	/** Light red color (100) */
	public static final Color LIGHT_RED = new Color(0xff0026);

	/** Dark red color (60) */
	public static final Color DARK_RED = new Color(0x990017);



	/** Orange color (Tangerine) */
	public static final Color ORANGE = new Color(0xF28500);

	/** Light orange color (Orange peel) */
	public static final Color LIGHT_ORANGE = new Color(0xff9d00);

	/** Dark orange color (Tangerine) (80) */
	public static final Color DARK_ORANGE = new Color(0xcc7000);



	/** Green color (Green (NCS)) */
	public static final Color GREEN = new Color(0x009F6B);

	/** Light green color (Persian green) */
	public static final Color LIGHT_GREEN = new Color(0x00A693);

	/** Dark green color (Cadmium green) (35) */
	public static final Color DARK_GREEN = new Color(0x005933);



	/** Yellow color (Cadmium yellow) (95) */
	public static final Color YELLOW = new Color(0xf2ea00);

	/** Light yellow color (Unmellow Yellow) */
	public static final Color LIGHT_YELLOW = new Color(0xFFFF66);

	/** Dark yellow color (Golden yellow) (90) */
	public static final Color DARK_YELLOW = new Color(0xe5c700);



	/** Grey color (Cadet grey) */
	public static final Color GREY = new Color(0x91A3B0);

	/** Light grey color (Cadet grey) (80) */
	public static final Color LIGHT_GREY = new Color(0xa7bdcc);

	/** Dark grey color (Davy's grey) */
	public static final Color DARK_GREY = new Color(0x555555);



	/** Black color */
	public static final Color BLACK = Color.black;

	/** White color */
	public static final Color WHITE = Color.white;



	//////////////////////////////////////////////// Nucleotide colors

	/** Color of the Adenine */
	public static final Color ADENINE = new Color(200, 0, 0);

	/** Color of the Cytosine */
	public static final Color CYTOSINE = new Color(0, 0, 200);

	/** Color of the Guanine */
	public static final Color GUANINE = new Color(255, 200, 0);

	/** Color of the Thymine */
	public static final Color THYMINE = new Color(0, 200, 0);

	/** Color of the Any */
	public static final Color ANY  = Colors.BLACK;

	/** Color of the Blank */
	public static final Color BLANK = Colors.GREY;

	/** Transparency of a rolled-over nucleotide */
	public static final int ROLLED_OVER_NUCLEOTIDE_TRANSPARENCY = 100;

	//////////////////////////////////////////////// Track colors

	/** Color of the vertical and horizontal lines of the track graphics */
	public static final Color TRACK_LINE = Colors.LIGHT_GREY;

	/** Color of the line in the middle of the track graphics */
	public static final Color TRACK_MIDDLE_LINE = Colors.RED;

	/** Default color of the score */
	public static final Color TRACK_SCORE = Colors.RED;

	/** Background color of the graphics part */
	public static final Color TRACK_BACKGROUND = Colors.WHITE;

	/** Color of the track name */
	public static final Color TRACK_NAME = Colors.BLUE;

	/** Color of the stripes */
	public static final Color STRIPES_COLOR = Colors.GREY;



	//////////////////////////////////////////////// Track handle colors

	/** Background color for the track handles */
	public static final Color 	TRACK_HANDLE_BACKGROUND = new Color(228, 236, 247);

	/** Rollover color for the track handles */
	public static final Color 	TRACK_HANDLE_ROLLOVER = new Color(187, 196, 209);

	/** Selected color for the track handles */
	public static final Color 	TRACK_HANDLE_SELECTED = new Color(157, 193, 228);


	/**
	 * @param color a color
	 * @param transparency alpha value in the range (0 - 255)
	 * @return a new color with the RGB values of the specified color and the alpha value of the specified transparency
	 */
	public static Color addTransparency(Color color, int transparency) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), transparency);
	}


	/**
	 * @return a color randomly generated
	 */
	public static Color generateRandomColor() {
		Random randomGen = new Random();
		int red = randomGen.nextInt(255);
		int green = randomGen.nextInt(255);
		int blue = randomGen.nextInt(255);
		if ((red + green + blue) > 510) {
			// we want dark colors
			return generateRandomColor();
		} else {
			return new Color(red, green, blue);
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
	 * @param color a {@link Color}
	 * @return a new color object that is the reversed of the specified one
	 */
	public static Color getReversedColor(Color color) {
		Color reverseColor;
		if (!color.equals(Color.BLACK)) {
			reverseColor = new Color(color.getRGB() ^ 0xffffff);
		} else {
			reverseColor = Colors.GREY;
		}
		reverseColor = new Color(reverseColor.getRed(), reverseColor.getGreen(), reverseColor.getBlue(), color.getAlpha());
		return reverseColor;
	}


	/**
	 * Associates a color to a {@link Nucleotide}
	 * @param nucleotide {@link Nucleotide}
	 * @return a {@link Color}
	 */
	public static Color nucleotideToColor(Nucleotide nucleotide) {
		switch (nucleotide) {
		case ADENINE:
			return Colors.ADENINE;
		case CYTOSINE:
			return Colors.CYTOSINE;
		case GUANINE:
			return Colors.GUANINE;
		case THYMINE:
			return Colors.THYMINE;
		case ANY:
			return Colors.ANY;
		case BLANK:
			return Colors.BLANK;
		default:
			return null;
		}
	}


	/**
	 * @param color a {@link Color}
	 * @return a new color identical to the specified one but with no alpha component
	 */
	public static Color removeTransparency(Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
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
			v = (int)((newScore * 255) / distanceQuarter);
			b = 255;
		} else if ((newScore > distanceQuarter) && (newScore <= (2 * distanceQuarter))) {
			r = 0;
			v = 255;
			b = (int)(255 - (((newScore - distanceQuarter) * 255) / distanceQuarter));
		} else if ((newScore > (2 * distanceQuarter)) && (newScore <= (3 * distanceQuarter))) {
			r = (int)(((newScore - (2 * distanceQuarter)) * 255) / distanceQuarter);
			v = 255;
			b = 0;
		} else if ((newScore > (3 * distanceQuarter)) && (newScore <= distance)) {
			r = 255;
			v = (int)(255 - (((newScore - (3 * distanceQuarter)) * 255) / distanceQuarter));
			b = 0;
		}
		return new Color(r, v, b);
	}


	/**
	 * Transforms a color used to display a variant stripe.
	 * Must be called when the mouse is over the stripe!
	 * @param color	the native color
	 * @return		a new color
	 */
	public static Color stripeFilter (Color color) {
		int stripesOpacity = MGDisplaySettings.getInstance().getVariousSettings().getColorOpacity();
		int invert = 255 - stripesOpacity;
		if (color == null) {
			color = Colors.BLACK;
		}
		Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), invert);
		return newColor;
	}
}
