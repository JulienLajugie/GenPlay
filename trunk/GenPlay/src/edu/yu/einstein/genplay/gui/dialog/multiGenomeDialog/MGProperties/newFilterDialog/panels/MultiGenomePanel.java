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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;

/**
 * @author Julien Lajugie
 */
public class MultiGenomePanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	private final JCheckBox[] boxes;
	private final JComboBox operatorCombobox;


	public MultiGenomePanel(List<String> genomeNames, VCFHeaderType selectedHeader, List<String> selectedGenomes, FormatFilterOperatorType selectedOperator) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Select Genome(s)"));

		boxes = new JCheckBox[genomeNames.size()];

		operatorCombobox = new JComboBox();
		operatorCombobox.setPrototypeDisplayValue("Mean");
		setHeaderType(selectedHeader);
		if (selectedOperator != null) {
			operatorCombobox.setSelectedItem(selectedOperator);
		}

		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;

		for (int i = 0; i < genomeNames.size(); i++) {
			JCheckBox checkBox = new JCheckBox(genomeNames.get(i));
			if (selectedGenomes != null) {
				checkBox.setSelected(selectedGenomes.contains(genomeNames.get(i)));
			}
			boxes[i] = checkBox;
			content.add(boxes[i], gbc);
			gbc.gridy++;
		}

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane jsp = new JScrollPane(content);
		jsp.setBorder(null);
		add(jsp, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 1;


		add(operatorCombobox, gbc);

		repaint();
	}


	public void setHeaderType(VCFHeaderType vcfHeaderType) {
		if (vcfHeaderType.getColumnCategory() != VCFColumnName.FORMAT) {
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
		revalidate();
	}
}
