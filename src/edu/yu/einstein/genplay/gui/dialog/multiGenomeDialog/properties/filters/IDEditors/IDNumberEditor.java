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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.NumberIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.NumberIDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.QualFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDNumberEditor implements IDEditor {

	private VCFHeaderType 	id;			// Header ID
	private String			category;	// category of the filter
	private JComboBox		inequationBox01;
	private JComboBox		inequationBox02;
	private JTextField		valueField01;
	private JTextField		valueField02;


	@Override
	public void updatePanel(JPanel panel) {
		// Back up the size of the panel
		Dimension previousDimension = panel.getPreferredSize();

		// Remove everything from the panel
		panel.removeAll();

		// Initializes boxes
		inequationBox01 = getBox(inequationBox01);
		inequationBox02 = getBox(inequationBox02);

		// Initializes text fields
		valueField01 = getTextField(valueField01);
		valueField02 = getTextField(valueField02);

		// Default setting
		inequationBox02.setEnabled(false);
		valueField02.setEnabled(false);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;

		// "Present" button
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 5, 0, 0);
		panel.add(getInequationPanel(inequationBox01, valueField01), gbc);

		// "Absent" button
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(getInequationPanel(inequationBox02, valueField02), gbc);

		// Create a new dimension based on the previous one
		Dimension dimension = new Dimension((int)previousDimension.getWidth(), 50);

		// Restore size to former value
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);
	}


	/**
	 * Create a panel that contain a "value" label, a combo box (inequation) and a text field to provide the value.
	 * @param box		the box to use
	 * @param field		the text field to use
	 * @return			the panel
	 */
	private JPanel getInequationPanel (JComboBox box, JTextField field) {
		JPanel panel = new JPanel();

		Dimension dimension = new Dimension(180, 30);

		// Restore size to former value
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy = 0;

		// Label
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(new JLabel("value"), gbc);

		// Combobox
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		panel.add(box, gbc);

		// Text field
		gbc.gridx = 2;
		gbc.weightx = 1;
		panel.add(field, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * Initializes a box
	 * @param box box to initialize
	 */
	private JComboBox getBox (JComboBox box) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(" ");
		model.addElement(InequalityOperators.INFERIOR);
		model.addElement(InequalityOperators.INFERIOR_OR_EQUAL);
		model.addElement(InequalityOperators.EQUAL);
		model.addElement(InequalityOperators.SUPERIOR);
		model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);

		box = new JComboBox(model);
		Dimension dimension = new Dimension(40, box.getFontMetrics(box.getFont()).getHeight() + 2);
		box.setMinimumSize(dimension);
		box.setPreferredSize(dimension);
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				if (box.equals(inequationBox01)) {
					doAction(inequationBox01, inequationBox02, valueField01, valueField02);
				} else {
					doAction(inequationBox02, inequationBox01, valueField02, valueField01);
				}
			}
		});
		return box;
	}


	/**
	 * Initializes a text field
	 * @param box text field to initialize
	 */
	private JTextField getTextField (JTextField field) {
		field = new JTextField();
		Dimension dimension = new Dimension(60, field.getFontMetrics(field.getFont()).getHeight() + 3);
		field.setMinimumSize(dimension);
		field.setPreferredSize(dimension);
		return field;
	}


	/**
	 * Updates model boxes according to the selected box value
	 * @param currentBox	the current used by the user
	 * @param otherBox		the other box
	 */
	private void doAction (JComboBox currentBox, JComboBox otherBox, JTextField currentField, JTextField otherField) {
		String value = currentBox.getSelectedItem().toString();
		DefaultComboBoxModel model = null;
		boolean enable = true;

		if (value.equals(" ")) {
			model = new DefaultComboBoxModel();
			model.addElement(" ");
			model.addElement(InequalityOperators.INFERIOR);
			model.addElement(InequalityOperators.INFERIOR_OR_EQUAL);
			model.addElement(InequalityOperators.EQUAL);
			model.addElement(InequalityOperators.SUPERIOR);
			model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);
			currentField.setEnabled(false);
		} else {
			InequalityOperators inequality = (InequalityOperators) currentBox.getSelectedItem();
			currentField.setEnabled(true);
			if (inequality == InequalityOperators.EQUAL) {
				otherBox.setEnabled(false);
				otherField.setEnabled(false);
			} else if (inequality == InequalityOperators.INFERIOR || inequality == InequalityOperators.INFERIOR_OR_EQUAL) {
				model = new DefaultComboBoxModel();
				model.addElement(" ");
				model.addElement(InequalityOperators.SUPERIOR);
				model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);
			} else if (inequality == InequalityOperators.SUPERIOR || inequality == InequalityOperators.SUPERIOR_OR_EQUAL){
				model = new DefaultComboBoxModel();
				model.addElement(" ");
				model.addElement(InequalityOperators.INFERIOR);
				model.addElement(InequalityOperators.INFERIOR_OR_EQUAL);
			}
		}

		if (model != null) {
			Object otherSelectedItem = otherBox.getSelectedItem();
			otherBox.setModel(model);
			otherBox.setSelectedItem(otherSelectedItem);
			otherBox.setEnabled(enable);
			otherField.setEnabled(enable);
		}

	}


	@Override
	public IDFilterInterface getFilter() {
		NumberIDFilterInterface filter = null;
		if (category.equals("QUAL") && id == null) {
			filter = new QualFilter();
		} else {
			filter = new NumberIDFilter();
		}

		filter.setID(id);
		filter.setCategory(category);

		if (inequationBox01.isEnabled() && !inequationBox01.getSelectedItem().equals(" ")){
			filter.setInequation01((InequalityOperators)inequationBox01.getSelectedItem());
			try {
				filter.setValue01(Float.parseFloat(valueField01.getText()));
			} catch (Exception e) {
				filter.setValue01(null);
			}
		}

		if (inequationBox02.isEnabled() && !inequationBox02.getSelectedItem().toString().equals(" ")) {
			filter.setInequation02((InequalityOperators)inequationBox02.getSelectedItem());
			try {
				filter.setValue02(Float.parseFloat(valueField02.getText()));
			} catch (Exception e) {
				filter.setValue02(null);
			}
		} else {
			filter.setInequation02(null);
			filter.setValue02(null);
		}

		return filter;
	}


	@Override
	public void setID(VCFHeaderType id) {
		this.id = id;
	}


	@Override
	public VCFHeaderType getID () {
		return id;
	}


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		InequalityOperators inequation01 = null;
		InequalityOperators inequation02 = null;
		Float value01 = null;
		Float value02 = null;

		if (filter instanceof QualFilter) {
			QualFilter castFilter = (QualFilter) filter;
			inequation01 = castFilter.getInequation01();
			inequation02 = castFilter.getInequation02();
			value01 = castFilter.getValue01();
			value02 = castFilter.getValue02();
		} else if (filter instanceof NumberIDFilter) {
			NumberIDFilter castFilter = (NumberIDFilter) filter;
			inequation01 = castFilter.getInequation01();
			inequation02 = castFilter.getInequation02();
			value01 = castFilter.getValue01();
			value02 = castFilter.getValue02();
		}

		inequationBox01.setSelectedItem(inequation01);
		valueField01.setText(value01.toString());
		if (inequation02 != null && value02 != null) {
			inequationBox02.setSelectedItem(inequation02);
			valueField02.setText(value02.toString());
		} else {
			inequationBox02.setSelectedIndex(0);
			valueField02.setText("");
		}
	}


	@Override
	public void setCategory(String category) {
		this.category = category;
	}


	@Override
	public String getCategory() {
		return category;
	}

}
