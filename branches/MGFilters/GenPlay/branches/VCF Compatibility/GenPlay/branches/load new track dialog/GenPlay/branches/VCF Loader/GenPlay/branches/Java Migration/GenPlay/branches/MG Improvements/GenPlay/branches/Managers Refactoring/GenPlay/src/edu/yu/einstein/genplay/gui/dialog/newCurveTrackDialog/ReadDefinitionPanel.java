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
package edu.yu.einstein.genplay.gui.dialog.newCurveTrackDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;


/**
 * Read panel of a {@link NewCurveTrackDialog}. Allows the user to shift and/or define the lenght of the reads
 * @author Julien Lajugie
 * @version 0.1
 */
class ReadDefinitionPanel extends JPanel {

	private static final long serialVersionUID = -4713934153945461579L;			// generated ID
	private static final DecimalFormat DF = new DecimalFormat("###,###,###"); 	// decimal format

	private final JCheckBox 			jcbShift;				// shift check box
	private final JFormattedTextField 	jftfShift;				// shift input box
	private final JLabel 				jlShiftBP;				// label bp after the shift input box
	private final JCheckBox 			jcbReadLength;			// read length box
	private final JFormattedTextField 	jftfReadLength;			// read length input box
	private final JLabel 				jlReadLengthBP;			// label bp after the shift input box

	private static boolean 	jcbShiftDefaultState = false;		// default state of the shift check box
	private static int 		jftfShiftDefaultValue = 0;			// default shift value
	private static boolean 	jcbReadLengthDefaultState = false;	// default state of the read length check box
	private static int 		jftfReadLengthDefaultValue = 1;		// default read length value


	/**
	 * Creates an instance of {@link ReadDefinitionPanel}
	 */
	ReadDefinitionPanel() {
		jcbShift = new JCheckBox("Shift:  ");
		jcbShift.setSelected(jcbShiftDefaultState);
		jcbShift.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				jftfShift.setEnabled(jcbShift.isSelected());				
			}
		});

		jftfShift = new JFormattedTextField(DF);
		((NumberFormatter) jftfShift.getFormatter()).setMinimum(0);
		jftfShift.setColumns(5);
		jftfShift.setValue(jftfShiftDefaultValue);		
		jftfShift.setEnabled(jcbShiftDefaultState);

		jlShiftBP = new JLabel("bp");

		jcbReadLength = new JCheckBox("Read Length:  ");
		jcbReadLength.setSelected(jcbReadLengthDefaultState);
		jcbReadLength.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				jftfReadLength.setEnabled(jcbReadLength.isSelected());				
			}
		});

		jftfReadLength = new JFormattedTextField(DF);
		((NumberFormatter) jftfReadLength.getFormatter()).setMinimum(1);
		jftfReadLength.setColumns(5);
		jftfReadLength.setValue(jftfReadLengthDefaultValue);
		jftfReadLength.setEnabled(jcbReadLengthDefaultState);

		jlReadLengthBP = new JLabel("bp");

		// add the components
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jcbShift, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jftfShift, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlShiftBP, c);

		c = new GridBagConstraints();
		c.gridx = 0; 
		c.gridy = 1;
		add(jcbReadLength, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		add(jftfReadLength, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		add(jlReadLengthBP, c);

		setBorder(BorderFactory.createTitledBorder("Read Definition"));
	}


	/**
	 * @return the shift value
	 */
	int getShiftValue() {
		if (jcbShift.isSelected()) {
			Number shiftNumber = ((Number) jftfShift.getValue());
			if (shiftNumber != null) {
				return shiftNumber.intValue();
			}
		}
		return 0;
	}


	/**
	 * @return the read length value. Returns zero if the read length is not specified 
	 */
	int getReadLengthValue() {
		if (jcbReadLength.isSelected()) {
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
		jcbShiftDefaultState = jcbShift.isSelected();
		jftfShiftDefaultValue = getShiftValue();
		jcbReadLengthDefaultState = jcbReadLength.isSelected();
		jftfReadLengthDefaultValue = getReadLengthValue();
	}
}
