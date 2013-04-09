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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ChromosomeWindowLists;
import edu.yu.einstein.genplay.util.FloatLists;


/**
 * This class scales a {@link SCWList} of {@link SimpleScoredChromosomeWindow} to be displayed on a track.
 * @author Julien Lajugie
 */
public class SimpleSCWLScaler implements DataScalerForTrackDisplay<SCWList, ListView<ScoredChromosomeWindow>> {

	/** Generate serial ID */
	private static final long serialVersionUID = -8886234764096047299L;

	/** Scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** The scw list scaled for a specified chromosome and xRatio */
	private ListView<ScoredChromosomeWindow> scaledSCWList;

	/** Data to be scaled for track display */
	private final SCWList dataToScale;


	/**
	 * Creates an instance of {@link SimpleSCWLScaler}
	 * @param dataToScale the data that needs to be scaled
	 */
	public SimpleSCWLScaler(SCWList dataToScale) {
		this.dataToScale = dataToScale;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getDataScaledForTrackDisplay() {
		GenomeWindow projectWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		double projectXRatio = ProjectManager.getInstance().getProjectWindow().getXRatio();
		// if the chromosome or the xRatio of the project window changed we need to rescale the data
		if (!projectWindow.getChromosome().equals(scaledChromosome) || (projectXRatio != scaledXRatio)) {
			scaledChromosome = projectWindow.getChromosome();
			scaledXRatio = projectXRatio;
			scaleChromosome();
		}
		if ((scaledSCWList == null) || (scaledSCWList.size() == 0)) {
			return null;
		}
		return ChromosomeWindowLists.sublist(scaledSCWList, projectWindow.getStart(), projectWindow.getStop());
	}


	@Override
	public SCWList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	private void scaleChromosome() {
		ListView<ScoredChromosomeWindow> currentChromosomeList;
		scaledSCWList = null;
		try {
			currentChromosomeList = dataToScale.getView(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			return;
		}
		if (currentChromosomeList == null) {
			return;
		}
		if (scaledXRatio > 1) {
			scaledSCWList = currentChromosomeList;
		} else {
			if (currentChromosomeList.size() > 0) {
				GenericSCWListViewBuilder scaledSCWListBuilder = new GenericSCWListViewBuilder(dataToScale.getScorePrecision);
				int start = currentChromosomeList.get(0).getStart();
				int stop = currentChromosomeList.get(0).getStop();
				ArrayList<Float> scoreList = new ArrayList<Float>();
				scoreList.add(currentChromosomeList.get(0).getScore());
				int i = 1;
				int j = 0;
				while (i < currentChromosomeList.size()) {
					double gapDistance = (currentChromosomeList.get(i).getStart() - stop) * scaledXRatio;
					double windowWidth = (currentChromosomeList.get(i).getStop() - start) * scaledXRatio;
					double currentScore = scoreList.get(0);
					double nextScore = currentChromosomeList.get(i).getScore();
					// we merge two intervals together if there is a gap smaller than 1 pixel and have the same score
					// or if the width of a window is smaller than 1
					while ( ((i + 1) < currentChromosomeList.size()) &&
							( ((gapDistance < 1) && (currentScore == nextScore)) ||
									((windowWidth < 1) && (nextScore != 0)))) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						stop = Math.max(stop, currentChromosomeList.get(i).getStop());
						scoreList.add(currentChromosomeList.get(i).getScore());
						i++;
						gapDistance = (currentChromosomeList.get(i).getStart() - stop) * scaledXRatio;
						windowWidth = (currentChromosomeList.get(i).getStop() - start) * scaledXRatio;
						nextScore = currentChromosomeList.get(i).getScore();
					}
					float score = FloatLists.average(scoreList);
					scaledSCWListBuilder.addElementToBuild(start, stop, score);
					scoreList = new ArrayList<Float>();
					scoreList.add(currentChromosomeList.get(i).getScore());
					start = currentChromosomeList.get(i).getStart();
					stop = currentChromosomeList.get(i).getStop();
					j++;
					i++;
				}
				scaledSCWList = scaledSCWListBuilder.getListView();
			}
		}
	}
}
