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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.reader.SCWReader;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;

/**
 * Factory class for vending {@link SCWList} objects.
 * @author Julien Lajugie
 */
public class SimpleSCWListFactory {


	/**
	 * Creates a dense {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Dense {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are consecutive (not separated by windows with a score of 0).
	 * @param scwReader a {@link SCWReader}
	 * @param scorePrecision precision of the scores of the windows of the {@link SCWList}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 */
	public static SCWList createDenseSCWArrayList(SCWReader scwReader, ScorePrecision scorePrecision, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException {
		ListViewBuilder<ScoredChromosomeWindow> lvBuilderPrototype = new DenseSCWListViewBuilder(scorePrecision);
		return createSimpleSCWArrayList(scwReader, scorePrecision, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a generic {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Generic {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are not consecutive (separated by windows with a score of 0).
	 * @param scwReader a {@link SCWReader}
	 * @param scorePrecision precision of the scores of the windows of the {@link SCWList}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 */
	public static SCWList createGenericSCWArrayList(SCWReader scwReader, ScorePrecision scorePrecision, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException {
		ListViewBuilder<ScoredChromosomeWindow> lvBuilderPrototype = new GenericSCWListViewBuilder(scorePrecision);
		return createSimpleSCWArrayList(scwReader, scorePrecision, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a mask {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Mask {@link SCWList} only contains windows with a score of 0 or 1
	 * @param scwReader a {@link SCWReader}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 */
	public static SCWList createMaskSCWArrayList(SCWReader scwReader, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException {
		ListViewBuilder<ScoredChromosomeWindow> lvBuilderPrototype = new MaskListViewBuilder();
		return createSimpleSCWArrayList(scwReader, null, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * @param scwReader a {@link SCWReader}
	 * @param scorePrecision precision of the scores of the scores of the genes and exons
	 * @param lvBuilderPrototype a prototype of builder
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 */
	private static SCWList createSimpleSCWArrayList(SCWReader scwReader, ScorePrecision scorePrecision, ListViewBuilder<ScoredChromosomeWindow> lvBuilderPrototype, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException {
		// retrieve the type of the SCWList underlying structure from the type of the builder prototype
		SCWListType listType = null;
		if (lvBuilderPrototype instanceof DenseSCWListViewBuilder) {
			listType = SCWListType.DENSE;
		} else if (lvBuilderPrototype instanceof GenericSCWListViewBuilder) {
			listType = SCWListType.GENERIC;
		} else if (lvBuilderPrototype instanceof MaskListViewBuilder) {
			listType = SCWListType.MASK;
		} else {
			throw new InvalidParameterException("Invalid ListViewBuilder Prototype");
		}
		ListOfListViewBuilder<ScoredChromosomeWindow> builder = new ListOfListViewBuilder<ScoredChromosomeWindow>(lvBuilderPrototype);
		ScoredChromosomeWindow currentWindow = null;
		Chromosome currentChromosome = null;
		// create object that will "flattened" pileups of overlapping windows
		PileupFlattener flattener = new PileupFlattener(scoreOperation);
		while ((currentWindow = scwReader.readScoredChromosomeWindow()) != null) {
			if (currentChromosome == null) {
				currentChromosome = scwReader.getCurrentChromosome();
			} else if (currentChromosome != scwReader.getCurrentChromosome()) {
				// at the end of a chromosome we flush the flattener and
				// retrieve the remaining of flattened windows
				List<ScoredChromosomeWindow> flattenedWindows = flattener.flush();
				for (ScoredChromosomeWindow scw: flattenedWindows) {
					builder.addElementToBuild(currentChromosome, scw);
				}
				currentChromosome = scwReader.getCurrentChromosome();
			} else {
				// we add the current window to the flattener and retrieve the list of
				// flattened windows
				List<ScoredChromosomeWindow> flattenedWindows = flattener.addWindow(currentWindow);
				for (ScoredChromosomeWindow scw: flattenedWindows) {
					builder.addElementToBuild(currentChromosome, scw);
				}
			}
		}
		return new SimpleSCWList(builder.getGenomicList(), listType, scorePrecision);
	}
}
