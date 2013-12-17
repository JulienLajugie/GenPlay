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
package edu.yu.einstein.genplay.gui.dialog.trackSettings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.gui.track.Track;

/**
 * A dialog to set the properties of a track.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class TrackSettingsPanel extends JPanel {

	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	private static final long serialVersionUID = -5402176442100056968L; // generated ID


	protected static final int labelWidth = 100;

	private final BasicOptionsPanel 	basicOptionsPanel;			// panel for the basic options
	private final AxisOptionsPanel 		axisOptionsPanel;			// panel for the axis options
	private final ScoreOptionsPanel 	scoreOptionsPanel;			// panel for the score options


	/**
	 * Creates an instance of {@link TrackSettingsPanel}
	 */
	public TrackSettingsPanel() {
		super();

		basicOptionsPanel = new BasicOptionsPanel();
		axisOptionsPanel = new AxisOptionsPanel();
		scoreOptionsPanel = new ScoreOptionsPanel();

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(basicOptionsPanel, c);

		c.gridx = 0;
		c.gridy = 1;
		add(axisOptionsPanel, c);

		c.gridx = 0;
		c.gridy = 2;
		add(scoreOptionsPanel, c);
	}


	/**
	 * @param track		track for the settings
	 */
	public void setOptions (Track track) {
		basicOptionsPanel.initValues(track);
		axisOptionsPanel.initValues(track);
		scoreOptionsPanel.initValues(track);
	}


	/**
	 * @return true if the horizontal lines are set as visible
	 */
	public boolean areHorizontalLinesVisibe() {
		return axisOptionsPanel.areHorizontalLinesVisibe();
	}


	/**
	 * @return true if the vertical lines are set as visible
	 */
	public boolean areVerticalLinesVisibe() {
		return axisOptionsPanel.areVerticalLinesVisibe();
	}


	/**
	 * @return the number of horizontal lines to show on the track
	 */
	public int getHorizontalLineCout() {
		return axisOptionsPanel.getHorizontalLineCout();
	}


	/**
	 * @return true if the score is set to be auto-rescaled. False otherwise
	 */
	public boolean isScoreAutoRescaled() {
		return scoreOptionsPanel.isScoreAutoRescaled();
	}


	/**
	 * @return the color of the score
	 */
	public Color getScoreColor() {
		return scoreOptionsPanel.getScoreColor();
	}


	/**
	 * @return the maximum score
	 */
	public double getScoreMaximum() {
		return scoreOptionsPanel.getScoreMaximum();
	}


	/**
	 * @return the minimun score
	 */
	public double getScoreMinimum() {
		return scoreOptionsPanel.getScoreMinimum();
	}


	/**
	 * @return the position of the score
	 */
	public int getScorePosition() {
		return scoreOptionsPanel.getScorePosition();
	}


	/**
	 * @return the height of the track
	 */
	public int getTrackHeight() {
		return basicOptionsPanel.getTrackHeight();
	}


	/**
	 * @return the name of the track
	 */
	public String getTrackName() {
		return basicOptionsPanel.getTrackName();
	}


	/**
	 * @return the number of vertical lines to show on the track
	 */
	public int getVerticalLineCout() {
		return axisOptionsPanel.getVerticalLineCout();
	}


	/**
	 * Format a label
	 * @param label a label
	 */
	protected static void formatLabel (JLabel label) {
		int height = label.getFontMetrics(label.getFont()).getHeight();
		Dimension dimension = new Dimension(130, height);
		label.setPreferredSize(dimension);
	}


	/**
	 * Format a field containing a number
	 * @param field
	 */
	protected static void formatNumberField (JTextField field) {
		field.setColumns(8);
	}


	/**
	 * Format a field containing a text
	 * @param field
	 */
	protected static void formatTextField (JTextField field) {
		field.setColumns(20);
	}
}
