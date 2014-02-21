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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.addOrEditVariantLayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.VariantLayerDisplaySettings;


/**
 * 
 * @author Julien Lajugie
 */
public class AddOrEditVariantLayerDialog extends JDialog {

	/** generated serial ID */
	private static final long serialVersionUID = 6669098909120555543L;

	/** Return value when OK has been clicked */
	public static final int APPROVE_OPTION = 0;


	/** Return value when Cancel has been clicked */
	public static final int CANCEL_OPTION = 1;

	/** Selected genome */
	private static VariantLayerDisplaySettings selectedGenome;


	/**
	 * Shows the dialog to select variant types and colors
	 * @param parentComponent parent component of the dialog to show
	 * @return the {@link VariantLayerDisplaySettings} to add. Null if canceled
	 */
	public static VariantLayerDisplaySettings showAddDialog(Component parentComponent) {
		AddOrEditVariantLayerDialog addOrEditVariantLayerDialog = new AddOrEditVariantLayerDialog(null, true);
		addOrEditVariantLayerDialog.setLocationRelativeTo(parentComponent);
		addOrEditVariantLayerDialog.setTitle("Add Variant Layer");
		addOrEditVariantLayerDialog.setVisible(true);
		if (addOrEditVariantLayerDialog.approved == APPROVE_OPTION) {
			return selectedGenome;
		} else {
			return null;
		}
	}


	/**
	 * Show the dialog to edit variant types and colors
	 * @param initialSelection initial selection shown on the dialog.
	 * @return the edited {@link VariantLayerDisplaySettings}. Null if canceled
	 */
	public static VariantLayerDisplaySettings showEditDialog(Component parentComponent, VariantLayerDisplaySettings initialSelection) {
		List<VariantLayerDisplaySettings> variantData = new ArrayList<VariantLayerDisplaySettings>();
		// we copy the input data because it will be directly modified
		variantData.add(new VariantLayerDisplaySettings(initialSelection.getGenome(),
				initialSelection.getAlleleType(),
				initialSelection.getVariationTypeList(),
				initialSelection.getColorList()));
		AddOrEditVariantLayerDialog addOrEditVariantLayerDialog = new AddOrEditVariantLayerDialog(variantData, false);
		addOrEditVariantLayerDialog.setLocationRelativeTo(parentComponent);
		addOrEditVariantLayerDialog.setTitle("Edit Variant Layer");
		addOrEditVariantLayerDialog.setVisible(true);
		if (addOrEditVariantLayerDialog.approved == APPROVE_OPTION) {
			return selectedGenome;
		} else {
			return null;
		}
	}


	/** Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not */
	private int approved = CANCEL_OPTION;

	/** Ok button */
	private final JButton jbOk;


	/**
	 * Creates an instance of {@link AddOrEditVariantLayerDialog}
	 * @param data data to display in the dialog
	 */
	public AddOrEditVariantLayerDialog (List<VariantLayerDisplaySettings> variantList, boolean isGenomeSelectable) {
		if(variantList == null) {
			variantList = createNewVariantDataList();
		}

		selectedGenome = variantList.get(0);

		// variant selection panel
		final VariantTypeSelectionPanel variantTypeSelectionPanel = new VariantTypeSelectionPanel(variantList);
		variantTypeSelectionPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(VariantTypeSelectionPanel.SELECTED_VARIANT_TYPES_PROPERTY_NAME)) {
					refreshOkButtonState();
				}
			}
		});

		// Genomes selection panel
		GenomeSelectionPanel genomeSelectionPanel = new GenomeSelectionPanel(variantList);
		genomeSelectionPanel.setEnabled(isGenomeSelectable);
		genomeSelectionPanel.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(GenomeSelectionPanel.SELECTED_GENOME_PROPERTY_NAME)) {
					selectedGenome = (VariantLayerDisplaySettings) evt.getNewValue();
					variantTypeSelectionPanel.setSelectedGenome(selectedGenome);
					refreshOkButtonState();
				}
			}
		});

		// ok button
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});
		refreshOkButtonState();
		getRootPane().setDefaultButton(jbOk);

		// cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.PAGE_START;
		add(genomeSelectionPanel, gbc);

		variantTypeSelectionPanel.setPreferredSize(genomeSelectionPanel.getPreferredSize());
		gbc.gridx = 1;
		add(variantTypeSelectionPanel, gbc);

		jbOk.setPreferredSize(jbCancel.getPreferredSize());
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(jbOk, gbc);

		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 1;
		add(jbCancel, gbc);

		// add components
		setModal(true);
		setResizable(false);
		pack();
	}


	/**
	 * @return a list with VariantData for each of the genomes of the multigenome project
	 */
	private List<VariantLayerDisplaySettings> createNewVariantDataList() {
		List<VariantLayerDisplaySettings> data = new ArrayList<VariantLayerDisplaySettings>();
		List<String> allGenomeNames = ProjectManager.getInstance().getMultiGenomeProject().getGenomeNames();
		for (String genomeName: allGenomeNames) {
			VariantLayerDisplaySettings variant = new VariantLayerDisplaySettings(genomeName, AlleleType.BOTH, new ArrayList<VariantType>(), new ArrayList<Color>());
			data.add(variant);
		}
		return data;
	}


	/**
	 * Enables the ok button if a genome and at least one variant type are selected. Disables ok button otherwise
	 */
	private void refreshOkButtonState() {
		jbOk.setEnabled((selectedGenome != null) && (selectedGenome.getVariationTypeList() != null) && !selectedGenome.getVariationTypeList().isEmpty());
	}
}
