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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.addOrEditVariantLayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;

/**
 * Panel to select the variant to display
 * @author Julien Lajugie
 */
class VariantTypeSelectionPanel extends JPanel {

	/**
	 * Data displayed in the VariantTypeSelectionPanel
	 * @author Julien Lajugie
	 */
	private class VariantTypeSelectionModel {

		/** Colors currently selected */
		private final Map<VariantData, Color[]> 	colorSelection;

		/** Selection state of the variant types */
		private final Map<VariantData, boolean[]> 	typeSelection;


		/**
		 * Creates a new instance of {@link VariantTypeSelectionModel}
		 * @param genomes genomes to edit
		 */
		VariantTypeSelectionModel(List<VariantData> genomes) {
			colorSelection = new HashMap<VariantData, Color[]>(genomes.size());
			typeSelection = new HashMap<VariantData, boolean[]>(genomes.size());
			for(VariantData currentGenome: genomes) {
				colorSelection.put(currentGenome, Arrays.copyOf(DEFAULT_VARIANT_COLOR, DEFAULT_VARIANT_COLOR.length));
				typeSelection.put(currentGenome, new boolean[3]);
				for (VariantType selectedType: currentGenome.getVariationTypeList()) {
					int index = currentGenome.getVariationTypeList().indexOf(selectedType);
					setSelected(currentGenome, selectedType, true);
					setColor(currentGenome, selectedType, currentGenome.getColorList().get(index));
				}
			}
		}

		/**
		 * @param genome a genome
		 * @param variantType a type of variant (insertion, deletion, SNP)
		 * @return the color currently selected for the specified variant type of the specified genome
		 */
		Color getColor(VariantData genome, VariantType variantType) {
			int index = getVariantTypeIndex(variantType);
			Color[] colors = colorSelection.get(genome);
			return colors[index];
		}


		/**
		 * @param genome a genome
		 * @param variantType a type of variant (insertion, deletion, SNP)
		 * @return true if the specified variant type of the specified genome is selected, false otherwise
		 */
		boolean isSelected(VariantData genome, VariantType variantType) {
			int index = getVariantTypeIndex(variantType);
			boolean[] selectedTypes = typeSelection.get(genome);
			return selectedTypes[index];
		}


		/**
		 * Sets the colors of the specified variant type of the specified genome
		 * @param genome a genome
		 * @param variantType a type of variant (insertion, deletion, SNP)
		 * @param color a color to set
		 */
		void setColor(VariantData genome, VariantType variantType, Color color) {
			int index = getVariantTypeIndex(variantType);
			Color[] colors = colorSelection.get(genome);
			colors[index] = color;
			if (genome.getVariationTypeList().contains(variantType)) {
				int indexToSet = genome.getVariationTypeList().indexOf(variantType);
				genome.getColorList().set(indexToSet, color);
			}
		}


		/**
		 * Sets the selection state of the specified variant type of the specified genome
		 * @param genome a genome
		 * @param variantType a type of variant (insertion, deletion, SNP)
		 * @param isSelected true if it's selected, false otherwise
		 */
		void setSelected(VariantData genome, VariantType variantType, boolean isSelected) {
			int index = getVariantTypeIndex(variantType);
			boolean[] selectedTypes = typeSelection.get(genome);
			selectedTypes[index] = isSelected;
			if (isSelected) {
				if (!genome.getVariationTypeList().contains(variantType)) {
					genome.getVariationTypeList().add(variantType);
					genome.getColorList().add(getColor(genome, variantType));
				}
			} else {
				if (genome.getVariationTypeList().contains(variantType)) {
					int indexToRemove = genome.getVariationTypeList().indexOf(variantType);
					genome.getVariationTypeList().remove(indexToRemove);
					genome.getColorList().remove(indexToRemove);
				}
			}
		}
	}


	/** Array of default variation colors (Insertion, Deletion, SNPs) */
	private static final Color[] DEFAULT_VARIANT_COLOR = {Colors.LIGHT_BLUE, Colors.GREEN, Colors.RED};

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	/** Property name of the selected variant types*/
	static final String SELECTED_VARIANT_TYPES_PROPERTY_NAME = "selectedVariantTypes";

