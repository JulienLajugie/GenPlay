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
package edu.yu.einstein.genplay.util;

import java.awt.Color;


/**
 * This enumeration contains a single static method that returns
 * a color for a track. The color is selected amid all 
 * the elements of the enumeration {@link TrackColor}. When all 
 * the colors of the enumeration have been selected the algorithm
 * start from the first color again. 
 * @author Julien Lajugie
 * @version 0.1
 */
public enum TrackColor {

	/** Dark blue {@link Color}  */
	DARK_BLUE (new Color(0x0000CD)),
	/** Light blue {@link Color}  */
	LIGHT_GREEN(new Color(0x7FFF00)),
	/** Gray {@link Color}  */
	GRAY (new Color(0x696969)),
	/** Pink {@link Color}  */
	PINK (new Color(0xFF69B4)),
	/**  Brown {@link Color} */
	BROWN (new Color(0x8B4513)),
	/** Light cyan {@link Color}  */
	LIGHT_CYAN (new Color(0x00FFFF)),
	/**  Dark green {@link Color} */
	DARK_GREEN (new Color(0x006400)),
	/** Orange {@link Color}  */
	ORANGE (new Color(0xFF4500)),
	/** Purple {@link Color}  */
	PURPLE (new Color(0x800080)),
	/** Light blue {@link Color}  */
	LIGHT_BLUE (new Color(0xADD8E6)),
	/** Red {@link Color}  */
	RED (Color.RED),
	/** Black {@link Color}  */
	BLACK (Color.BLACK),
	/** Yellow {@link Color}  */
	YELLOW (new Color(0xFFD700)),
	/** Dark cyan {@link Color}  */
	DARK_CYAN (new Color(0x008B8B));

	private static int currentColorInt = 0; // static field that saves the next selected color index
	private final Color color; 				// color field of the TrackColorEnum


	/**
	 * Private constructor. Creates an instance of a {@link TrackColor}
	 * @param color color of the enum element
	 */
	private TrackColor(Color color){
		this.color = color;
	}


	/**
	 * @return a color for the track. The color is selected amid all 
	 * the elements of the enumeration {@link TrackColor}. When all 
	 * the colors of the enumeration have been selected the algorithm
	 * start from the first color again. 
	 */
	public static Color getTrackColor() {
		TrackColor[] trackColors = TrackColor.values();
		Color currentColor = trackColors[currentColorInt].color;
		currentColorInt++;
		// we want the value to be in the array length range
		currentColorInt = currentColorInt % trackColors.length;
		return currentColor;		
	}
}

