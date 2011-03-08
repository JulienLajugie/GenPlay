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
package edu.yu.einstein.genplay.gui.action.curveTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.GraphicsType;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.TrackAppearanceOptionPane;
import edu.yu.einstein.genplay.gui.track.CurveTrack;



/**
 * Opens the appearance menu 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class CTAAppearance extends TrackListAction {

	private static final long serialVersionUID = -6622367991983310373L;	// generated ID
	private static final String ACTION_NAME = "Appearance"; // action name
	private static final String DESCRIPTION = "Change the appearance of the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "CTAAppearance";


	/**
	 * Creates an instance of {@link CTAAppearance}
	 */
	public CTAAppearance() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Opens the appearance menu 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CurveTrack<?> selectedTrack = (CurveTrack<?>) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean showHorizontalLines = selectedTrack.isShowHorizontalGrid();
			int xLineCount = selectedTrack.getVerticalLineCount();
			int yLineCount = selectedTrack.getHorizontalLinesCount();
			Color trackColor = selectedTrack.getTrackColor();
			GraphicsType trackType = selectedTrack.getTypeOfGraph();
			TrackAppearanceOptionPane taop = new TrackAppearanceOptionPane(showHorizontalLines, xLineCount, yLineCount, trackColor, trackType);
			if (taop.showTrackConfiguration(getRootPane()) == TrackAppearanceOptionPane.APPROVE_OPTION) {
				if (taop.getShowHorizontalGrid() != showHorizontalLines) {
					selectedTrack.setShowHorizontalGrid(taop.getShowHorizontalGrid());
				}
				if (taop.getXLineCount() != xLineCount) {
					selectedTrack.setVerticalLineCount(taop.getXLineCount());			
				}
				if (taop.getYLineCount() != yLineCount) {
					selectedTrack.setHorizontalLinesCount(taop.getYLineCount());
				}
				if (taop.getCurvesColor() != trackColor) {
					selectedTrack.setTrackColor(taop.getCurvesColor());
				}
				if (taop.getGraphicsType() != trackType) {
					selectedTrack.setTypeOfGraph(taop.getGraphicsType());
				}
			}
		}
	}
}
