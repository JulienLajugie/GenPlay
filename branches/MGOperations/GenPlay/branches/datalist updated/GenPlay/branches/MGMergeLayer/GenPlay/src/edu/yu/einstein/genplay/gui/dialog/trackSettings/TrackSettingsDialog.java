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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Images;

/**
 * A dialog to set the properties of a track.
 * @author Julien Lajugie
 */
public class TrackSettingsDialog extends JDialog implements ActionListener  {

	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	private static final long serialVersionUID = -5402176442100056968L; // generated ID

	private final BasicOptionsPanel 	basicOptionsPanel;			// panel for the basic options
	private final AxisOptionsPanel 		axisOptionsPanel;			// panel for the axis options
	private final ScoreOptionsPanel 	scoreOptionsPanel;			// panel for the score options

	private final JButton				jbOk; 						// button OK
	private final JButton				jbCancel;					// button Cancel

	private int							approved = CANCEL_OPTION;	// approved or canceled


	/**
	 * Creates an instance of {@link TrackSettingsDialog}
	 */
	public TrackSettingsDialog() {
		super();
		setModal(true);

		basicOptionsPanel = new BasicOptionsPanel();
		axisOptionsPanel = new AxisOptionsPanel();
		scoreOptionsPanel = new ScoreOptionsPanel();

		jbOk = new JButton("Ok");
		jbOk.addActionListener(this);
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

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

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		add(jbOk, c);

		c.gridx = 1;
		c.gridy = 3;
		add(jbCancel, c);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbOk) {
			if (scoreOptionsPanel.validateInput()) {
				approved = APPROVE_OPTION;
				dispose();
			}
		} else if(e.getSource() == jbCancel) {
			approved = CANCEL_OPTION;
			dispose();
		}
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
	 * Displays the layer settings dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @param track		track for the settings
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, Track track) {
		basicOptionsPanel.initValues(track);
		axisOptionsPanel.initValues(track);
		scoreOptionsPanel.initValues(track);
		// dialog properties
		setTitle("Track Settings");
		setIconImage(Images.getApplicationImage());
		setLocationRelativeTo(parent);
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setVisible(true);
		return approved;
	}
}
