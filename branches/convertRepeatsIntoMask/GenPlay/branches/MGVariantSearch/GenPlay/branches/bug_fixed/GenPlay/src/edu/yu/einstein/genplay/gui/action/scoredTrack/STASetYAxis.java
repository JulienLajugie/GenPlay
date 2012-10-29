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
package edu.yu.einstein.genplay.gui.action.scoredTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.YAxisOptionPane;
import edu.yu.einstein.genplay.gui.track.ScoredTrack;


/**
 * Asks the user a maximum and a minimum value for the score 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class STASetYAxis extends TrackListAction {

	private static final long serialVersionUID = 2695583198943464561L; // generated ID
	private static final String ACTION_NAME = "Set Y Axis"; // action name
	private static final String DESCRIPTION = "Set the minimum and the maximum on the Y axis"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "STASetYAxis";


	/**
	 * Creates an instance of {@link STASetYAxis}
	 */
	public STASetYAxis() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Asks the user a maximum and a minimum value for the score  
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ScoredTrack<?> selectedTrack = (ScoredTrack<?>) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			double currentMin = selectedTrack.getYMin();
			double currentMax = selectedTrack.getYMax();
			boolean currentYAutoscale = selectedTrack.isYAutoscale();
			YAxisOptionPane yAxisOptionPane = new YAxisOptionPane();
			if (yAxisOptionPane.showDialog(getRootPane(), currentMin, currentMax, currentYAutoscale) == YAxisOptionPane.APPROVE_OPTION) {
				double newMin = yAxisOptionPane.getYMin();
				double newMax = yAxisOptionPane.getYMax();
				boolean newYAutoscale = yAxisOptionPane.isYAutoscale();
				// case where the minimum is greater than the maximum
				if (!newYAutoscale && (newMin >= newMax)) {
					JOptionPane.showMessageDialog(getRootPane(), "The minimum value must be smaller than the maximum one", "Error", JOptionPane.ERROR_MESSAGE);
					this.actionPerformed(arg0);
				} else {
					if (newYAutoscale != currentYAutoscale) {
						selectedTrack.setYAutoscale(newYAutoscale);
					}
					if (!selectedTrack.isYAutoscale()) {
						if (newMin != currentMin) {
							selectedTrack.setYMin(newMin);
						}
						if (newMax != currentMax) {
							selectedTrack.setYMax(newMax);
						}
					}
				}
			}
		}
	}
}
