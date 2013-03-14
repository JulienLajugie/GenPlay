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
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.MaskChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ChromosomeWindowLists;
import edu.yu.einstein.genplay.util.DoubleLists;


/**
 * This class scales a {@link ScoredChromosomeWindowList} of {@link MaskChromosomeWindow} to be displayed on a track.
 * @author Julien Lajugie
 */
public class MaskSCWLScaler implements DataScalerForTrackDisplay<ScoredChromosomeWindowList, List<ScoredChromosomeWindow>> {

	/** Generate serial ID */
	private static final long serialVersionUID = 264219231566141709L;

	private Chromosome 							scaledChromosome; 	// scaled chromosome
	private double 								scaledXRatio;		// scaled xRatio (ratio between the track width and the displayed genome window width)
	private List<ScoredChromosomeWindow>		scaledSCWList;		// the scw list scaled for a specified chromosome and xRatio
	private final ScoredChromosomeWindowList	dataToScale;		// data to be scaled for track display


	/**
	 * Creates an instance of {@link MaskSCWLScaler}
	 * @param dataToScale the data that needs to be scaled
	 */
	public MaskSCWLScaler(ScoredChromosomeWindowList dataToScale) {
		this.dataToScale = dataToScale;
	}


	@Override
	public List<ScoredChromosomeWindow> getDataScaledForTrackDisplay() {
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
	public ScoredChromosomeWindowList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Merges two windows together if the gap between this two windows is not visible
	 */
	private void scaleChromosome() {
		List<ScoredChromosomeWindow> currentChromosomeList;
		try {
			currentChromosomeList = dataToScale.getView(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			scaledChromosome = null;
			return;
		}
		if (currentChromosomeList == null) {
			scaledSCWList = null;
			return;
		}
		if (scaledXRatio > 1) {
			scaledSCWList = currentChromosomeList;
		} else {
			scaledSCWList = new ArrayList<ScoredChromosomeWindow>();
			if (currentChromosomeList.size() > 0) {
				ArrayList<Double> scoreList = new ArrayList<Double>();
				scaledSCWList.add(new MaskChromosomeWindow(currentChromosomeList.get(0)));
				scoreList.add(currentChromosomeList.get(0).getScore());
				int i = 1;
				int j = 0;
				while (i < currentChromosomeList.size()) {
					double gapDistance = (currentChromosomeList.get(i).getStart() - scaledSCWList.get(j).getStop()) * scaledXRatio;
					double windowWidth = (currentChromosomeList.get(i).getStop() - scaledSCWList.get(j).getStart()) * scaledXRatio;
					double currentScore = scaledSCWList.get(j).getScore();
					double nextScore = currentChromosomeList.get(i).getScore();
					// we merge two intervals together if there is a gap smaller than 1 pixel and have the same score
					// or if the width of a window is smaller than 1
					while ( ((i + 1) < currentChromosomeList.size()) &&
							( ((gapDistance < 1) && (currentScore == nextScore)) ||
									((windowWidth < 1) && (nextScore != 0)))) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(scaledSCWList.get(j).getStop(), currentChromosomeList.get(i).getStop());
						scaledSCWList.get(j).setStop(newStop);
						scoreList.add(currentChromosomeList.get(i).getScore());
						i++;
						gapDistance = (currentChromosomeList.get(i).getStart() - scaledSCWList.get(j).getStop()) * scaledXRatio;
						windowWidth = (currentChromosomeList.get(i).getStop() - scaledSCWList.get(j).getStart()) * scaledXRatio;
						nextScore = currentChromosomeList.get(i).getScore();
					}
					scaledSCWList.get(j).setScore(DoubleLists.average(scoreList));
					scaledSCWList.add(new MaskChromosomeWindow(currentChromosomeList.get(i)));
					scoreList = new ArrayList<Double>();
					scoreList.add(currentChromosomeList.get(i).getScore());
					j++;
					i++;
				}
			}
		}
	}
}
