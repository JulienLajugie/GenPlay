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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.SimpleListView.SimpleListViewBuilder;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ChromosomeWindowLists;


/**
 * This class scales a {@link RepeatFamilyList} to be displayed on a track.
 * @author Julien Lajugie
 */
public class RepeatListScaler implements DataScalerForTrackDisplay<RepeatFamilyList, List<RepeatFamilyListView>> {

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
	public RepeatListScaler(RepeatFamilyList dataToScale) {
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
			RepeatFamilyListViewBuilder builder = new RepeatFamilyListViewBuilder(currentFamily.getName());
			RepeatFamilyListView familyToAdd = (RepeatFamilyListView) ChromosomeWindowLists.sublist(currentFamily, projectWindow.getStart(), projectWindow.getStop(), builder);
			resultList.add(familyToAdd);
		}
		return resultList;
	}


	@Override
	public RepeatFamilyList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Merges two repeats of the same family together if the gap between
	 * the two repeats is not visible at the current zoom level
	 */
	private void scaleChromosome() {
		ListView<RepeatFamilyListView> currentChromosomeList;
		scaledRepeatList = null;
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
				while (i < currentChromosomeList.size()) {
					int currentStart = currentFamily.get(i).getStart();
					int currentStop = currentFamily.get(i).getStop();
					// we merge two windows together if there is a next window
					// and if the gap between the current window and the next one is smaller than 1 pixel
					while (((i + 1) < currentChromosomeList.size())
							&& ((currentFamily.get(i + 1).getStart() - currentStop) < pixelGenomicWidth)) {
						i++;
						// the new stop is the one of the next window
						currentStop = currentFamily.get(i).getStop();
					}
					familyBuilder.addElementToBuild(new SimpleChromosomeWindow(currentStart, currentStop));
					i++;
				}
				familyListBuilder.addElementToBuild((RepeatFamilyListView) familyBuilder.getListView());
			}
			scaledRepeatList = familyListBuilder.getListView();
		}
	}
}
