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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class SettingsPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3353198770426567657L;

	// Transparency option
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static final int TRANSPARENCY_INIT = 50;
	private JLabel sliderValue;

	// Legend option
	private boolean showLegend = true;


	/**
	 * Constructor of {@link SettingsPanel}
	 */
	protected SettingsPanel () {
		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;


		// Stripes transparency option title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = PropertiesDialog.FIRST_TITLE_INSET;
		add(Utils.getTitleLabel("Stripes transparency"), gbc);

		// Slider for stripes transparency option
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getSliderPanel(), gbc);

		// Stripes legend option title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Show stripes legend"), gbc);

		// Radios for stripes legend option
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		gbc.weighty = 1;
		add(getStripeLegendPanel(), gbc);

	}


	/////////////////////////////////////////// Stripes transparency option

	/**
	 * Creates a panel that contains a slider and a label to show its current value.
	 * @return the panel
	 */
	private JPanel getSliderPanel () {
		// Initializes the panel and the layout
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(2, 1, 0, 0);
		panel.setLayout(layout);

		// Initializes the slider label value
		sliderValue = new JLabel(TRANSPARENCY_INIT + " %", SwingConstants.CENTER);

		// Initializes the slider
		JSlider slider = new JSlider(JSlider.HORIZONTAL, TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				int transparency = (int)source.getValue();
				sliderValue.setText(transparency + " %");
			}
		});
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);

		// Adds label and slider to the panel
		panel.add(sliderValue);
		panel.add(slider);

		// Returns the panel
		return panel;
	}


	/////////////////////////////////////////// Stripes legend option

	/**
	 * Creates a panel that contains a slider and a label to show its current value.
	 * @return the panel
	 */
	private JPanel getStripeLegendPanel () {
		// Initializes the panel and the layout
		JPanel panel = new JPanel();

		// Initializes the radio buttons
		JRadioButton yesButton = new JRadioButton("yes");
		JRadioButton noButton = new JRadioButton("no");
		if (showLegend) {
			yesButton.setSelected(true);
		} else {
			noButton.setSelected(true);
		}

		// Group the radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(yesButton);
		group.add(noButton);

		//Register a listener for the radio buttons
		yesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setShowLegend(true);
			}
		});
		noButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setShowLegend(false);
			}
		});

		// Adds label and slider to the panel
		panel.add(yesButton);
		panel.add(noButton);

		// Returns the panel
		return panel;
	}


	/**
	 * @param showLegend the showLegend to set
	 */
	private void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

}
