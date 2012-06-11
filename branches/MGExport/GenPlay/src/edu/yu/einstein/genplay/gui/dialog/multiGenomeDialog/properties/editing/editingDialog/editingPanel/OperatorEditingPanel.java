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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.utils.FormatFilterOperatorType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class OperatorEditingPanel extends EditingPanel<FormatFilterOperatorType> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private VCFHeaderType header;
	private Boolean multipleSelection;

	private JRadioButton andOperator;
	private JRadioButton orOperator;
	private JRadioButton sumOperator;
	private JRadioButton meanOperator;


	/**
	 * Constructor of {@link OperatorEditingPanel}
	 */
	public OperatorEditingPanel() {
		super("Operator");
		header = null;
		multipleSelection = null;
	}


	@Override
	protected void initializeContentPanel() {
		// Init the content panel
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;


		// Creates the radio buttons
		andOperator = new JRadioButton(FormatFilterOperatorType.AND.toString());
		orOperator = new JRadioButton(FormatFilterOperatorType.OR.toString());
		sumOperator = new JRadioButton(FormatFilterOperatorType.SUM.toString());
		meanOperator = new JRadioButton(FormatFilterOperatorType.MEAN.toString());
		disableRadios();


		// Group the radio buttons
		ButtonGroup group = new ButtonGroup();
		group.add(andOperator);
		group.add(orOperator);
		group.add(sumOperator);
		group.add(meanOperator);


		// Adds the radio buttons
		gbc.gridy = 0;
		contentPanel.add(andOperator, gbc);
		gbc.gridy++;
		contentPanel.add(orOperator, gbc);
		gbc.gridy++;
		contentPanel.add(sumOperator, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		contentPanel.add(meanOperator, gbc);

	}


	@Override
	public void update(Object object) {
		if (object instanceof VCFHeaderType) {
			header = (VCFHeaderType) object;
		} else if (object instanceof List<?>) {
			if (((List<?>)object).size() > 1) {
				multipleSelection = true;
			} else {
				multipleSelection = false;
			}
		} else {
			header = null;
			multipleSelection = null;
		}

		if (header != null && multipleSelection != null) {
			if (multipleSelection && header.getColumnCategory() == VCFColumnName.FORMAT) {
				VCFHeaderAdvancedType advancedHeader = (VCFHeaderAdvancedType) header;
				if (advancedHeader.getType() == Integer.class || advancedHeader.getType() == Float.class) {
					radiosNumberConfiguration();
				} else {
					radiosStringConfiguration();
				}
			} else {
				disableRadios();
			}
		} else {
			disableRadios();
		}

	}


	/**
	 * Unselect all the radio buttons
	 */
	private void unselectRadios () {
		andOperator.setSelected(false);
		orOperator.setSelected(false);
		sumOperator.setSelected(false);
		meanOperator.setSelected(false);
	}


	/**
	 * Disable all the radio buttons
	 */
	private void disableRadios () {
		andOperator.setEnabled(false);
		orOperator.setEnabled(false);
		sumOperator.setEnabled(false);
		meanOperator.setEnabled(false);
	}


	/**
	 * Enable/Disable the radios for String configuration
	 */
	private void radiosStringConfiguration () {
		andOperator.setEnabled(true);
		orOperator.setEnabled(true);
		sumOperator.setEnabled(false);
		meanOperator.setEnabled(false);
	}


	/**
	 * Enable/Disable the radios for number configuration
	 */
	private void radiosNumberConfiguration () {
		andOperator.setEnabled(true);
		orOperator.setEnabled(true);
		sumOperator.setEnabled(true);
		meanOperator.setEnabled(true);
	}


	/**
	 * @return the selected operator
	 */
	public FormatFilterOperatorType getSelectedOperator () {
		if (andOperator.isEnabled() || orOperator.isEnabled() || sumOperator.isEnabled() || meanOperator.isEnabled()) {
			if (andOperator.isSelected()) {
				return FormatFilterOperatorType.AND;
			} else if (orOperator.isSelected()) {
				return FormatFilterOperatorType.OR;
			} else if (sumOperator.isSelected()) {
				return FormatFilterOperatorType.SUM;
			} else if (meanOperator.isSelected()) {
				return FormatFilterOperatorType.MEAN;
			}
			return null;
		}
		return null;
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (header.getColumnCategory() == VCFColumnName.FORMAT) {
			if (multipleSelection != null && multipleSelection) {
				if (getSelectedOperator() == null) {
					errors += "Operator selection\n";
				}
			}
		}
		return errors;
	}


	@Override
	public void reset() {
		unselectRadios();
		disableRadios();
		element = null;
		header = null;
		multipleSelection = null;
	}


	@Override
	public void initialize(FormatFilterOperatorType element) {
		if (element != null) {
			if (andOperator.getText().equals(element.toString())) {
				andOperator.setSelected(true);
			} else if (orOperator.getText().equals(element.toString())) {
				orOperator.setSelected(true);
			} else if (sumOperator.getText().equals(element.toString())) {
				sumOperator.setSelected(true);
			} else if (meanOperator.getText().equals(element.toString())) {
				meanOperator.setSelected(true);
			}
		}
	}
}
