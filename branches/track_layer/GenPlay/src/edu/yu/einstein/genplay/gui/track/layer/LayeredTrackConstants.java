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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * This class defines the constants used by {@link LayeredTrack}
 * @author Julien Lajugie
 */
public class LayeredTrackConstants {

	/**
	 * Border of a track when the track is being dragged down
	 */
	public static final Border DRAG_DOWN_BORDER =
			BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.BLACK);


	/**
	 * Border of a track when the track starts being dragged
	 */
	public static final Border DRAG_START_BORDER =
			BorderFactory.createMatteBorder(2, 2, 2, 2, Colors.BLACK);


	/**
	 * Border of a track when the track is being dragged up
	 */
	public static final Border DRAG_UP_BORDER =
			BorderFactory.createMatteBorder(2, 0, 1, 0, Colors.BLACK);


	/**
	 * Name of the font
	 */
	public static final String FONT_NAME = "ARIAL";


	/**
	 * Size of the font
	 */
	public static final int FONT_SIZE = 10;


	/**
	 * Default border of a track
	 */
	public static final Border REGULAR_BORDER = 
			BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BLACK);


	/**
	 * Color of the stripes
	 */
	public static final Color STRIPES_COLOR = Colors.GREY;


	/**
	 * Transparency of the stripes
	 */
	public static final int	STRIPES_TRANSPARENCY = 150;


	/**
	 * Height of a track
	 */
	public static final int TRACK_HEIGHT = 100;


	/**
	 * Minimum height of a track
	 */
	public static final int TRACK_MINIMUM_HEIGHT = 30;


	/**
	 * Number of vertical lines to print
	 */
	public static final int	VERTICAL_LINE_COUNT = 10;
}
