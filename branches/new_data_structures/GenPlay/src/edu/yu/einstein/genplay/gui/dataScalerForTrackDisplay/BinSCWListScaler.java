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

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ListView.ChromosomeWindowListViews;


/**
 * This class scales a {@link BinSCWListScaler} to be displayed on a track.
 * @author Julien Lajugie
 */
public class BinSCWListScaler implements DataScalerForTrackDisplay<BinList, ListView<ScoredChromosomeWindow>> {

	/** Scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** The scw list scaled for a specified chromosome and xRatio */
	private ListView<ScoredChromosomeWindow> scaledSCWList;

	/** Data to be scaled for track display */
	private final BinList dataToScale;


	/**
	 * Creates an instance of {@link BinSCWListScaler}
	 * @param dataToScale the data that needs to be scaled
	 */
	public BinSCWListScaler(BinList dataToScale) {
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
		return ChromosomeWindowListViews.sublist(scaledSCWList, projectWindow.getStart(), projectWindow.getStop());
	}


	@Override
	public BinList getDataToScale() {
		return dataToScale;
	}


	private void scaleChromosome() {
		ListView<ScoredChromosomeWindow> currentChromosomeList;
		scaledSCWList = null;
		try {
			currentChromosomeList = dataToScale.get(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			return;
		}
		if ((currentChromosomeList == null) || currentChromosomeList.isEmpty()) {
			return;
		}
		int binSize = dataToScale.getBinSize();
		if ((scaledXRatio * binSize) >= 1) {
			scaledSCWList = currentChromosomeList;
			return;
		}
		int i = 0;
		double ratio = 0;
		while ((i < BinList.AVERAGE_BIN_SIZE_FACTORS.length) && (ratio < 1)) {
			binSize = dataToScale.getBinSize() * BinList.AVERAGE_BIN_SIZE_FACTORS[i];
			ratio = scaledXRatio * binSize;
			scaledSCWList = dataToScale.getAveragedList(i).get(scaledChromosome);
		}
		if (ratio == 1) {
			return;
		}

		// we calculate how many windows are printable depending on the screen resolution
		int fittedBinSize = binSize * (int) ( 1 / (scaledXRatio * binSize));
		int binSizeRatio  = fittedBinSize / binSize;
		// if the fitted bin size is smaller than the regular bin size we don't modify the data
		if (fittedBinSize <= binSize) {
			return;
		}

		// otherwise we calculate the average because we have to print more than
		// one bin per pixel
		ListViewBuilder<ScoredChromosomeWindow> lvBuilder = new BinListViewBuilder(fittedBinSize);
		//scaledSCWList = new double[(acceleratorCurrentChromo.length / binSizeRatio) + 1];

		for(int j = 0; j < scaledSCWList.size(); j += binSizeRatio) {
			float sum = 0;
			int n = 0;
			for(int k = 0; k < binSizeRatio; k ++) {
				if (((j + k) < scaledSCWList.size()) && (scaledSCWList.get(j + k).getScore() != 0)){
					sum += scaledSCWList.get(j + k).getScore();
					n++;
				}
			}
			int start = j * binSize;
			int stop = start + fittedBinSize;
			float score = 0f;
			if (n > 0) {
				score = sum / n;
			}
			ScoredChromosomeWindow scwToAdd = new SimpleScoredChromosomeWindow(start, stop, score);
			lvBuilder.addElementToBuild(scwToAdd);
		}
		scaledSCWList = lvBuilder.getListView();
	}
}
