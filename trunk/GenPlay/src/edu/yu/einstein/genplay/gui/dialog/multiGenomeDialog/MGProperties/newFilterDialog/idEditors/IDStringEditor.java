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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.idEditors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderBasicType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.AltFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.FilterFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.StringIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.StringIDFilterInterface;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDStringEditor implements IDEditor {

	private final static String CONSTRAINT_LABEL_TTT 	= "Select a constraint for the value.";
	private final static String PRESENT_TTT 			= "The associated file data will must contain the value in order to be accepted.";
	private final static String ABSENT_TTT 				= "The associated file data will must NOT contain the value in order to be accepted.";
	private final static String PRESENT 				= "must contain";
	private final static String ABSENT 					= "must not contain";
	private final static String VALUE_LABEL_TTT 		= "Define a pattern to filter.";
	private final static String VALUE_BOX_TTT			= "Type or select a value.";

	private JPanel			panel;
	private VCFHeaderType 	header;				// Header ID
	private final JLabel 			constraintLabel;	// Label for naming the constraint
	private final JLabel 			valueLabel;			// Label for naming the value
	private JComboBox		jcValue;			// Editable combo box for selecting the value
	private List<String>	defaultElements;	// The default elements for the value box
	private JRadioButton	present;			// Radio box for PRESENT value
	private JRadioButton	absent;				// Radio box for ABSENT value


	/**
	 * Constructor of {@link IDStringEditor}
	 */
	public IDStringEditor () {
		defaultElements = new ArrayList<String>();
		// Creates the labels
		constraintLabel = new JLabel("Constraint:");
		constraintLabel.setToolTipText(CONSTRAINT_LABEL_TTT);
		valueLabel = new JLabel("Value:");
		valueLabel.setToolTipText(VALUE_LABEL_TTT);
	}


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Creates the radio boxes
		present = new JRadioButton(PRESENT);
		present.setToolTipText(PRESENT_TTT);
		absent = new JRadioButton(ABSENT);
		absent.setToolTipText(ABSENT_TTT);

		// Creates the value box
		jcValue = getValueBox();
		jcValue.setToolTipText(VALUE_BOX_TTT);

		// Creates the group
		ButtonGroup group = new ButtonGroup();
		group.add(present);
		group.add(absent);

		// Default setting
		present.setSelected(true);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Constraint label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(constraintLabel, gbc);

		// "Present" box
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		panel.add(present, gbc);

		// "Absent" box
		gbc.gridy++;
		panel.add(absent, gbc);

		// Value label
		gbc.gridy++;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(valueLabel, gbc);

		// Value box
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		gbc.weighty = 1;
		panel.add(jcValue, gbc);

		return panel;
	}


	@Override
	public IDFilterInterface getFilter() {
		StringIDFilterInterface filter = null;
		if (header instanceof VCFHeaderBasicType) {
			if (header.getColumnCategory() == VCFColumnName.ALT) {
				filter = new AltFilter();
			} else if (header.getColumnCategory() == VCFColumnName.FILTER) {
				filter = new FilterFilter();
			}
		} else {
			filter = new StringIDFilter();
		}
		filter.setHeaderType(header);

		if (jcValue.getSelectedItem() != null) {
			filter.setValue(jcValue.getSelectedItem().toString());
		} else {
			filter.setValue(null);
		}
		if (present.isSelected()) {
			filter.setRequired(true);
		} else {
			filter.setRequired(false);
		}

		return filter;
	}


	@Override
	public void setHeaderType(VCFHeaderType id) {
		this.header = id;
	}


	@Override
	public VCFHeaderType getHeaderType () {
		return header;
	}


	/**
	 * Creates the value box and return it
	 * @return the value box
	 */
	private JComboBox getValueBox () {
		JComboBox box = new JComboBox();
		DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
		for (String s: defaultElements) {
			model.addElement(s);
		}
		box.setEditable(true);
		return box;
	}


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		boolean isRequired = false;
		String value = null;
		if (filter instanceof StringIDFilter) {
			StringIDFilter castFilter = (StringIDFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else if (filter instanceof AltFilter) {
			AltFilter castFilter = (AltFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else if (filter instanceof FilterFilter) {
			FilterFilter castFilter = (FilterFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else {
			System.out.println("IDStringEditor.initializesPanel()");
		}

		if (isRequired) {
			present.setSelected(true);
		} else {
			absent.setSelected(true);
		}
		((DefaultComboBoxModel)jcValue.getModel()).addElement(value);
		jcValue.setSelectedItem(value);
	}


	/**
	 * @param defaultElements the defaultElements to set
	 */
	public void setDefaultElements(List<String> defaultElements) {
		this.defaultElements = defaultElements;
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (header == null) {
			errors += "ID selection\n";
		}

		if (jcValue.getSelectedItem() == null) {
			errors += "Value selection\n";
		} else if (jcValue.getSelectedItem().toString().isEmpty()){
			errors += "Value selection\n";
		}
		return errors;
	}


	@Override
	public void setEnabled(boolean b) {
		if (panel != null) {
			panel.setEnabled(b);
			constraintLabel.setEnabled(b);
			valueLabel.setEnabled(b);
			jcValue.setEnabled(b);
			present.setEnabled(b);
			absent.setEnabled(b);
		}
	}


	@Override
	public boolean isEnabled() {
		return panel.isEnabled();
	}

}
