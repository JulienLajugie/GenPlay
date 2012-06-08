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

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.EditingDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.FileEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.FilterEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.GenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.IDEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.OperatorEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel.TrackEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.filters.FiltersData;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class EditingDialogManagerForFilters implements EditingDialogManagerInterface<FiltersData>{

	private List<EditingPanel<?>> 		editingPanelList;		// List of editing panel
	private TrackEditingPanel 			trackEditingPanel;		// Panel to edit the tracks
	private FileEditingPanel 			fileEditingPanel;		// Panel to edit the file
	private IDEditingPanel 				IDEditingPanel;			// Panel to edit the ID
	private GenomeEditingPanel			genomeEditingPanel;		// Panel to edit the genomes
	private OperatorEditingPanel 		operatorEditingPanel;	// Panel to edit the operator
	private FilterEditingPanel 			filterEditingPanel;		// Panel to edit the filter

	private FiltersData currentData;						// The current filter data (can be null)


	/**
	 * Constructor of {@link EditingDialogManagerForFilters}
	 */
	public EditingDialogManagerForFilters () {
		// Tracks editing panel
		trackEditingPanel = new TrackEditingPanel();
		
		// File editing panel
		fileEditingPanel = new FileEditingPanel();

		// ID editing panel
		IDEditingPanel = new IDEditingPanel();
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

		if (editingDialog.showDialog(null) == EditingDialog.APPROVE_OPTION) {
			List<FiltersData> data = retrieveData();
			currentData = null;
			resetPanels();
			return data;
		}
		resetPanels();
		return null;
	}


	@Override
	public void setData(FiltersData data) {
		this.currentData = data;
	}


	private void initializePanels () {
		fileEditingPanel.update(ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles());

		if (currentData != null) {
			fileEditingPanel.initialize(currentData.getReader());

			IDEditingPanel.initialize(currentData.getFilter().getHeaderType());

			genomeEditingPanel.initialize(currentData.getVCFFilter().getFilter().getGenomeNames());

			operatorEditingPanel.initialize(currentData.getVCFFilter().getFilter().getOperator());

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
		IDFilterInterface IDFilter = filterEditingPanel.getIDFilter();
		Track<?>[] trackList = trackEditingPanel.getSelectedTracks();

		if (IDFilter.getHeaderType() != null && IDFilter.getHeaderType().getColumnCategory() == VCFColumnName.FORMAT) {
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
			currentData.getVCFFilter().setVCFFile(vcfFile);
			currentData.getVCFFilter().setFilter(IDFilter);
			currentData.setTrackList(trackList);

			data = currentData;
		} else {
			data = new FiltersData(vcfFile, IDFilter, trackList);
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
