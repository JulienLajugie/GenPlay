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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.SimpleListView.SimpleListViewBuilder;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ListView.ChromosomeWindowListViews;


/**
 * This class scales a {@link RepeatFamilyList} to be displayed on a track.
 * @author Julien Lajugie
 */
class RepeatListScaler implements DataScalerForTrackDisplay<RepeatFamilyList, List<RepeatFamilyListView>> {

	/**
	 * Threads that computes the scaled data for the chromosome currently displayed
	 * at the current zoom level and screen resolution.
	 * @author Julien Lajugie
	 */
	private class ScalerThread extends Thread {

		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();
			setName("Data Scaler Thread");
			ListView<RepeatFamilyListView> currentChromosomeList;
			try {
				currentChromosomeList = dataToScale.get(scaledChromosome);
			} catch (InvalidChromosomeException e) {
				ExceptionManager.getInstance().caughtException(e);
				scaledChromosome = null;
				return;
			}
			if ((currentChromosomeList == null) || currentChromosomeList.isEmpty()) {
				return;
			}
			if (scaledXRatio > 1) {
				scaledRepeatList = currentChromosomeList;
			} else {
				// compute the width on the genome that takes up 1 pixel on the screen
				double pixelGenomicWidth = 1 / scaledXRatio;
				ListViewBuilder<RepeatFamilyListView> familyListBuilder = new SimpleListViewBuilder<RepeatFamilyListView>();
				for (RepeatFamilyListView currentFamily : currentChromosomeList) {
					RepeatFamilyListViewBuilder familyBuilder = new RepeatFamilyListViewBuilder(currentFamily.getName());
					int i = 0;
					while (i < currentFamily.size()) {
						if (thisThread != scalerThread) {
							scaledRepeatList = null;
							return;
						}
						int currentStart = currentFamily.get(i).getStart();
						int currentStop = currentFamily.get(i).getStop();
						// we merge two windows together if there is a next window
						// and if the gap between the current window and the next one is smaller than 1 pixel
						while (((i + 1) < currentFamily.size())
								&& ((currentFamily.get(i + 1).getStart() - currentStop) < pixelGenomicWidth)) {
							i++;
							// the new stop is the one of the next window
							currentStop = currentFamily.get(i).getStop();
						}
						familyBuilder.addElementToBuild(currentStart, currentStop);
						i++;
					}
					familyListBuilder.addElementToBuild((RepeatFamilyListView) familyBuilder.getListView());
				}
				scaledRepeatList = familyListBuilder.getListView();
				DataScalerManager.getInstance().redrawLayers(RepeatListScaler.this);
			}
		}
	}


	/** Thread that scales the data */
	private ScalerThread scalerThread;

	/** Scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** Data to be scaled for track display */
	private final RepeatFamilyList dataToScale;

	/** The list of repeat families scaled for a specified chromosome and xRatio */
	private ListView<RepeatFamilyListView> scaledRepeatList;


	/**
	 * Creates an instance of {@link RepeatListScaler}
	 * @param dataToScale data that needs to be scaled
	 */
	RepeatListScaler(RepeatFamilyList dataToScale) {
		this.dataToScale = dataToScale;
	}


	@Override
	public List<RepeatFamilyListView> getDataScaledForTrackDisplay() {
		GenomeWindow projectWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		double projectXRatio = ProjectManager.getInstance().getProjectWindow().getXRatio();
		// if the chromosome or the xRatio of the project window changed we need to rescale the data
		if (!projectWindow.getChromosome().equals(scaledChromosome) || (projectXRatio != scaledXRatio)) {
			scaledChromosome = projectWindow.getChromosome();
			scaledXRatio = projectXRatio;
			scaleChromosome();
		}
		if ((scaledRepeatList == null) || (scaledRepeatList.size() == 0)) {
			return null;
		}
		List<RepeatFamilyListView> resultList = new ArrayList<RepeatFamilyListView>();
		// search repeats for each family
		for (RepeatFamilyListView currentFamily : scaledRepeatList) {
			// retrieve the sublist of genes that are located between the start and stop displayed positions
			RepeatFamilyListView familyToAdd = new RepeatFamilyListView(currentFamily.getName(), ChromosomeWindowListViews.subList(currentFamily, projectWindow.getStart(), projectWindow.getStop()));
			resultList.add(familyToAdd);
		}
		return resultList;
	}


	@Override
	public RepeatFamilyList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Starts the thread that scales the current chromosome
	 * for the current zoom level and screen resolution
	 */
	private void scaleChromosome() {
		scaledRepeatList = null;
		scalerThread = new ScalerThread();
		scalerThread.start();
	}
}
