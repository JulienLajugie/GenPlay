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
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * Read panel of a {@link NewCurveLayerDialog}. Allows the user to shift and/or define the length of the reads
 * @author Julien Lajugie
 */
class ReadDefinitionPanel extends JPanel {

	private static final long serialVersionUID = -4713934153945461579L;	// generated ID

	private final JCheckBox 			jcbIsUserDefinedFragmentLength;	// checkbox to select if the fragment length is user defined
	private final JLabel 				jlHelp;							// label help

	private final JLabel 				jlFragmentLength;				// label fragment length
	private final JFormattedTextField 	jftfFragmentLength;				// fragment length input box
	private final JLabel 				jlFragmentLengthBP;				// label bp after the shift input box

	private final JLabel			 	jlReadLength;					// label read length
	private final JFormattedTextField 	jftfReadLength;					// read length input box
	private final JLabel 				jlReadLengthBP;					// label bp after the read length input box

	private static boolean 	jcbDefaultState = false;					// default state of the check box
	private static int 		jftfReadLengthDefaultValue = 100;			// default read length value
	private static int 		jftfFragmentLengthDefaultValue = 300;		// default fragment length value

	/**
	 * Creates an instance of {@link ReadDefinitionPanel}
	 */
	ReadDefinitionPanel() {
		jcbIsUserDefinedFragmentLength = new JCheckBox("Define Fragment Length");
		jcbIsUserDefinedFragmentLength.setSelected(jcbDefaultState);
		jcbIsUserDefinedFragmentLength.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				jftfFragmentLength.setEnabled(jcbIsUserDefinedFragmentLength.isSelected());
				jftfReadLength.setEnabled(jcbIsUserDefinedFragmentLength.isSelected());
			}
		});

		// tooltip
		jlHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlHelp.setToolTipText("<html>Check to define the size of the fragments.<br>" +
				"Only the start position define in the file will be used. Positions are defined as follow:<br>" +
				"On <b>5'</b>: <b>start</b> = start from file, <b>stop</b> = (start + fragment length)<br>" +
				"On <b>3'</b>: <b>start</b> = (start from file + read length - fragment length), <b>stop</b> = (start + fragment length)<br></html>");

		// fragment length components
		jlFragmentLength = new JLabel("Fragment Length:");
		jftfFragmentLength = new JFormattedTextField(NumberFormats.getPositionFormat());
		((NumberFormatter) jftfFragmentLength.getFormatter()).setMinimum(1);
		jftfFragmentLength.setColumns(6);
		jftfFragmentLength.setValue(jftfFragmentLengthDefaultValue);
		jftfFragmentLength.setEnabled(jcbDefaultState);
		jlFragmentLengthBP = new JLabel("bp");

		// read length
		jlReadLength = new JLabel("Read Length:");
		jftfReadLength = new JFormattedTextField(NumberFormat.getInstance());
		((NumberFormatter) jftfReadLength.getFormatter()).setMinimum(1);
		jftfReadLength.setColumns(6);
		jftfReadLength.setValue(jftfReadLengthDefaultValue);
		jftfReadLength.setEnabled(jcbDefaultState);
		jlReadLengthBP = new JLabel("bp");

		// add the components
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jcbIsUserDefinedFragmentLength, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlHelp, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlFragmentLength, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jftfFragmentLength, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlFragmentLengthBP, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlReadLength, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jftfReadLength, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlReadLengthBP, c);
		setBorder(BorderFactory.createTitledBorder("Fragment Length"));
	}


	/**
	 * @return the shift value
	 */
	int getFragmentLengthValue() {
		if (jcbIsUserDefinedFragmentLength.isSelected()) {
			Number fragmentLengthNumber = ((Number) jftfFragmentLength.getValue());
			if (fragmentLengthNumber != null) {
				return fragmentLengthNumber.intValue();
			}
		}
		return 0;
	}


	/**
	 * @return the read length value. Returns zero if the read length is not specified
	 */
	int getReadLengthValue() {
		if (jcbIsUserDefinedFragmentLength.isSelected()) {
			Number readLengthNumber = ((Number) jftfReadLength.getValue());
			if (readLengthNumber != null) {
				return readLengthNumber.intValue();
			}
		}
		return 0;
	}


	/**
	 * Saves the selected state of the different components
	 */
	void saveDefault() {
		jcbDefaultState = jcbIsUserDefinedFragmentLength.isSelected();
		jftfFragmentLengthDefaultValue= getFragmentLengthValue();
		jftfReadLengthDefaultValue = getReadLengthValue();
	}
}
