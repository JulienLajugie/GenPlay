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
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.gui.track.layer.background.BackgroundData;

/**
 * Panel of the track setting dialog with the settings related to the axis
 * @author Julien Lajugie
 */
class AxisOptionsPanel extends JPanel {

	private static final long serialVersionUID = 9184425941274260205L; // generated serial ID

	private final JLabel				jlHorizontalLines;			// label horizontal lines
	private final JCheckBox				jcbHorizontalLines;			// check box horizontal lines
	private final JLabel				jlHorizontalLineCount;		// label horizontal line count
	private final JFormattedTextField 	jftfHorizontalLineCount;	// textField horizontal line count
	private final JLabel				jlVerticalLines;			// label vertical lines
	private final JCheckBox				jcbVerticalLines;			// check box vertical lines
	private final JLabel				jlVerticalLineCount;		// label vertical line count
	private final JFormattedTextField 	jftfVerticalLineCount;		// textField vertical line count


	/**
	 * Creates an instance of {@link AxisOptionsPanel}
	 */
	AxisOptionsPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Axis Options"));

		// horizontal lines
		jlHorizontalLines = new JLabel("Show horizontal lines");
		jcbHorizontalLines = new JCheckBox();
		jlHorizontalLineCount = new JLabel("Horizontal line count");
		jftfHorizontalLineCount = new JFormattedTextField(createLineCountFormatter(TrackConstants.MAXIMUM_HORIZONTAL_LINE_COUNT));

		// vertical lines
		jlVerticalLines = new JLabel("Show vertical lines");
		jcbVerticalLines = new JCheckBox();
		jlVerticalLineCount = new JLabel("Vertical line count");
		jftfVerticalLineCount = new JFormattedTextField(createLineCountFormatter(TrackConstants.MAXIMUM_VERTICAL_LINE_COUNT));

		// Format components
		TrackSettingsPanel.formatLabel(jlHorizontalLines);
		TrackSettingsPanel.formatLabel(jlHorizontalLineCount);
		TrackSettingsPanel.formatLabel(jlVerticalLines);
		TrackSettingsPanel.formatLabel(jlVerticalLineCount);
		TrackSettingsPanel.formatNumberField(jftfHorizontalLineCount);
		TrackSettingsPanel.formatNumberField(jftfVerticalLineCount);


		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.5;
		add(jlHorizontalLines, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.9;
		add(jcbHorizontalLines, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		add(jlHorizontalLineCount, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.9;
		add(jftfHorizontalLineCount, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.1;
		add(jlVerticalLines, c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.9;
		add(jcbVerticalLines, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.1;
		add(jlVerticalLineCount, c);

		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0.9;
		add(jftfVerticalLineCount, c);
	}


	/**
	 * @return true if the horizontal lines are set as visible
	 */
	boolean areHorizontalLinesVisibe() {
		return jcbHorizontalLines.isSelected();
	}


	/**
	 * @return true if the vertical lines are set as visible
	 */
	boolean areVerticalLinesVisibe() {
		return jcbVerticalLines.isSelected();
	}


	/**
	 * @param maximumValue maximum value allowed in the text fields
	 * @return a formatter for the line count formated text fields
	 */
	private AbstractFormatter createLineCountFormatter(int maximumValue) {
		NumberFormat lineCountFormat = NumberFormat.getIntegerInstance();
		lineCountFormat.setMaximumFractionDigits(0);
		lineCountFormat.setMaximumIntegerDigits(3);
		lineCountFormat.setMinimumIntegerDigits(1);
		NumberFormatter lineCountFormatter = new NumberFormatter(lineCountFormat);
		lineCountFormatter.setMinimum(0);
		lineCountFormatter.setMaximum(maximumValue);
		return lineCountFormatter;
	}


	/**
	 * @return the number of horizontal lines to show on the track
	 */
	int getHorizontalLineCout() {
		return ((Number) jftfHorizontalLineCount.getValue()).intValue();
	}


	/**
	 * @return the number of vertical lines to show on the track
	 */
	int getVerticalLineCout() {
		return ((Number) jftfVerticalLineCount.getValue()).intValue();
	}


	/**
	 * Initializes the values of the components of this panel
	 * @param track
	 */
	void initValues(Track track) {
		BackgroundData backgroundData = track.getBackgroundLayer().getData();
		jcbHorizontalLines.setSelected(backgroundData.isHorizontalGridVisible());
		jftfHorizontalLineCount.setValue(backgroundData.getHorizontalLineCount());
		jcbVerticalLines.setSelected(backgroundData.isVerticalGridVisible());
		jftfVerticalLineCount.setValue(backgroundData.getVerticalLineCount());
	}
}
