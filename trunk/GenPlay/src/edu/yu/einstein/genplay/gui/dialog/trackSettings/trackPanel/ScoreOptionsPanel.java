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
package edu.yu.einstein.genplay.gui.dialog.trackSettings.trackPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.gui.track.TrackScore;
import edu.yu.einstein.genplay.gui.track.layer.foreground.ForegroundData;
import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;

/**
 * Panel of the track setting dialog with the settings related to the score of the track
 * @author Julien Lajugie
 */
class ScoreOptionsPanel extends JPanel implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 6921118793086413993L; // generated serial ID

	private final JLabel 				jlScoreMin;				// minimum score value label
	private final JFormattedTextField 	jftfScoreMin;			// minimum score value text field
	private final JLabel 				jlScoreMax;				// maximum score value label
	private final JFormattedTextField 	jftfScoreMax;			// maximum score value text field
	private final JLabel				jlScoreAuto;			// autoscaled score label
	private final JCheckBox				jcbScoreAuto;			// autoscaled score check box
	private final JLabel				jlScorePosition;		// label score position
	private final JRadioButton			jrbTopPosition;			// radio button top score position
	private final JRadioButton			jrbBottomPosition;		// radio button bottom score position
	private final ButtonGroup			scorePositionGroup;		// goup for the score position
	private final JLabel				jlScoreColor;			// label score color
	private final JButton				jbScoreColor;			// button score color


	/**
	 * Creates an instance of a {@link ScoreOptionsPanel}
	 */
	ScoreOptionsPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Score Options"));

		jlScoreMin = new JLabel("Minimum Score");
		jftfScoreMin = new JFormattedTextField(createScoreFormatter());

		jlScoreMax = new JLabel("Maximum Score");
		jftfScoreMax = new JFormattedTextField(createScoreFormatter());

		jlScoreAuto = new JLabel("Auto-Rescaled");
		jcbScoreAuto = new JCheckBox();
		jcbScoreAuto.addChangeListener(this);

		jlScorePosition = new JLabel("Score Position");
		jrbTopPosition = new JRadioButton("Top");
		jrbBottomPosition = new JRadioButton("Bottom");

		// create the group for the radio buttons
		scorePositionGroup = new ButtonGroup();
		scorePositionGroup.add(jrbTopPosition);
		scorePositionGroup.add(jrbBottomPosition);

		jlScoreColor = new JLabel("Score Color");
		jbScoreColor = new JButton("Color");
		jbScoreColor.addActionListener(this);

		// Format components
		TrackSettingsPanel.formatNumberField(jftfScoreMin);
		TrackSettingsPanel.formatNumberField(jftfScoreMax);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 1;
		add(jlScoreMin, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		add(jftfScoreMin, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		add(jlScoreMax, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		add(jftfScoreMax, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		add(jlScoreAuto, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		add(jcbScoreAuto, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		add(jlScorePosition, c);

		c.gridx = 1;
		add(jrbTopPosition, c);

		c.gridx = 2;
		c.weightx = 0.9;
		add(jrbBottomPosition, c);

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.1;
		add(jlScoreColor, c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		add(jbScoreColor, c);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Color currentColor = jbScoreColor.getBackground();
		Color newScoreColor = GenPlayColorChooser.showDialog(getRootPane(), "Choose a color for the selected track score", currentColor);
		if (newScoreColor != null) {
			jbScoreColor.setBackground(newScoreColor);
		}
	}


	/**
	 * @return a formatter for the height formated text field
	 */
	private AbstractFormatter createScoreFormatter() {
		NumberFormat scoreFormat = NumberFormat.getNumberInstance();
		NumberFormatter scoreFormatter = new NumberFormatter(scoreFormat);
		return scoreFormatter;
	}


	/**
	 * @return the color of the score
	 */
	Color getScoreColor() {
		return jbScoreColor.getBackground();
	}


	/**
	 * @return the maximum score
	 */
	float getScoreMaximum() {
		return ((Number) jftfScoreMax.getValue()).floatValue();
	}


	/**
	 * @return the minimun score
	 */
	float getScoreMinimum() {
		return ((Number) jftfScoreMin.getValue()).floatValue();
	}


	/**
	 * @return the position of the score
	 */
	int getScorePosition() {
		if (jrbTopPosition.isSelected()) {
			return TrackConstants.TOP_SCORE_POSITION;
		} else {
			return TrackConstants.BOTTOM_SCORE_POSITION;
		}
	}


	/**
	 * Initializes the values of the components of this panel
	 * @param track
	 */
	void initValues(Track track) {
		ForegroundData foregroundData = track.getForegroundLayer().getData();
		TrackScore trackScore = track.getScore();

		jftfScoreMin.setValue(trackScore.getMinimumScore());
		jftfScoreMax.setValue(trackScore.getMaximumScore());
		jcbScoreAuto.setSelected(trackScore.isScoreAxisAutorescaled());

		// select the appropriate radio button
		if (foregroundData.getScorePosition() == TrackConstants.TOP_SCORE_POSITION) {
			jrbTopPosition.setSelected(true);
		} else if (foregroundData.getScorePosition() == TrackConstants.BOTTOM_SCORE_POSITION) {
			jrbBottomPosition.setSelected(true);
		}
		jbScoreColor.setBackground(foregroundData.getScoreColor());
	}


	/**
	 * @return true if the score is set to be auto-rescaled. False otherwise
	 */
	boolean isScoreAutoRescaled() {
		return jcbScoreAuto.isSelected();
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == jcbScoreAuto) {
			if (jcbScoreAuto.isSelected()) {
				jftfScoreMin.setEnabled(false);
				jftfScoreMax.setEnabled(false);
			} else {
				jftfScoreMin.setEnabled(true);
				jftfScoreMax.setEnabled(true);
			}
		}
	}


	/**
	 * Make sure that the minimum score is smaller than the maximum score
	 * @return true if the input are valid
	 */
	public boolean validateInput() {
		if (!isScoreAutoRescaled() && (getScoreMinimum() >= getScoreMaximum())) {
			JOptionPane.showMessageDialog(getRootPane(), "The maximum score value must be greater than the minimum one.", "Invalid Scores", JOptionPane.WARNING_MESSAGE);
			return false;
		} else {
			return true;
		}
	}
}
