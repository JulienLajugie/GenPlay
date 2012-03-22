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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.enums.Strand;


/**
 * Strand selection panel of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class StrandSelectionPanel extends JPanel {

	private static final long serialVersionUID = -2426572515664231706L;	//generated ID
	private final JRadioButton			jrPlus;							// 5' Strand radio button
	private final JRadioButton			jrMinus;						// 3' Strand radio button 
	private final JRadioButton			jrBoth;							// both strands radio button
	private final ButtonGroup			strandRadioGroup;				// group for the strand radio buttons
	private static boolean				jrPlusDefaultState = false;	 	// default selection state of the 5' button 
	private static boolean				jrMinusDefaultState = false;	// default selection state of the 3' button
	private static boolean				jrBothDefaultState = true;		// default selection state of the both button

	
	/**
	 * Creates an instance of {@link StrandSelectionPanel}
	 */
	StrandSelectionPanel() {
		jrPlus = new JRadioButton("5' Strand");
		jrMinus = new JRadioButton("3' Strand");
		jrBoth = new JRadioButton("Both Strands");

		strandRadioGroup = new ButtonGroup();
		strandRadioGroup.add(jrPlus);
		strandRadioGroup.add(jrMinus);
		strandRadioGroup.add(jrBoth);

		jrPlus.setSelected(jrPlusDefaultState);
		jrMinus.setSelected(jrMinusDefaultState);
		jrBoth.setSelected(jrBothDefaultState);

		// add the components
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrPlus, c);
		
		c = new GridBagConstraints();
		c.gridwidth = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrMinus, c);
		
		c = new GridBagConstraints();
		c.gridwidth = 3;		
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrBoth, c);
		
		setBorder(BorderFactory.createTitledBorder("Strand"));
	}


	/**
	 * @return the Strand to extract. Null if both
	 */
	Strand getStrandToExtract() {
		if (jrPlus.isSelected()) {
			return Strand.FIVE;
		} else if (jrMinus.isSelected()) {
			return Strand.THREE;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Saves the selected state of the radio buttons
	 */
	void saveDefault() {
		jrPlusDefaultState = jrPlus.isSelected();
		jrMinusDefaultState = jrMinus.isSelected();
		jrBothDefaultState = jrBoth.isSelected();
	}
}
