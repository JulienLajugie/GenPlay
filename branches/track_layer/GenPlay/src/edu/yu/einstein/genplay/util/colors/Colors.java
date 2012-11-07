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



	//////////////////////////////////////////////// Track graphics colors

	/** Color of the vertical and horizontal lines of the track graphics */
	public static final Color TRACK_GRAPHICS_LINE = Colors.LIGHT_GREY;

	/** Color of the line in the middle of the track graphics */
	public static final Color TRACK_GRAPHICS_MIDDLE_LINE = Colors.RED;

	/** Background color of the graphics part */
	public static final Color TRACK_GRAPHICS_BACKGROUND = Colors.WHITE;




	//////////////////////////////////////////////// Track handle colors

	/** Background color for the track handles */
	public static final Color 	TRACK_HANDLE_BACKGROUND = new Color(228, 236, 247);

	/** Rollover color for the track handles */
	public static final Color 	TRACK_HANDLE_ROLLOVER = new Color(187, 196, 209);

	/** Selected color for the track handles */
	public static final Color 	TRACK_HANDLE_SELECTED = new Color(157, 193, 228);
}
