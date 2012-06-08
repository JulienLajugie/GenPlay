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
package edu.yu.einstein.genplay.gui.action.emptyTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.track.MultiCurvesTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackChooser.MultiTrackChooser;


/**
 * Shows the content of multiple tracks in a new one
 * @author Julien Lajugie
 * @version 0.1
 */
public class ETAGenerateMultiCurvesTrack extends TrackListAction {

	private static final long serialVersionUID = -7961676275489605138L; // generated ID
	private static final String ACTION_NAME = "Generate Multi Curves Track"; // action name
	private static final String DESCRIPTION = "Show the content of multiple fixed or vaiable window tracks in the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETAGenerateMultiCurvesTrack";


	/**
	 * Creates an instance of {@link ETAGenerateMultiCurvesTrack}
	 */
	public ETAGenerateMultiCurvesTrack() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Shows the content of multiple tracks in a new one
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Track<?>[] selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getCurveTracks());
		if (selectedTracks != null) {
			if (selectedTracks.length > 1) {
				CurveTrack<?>[] curveTracks = new CurveTrack[selectedTracks.length];
				String trackName = "";
				for (int i = 0; i < curveTracks.length; i++) {
					curveTracks[i] = (CurveTrack<?>) selectedTracks[i];
					if (i != curveTracks.length - 1) {
						trackName += selectedTracks[i].getName() + ", ";
					} else {
						trackName += selectedTracks[i].getName();
					}
				}
				int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
				ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
				MultiCurvesTrack newTrack = new MultiCurvesTrack(selectedTrackIndex + 1, curveTracks);
				getTrackList().setTrack(selectedTrackIndex, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), trackName, stripes, getTrackList().getSelectedTrack().getStripesList(), getTrackList().getSelectedTrack().getFiltersList());	
			} else {
				JOptionPane.showMessageDialog(getRootPane(), "You must select at least two tracks", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
