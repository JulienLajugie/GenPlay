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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingDialogManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.GenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.TrackEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.VariationTypeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForStripes implements EditingDialogManagerInterface<StripesData>{

	private List<EditingPanel<?>> 			editingPanelList;			// List of editing panel
	private GenomeEditingPanel 				genomeEditingPanel;			// Panel to edit the genomes
	private VariationTypeEditingPanel 		variationTypeEditingPanel;	// Panel to edit the variations
	private TrackEditingPanel 				trackEditingPanel;			// Panel to edit the tracks

	private StripesData currentData;								// The current stripe data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForStripes}
	 */
	public EditingDialogManagerForStripes () {
		// Genomes editing panel
		genomeEditingPanel = new GenomeEditingPanel(false);

		// Stripes editing panel
		variationTypeEditingPanel = new VariationTypeEditingPanel();
		genomeEditingPanel.addPanelListener(variationTypeEditingPanel);

		// Tracks editing panel
		trackEditingPanel = new TrackEditingPanel();

		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(genomeEditingPanel);
		editingPanelList.add(variationTypeEditingPanel);
		editingPanelList.add(trackEditingPanel);
	}


	@Override
	public List<EditingPanel<?>> getEditingPanelList() {
		return editingPanelList;
	}


	@Override
	public List<StripesData> showDialog() {
		initializePanels();

		EditingDialog<StripesData> editingDialog = new EditingDialog<StripesData>(this);

		if (editingDialog.showDialog(null) == EditingDialog.APPROVE_OPTION) {
			List<StripesData> data = retrieveData();
			currentData = null;
			resetPanels();
			return data;
		}
		return null;
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

		for (String genomeName: genomeNames) {
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
		}

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
