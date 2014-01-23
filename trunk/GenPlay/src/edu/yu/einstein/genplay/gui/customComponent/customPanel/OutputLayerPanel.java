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
package edu.yu.einstein.genplay.gui.customComponent.customPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * Panel for selecting the output track and the layer name of an operation
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class OutputLayerPanel extends JPanel {

	private static final long serialVersionUID = -966335984511123423L; // generated serial ID
	private final JTextField 	jtfLayerName;		// Text field for the layer name
	private Track 				outputTrack;		// The output track


	/**
	 * Creates an instance of {@link OutputLayerPanel}
	 */
	public OutputLayerPanel() {
		this(null);
	}


	/**
	 * Creates an instance of {@link OutputLayerPanel}
	 * @param defaultLayerName default layer name
	 */
	public OutputLayerPanel(String defaultLayerName) {
		// Creates panel elements
		JLabel jlTrackName = new JLabel("Output track name:");
		JLabel jlTrack = new JLabel("Output track:");
		jtfLayerName = new JTextField();
		if (defaultLayerName != null) {
			jtfLayerName.setText(defaultLayerName);;
		}
		jtfLayerName.setColumns(15);
		JComboBox jcbOutputTrack = new JComboBox(MainFrame.getInstance().getTrackListPanel().getModel().getTracks());
		jcbOutputTrack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				outputTrack = (Track) ((JComboBox)e.getSource()).getSelectedItem();}
		});
		jcbOutputTrack.setSelectedIndex(0);

		setBorder(BorderFactory.createTitledBorder("Output Track"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;

		gbc.weightx = 1;
		gbc.weighty = 0;

		// Add the output layer name
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 5, 0, 0);
		add(jlTrackName, gbc);
		gbc.gridx = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(jtfLayerName, gbc);

		// Add the output track selection box
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		add(jlTrack, gbc);
		gbc.gridx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 0, 10, 0);
		add(jcbOutputTrack, gbc);
	}


	/**
	 * @return the name of the layer
	 */
	public String getLayerName() {
		return jtfLayerName.getText();
	}


	/**
	 * @return the output track
	 */
	public Track getOutputTrack() {
		return outputTrack;
	}
}
