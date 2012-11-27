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

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.multiGenome.filter.BasicFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.display.DescriptionDisplayPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.AdvancedFilterEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.AdvancedFilterSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.TrackSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.old.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForAdvancedFilters implements EditingDialogManagerInterface<FiltersData>{

	private final List<EditingPanel<?>> 		editingPanelList;			// List of editing panel
	private final AdvancedFilterSelectionPanel	advancedFilterEditingPanel;	// Panel to select the filter
	private final DescriptionDisplayPanel		descriptionEditingPanel;	// Description panel for the filter
	private final AdvancedFilterEditingPanel 	filterEditingPanel;			// Panel to edit the filter
	private final TrackSelectionPanel 			trackEditingPanel;			// Panel to edit the tracks

	private FiltersData currentData;						// The current filter data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForAdvancedFilters}
	 */
	public EditingDialogManagerForAdvancedFilters () {
		// Create the advanced filter panel
		advancedFilterEditingPanel = new AdvancedFilterSelectionPanel();

		// Create the description panel
		descriptionEditingPanel = new DescriptionDisplayPanel();
		advancedFilterEditingPanel.addPanelListener(descriptionEditingPanel);

		// Create the panel to edit the filter
		filterEditingPanel = new AdvancedFilterEditingPanel();
		advancedFilterEditingPanel.addPanelListener(filterEditingPanel);

		// Tracks editing panel
		trackEditingPanel = new TrackSelectionPanel();

		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(trackEditingPanel);
		editingPanelList.add(advancedFilterEditingPanel);
		editingPanelList.add(descriptionEditingPanel);
		editingPanelList.add(filterEditingPanel);
	}


	@Override
	public List<EditingPanel<?>> getEditingPanelList() {
		return editingPanelList;
	}


	@Override
	public List<FiltersData> showDialog() {
		initializePanels();
		EditingDialog<FiltersData> editingDialog = new EditingDialog<FiltersData>(this);
		List<FiltersData> data = null;
		if (editingDialog.showDialog(null) == EditingDialog.APPROVE_OPTION) {
			data = retrieveData();
		}
		currentData = null;
		resetPanels();
		return data;
	}


	@Override
	public void setData(FiltersData data) {
		this.currentData = data;
	}


	private void initializePanels () {
		advancedFilterEditingPanel.update(getAllAdvancedFilter());

		if (currentData != null) {
			advancedFilterEditingPanel.initialize(currentData.getFilter());

			filterEditingPanel.initialize(currentData.getFilter());

			trackEditingPanel.initialize(currentData.getTrackList());
		}
	}


	/**
	 * Retrieves all the information from the panel in order to create/set the filter data object.
	 * If a current filter data has been defined, it will be set and returned.
	 * If no current filter data has been defined, a new one will be created.
	 * @return the {@link FiltersData}
	 */
	private List<FiltersData> retrieveData () {
		FilterInterface filter = filterEditingPanel.getFilter();
		Track<?>[] trackList = trackEditingPanel.getSelectedTracks();

		List<FiltersData> result = new ArrayList<FiltersData>();
		FiltersData data;

		if (currentData != null) {
			currentData.getMGFilter().setFilter(filter);
			currentData.setTrackList(trackList);

			data = currentData;
		} else {
			MGFilter mgFilter = new BasicFilter();
			mgFilter.setFilter(filter);
			data = new FiltersData(mgFilter, trackList);
		}
		result.add(data);

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


	private List<FilterInterface> getAllAdvancedFilter () {
		List<FilterInterface> list = new ArrayList<FilterInterface>();

		return list;
	}
}
