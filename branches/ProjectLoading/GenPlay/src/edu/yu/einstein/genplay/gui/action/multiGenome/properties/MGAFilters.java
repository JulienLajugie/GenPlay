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
package edu.yu.einstein.genplay.gui.action.multiGenome.properties;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.MGFiltersManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;


/**
 * This class updates the filters in a multi genome project
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAFilters extends TrackListActionWorker<Track<?>[]> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION =
			"Performs the multi genome algorithm for SNPs"; 						// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 			// mnemonic key
	private static		 String 			ACTION_NAME = "Updating filters";	// action name

	private final MGFiltersManager filterManager;
	private boolean hasBeenInitialized;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Filters for Multi Genome";


	/**
	 * Creates an instance of {@link MGAFilters}.
	 */
	public MGAFilters() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		filterManager = MGFiltersManager.getInstance();
		hasBeenInitialized = false;
	}


	@Override
	protected Track<?>[] processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();

		// Checks if the project is multi-genome
		if (projectManager.isMultiGenomeProject()) {
			if (!hasBeenInitialized) {
				filterManager.initializeFilterLists();
			}

			if (filterManager.hasToBeRun()) {

				// Notifies the action
				notifyActionStart(ACTION_NAME, 1, false);

				filterManager.retrieveDataFromVCF();

				List<MGFilter> filterListToUpdate = filterManager.getFilterListToUpdate();

				for (MGFilter filter: filterListToUpdate) {
					if (filter instanceof VCFFilter) {
						VCFFilter vcfFilter = (VCFFilter) filter;
						vcfFilter.generateFilter(filterManager.getResultOfFilter(vcfFilter));
					} else {
						filter.generateFilter();
					}
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


	/**
	 * @return true if the operation has to be processed, false if no need
	 */
	public boolean hasToBeProcessed () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			filterManager.initializeFilterLists();
			hasBeenInitialized = true;
			return filterManager.hasToBeRun();
		}
		return false;
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
	public void setPreviousFilterList(List<MGFilter> previousFilterList) {
		filterManager.setPreviousFilterList(previousFilterList);
		hasBeenInitialized = false;
	}


	/**
	 * @param chromosomeHasChanged the chromosomeHasChanged to set
	 */
	public void setChromosomeHasChanged(boolean chromosomeHasChanged) {
		filterManager.setChromosomeHasChanged(chromosomeHasChanged);
		hasBeenInitialized = false;
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}