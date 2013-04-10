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
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ChromosomeWindowLists;

public class RepeatListScaler implements DataScalerForTrackDisplay<RepeatFamilyList, List<RepeatFamilyListView>> {

	/** Scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** Data to be scaled for track display */
	private final RepeatFamilyList dataToScale;

	/** The list of repeat families scaled for a specified chromosome and xRatio */
	private List<RepeatFamilyListView> scaledRepeatList;


	protected void fitToScreen() {
		List<RepeatFamilyListView> currentChromosomeList;
		try {
			currentChromosomeList = dataToScale.get(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			fittedDataList = null;
			return;
		}

		if (fittedXRatio > 1) {
			fittedDataList = currentChromosomeList;
		} else {
			fittedDataList = new ArrayList<RepeatFamilyListView>();
			for (RepeatFamilyListView currentFamily : currentChromosomeList) {
				if (currentFamily.repeatCount() > 1) {
					RepeatFamilyListView fittedFamily = new RepeatFamilyListView(currentFamily.getName());
					fittedFamily.addRepeat(new SimpleChromosomeWindow(currentFamily.getRepeat(0)));
					int i = 1;
					int j = 0;
					while (i < currentFamily.repeatCount()) {
						double distance = (currentFamily.getRepeat(i).getStart() - fittedFamily.getRepeat(j).getStop()) * fittedXRatio;
						while ((distance < 1) && ((i + 1) < currentFamily.repeatCount())) {
							int newStop = Math.max(fittedFamily.getRepeat(j).getStop(), currentFamily.getRepeat(i).getStop());
							fittedFamily.getRepeat(j).setStop(newStop);
							i++;
							distance = (currentFamily.getRepeat(i).getStart() - fittedFamily.getRepeat(j).getStop()) * fittedXRatio;
						}
						fittedFamily.addRepeat(new SimpleChromosomeWindow(currentFamily.getRepeat(i)));
						i++;
						j++;
					}
					fittedDataList.add(fittedFamily);
				} else if (currentFamily.repeatCount() == 1) {
					RepeatFamilyListView fittedFamily = new RepeatFamilyListView(currentFamily.getName());
					fittedFamily.addRepeat(new SimpleChromosomeWindow(currentFamily.getRepeat(0)));
					fittedDataList.add(fittedFamily);
				}
			}
		}
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
		if (scaledRepeatList == null) {
			return null;
		}
		List<ListView<Gene>> resultList = new ArrayList<ListView<Gene>>();
		// search genes for each line
		for (RepeatFamilyListView currentLine : scaledRepeatList) {
			// retrieve the sublist of genes that are located between the start and stop displayed positions
			RepeatFamilyListView lineToAdd = ChromosomeWindowLists.sublist(currentLine, projectWindow.getStart(), projectWindow.getStop());
			resultList.add(lineToAdd);
		}
		return resultList;








		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		List<RepeatFamilyListView> resultList = new ArrayList<RepeatFamilyListView>();

		for (RepeatFamilyListView currentFamily : fittedDataList) {
			int indexStart = findStart(currentFamily.getRepeatList(), start, 0, currentFamily.getRepeatList().size());
			int indexStop = findStop(currentFamily.getRepeatList(), stop, 0, currentFamily.getRepeatList().size());
			if ((indexStart > 0) && (currentFamily.getRepeatList().get(indexStart - 1).getStop() > start)) {
				indexStart--;
			}
			resultList.add(new RepeatFamilyListView(currentFamily.getName()));
			if ((indexStart == indexStop) && (indexStart < currentFamily.repeatCount())) {
				if (currentFamily.getRepeat(indexStart).getStart() < stop) {
					resultList.get(resultList.size() - 1).addRepeat(currentFamily.getRepeat(indexStart));
				}
			} else {
				for (int i = indexStart; i <= indexStop; i++) {
					if (i < currentFamily.repeatCount()) {
						resultList.get(resultList.size() - 1).addRepeat(currentFamily.getRepeat(i));
					}
				}
			}
		}
		return resultList;
	}


	@Override
	public RepeatFamilyList getDataToScale() {
		return dataToScale;
	}


	private void scaleChromosome() {
		// TODO Auto-generated method stub

	}
}
