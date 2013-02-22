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
package edu.yu.einstein.genplay.gui.track;

import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * This class defines the constants used by {@link Layer}
 * @author Julien Lajugie
 */
public class TrackConstants {

	/**
	 * The score of the track is drawn on the bottom of the track
	 */
	public static final int BOTTOM_SCORE_POSITION = 1;


	/**
	 * Default cursor showed on a track
	 */
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);


	/**
	 * Default graph type displayed in a graph layer
	 */
	public static final GraphType DEFAULT_GRAPH_TYPE = GraphType.BAR;


	/**
	 * Default score auto-rescaled value of a track
	 */
	public static final boolean DEFAULT_IS_SCORE_AUTO_RESCALED = true;


	/**
	 * Default maximum score displayed in a track
	 */
	public static final double DEFAULT_MAXIMUM_SCORE = 100;


	/**
	 * Default minimum score displayed in a track
	 */
	public static final double DEFAULT_MINIMUM_SCORE = 0;


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
	 * Font for the legend
	 */
	public static final Font FONT_LEGEND = new Font("Verdana", Font.PLAIN, 9);


	/**
	 * Font for the active layer of the legend
	 */
	public static final Font FONT_LEGEND_ACTIVE_LAYER = new Font("Verdana", Font.BOLD, 10);


	/**
	 * Font for the numbers on the ruler
	 */
	public static final Font FONT_RULER = new Font("Verdana", Font.PLAIN, 9);


	/**
	 * Default track font
	 */
	public static final Font FONT_DEFAULT = new Font("Arial", Font.PLAIN, 10);


	/**
	 * Number if horizontal lines displayed in the background of a track
	 */
	public static final int	HORIZONTAL_LINE_COUNT = 10;


	/**
	 * Horizontal grid of track should be visible or hidden by default
	 */
	public static final boolean IS_HORIZONTAL_GRID_VISIBLE = false;


	/**
	 * Vertical grid of track should be visible or hidden by default
	 */
	public static final boolean IS_VERTICAL_GRID_VISIBLE = true;


	/**
	 * Minimum height of a track
	 */
	public static final int MAXIMUM_HEIGHT = 9999;


	/**
	 * Maximum number if horizontal lines displayed in the background of a track
	 */
	public static final int	MAXIMUM_HORIZONTAL_LINE_COUNT = 100;


	/**
	 * Maximum number of vertical lines displayed in the background of a track
	 */
	public static final int	MAXIMUM_VERTICAL_LINE_COUNT = 100;


	/**
	 * Minimum height of a track
	 */
	public static final int MINIMUM_HEIGHT = 30;


	/**
	 * Prefix of the default track name
	 */
	public static final String NAME_PREFIX = "Track #";


	/**
	 * Default border of a track
	 */
	public static final Border REGULAR_BORDER =
			BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BLACK);


	/**
	 * Cursor show when a track is scrolled on the left
	 */
	public static final Cursor SCROLL_LEFT_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);


	/**
	 * Cursor show when a track is scrolled on the right
	 */
	public static final Cursor SCROLL_RIGHT_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR);


	/**
	 * Transparency of the stripes
	 */
	public static final int	STRIPES_TRANSPARENCY = 150;


	/**
	 * The score of the track is drawn on top of the track
	 */
	public static final int TOP_SCORE_POSITION = 0;


	/**
	 * Number of vertical lines displayed in the background of a track
	 */
	public static final int	VERTICAL_LINES_COUNT = 10;
}
