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
package edu.yu.einstein.genplay.core.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFiltersManager {

	private static	MGFiltersManager	instance = null;		// unique instance of the singleton

	private List<VCFFilter> previousFilterList;						// List of previous filters
	private List<VCFFilter> currentFilterList;						// List of new filters
	private List<VCFFilter> filterListToUpdate;						// List of filters to update
	private boolean chromosomeHasChanged;

	private Map<VCFFile, List<VCFFilter>> filterMap;
	private Map<VCFFile, List<Map<String, Object>>> resultMap;


	/**
	 * Constructor of {@link MGFiltersManager}
	 */
	private MGFiltersManager () {
		reset();
	}


	/**
	 * @return an instance of a {@link MGFiltersManager}. 
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static MGFiltersManager getInstance() {
		if (instance == null) {
			synchronized(MGFiltersManager.class) {
				if (instance == null) {
					instance = new MGFiltersManager();
				}
			}
		}
		return instance;
	}


	/**
	 * Reset to initial value all class attributes.
	 */
	public void reset () {
		chromosomeHasChanged = false;
		previousFilterList = null;
		filterListToUpdate = null;
		filterMap = null;
		resultMap = null;
		currentFilterList = MGDisplaySettings.getInstance().getFilterSettings().getAllVCFFilters();
	}


	/**
	 * Initializes the list of filters to delete/create/update.
	 */
	public void initializeFilterLists () {
		currentFilterList = MGDisplaySettings.getInstance().getFilterSettings().getAllVCFFilters();

		// Create the list of filters to update
		if (chromosomeHasChanged) {
			filterListToUpdate = currentFilterList;
		} else {
			filterListToUpdate = new ArrayList<VCFFilter>();

			// Initializes list of filters to create and to update
			for (VCFFilter newFilter: currentFilterList) {
				boolean found = false;
				int index = 0;
				while (!found && index < previousFilterList.size()) {
					if (newFilter.equals(previousFilterList.get(index))) {
						found = true;
					}
					index++;
				}

				if (!found) {
					filterListToUpdate.add(newFilter);
				}
			}
		}

		// Gather filters by readers
		filterMap = new HashMap<VCFFile, List<VCFFilter>>();
		for (VCFFilter filter: filterListToUpdate) {
			if (!filterMap.containsKey(filter.getVCFFile())) {
				filterMap.put(filter.getVCFFile(), new ArrayList<VCFFilter>());
			}
			filterMap.get(filter.getVCFFile()).add(filter);
		}
	}

	/*private void printList (List<VCFFilter> list) {
		if (list == null) {
			System.out.println("The list is null");
		} else {
			if (list.size() == 0) {
				System.out.println("The list is empty");
			} else {
				for (VCFFilter filter: list) {
					System.out.println(filter.hashCode() + ": " + filter.getFilter().toStringForDisplay());
				}
			}
		}
	}*/


	/**
	 * Retrieves the VCF lines from each VCF files.
	 * It retrieves only the required columns.
	 */
	public void retrieveDataFromVCF () {
		if (filterMap != null && filterMap.size() > 0) {
			resultMap = new HashMap<VCFFile, List<Map<String,Object>>>();
			Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();

			for (VCFFile vcfFile: filterMap.keySet()) {

				List<String> columnNameList = new ArrayList<String>();
				for (VCFFilter filter: filterMap.get(vcfFile)) {
					VCFColumnName columnName = filter.getFilter().getColumnName();
					columnNameList.add(columnName.toString());
					if (columnName == VCFColumnName.FORMAT) {
						List<String> genomeNames = filter.getFilter().getGenomeNames();
						for (String genomeName: genomeNames) {
							String genomeRawName = FormattedMultiGenomeName.getRawName(genomeName);
							if (!columnNameList.contains(genomeRawName)) {
								columnNameList.add(genomeRawName);
							}
						}
					}
				}

				List<Map<String, Object>> results = null;
				try {
					results = vcfFile.getReader().query(chromosome.getName(), 0, chromosome.getLength(), columnNameList);
				} catch (IOException e) {
					e.printStackTrace();
				}

				resultMap.put(vcfFile, results);
			}
		}
	}


	/**
	 * @return true if filters must be created, false otherwise
	 */
	public boolean hasToBeRun () {
		if (filterListToUpdate != null && filterListToUpdate.size() > 0) {
			return true;
		}
		return false;
	}


	/**
	 * @param filter the filter
	 * @return the result of the query on the VCF file related to the filter
	 */
	public List<Map<String, Object>> getResultOfFilter (VCFFilter filter) {
		return resultMap.get(filter.getVCFFile());
	}


	/**
	 * @param previousFilterList the previousFilterList to set
	 */
	public void setPreviousFilterList(List<VCFFilter> previousFilterList) {
		this.previousFilterList = previousFilterList;
	}


	/**
	 * @return the chromosomeHasChanged
	 */
	public boolean isChromosomeHasChanged() {
		return chromosomeHasChanged;
	}


	/**
	 * @param chromosomeHasChanged the chromosomeHasChanged to set
	 */
	public void setChromosomeHasChanged(boolean chromosomeHasChanged) {
		this.chromosomeHasChanged = chromosomeHasChanged;
	}


	/**
	 * @return the filterListToUpdate
	 */
	public List<VCFFilter> getFilterListToUpdate() {
		return filterListToUpdate;
	}

}
