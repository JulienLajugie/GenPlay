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
package edu.yu.einstein.genplay.gui.dialog.trackSettings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;

/**
 * Panel with the basic options (track name, track heigth) of the track setting dialog
 * @author Julien Lajugie
 */
class BasicOptionsPanel extends JPanel {

	private static final long serialVersionUID = -2493192366088975913L; // generate serial ID

	private final JLabel 				jlName;			// label track name
	private final JTextField			jtfName;		// text field for the track name
	private final JLabel				jlHeight;		// label track height
	private final JFormattedTextField 	jftfHeight;		// track height text field


	/**
	 * Creates an instance of {@link BasicOptionsPanel}
	 */
	BasicOptionsPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Basic Options"));

		jlName = new JLabel("Name");
		jtfName = new JTextField();

		jlHeight = new JLabel("Height");
		jftfHeight = new JFormattedTextField(createHeightFormatter());

		// Format components
		TrackSettingsPanel.formatLabel(jlName);
		TrackSettingsPanel.formatLabel(jlHeight);
		TrackSettingsPanel.formatTextField(jtfName);
		TrackSettingsPanel.formatNumberField(jftfHeight);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.5;
		add(jlName, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.9;
		add(jtfName, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		add(jlHeight, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.9;
		add(jftfHeight, c);
	}


	/**
	 * @return a formatter for the height formated text field
	 */
	private AbstractFormatter createHeightFormatter() {
		NumberFormat heightFormat = NumberFormat.getIntegerInstance();
		heightFormat.setMaximumFractionDigits(0);
		heightFormat.setMaximumIntegerDigits(4);
		heightFormat.setMinimumIntegerDigits(1);
		NumberFormatter heightFormatter = new NumberFormatter(heightFormat);
		heightFormatter.setMinimum(TrackConstants.MINIMUM_HEIGHT);
		heightFormatter.setMaximum(TrackConstants.MAXIMUM_HEIGHT);
		return heightFormatter;
	}


	/**
	 * @return the height of the track
	 */
	int getTrackHeight() {
		return ((Number) jftfHeight.getValue()).intValue();
	}


	/**
	 * @return the name of the track
	 */
	String getTrackName() {
		return jtfName.getText();
	}


	/**
	 * Initializes the values of the components of this panel
	 * @param track
	 */
	void initValues(Track track) {
		jtfName.setText(track.getName());
		jftfHeight.setValue(track.getHeight());
	}
}
