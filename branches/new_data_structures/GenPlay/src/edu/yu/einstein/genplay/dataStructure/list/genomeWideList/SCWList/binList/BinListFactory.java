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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.pileupFlattener.BinListPileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.PileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;


/**
 * Factory class for vending {@link SimpleSCWList} objects.
 * @author Julien Lajugie
 */
public class BinListFactory {


	/**
	 * Creates a {@link BinList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * @param scwReader a {@link SCWReader}
	 * @param binSize size of the bins of the binlist
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return A new {@link BinList}
	 * @throws InvalidChromosomeException
	 * @throws ObjectAlreadyBuiltException
	 * @throws ElementAddedNotSortedException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 */
	public static BinList createBinList(SCWReader scwReader, int binSize, ScoreOperation scoreOperation)
			throws InvalidChromosomeException, ObjectAlreadyBuiltException, ElementAddedNotSortedException, IOException, InterruptedException, ExecutionException, CloneNotSupportedException {
		BinListViewBuilder lvBuilderPrototype = new BinListViewBuilder(binSize);
		ListOfListViewBuilder<ScoredChromosomeWindow> builder = new ListOfListViewBuilder<ScoredChromosomeWindow>(lvBuilderPrototype);
		Chromosome currentChromosome = null;

		// create object that will "flattened" pileups of overlapping windows
		PileupFlattener flattener = new BinListPileupFlattener(binSize, scoreOperation);
		while (scwReader.readItem()) {
			ScoredChromosomeWindow currentWindow = new SimpleScoredChromosomeWindow(scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
			if (currentChromosome == null) {
				currentChromosome = scwReader.getChromosome();
			} else if (currentChromosome != scwReader.getChromosome()) {
				// at the end of a chromosome we flush the flattener and
				// retrieve the remaining of flattened windows
				List<ScoredChromosomeWindow> flattenedWindows = flattener.flush();
				for (ScoredChromosomeWindow scw: flattenedWindows) {
					builder.addElementToBuild(currentChromosome, scw);
				}
				currentChromosome = scwReader.getChromosome();
			} else {
				// we add the current window to the flattener and retrieve the list of
				// flattened windows
				List<ScoredChromosomeWindow> flattenedWindows = flattener.addWindow(currentWindow);
				for (ScoredChromosomeWindow scw: flattenedWindows) {
					builder.addElementToBuild(currentChromosome, scw);
				}
			}
		}
		return new BinList(builder.getGenomicList(), binSize);
	}
}
