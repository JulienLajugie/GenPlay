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

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.FilterEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.GenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.editing.OperatorEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.FileSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.IDFilterSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection.TrackSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.old.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForFilters implements EditingDialogManagerInterface<FiltersData>{

	private final List<EditingPanel<?>> 		editingPanelList;		// List of editing panel
	private final TrackSelectionPanel 			trackEditingPanel;		// Panel to edit the tracks
	private final FileSelectionPanel 			fileEditingPanel;		// Panel to edit the file
	private final IDFilterSelectionPanel 		IDEditingPanel;			// Panel to edit the ID
	private final GenomeEditingPanel			genomeEditingPanel;		// Panel to edit the genomes
	private final OperatorEditingPanel 			operatorEditingPanel;	// Panel to edit the operator
	private final FilterEditingPanel 			filterEditingPanel;		// Panel to edit the filter

	private FiltersData currentData;						// The current filter data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForFilters}
	 */
	public EditingDialogManagerForFilters () {
		// Tracks editing panel
		trackEditingPanel = new TrackSelectionPanel();

		// File editing panel
		fileEditingPanel = new FileSelectionPanel();

		// ID editing panel
		IDEditingPanel = new IDFilterSelectionPanel();
		fileEditingPanel.addPanelListener(IDEditingPanel);

		// Genomes editing panel
		genomeEditingPanel = new GenomeEditingPanel(true);
		fileEditingPanel.addPanelListener(genomeEditingPanel);
		IDEditingPanel.addPanelListener(genomeEditingPanel);

		// Operator editing panel
		operatorEditingPanel = new OperatorEditingPanel();
		fileEditingPanel.addPanelListener(operatorEditingPanel);
		IDEditingPanel.addPanelListener(operatorEditingPanel);
		genomeEditingPanel.addPanelListener(operatorEditingPanel);

		// Filter editing panel
		filterEditingPanel = new FilterEditingPanel();
		IDEditingPanel.addPanelListener(filterEditingPanel);


		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(trackEditingPanel);
		editingPanelList.add(fileEditingPanel);
		editingPanelList.add(IDEditingPanel);
		editingPanelList.add(genomeEditingPanel);
		editingPanelList.add(operatorEditingPanel);
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
		fileEditingPanel.update(ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles());

		if (currentData != null) {
			fileEditingPanel.initialize(currentData.getReader());

			IDEditingPanel.initialize(((IDFilterInterface) currentData.getFilter()).getHeaderType());

			genomeEditingPanel.initialize(((IDFilterInterface) currentData.getMGFilter().getFilter()).getGenomeNames());

			operatorEditingPanel.initialize(((IDFilterInterface) currentData.getMGFilter().getFilter()).getOperator());

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
		VCFFile vcfFile = fileEditingPanel.getSelectedVCFFile();
		IDFilterInterface IDFilter = (IDFilterInterface) filterEditingPanel.getFilter();
		Track<?>[] trackList = trackEditingPanel.getSelectedTracks();

		if ((IDFilter.getHeaderType() != null) && (IDFilter.getHeaderType().getColumnCategory() == VCFColumnName.FORMAT)) {
			List<String> genomeNames = genomeEditingPanel.getSelectedGenomes();
			FormatFilterOperatorType operator = operatorEditingPanel.getSelectedOperator();
			IDFilter.setGenomeNames(genomeNames);
			IDFilter.setOperator(operator);
		} else {
			IDFilter.setGenomeNames(null);
			IDFilter.setOperator(null);
		}

		List<FiltersData> result = new ArrayList<FiltersData>();
		FiltersData data;

		if (currentData != null) {
			((VCFFilter) currentData.getMGFilter()).setVCFFile(vcfFile);
			currentData.getMGFilter().setFilter(IDFilter);
			currentData.setTrackList(trackList);

			data = currentData;
		} else {
			MGFilter filter = new VCFFilter(IDFilter, vcfFile);
			data = new FiltersData(filter, trackList);
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
}
