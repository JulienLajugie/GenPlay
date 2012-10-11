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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.GenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.VariationTypeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.TrackSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForStripes implements EditingDialogManagerInterface<StripesData>{

	private final List<EditingPanel<?>> 			editingPanelList;			// List of editing panel
	private final TrackSelectionPanel 				trackEditingPanel;			// Panel to edit the tracks
	private final GenomeEditingPanel 				genomeEditingPanel;			// Panel to edit the genomes
	private final VariationTypeEditingPanel 		variationTypeEditingPanel;	// Panel to edit the variations

	private StripesData currentData;								// The current stripe data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForStripes}
	 */
	public EditingDialogManagerForStripes () {
		// Tracks editing panel
		trackEditingPanel = new TrackSelectionPanel();

		// Genomes editing panel
		genomeEditingPanel = new GenomeEditingPanel(false);

		// Stripes editing panel
		variationTypeEditingPanel = new VariationTypeEditingPanel();
		genomeEditingPanel.addPanelListener(variationTypeEditingPanel);


		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(trackEditingPanel);
		editingPanelList.add(genomeEditingPanel);
		editingPanelList.add(variationTypeEditingPanel);
	}


	@Override
	public List<EditingPanel<?>> getEditingPanelList() {
		return editingPanelList;
	}


	@Override
	public List<StripesData> showDialog() {
		initializePanels();
		EditingDialog<StripesData> editingDialog = new EditingDialog<StripesData>(this);
		List<StripesData> data = null;
		if (editingDialog.showDialog(null) == EditingDialog.APPROVE_OPTION) {
			data = retrieveData();
		}
		currentData = null;
		resetPanels();
		return data;
	}


	@Override
	public void setData(StripesData data) {
		this.currentData = data;
	}


	private void initializePanels () {
		List<String> allGenomeNames = ProjectManager.getInstance().getMultiGenomeProject().getGenomeNames();
		genomeEditingPanel.update(allGenomeNames);
		if (currentData != null) {
			List<String> genomeNames = new ArrayList<String>();
			genomeNames.add(currentData.getGenome());
			genomeEditingPanel.initialize(genomeNames);

			variationTypeEditingPanel.initialize(currentData.getVariationTypeList());

			trackEditingPanel.initialize(currentData.getTrackList());
		}
	}


	/**
	 * Retrieves all the information from the panel in order to create/set the stripe data object.
	 * If a current stripe data has been defined, it will be set and returned.
	 * If no current stripe data has been defined, a new one will be created.
	 * @return the {@link StripesData}
	 */
	private List<StripesData> retrieveData () {
		List<String> genomeNames = genomeEditingPanel.getSelectedGenomes();
		AlleleType alleleType = variationTypeEditingPanel.getSelectedAlleleType();
		List<VariantType> variantList = variationTypeEditingPanel.getSelectedVariantTypes();
		List<Color> colorList = variationTypeEditingPanel.getSelectedColors();
		Track<?>[] trackList = trackEditingPanel.getSelectedTracks();

		List<StripesData> result = new ArrayList<StripesData>();

		if (currentData != null) {
			currentData.setGenome(genomeNames.get(0));
			currentData.setAlleleType(alleleType);
			currentData.setVariationTypeList(variantList);
			currentData.setColorList(colorList);
			currentData.setTrackList(trackList);
			StripesData data = currentData;
			result.add(data);
			if (genomeNames.size() > 1) {
				String message = "You are editing a stripe and more than one genome has been selected.\n";
				message += "Only the first genome will be taken into account:\n";
				message += genomeNames.get(0);
				JOptionPane.showMessageDialog(null, message, "Stripe editing message", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			for (String genomeName: genomeNames) {
				result.add(new StripesData(genomeName, alleleType, variantList, colorList, trackList));
			}
		}

		/*for (String genomeName: genomeNames) {
			StripesData data;
			if (currentData != null) {
				currentData.setGenome(genomeName);
				currentData.setAlleleType(alleleType);
				currentData.setVariationTypeList(variantList);
				currentData.setColorList(colorList);
				currentData.setTrackList(trackList);
				data = currentData;
			} else {
				data = new StripesData(genomeName, alleleType, variantList, colorList, trackList);
			}
			result.add(data);
		}*/

		return result;
	}


	/**
	 * Resets all the panels
	 */
	private void resetPanels () {
		for (EditingPanel<?> panel: editingPanelList) {
			panel.reset();
		}
	}

}
