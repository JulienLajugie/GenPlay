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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.GenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.VariationTypeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForVariants implements EditingDialogManagerInterface<VariantData>{

	private final List<EditingPanel<?>> 			editingPanelList;			// List of editing panel
	private final GenomeEditingPanel 				genomeEditingPanel;			// Panel to edit the genomes
	private final VariationTypeEditingPanel 		variationTypeEditingPanel;	// Panel to edit the variations
	private VariantData 							currentData;				// The current stripe data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForVariants}
	 */
	public EditingDialogManagerForVariants () {
		// Genomes editing panel
		genomeEditingPanel = new GenomeEditingPanel(false);

		// Stripes editing panel
		variationTypeEditingPanel = new VariationTypeEditingPanel();
		genomeEditingPanel.addPanelListener(variationTypeEditingPanel);

		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(genomeEditingPanel);
		editingPanelList.add(variationTypeEditingPanel);
	}


	@Override
	public List<EditingPanel<?>> getEditingPanelList() {
		return editingPanelList;
	}


	private void initializePanels () {
		List<String> allGenomeNames = ProjectManager.getInstance().getMultiGenomeProject().getGenomeNames();
		genomeEditingPanel.update(allGenomeNames);
		if (currentData != null) {
			List<String> genomeNames = new ArrayList<String>();
			genomeNames.add(currentData.getGenome());
			genomeEditingPanel.initialize(genomeNames);

			variationTypeEditingPanel.initialize(currentData.getVariationTypeList());
		}
	}


	/**
	 * Resets all the panels
	 */
	private void resetPanels () {
		for (EditingPanel<?> panel: editingPanelList) {
			panel.reset();
		}
	}


	/**
	 * Retrieves all the information from the panel in order to create/set the stripe data object.
	 * If a current stripe data has been defined, it will be set and returned.
	 * If no current stripe data has been defined, a new one will be created.
	 * @return the {@link VariantData}
	 */
	private List<VariantData> retrieveData () {
		List<String> genomeNames = genomeEditingPanel.getSelectedGenomes();
		List<VariantType> variantList = variationTypeEditingPanel.getSelectedVariantTypes();
		List<Color> colorList = variationTypeEditingPanel.getSelectedColors();

		List<VariantData> result = new ArrayList<VariantData>();

		if (currentData != null) {
			currentData.setGenome(genomeNames.get(0));
			currentData.setAlleleType(AlleleType.BOTH);
			currentData.setVariationTypeList(variantList);
			currentData.setColorList(colorList);
			VariantData data = currentData;
			result.add(data);
			if (genomeNames.size() > 1) {
				String message = "You are editing a stripe and more than one genome has been selected.\n";
				message += "Only the first genome will be taken into account:\n";
				message += genomeNames.get(0);
				JOptionPane.showMessageDialog(null, message, "Variant editing message", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			for (String genomeName: genomeNames) {
				result.add(new VariantData(genomeName, AlleleType.BOTH, variantList, colorList));
			}
		}

		return result;
	}


	@Override
	public void setData(VariantData data) {
		currentData = data;
	}


	/**
	 * Enable/Disable the selection of the genome
	 * @param enable true to enable, false to disable
	 */
	public void setEnableSelection (boolean enable) {
		genomeEditingPanel.setEnableSelection(enable);
	}


	@Override
	public List<VariantData> showDialog() {
		initializePanels();
		EditingDialog<VariantData> editingDialog = new EditingDialog<VariantData>(this);
		editingDialog.setTitle("Add a Variant Layer");
		List<VariantData> data = new ArrayList<VariantData>();
		if (editingDialog.showDialog(MainFrame.getInstance().getRootPane()) == EditingDialog.APPROVE_OPTION) {
			data = retrieveData();
		}
		currentData = null;
		resetPanels();
		return data;
	}
}
