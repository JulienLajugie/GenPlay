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
package yu.einstein.gdp2.gui.action.scoredTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.TwoNumbersOptionPane;
import yu.einstein.gdp2.gui.track.ScoredTrack;


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
			Number[] minMax = TwoNumbersOptionPane.getValue(getRootPane(), "Y Axis", "Minimum:", "Maximum:", new DecimalFormat("#.###"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, currentMin, currentMax);			
			if ((minMax != null) && (minMax[0] != null) && (minMax[1] != null)) {				
				Double newMin = minMax[0].doubleValue();
				Double newMax = minMax[1].doubleValue();
				// case where the minimum is greater than the maximum
				if (newMin >= newMax) {
					JOptionPane.showMessageDialog(getRootPane(), "The minimum value must be smaller than the maximum", "Error", JOptionPane.ERROR_MESSAGE);
					this.actionPerformed(arg0);
				} else {
					if (newMin != currentMin) {
						selectedTrack.setYMin(newMin);
					}
					if (minMax[1].doubleValue() != currentMax) {
						selectedTrack.setYMax(newMax);
					}
				}					
			}
		}
	}
}
