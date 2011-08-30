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
package edu.yu.einstein.genplay.gui.dialog.filterDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;


/**
 * The filter panel part of the {@link FilterDialog}.
 * This abstract class is overridden by the different kind of filter panels
 * @author Julien Lajugie
 * @version 0.1
 */
abstract class FilterPanel extends JPanel {

	private static final long serialVersionUID = -1162560421976891221L;	// generated ID
	private final JTextArea 			jlDescription;	// text area showing a description of the filter
	private final JLabel 				jlMin;			// label for the min input
	private final JLabel 				jlMax;			// label for the max input
	private final JFormattedTextField 	jftfMin;		// min input box
	private final JFormattedTextField 	jftfMax;		// max input box
	private final JRadioButton 			jrbRemove;		// radio button remove
	private final JRadioButton 			jrbSaturate;	// radio button saturate
	private final JTextArea 			jtaRadioText;	// text area with explanation for the radio buttons 


	/**
	 * Creates an instance of a {@link FilterPanel}
	 * @param name name of the panel
	 * @param filterDescription description of the filter
	 * @param textMin text of the label describing the min input box
	 * @param textMax text of the label describing the max input box
	 * @param df decimal format for the input
	 * @param defaultMin default min
	 * @param defaultMax default max
	 */
	FilterPanel(String name, String filterDescription, String textMin, String textMax, DecimalFormat df, Number defaultMin, Number defaultMax, boolean defaultIsSaturation) {		
		super();
		// set the name of the filter panel
		setName(name);
		// create the description text area
		jlDescription = new JTextArea(filterDescription);
		jlDescription.setEditable(false);
		jlDescription.setBackground(getBackground());
		// create the labels
		jlMin = new JLabel(textMin);
		jlMax = new JLabel(textMax);
		// create the input boxes
		jftfMin = new JFormattedTextField(df);
		jftfMin.setValue(defaultMin);
		jftfMax = new JFormattedTextField(df);
		jftfMax.setValue(defaultMax);
		// create the radio buttons
		jrbRemove = new JRadioButton("Remove");
		jrbSaturate = new JRadioButton("Saturate");		
		// group for the radio buttons
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbRemove);
		radioGroup.add(jrbSaturate);
		jrbRemove.setSelected(!defaultIsSaturation);
		jrbSaturate.setSelected(defaultIsSaturation);
		// create the text area with explanation for the ratio buttons
		jtaRadioText = new JTextArea("Choose Remove to set the filtered values to zero\n" +
		"Choose Saturate to set the filtered values to the boundary value");
		jtaRadioText.setEditable(false);
		jtaRadioText.setBackground(getBackground());

		// add the components 
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 30, 0);
		add(jlDescription, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(jlMin, c);

		c.gridx = 1;
		add(jftfMin, c);

		c.gridx = 0;
		c.gridy = 2;
		add(jlMax, c);

		c.gridx = 1;
		add(jftfMax, c);

		// we don't add the saturation components if this option is not available
		if (isSaturable()) {
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(30, 0, 0, 0);
			add(jrbRemove, c);

			c.gridx = 1;
			add(jrbSaturate, c);

			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 2;
			c.insets = new Insets(0, 0, 0, 0);
			add(jtaRadioText, c);
		}
	}


	/**
	 * @return the input for the max value
	 */
	Number getMaxInput() {
		Number max = (Number) jftfMax.getValue();
		return max;
	}


	/**
	 * @return the input for the min value
	 */
	Number getMinInput() {
		Number min = (Number) jftfMin.getValue();
		return min;
	}


	/**
	 * Verifies that the input are valid. Shows a warning message if not.
	 * @return true if the input are valid. False otherwise
	 */
	abstract boolean isInputValid();


	/**
	 * @return true if the saturation option is available for a filter
	 */
	abstract boolean isSaturable();


	/**
	 * @return true if the saturation option is selected. False if the remove option is selected
	 */
	boolean isSaturation() {
		if (isSaturable()) {
			return jrbSaturate.isSelected();	
		} else {
			// if the saturation option is not available we return false
			return false;
		}
	}


	/**
	 * Saves the saturation option state
	 */
	abstract void saveIsSaturation();


	/**
	 * Saves the maximum input 
	 */
	abstract void saveMax();


	/**
	 * Saves the minimum input 
	 */
	abstract void saveMin();


	@Override
	public String toString() {
		return getName();
	}
}
