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
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.util.Images;


/**
 * Panel of a {@link NewCurveLayerDialog} with an input box for the bin size
 * @author Julien Lajugie
 */
class BinSizePanel extends JPanel {

	private static final long serialVersionUID = -7359118518250220846L;	// generated ID
	private static final int 	MAX_BINSIZE = Integer.MAX_VALUE;		// maximum bin size
	private static final int 	SPINNER_STEP = 100; 					// step of the spinner
	private final JCheckBox		jcbCreateBinList;						// check box that can be checked to create a bin lists
	private final JLabel 		jlCreateBinListHelp;					// label create bin list help
	private final JSpinner 		jsBinSize; 								// spinner for the binsize input
	private static int 			defaultBinSize = 1000; 					// default binsize
	private static boolean		defaultIsCreateBinListSelected;			// default state of the check box to create bin lists

	/**
	 * Creates an instance of {@link BinSizePanel}
	 */
	BinSizePanel() {
		super();
		// check box
		jcbCreateBinList = new JCheckBox("Score Bins");
		jcbCreateBinList.setSelected(defaultIsCreateBinListSelected);
		jcbCreateBinList.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (jsBinSize != null) {
					jsBinSize.setEnabled(jcbCreateBinList.isSelected());
				}
			}
		});

		// tooltip
		jlCreateBinListHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlCreateBinListHelp.setToolTipText("<html>Choose this option to create bins of a specified constant length.<br/>" +
				"The score of each bin is the result of the sum, average maximum or minimum of the scores of the reads / windows that overlap the bin.<br/>" +
				"If a read / window overlap more than one bin, its score is divided between the different bins proportionally to the length that overlaps each bin.<br/>" +
				"Bin lists are generally more memory efficient and offer operations that are not available otherwise.</html>");

		// spinner
		SpinnerNumberModel snm = new SpinnerNumberModel(defaultBinSize, 1, MAX_BINSIZE, SPINNER_STEP);
		jsBinSize = new JSpinner(snm);
		jsBinSize.setEnabled(defaultIsCreateBinListSelected);

		// add components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		add(jcbCreateBinList, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 1;
		add(jlCreateBinListHelp, gbc);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 10, 0);
		add(jsBinSize, gbc);

		setBorder(BorderFactory.createTitledBorder("Bin Size"));
	}


	/**
	 * @return the selected binsize
	 */
	int getBinSize() {
		return (Integer) jsBinSize.getValue();
	}


	/**
	 * @return true if the user decided to create a bin list
	 */
	boolean isCreateBinListSelected() {
		return jcbCreateBinList.isSelected();
	}


	/**
	 * Saves the selected bin size as default
	 */
	void saveDefault() {
		defaultIsCreateBinListSelected = isCreateBinListSelected();
		defaultBinSize = getBinSize();
	}
}
