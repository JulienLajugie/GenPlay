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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.advancedEditors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.advancedFilters.TrackMaskFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackMaskEditor implements AdvancedEditor {

	private JPanel			panel;
	private JLabel 			jlDescription;		// Label for the description
	private JComboBox		jcTracks;			// Combo box to select a track


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Creates the label
		jlDescription = new JLabel("Select the mask:");

		// Creates the radio boxes
		jcTracks = new JComboBox(MainFrame.getInstance().getTrackList().getTrackList());

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(jlDescription, gbc);

		// Track box
		gbc.gridy = 1;
		gbc.weighty = 1;
		panel.add(jcTracks, gbc);

		return panel;
	}


	@Override
	public FilterInterface getFilter() {
		TrackMaskFilter filter = new TrackMaskFilter();
		Track<?> track = (Track<?>) jcTracks.getSelectedItem();
		filter.setTrack(track);
		return filter;
	}


	@Override
	public void initializesPanel(FilterInterface filter) {
		if (filter instanceof TrackMaskFilter) {
			jcTracks.setSelectedItem(((TrackMaskFilter) filter).getTrack());
		}
	}


	@Override
	public String getErrors() {
		String errors = "";
		return errors;
	}


}
