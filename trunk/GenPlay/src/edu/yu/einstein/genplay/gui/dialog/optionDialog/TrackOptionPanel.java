/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;

/**
 * Panel of the {@link OptionDialog} that allows to configure the tracks *
 * @author Julien Lajugie
 * @version 0.1
 */
final class TrackOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 1941311091566384114L; 	// generated ID
	private static final int MIN_TRACK_COUNT = 1; 						// minimum number of tracks
	private static final int MAX_TRACK_COUNT = 1024; 					// maximum number of tracks
	private static final int MIN_TRACK_HEIGHT = 30; 					// minimum height of the tracks
	private static final int MAX_TRACK_HEIGHT = 2000; 					// maximum height of the tracks
	private final JLabel 				jlTrackCount; 		// label track count
	private final JFormattedTextField 	jftfTrackCount; 	// text field track count
	private final JLabel 				jlTrackHeight; 		// label track height
	private final JFormattedTextField 	jftfTrackHeight;	// text field track count
	private final JLabel 				jlUndoCount; 		// label undo count
	private final JFormattedTextField 	jftfUndoCount; 		// label undo count
	private final JLabel 				jlResetTrack;	 	// label reset track
	private final JCheckBox				jcResetTrack;	 	// checkbox reset track
	private final JLabel 				jlCacheTrack;	 	// label cache track
	private final JCheckBox				jcCacheTrack;	 	// checkbox cache track
	private final JLabel 				jlLegend;	 		// label legend (multi-genome)
	private final JCheckBox				jcLegend;	 		// checkbox legend (multi-genome)


	/**
	 * Creates an instance of {@link TrackOptionPanel}
	 */
	TrackOptionPanel() {
		super("Track Option");

		jlTrackCount = new JLabel("Number of Tracks:");

		jftfTrackCount = new JFormattedTextField(NumberFormat.getInstance());
		((NumberFormatter) jftfTrackCount.getFormatter()).setMinimum(MIN_TRACK_COUNT);
		((NumberFormatter) jftfTrackCount.getFormatter()).setMaximum(MAX_TRACK_COUNT);
		jftfTrackCount.setColumns(5);
		jftfTrackCount.setValue(configurationManager.getTrackCount());
		jftfTrackCount.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setTrackCount(((Number) jftfTrackCount.getValue()).intValue());
			}
		});

		jlTrackHeight = new JLabel("Default Track Height:");

		jftfTrackHeight = new JFormattedTextField(NumberFormat.getInstance());
		((NumberFormatter) jftfTrackHeight.getFormatter()).setMinimum(MIN_TRACK_HEIGHT);
		((NumberFormatter) jftfTrackHeight.getFormatter()).setMaximum(MAX_TRACK_HEIGHT);
		jftfTrackHeight.setColumns(5);
		jftfTrackHeight.setValue(configurationManager.getTrackHeight());
		jftfTrackHeight.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setTrackHeight(((Number) jftfTrackHeight
						.getValue()).intValue());
			}
		});

		jlUndoCount = new JLabel("Undo Count:");
		jftfUndoCount = new JFormattedTextField(NumberFormat.getInstance());
		((NumberFormatter) jftfUndoCount.getFormatter()).setMinimum(0);
		((NumberFormatter) jftfUndoCount.getFormatter()).setMaximum(Integer.MAX_VALUE);
		jftfUndoCount.setColumns(5);
		jftfUndoCount.setValue(configurationManager.getUndoCount());
		jftfUndoCount.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setUndoCount(((Number) jftfUndoCount.getValue()).intValue());
			}
		});

		jlResetTrack = new JLabel("Enable reset:");
		jcResetTrack = new JCheckBox();
		jcResetTrack.setSelected(ConfigurationManager.getInstance().isResetTrack());
		jcResetTrack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				configurationManager.setResetTrack(jcResetTrack.isSelected());
			}
		});

		jlCacheTrack = new JLabel("Enable cache:");
		jcCacheTrack = new JCheckBox();
		jcCacheTrack.setSelected(ConfigurationManager.getInstance().isCacheTrack());
		jcCacheTrack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				configurationManager.setCacheTrack(jcCacheTrack.isSelected());
			}
		});

		jlLegend = new JLabel("Show Legend:");
		jcLegend = new JCheckBox();
		jcLegend.setSelected(ConfigurationManager.getInstance().isLegend());
		jcLegend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				configurationManager.setLegend(jcLegend.isSelected());
			}
		});


		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlTrackCount, c);

		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfTrackCount, c);

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlTrackHeight, c);

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfTrackHeight, c);

		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlUndoCount, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfUndoCount, c);

		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlResetTrack, c);

		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jcResetTrack, c);

		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlCacheTrack, c);

		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jcCacheTrack, c);

		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlLegend, c);

		c.gridx = 1;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jcLegend, c);
	}
}
