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
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
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
	
	private Map<VCFReader, List<VCFFilter>> filterMap;
	private Map<VCFReader, List<Map<String, Object>>> resultMap;


	/**
	 * Constructor of {@link MGFiltersManager}
	 */
	protected MGFiltersManager () {
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
		filterMap = new HashMap<VCFReader, List<VCFFilter>>();
		for (VCFFilter filter: filterListToUpdate) {
			if (!filterMap.containsKey(filter.getReader())) {
				filterMap.put(filter.getReader(), new ArrayList<VCFFilter>());
			}
			filterMap.get(filter.getReader()).add(filter);
		}
		
		/*String info = "";
		for (VCFReader reader: filterMap.keySet()) {
			info += reader.getFile().getName() + ": ";
			for (VCFFilter filter: filterMap.get(reader)) {
				info += filter.getFilter().toStringForDisplay() + "; ";
			}
			info += "\n";
		}
		System.out.println(info);*/
	}

	
	/**
	 * Retrieves the VCF lines from each VCF files.
	 * It retrieves only the required columns.
	 */
	public void retrieveDataFromVCF () {
		if (filterMap != null && filterMap.size() > 0) {
			resultMap = new HashMap<VCFReader, List<Map<String,Object>>>();
			Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
			
			for (VCFReader reader: filterMap.keySet()) {
				
				List<String> columnNameList = new ArrayList<String>();
				for (VCFFilter filter: filterMap.get(reader)) {
					columnNameList.add(filter.getFilter().getColumnName().toString());
				}
				
				List<Map<String, Object>> results = null;
				try {
					results = reader.query(chromosome.getName(), 0, chromosome.getLength(), columnNameList);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				resultMap.put(reader, results);
			}
		}
		
		String info = "";
		for (VCFReader reader: resultMap.keySet()) {
			info += reader.getFile().getName() + ": " + resultMap.get(reader).size();
			info += "\n";
		}
		System.out.println(info);
	}
	
	
	/**
	 * @param filter the filter
	 * @return the result of the query on the VCF file related to the filter
	 */
	public List<Map<String, Object>> getResultOfFilter (VCFFilter filter) {
		return resultMap.get(filter);
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