	/** Index of the SNP color in the color list */
	private static final int SNP_INDEX = 0;

	/** Index of the insertion color in the color list */
	private static final int INSERTION_INDEX = 1;

	/** Index of the deletion color in the color list */
	private static final int DELETION_INDEX = 2;

	/** Selected genome with it's current color and variant type selection */
	private VariantData selectedGenome;

	/** Data displayed in this panel */
	private final VariantTypeSelectionModel model;


	/**
	 * Creates an instance of {@link VariantTypeSelectionPanel}
	 * @param genomes {@link VariantData} to edit
	 */
	VariantTypeSelectionPanel(List<VariantData> genomes) {
		if ((genomes == null) || genomes.isEmpty()) {
			throw new InvalidParameterException("The list of genome to edit cannot be null or empty");
		}
		model = new VariantTypeSelectionModel(genomes);
		setSelectedGenome(genomes.get(0));
	}


	/**
	 * Create a check box to select / unselect a variant type
	 * @param variantType
	 * @return a new {@link JCheckBox}
	 */
	private JCheckBox createCheckBox(final VariantType variantType) {
		final JCheckBox checkBox = new JCheckBox();
		checkBox.setToolTipText("Enable/Disable " + variantType.toString().toLowerCase() + ".");
		checkBox.setSelected(model.isSelected(selectedGenome, variantType));
		checkBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				model.setSelected(selectedGenome, variantType, checkBox.isSelected());
				firePropertyChange(SELECTED_VARIANT_TYPES_PROPERTY_NAME, null, selectedGenome);
			}
		});
		return checkBox;
	}


	/**
	 * Creates an button to select a color for a variant type
	 * @param variantType
	 * @return a clickable {@link JPanel} to select a color
	 */
	private JPanel createColorButton(final VariantType variantType) {
		JPanel colorButton = new JPanel();
		colorButton.setOpaque(true);
		colorButton.setPreferredSize(new Dimension(13, 13));
		colorButton.setToolTipText("Select color for " + variantType.toString().toLowerCase() + ".");
		colorButton.setBackground(model.getColor(selectedGenome, variantType));
		colorButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPanel button = (JPanel) e.getSource();
				Color newColor = GenPlayColorChooser.showDialog(VariantTypeSelectionPanel.this, button.getBackground());
				if (newColor != null) {
					button.setBackground(newColor);
					model.setColor(selectedGenome, variantType, newColor);
				}
			}
		});
		return colorButton;
	}


	/**
	 * Creates the panel to select variant types and there colors
	 */
	private void createVariationSelectionPanel () {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridy = 0;

		VariantType[] types = {VariantType.SNPS, VariantType.INSERTION, VariantType.DELETION};

		for(VariantType vt: types) {
			JLabel variantTypeLabel = new JLabel(vt.toString());
			JCheckBox checkBox = createCheckBox(vt);
			JPanel colorButton = createColorButton(vt);

			// Variation name
			gbc.gridx = 0;
			add(variantTypeLabel, gbc);

			// Selection button
			gbc.gridx = 1;
			add(checkBox, gbc);

			// Color button
			gbc.gridx = 2;
			add(colorButton, gbc);

			gbc.gridy++;
		}
		setBorder(BorderFactory.createTitledBorder("Variant Type(s)"));
	}


	/**
	 * @param variantType
	 * @return the index of the spcified variant type
	 */
	private final int getVariantTypeIndex(VariantType variantType) {
		switch (variantType) {
		case SNPS:
			return SNP_INDEX;
		case INSERTION:
			return INSERTION_INDEX;
		case DELETION:
			return DELETION_INDEX;
		default:
			throw new InvalidParameterException("Invalid variant type: " + variantType.toString());
		}
	}


	/**
	 * Sets the selected genome and refresh the content of the panel
	 * @param selectedGenome
	 */
	public void setSelectedGenome(VariantData selectedGenome) {
		this.selectedGenome = selectedGenome;
		removeAll();
		createVariationSelectionPanel();
		revalidate();
	}
}
