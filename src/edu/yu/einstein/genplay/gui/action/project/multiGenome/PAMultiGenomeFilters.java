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
package edu.yu.einstein.genplay.gui.action.project.multiGenome;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.MGFiltersManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * This class updates the filters in a multi genome project
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PAMultiGenomeFilters extends TrackListActionWorker<Track<?>[]> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION = 
		"Performs the multi genome algorithm for SNPs"; 						// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 			// mnemonic key
	private static		 String 			ACTION_NAME = "Updating filters";	// action name

	private final MGFiltersManager filterManager;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Filters for Multi Genome";


	/**
	 * Creates an instance of {@link PAMultiGenomeFilters}.
	 */
	public PAMultiGenomeFilters() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		filterManager = MGFiltersManager.getInstance();
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();

		// Checks if the project is multi-genome
		if (projectManager.isMultiGenomeProject()) {

			filterManager.initializeFilterLists();
			
			if (filterManager.hasToBeRun()) {
				// Notifies the action
				notifyActionStart(ACTION_NAME, 1, false);
				
				filterManager.retrieveDataFromVCF();
				
				List<VCFFilter> filterListToUpdate = filterManager.getFilterListToUpdate();
				//printList("Filters to update", filterListToUpdate);
	
				for (VCFFilter filter: filterListToUpdate) {
					filter.generateFilter(filterManager.getResultOfFilter(filter));
				}
			}
		}

		return null;
	}


	@Override
	protected void doAtTheEnd(Track<?>[] actionResult) {
		filterManager.reset();
		if (latch != null) {
			latch.countDown();
		}
	}


	
	@SuppressWarnings("unused")
	private void printList (String title, List<VCFFilter> list) {
		System.out.println("===== " + title);
		if (list.size() == 0) {
			System.out.println("Empty list");
		} else {
			for (VCFFilter filter: list) {
				filter.show();
			}
		}
		System.out.println("=====");
	}


	/**
	 * @param previousFilterList the previousFilterList to set
	 */
	public void setPreviousFilterList(List<VCFFilter> previousFilterList) {
		filterManager.setPreviousFilterList(previousFilterList);
	}


	/**
	 * @param chromosomeHasChanged the chromosomeHasChanged to set
	 */
	public void setChromosomeHasChanged(boolean chromosomeHasChanged) {
		filterManager.setChromosomeHasChanged(chromosomeHasChanged);
	}
	
	
	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}