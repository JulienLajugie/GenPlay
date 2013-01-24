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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection;

import java.awt.Dimension;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.trackChooser.ExpressTrackChooserPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackSelectionPanel extends EditingPanel<Track[]> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private ExpressTrackChooserPanel trackChooserPanel;


	/**
	 * Constructor of {@link TrackSelectionPanel}
	 */
	public TrackSelectionPanel() {
		super("Track(s)");

	}


	@Override
	public String getErrors() {
		String errors = "";
		if ((getSelectedTracks() == null) || (getSelectedTracks().length == 0)) {
			errors += "Track(s) selection\n";
		}
		return errors;
	}


	/**
	 * @return the selected tracks
	 */
	public Track[] getSelectedTracks () {
		return trackChooserPanel.getSelectedTrack();
	}


	@Override
	public void initialize(Track[] element) {
		if (element != null) {
			trackChooserPanel.setSelectedTrack(element);
		}
	}


	@Override
	protected void initializeContentPanel() {
		trackChooserPanel = new ExpressTrackChooserPanel();
		contentPanel = trackChooserPanel;

		Dimension panelDimension = trackChooserPanel.getDimension();

		int width = panelDimension.width;
		int height = panelDimension.height;

		if (width <= EditingPanel.MINIMUM_WIDTH) {
			width = EditingPanel.MINIMUM_WIDTH;
		}
		if (height <= EditingPanel.CONTENT_HEIGHT) {
			height = EditingPanel.CONTENT_HEIGHT;
		}

		Dimension newDimension = new Dimension(width, height);
		trackChooserPanel.setListSize(newDimension);
	}


	@Override
	public void reset() {
		trackChooserPanel.reset();
	}


	@Override
	public void update(Object object) {}

}
