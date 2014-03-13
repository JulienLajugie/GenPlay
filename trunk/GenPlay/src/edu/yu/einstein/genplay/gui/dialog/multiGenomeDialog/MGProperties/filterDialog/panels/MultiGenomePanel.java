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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;

/**
 * Panel to select genomes and operator. This panel is used when the VCF field of the filter is FORMAT.
 * The FORMAT field has different values for each genomes of the VCF file and the user needs to select
 * on which genomes he wants to apply the filter.
 * @author Julien Lajugie
 */
public class MultiGenomePanel extends JPanel implements ItemListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	/** Name of the property that is true when the selection in this panel is valid, false otherwise */
	public static final String IS_SELECTION_VALID_PROPERTY_NAME = "Is selection valid";

	private final JScrollPane	genomeScroll;		// scroll pane with the check boxes
	private final JComboBox 	operatorCombobox;	// combo box to choose an operator
	private JCheckBox[] 		genomeBoxes;		// check boxes to select genomes
	private boolean 			isSelectionValid;	// true if the current selection is valid


	/**
	 * Creates an instance of {@link MultiGenomePanel}
	 * @param genomeNames name of the genomes displayed in the panel
	 * @param selectedHeader VCF field currently being filtered
	 * @param selectedGenomes list of the genomes preselected, can be null
	 * @param selectedOperator operator preselected, can be null
	 */
	public MultiGenomePanel(List<String> genomeNames, VCFHeaderType selectedHeader, List<String> selectedGenomes, FormatFilterOperatorType selectedOperator) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Select Genome(s)"));

		// create the components
		JLabel jlGenome = new JLabel("Apply FORMAT filter on:");

		genomeScroll = new JScrollPane();
		setGenomeList(genomeNames, selectedGenomes);

		JLabel jlOperator = new JLabel("Using operator:");

		operatorCombobox = new JComboBox();
		operatorCombobox.setPrototypeDisplayValue("Mean");
		setHeaderType(selectedHeader);
		if (selectedOperator != null) {
			operatorCombobox.setSelectedItem(selectedOperator);
		}

		checkIfGenomeComboEnabled();

		// add the components
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 1;

		add(jlGenome, gbc);

		gbc.gridy = 1;
		add(genomeScroll, gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(20, 0, 0, 0);
		add(jlOperator, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 3;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weighty = 1;
		add(operatorCombobox, gbc);
	}


	/**
	 * Enables the operator combo box if more than one genome is selected. \
	 * Disables it otherwise
	 */
	private void checkIfGenomeComboEnabled() {
		int selectedGenomeCount = 0;
		for (JCheckBox jcb: genomeBoxes) {
			if (jcb.isSelected()) {
				selectedGenomeCount++;
			}
		}
		operatorCombobox.setEnabled(selectedGenomeCount > 1);
	}


	/**
	 * Checks if the current selection made by the user is valid and update the
	 * {@link #isSelectionValid} property. Fire a property change event if the
	 * property changes.
	 */
	private final void checkIfSelectionIsValid() {
		boolean newIsSelectionValid = false;
		for (int i = 0; (i < genomeBoxes.length) && !newIsSelectionValid; i++) {
			if (genomeBoxes[i].isSelected()) {
				newIsSelectionValid = true;
			}
		}
		if (newIsSelectionValid != isSelectionValid) {
			boolean oldIsSelectionValid = isSelectionValid;
			isSelectionValid = newIsSelectionValid;
			firePropertyChange(IS_SELECTION_VALID_PROPERTY_NAME, oldIsSelectionValid, newIsSelectionValid);
		}
	}


	/**
	 * The list of the selected genomes
	 */
	public List<String> getSelectedGenomes() {
		List<String> resultList = new ArrayList<String>();
		for (JCheckBox jcb: genomeBoxes) {
			if (jcb.isSelected()) {
				resultList.add(jcb.getText());
			}
		}
		return resultList;
	}


	/**
	 * @return the selected operator
	 */
	public FormatFilterOperatorType getSelectedOperator() {
		return (FormatFilterOperatorType) operatorCombobox.getSelectedItem();
	}


	/**
	 * @return true if the current selection is valid, false otherwise
	 */
	public boolean isSelectionValid() {
		return isSelectionValid;
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		checkIfSelectionIsValid();
		checkIfGenomeComboEnabled();
	}


	/**
	 * Sets the list of genomes displayed in the panel
	 * @param genomeNames list of genome names
	 */
	public void setGenomeList(List<String> genomeNames) {
		setGenomeList(genomeNames, null);
	}


	/**
	 * Sets the list of genomes displayed in the panel.
	 * The specified genomes are selected the others are unselected.
	 * @param genomeNames list of genome names
	 * @param selectedGenomes this genomes will be selected, other genomes will be unselected. Everything will be selected if null
	 */
	private void setGenomeList(List<String> genomeNames, List<String> selectedGenomes) {
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;

		genomeBoxes = new JCheckBox[genomeNames.size()];
		for (int i = 0; i < genomeNames.size(); i++) {
			JCheckBox checkBox = new JCheckBox(genomeNames.get(i));
			if (selectedGenomes != null) {
				checkBox.setSelected(selectedGenomes.contains(genomeNames.get(i)));
			} else {
				checkBox.setSelected(true);
			}
			checkBox.addItemListener(this);
			genomeBoxes[i] = checkBox;
			content.add(genomeBoxes[i], gbc);
			gbc.gridy++;
		}
		genomeScroll.setViewportView(content);
		if (genomeNames.size() <= 1) {
			setVisible(false);
		}
		checkIfSelectionIsValid();
	}


	/**
	 * Sets the VCF filed that is selected. The content of this panel will
	 * adapt to the type of the field
	 * @param vcfHeaderType a VCF field
	 */
	public void setHeaderType(VCFHeaderType vcfHeaderType) {
		if ((genomeBoxes.length <= 1) || (vcfHeaderType.getColumnCategory() != VCFColumnName.FORMAT)) {
			setVisible(false);
		} else {
			setVisible(true);
			operatorCombobox.removeAllItems();
			if (vcfHeaderType instanceof VCFHeaderAdvancedType) {
				VCFHeaderAdvancedType advancedHeader = (VCFHeaderAdvancedType) vcfHeaderType;
				operatorCombobox.addItem(FormatFilterOperatorType.AND);
				operatorCombobox.addItem(FormatFilterOperatorType.OR);
				if ((advancedHeader.getType() == Integer.class) || (advancedHeader.getType() == Float.class)) {
					operatorCombobox.addItem(FormatFilterOperatorType.MEAN);
					operatorCombobox.addItem(FormatFilterOperatorType.SUM);
				}
			}
		}
	}
}
