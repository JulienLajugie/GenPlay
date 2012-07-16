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

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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
	private JLabel 			jlTrack;		// Label for the description
	private JLabel 			jlMethod;		// Label for the description
	private JComboBox		jcTracks;		// Combo box to select a track
	private JRadioButton	jrVariation;	// Radio box for strict variation method
	private JRadioButton	jrOverlap;		// Radio box for overlap method


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Creates the labels
		jlTrack = new JLabel("Select the mask:");
		jlMethod = new JLabel("Select mask method:");

		// Creates the track selection box
		jcTracks = new JComboBox(MainFrame.getInstance().getTrackList().getTrackList());

		// Creates the radio boxes
		jrVariation = new JRadioButton("Variation");
		jrVariation.setToolTipText("The VCF line is used to compare variation.");
		jrOverlap = new JRadioButton("Overlap");
		jrOverlap.setToolTipText("Only start and stop information are used and can overlap to pass the filter.");

		// Creates the group
		ButtonGroup group = new ButtonGroup();
		group.add(jrVariation);
		group.add(jrOverlap);

		// Default setting
		jrVariation.setSelected(true);

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
		panel.add(jlTrack, gbc);

		// Track box
		gbc.gridy++;
		panel.add(jcTracks, gbc);

		// Label
		gbc.gridy++;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(jlMethod, gbc);

		// "Exact variation" button
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		panel.add(jrVariation, gbc);

		// "Overlap" button
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(jrOverlap, gbc);

		return panel;
	}


	@Override
	public FilterInterface getFilter() {
		TrackMaskFilter filter = new TrackMaskFilter();
		Track<?> track = (Track<?>) jcTracks.getSelectedItem();
		filter.setTrack(track);
		if (jrVariation.isSelected()) {
			filter.setStrictComparisonMethod();
		} else {
			filter.setOverlapComparisonMethod();
		}
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
