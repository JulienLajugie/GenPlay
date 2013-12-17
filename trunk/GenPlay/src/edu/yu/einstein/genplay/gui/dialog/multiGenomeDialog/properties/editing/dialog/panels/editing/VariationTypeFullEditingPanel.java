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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VariationTypeFullEditingPanel extends EditingPanel<List<VariantType>> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	private Color[]				defaultVariationColor;	// Array of default variation colors (Insertion, Deletion, SNPs)
	private JComboBox 			jcbAllele;			// The combo box for the allele selection
	private List<VariantType> 	variationName;		// Variation names list
	private List<JCheckBox> 	selectedVariation;	// Variation check box selection list
	private List<JButton> 		variationColor;		// Variation color list


	/**
	 * Constructor of {@link VariationTypeFullEditingPanel}
	 */
	public VariationTypeFullEditingPanel() {
		super("Variant(s)");
	}


	@Override
	protected void initializeContentPanel() {
		JLabel jlAllele = new JLabel("Allele");
		JLabel jlVariation = new JLabel("Variations");

		defaultVariationColor = new Color[3];
		defaultVariationColor[0] = Colors.GREEN;
		defaultVariationColor[1] = Colors.RED;
		defaultVariationColor[2] = Colors.LIGHT_BLUE;

		// Init the content panel
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Allele selection title
		gbc.gridy++;
		gbc.insets = new Insets(10, 10, 10, 0);
		contentPanel.add(jlAllele, gbc);

		// Allele selection box
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		contentPanel.add(getAlleleBox(), gbc);

		// Variation selection title
		gbc.gridy++;
		gbc.insets = new Insets(20, 10, 10, 0);
		contentPanel.add(jlVariation, gbc);

		// Variation selection panel
		gbc.gridy++;
		gbc.insets = new Insets(5, 20, 0, 0);
		gbc.weighty = 1;
		contentPanel.add(getVariationSelectionPanel(), gbc);
	}


	/**
	 * Creates a combo box containing in order to choose the allele
	 * @return the combo box
	 */
	private JComboBox getAlleleBox () {
		// Creates the array containing the different alleles
		Object[] alleles = new Object[]{AlleleType.BOTH, AlleleType.ALLELE01, AlleleType.ALLELE02};

		// Creates the box
		jcbAllele = new JComboBox(alleles);
		int height = jcbAllele.getFontMetrics(jcbAllele.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(100, height);
		jcbAllele.setPreferredSize(dimension);
		jcbAllele.setMinimumSize(dimension);
		jcbAllele.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox)e.getSource();
				String element = box.getSelectedItem().toString();
				box.setToolTipText(element);
			}
		});
		jcbAllele.setToolTipText("Select an allele.");
		return jcbAllele;
	}


	/**
	 * Creates the variation selection panel.
	 * It contains all variation with a check box to select it for display and a button to choose its color.
	 * @return the variation selection panel
	 */
	private JPanel getVariationSelectionPanel () {
		// Initialize lists
		variationName = new ArrayList<VariantType>();
		selectedVariation = new ArrayList<JCheckBox>();
		variationColor = new ArrayList<JButton>();

		// Fill the list that contains the variation names
		variationName.add(VariantType.INSERTION);	// INSERTION will be on index 0
		variationName.add(VariantType.DELETION);	// DELETION will be on index 1
		variationName.add(VariantType.SNPS);		// SNPS will be on index 2

		// Fill the list that contains the checkbox for selecting variation
		for (int i = 0; i < 3; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setEnabled(false);
			checkBox.setBorder(null);
			checkBox.setMargin(new Insets(0, 0, 0, 0));
			checkBox.setToolTipText("Enable/Disable " + variationName.get(i).toString().toLowerCase() + ".");
			selectedVariation.add(checkBox);
		}

		// Fill the list that contains the button for selecting variation color
		for (int i = 0; i < 3; i++) {
			JButton colorButton = new JButton();
			Dimension colorDim = new Dimension(13, 13);
			colorButton.setPreferredSize(colorDim);
			colorButton.setBorder(null);
			colorButton.setToolTipText("Select color for " + variationName.get(i).toString().toLowerCase() + ".");
			colorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JButton button = (JButton) arg0.getSource();
					Color newColor = GenPlayColorChooser.showDialog(getCurrentInstance(), button.getBackground());
					if (newColor != null) {
						button.setBackground(newColor);
					}
				}
			});
			// Initialize button color
			colorButton.setBackground(defaultVariationColor[i]);

			// Add the button to the list
			variationColor.add(colorButton);
		}


		// Create the panel
		JPanel panel = new JPanel();
		int height = getFontMetrics(getFont()).getHeight() * 3;
		Dimension dimension = new Dimension(150, height);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Insets
		Insets nameInset = new Insets(0, 0, 0, 0);
		Insets selectInset = new Insets(0, 15, 0, 0);
		Insets colorInset = new Insets(0, 10, 0, 0);

		// Add components to the panel
		for (int i = 0; i < 3; i++) {
			if (i == 2) {
				gbc.weighty = 1;
			}

			// Variation name
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.insets = nameInset;
			panel.add(new JLabel(variationName.get(i).toString()), gbc);

			// Selection button
			gbc.gridx = 1;
			gbc.gridy = i;
			gbc.insets = selectInset;
			panel.add(selectedVariation.get(i), gbc);

			// Color button
			gbc.gridx = 2;
			gbc.gridy = i;
			gbc.insets = colorInset;
			panel.add(variationColor.get(i), gbc);
		}

		// Return the panel
		return panel;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void update(Object object) {
		List<String> genomeNames = null;

		if (object instanceof List<?>) {
			if ((((List<?>)object).size() > 0) && (((List<?>)object).get(0) instanceof String)) {
				genomeNames = (List<String>) object;
			}
		}

		if (genomeNames != null) {
			for (int i = 0; i < variationName.size(); i++) {
				VariantType type = variationName.get(i);
				boolean exist = true;
				int j = 0;
				while (exist && (j < genomeNames.size())) {
					String genomeName = genomeNames.get(j);
					List<VCFFile> readers = ProjectManager.getInstance().getMultiGenomeProject().getVCFFiles(genomeName, type);
					if (readers.size() == 0) {
						exist = false;
					}
					j++;
				}
				if (exist) {
					selectedVariation.get(i).setEnabled(true);
				} else {
					selectedVariation.get(i).setEnabled(false);
					selectedVariation.get(i).setSelected(false);
				}
			}
		}
	}


	/**
	 * @return the stripes editing panel instance
	 */
	protected Component getCurrentInstance() {
		return this;
	}


	/**
	 * @return the selected {@link AlleleType}
	 */
	public AlleleType getSelectedAlleleType () {
		return (AlleleType) jcbAllele.getSelectedItem();
	}


	/**
	 * @return the list of selected variant types
	 */
	public List<VariantType> getSelectedVariantTypes () {
		List<VariantType> list = new ArrayList<VariantType>();
		for (int i = 0; i < selectedVariation.size(); i++) {
			if (selectedVariation.get(i).isSelected()) {
				list.add(variationName.get(i));
			}
		}
		return list;
	}


	/**
	 * @return the list of color according to the selected variant types
	 */
	public List<Color> getSelectedColors () {
		List<Color> list = new ArrayList<Color>();
		for (int i = 0; i < selectedVariation.size(); i++) {
			if (selectedVariation.get(i).isSelected()) {
				list.add(variationColor.get(i).getBackground());
			}
		}
		return list;
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (getSelectedVariantTypes().size() == 0) {
			errors += "Variation type selection\n";
		}
		return errors;
	}


	@Override
	public void reset() {
		for (JCheckBox box: selectedVariation) {
			box.setSelected(false);
			box.setEnabled(false);
		}
	}


	@Override
	public void initialize(List<VariantType> element) {
		if (element != null) {
			for (VariantType variantType: element) {
				int index = variationName.indexOf(variantType);
				selectedVariation.get(index).setSelected(true);
				selectedVariation.get(index).setEnabled(true);
			}
		}
	}
}
