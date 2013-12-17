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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderBasicType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.NumberIDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.QualFilter;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDNumberEditor implements IDEditor {

	private final static String VALUE_LABEL_TTT 		= "The processing data in the file.";
	private final static String INEQUATION_TTT 			= "Select an operator.";
	private final static String VALUE_JTF_TTT 			= "Constraint value.";
	private final static String AND_OPERATOR_TTT 		= "Both equation will have to be true in order to accept the processing data.";
	private final static String OR_OPERATOR_TTT 		= "Only one equation will have to be true in order to accept the processing data.";

	private JPanel			panel;
	private VCFHeaderType 	header;				// Header ID
	private JComboBox		inequationBox01;	// First inequation
	private JComboBox		inequationBox02;	// Second inequation
	private JTextField		valueField01;		// First value
	private JTextField		valueField02;		// Second value
	private JRadioButton	andButton;			// AND operator button
	private JRadioButton	orButton;			// OR operator button


	@Override
	public JPanel updatePanel() {
		panel = new JPanel();

		// Initializes boxes
		inequationBox01 = getBox(inequationBox01);
		inequationBox01.setToolTipText(INEQUATION_TTT);
		inequationBox02 = getBox(inequationBox02);
		inequationBox02.setToolTipText(INEQUATION_TTT);

		// Initializes text fields
		valueField01 = getTextField(valueField01);
		valueField01.setToolTipText(VALUE_JTF_TTT);
		valueField02 = getTextField(valueField02);
		valueField02.setToolTipText(VALUE_JTF_TTT);

		// Default setting
		inequationBox02.setEnabled(false);
		valueField02.setEnabled(false);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// First inequation
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 0);
		panel.add(getInequationPanel(inequationBox01, valueField01), gbc);

		// Operators
		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 10, 0);
		panel.add(getOperatorPanel(), gbc);

		// Second inequation
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 0, 0);
		panel.add(getInequationPanel(inequationBox02, valueField02), gbc);

		return panel;
	}


	/**
	 * Create a panel that contain a "value" label, a combo box (inequation) and a text field to provide the value.
	 * @param box		the box to use
	 * @param field		the text field to use
	 * @return			the panel
	 */
	private JPanel getInequationPanel (JComboBox box, JTextField field) {
		JPanel panel = new JPanel();

		Dimension dimension = new Dimension(185, 30);

		JLabel valueLabel = new JLabel("value");
		valueLabel.setToolTipText(VALUE_LABEL_TTT);

		// Restore size to former value
		panel.setPreferredSize(dimension);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridy = 0;

		// Label
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(valueLabel, gbc);

		// Combobox
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
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
		model.addElement(InequalityOperators.DIFFERENT);
		model.addElement(InequalityOperators.SUPERIOR);
		model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);

		box = new JComboBox(model);
		Dimension dimension = new Dimension(40, box.getFontMetrics(box.getFont()).getHeight() + 2);
		box.setPreferredSize(dimension);
		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshBoxes();
			}
		});
		return box;
	}


	private JPanel getOperatorPanel () {
		JPanel panel = new JPanel();

		Dimension dimension = new Dimension(195, 30);

		// Restore size to former value
		panel.setPreferredSize(dimension);

		// Initializes radio boxes
		andButton = new JRadioButton("and");
		andButton.setSelected(true);
		andButton.setToolTipText(AND_OPERATOR_TTT);
		andButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshBoxes();
			}
		});
		orButton = new JRadioButton("or");
		orButton.setToolTipText(OR_OPERATOR_TTT);
		orButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refreshBoxes();
			}
		});

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(andButton);
		group.add(orButton);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy = 0;

		// AND radio button
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(andButton, gbc);

		// OR radio button
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 20, 0, 0);
		panel.add(orButton, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * Initializes a text field
	 * @param box text field to initialize
	 */
	private JTextField getTextField (JTextField field) {
		field = new JTextField();
		Dimension dimension = new Dimension(80, field.getFontMetrics(field.getFont()).getHeight() + 3);
		field.setPreferredSize(dimension);
		return field;
	}


	/**
	 * Refreshes the combo boxes according to their content and the "and" / "or" radio buttons.
	 * A superior operator in a box will involve inferior operators in the second box.
	 * The equal operator in the first box with the "and" box selected will disable the second box.
	 * ...
	 */
	private void refreshBoxes () {
		InequalityOperators operator01 = getInequalityOperator(inequationBox01);
		InequalityOperators operator02 = getInequalityOperator(inequationBox02);

		if (operator01 == null){
			inequationBox01.setModel(getFullModel());
			inequationBox02.setEnabled(false);
			valueField02.setEnabled(false);
		} else {
			if (operator01.equals(InequalityOperators.EQUAL)) {
				if (andButton.isSelected()) {
					inequationBox02.setEnabled(false);
					valueField02.setEnabled(false);
				} else {
					inequationBox02.setEnabled(true);
					valueField02.setEnabled(true);
					inequationBox02.setModel(getFullModel());
				}
			} else {
				inequationBox02.setEnabled(true);
				valueField02.setEnabled(true);
				if (operator01 == InequalityOperators.SUPERIOR || operator01 == InequalityOperators.SUPERIOR_OR_EQUAL) {
					inequationBox02.setModel(getInferiorModel());
				} else if (operator01 == InequalityOperators.INFERIOR || operator01 == InequalityOperators.INFERIOR_OR_EQUAL) {
					inequationBox02.setModel(getSuperiorModel());
				} else {
					inequationBox02.setModel(getFullModel());
				}
				if (orButton.isSelected()) {
					((DefaultComboBoxModel)(inequationBox02.getModel())).addElement(InequalityOperators.EQUAL);
				}
			}
			setSelectedItemInBox(inequationBox02, operator02);
		}
	}


	/**
	 * Create and return a combo box model containing the full list of operators.
	 * @return the full model of operator
	 */
	private DefaultComboBoxModel getFullModel () {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(" ");
		model.addElement(InequalityOperators.SUPERIOR);
		model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);
		model.addElement(InequalityOperators.INFERIOR);
		model.addElement(InequalityOperators.INFERIOR_OR_EQUAL);
		model.addElement(InequalityOperators.DIFFERENT);
		model.addElement(InequalityOperators.EQUAL);
		return model;
	}


	/**
	 * Create and return a combo box model containing the list of superior operators and the different one.
	 * @return the full model of operator
	 */
	private DefaultComboBoxModel getSuperiorModel () {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(" ");
		model.addElement(InequalityOperators.SUPERIOR);
		model.addElement(InequalityOperators.SUPERIOR_OR_EQUAL);
		model.addElement(InequalityOperators.DIFFERENT);
		return model;
	}


	/**
	 * Create and return a combo box model containing the full list of inferior operators and the different one.
	 * @return the full model of operator
	 */
	private DefaultComboBoxModel getInferiorModel () {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(" ");
		model.addElement(InequalityOperators.INFERIOR);
		model.addElement(InequalityOperators.INFERIOR_OR_EQUAL);
		model.addElement(InequalityOperators.DIFFERENT);
		return model;
	}


	/**
	 * Checks the validity and gets the selected operator of a combo box
	 * @param comboBox the combo box
	 * @return its operator, null if not valid (disabled box/empty selection
	 */
	private InequalityOperators getInequalityOperator (JComboBox comboBox) {
		InequalityOperators operator = null;

		if (comboBox.isEnabled() && comboBox.getSelectedItem() != null && !comboBox.getSelectedItem().equals(" ")) {
			operator = (InequalityOperators) comboBox.getSelectedItem();
		}

		return operator;
	}


	/**
	 * Set an operator in a combo box, if the operator does not exist, the first value is selected.
	 * @param comboBox the combo box
	 * @param operator the operator
	 */
	private void setSelectedItemInBox (JComboBox comboBox, InequalityOperators operator) {
		boolean found = false;

		if (operator != null) {
			DefaultComboBoxModel model = ((DefaultComboBoxModel)(inequationBox02.getModel()));
			if (model.getIndexOf(operator) >= 0) {
				found = true;
			}
		}

		if (found) {
			comboBox.setSelectedItem(operator);
		} else {
			comboBox.setSelectedIndex(0);
		}
	}


	@Override
	public IDFilterInterface getFilter() {
		NumberIDFilterInterface filter = null;
		if (header instanceof VCFHeaderBasicType && header.getColumnCategory() == VCFColumnName.QUAL) {
			filter = new QualFilter();
		} else {
			filter = new NumberIDFilter();
		}
		filter.setHeaderType(header);

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

		if (andButton.isSelected()) {
			filter.setCumulative(true);
		} else {
			filter.setCumulative(false);
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


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		InequalityOperators inequation01 = null;
		InequalityOperators inequation02 = null;
		Float value01 = null;
		Float value02 = null;

		NumberIDFilterInterface castFilter = (NumberIDFilterInterface) filter;
		inequation01 = castFilter.getInequation01();
		inequation02 = castFilter.getInequation02();
		value01 = castFilter.getValue01();
		value02 = castFilter.getValue02();
		if (castFilter.isCumulative()) {
			andButton.setSelected(true);
		} else {
			orButton.setSelected(true);
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
	public String getErrors() {
		String errors = "";
		if (header == null) {
			errors += "bID selection\n";
		}

		// First value MUST be filed
		if (inequationBox01.isEnabled() && inequationBox01.getSelectedItem().toString().equals(" ")) {
			errors += "First equation operator invalid\n";
		}
		try {
			Float.parseFloat(valueField01.getText());
		} catch (Exception e) {
			errors += "First equation value invalid\n";
		}


		if (inequationBox02.isEnabled() && !inequationBox02.getSelectedItem().toString().equals(" ")) {
			//errors += "Second equation operator invalid\n";
			try {
				Float.parseFloat(valueField02.getText());
			} catch (Exception e) {
				errors += "Second equation value invalid\n";
			}
		}

		return errors;
	}
	
	
	@Override
	public void setEnabled(boolean b) {
		if (panel != null) {
			panel.setEnabled(b);
			inequationBox01.setEnabled(b);
			inequationBox02.setEnabled(b);
			valueField01.setEnabled(b);
			valueField02.setEnabled(b);
			andButton.setEnabled(b);
			orButton.setEnabled(b);
		}
	}
	
	
	@Override
	public boolean isEnabled() {
		return panel.isEnabled();
	}
	
}
