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

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.dataReader.StrandReader;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTwoLayers;
import edu.yu.einstein.genplay.core.operation.binList.BLOTwoLayers;
import edu.yu.einstein.genplay.core.pileupFlattener.BinListPileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.GenomeWideFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.PileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.SimpleSCWPileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;
import edu.yu.einstein.genplay.exception.exceptions.ElementAddedNotSortedException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Factory class for vending {@link SimpleSCWList} objects.
 * @author Julien Lajugie
 */
public class SCWListFactory {


	/**
	 * Creates a {@link BinList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * @param scwReader a {@link SCWReader}
	 * @param binSize size of the bins of the binlist
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return A new {@link BinList}
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 * @throws ElementAddedNotSortedException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public static BinList createBinList(SCWReader scwReader, int binSize, ScoreOperation scoreOperation)
			throws CloneNotSupportedException, ElementAddedNotSortedException, InvalidChromosomeException,
			ObjectAlreadyBuiltException, IOException, InterruptedException, ExecutionException {
		// create object that will "flattened" pileups of overlapping windows
		BinListPileupFlattener flattenerPrototype = new BinListPileupFlattener(binSize, scoreOperation);
		GenomeWideFlattener genomeWideFlattener = new GenomeWideFlattener(flattenerPrototype);
		while (scwReader.readItem()) {
			genomeWideFlattener.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
		}
		return new BinList(genomeWideFlattener.getListOfListViews());
	}


	/**
	 * Creates a dense {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Dense {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are consecutive (not separated by windows with a score of 0).
	 * @param scwReader a {@link SCWReader}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static SCWList createDenseSCWList(SCWReader scwReader, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype = new DenseSCWListViewBuilder();
		return createSimpleSCWList(scwReader, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a generic {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Generic {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are not consecutive (separated by windows with a score of 0).
	 * @param scwReader a {@link SCWReader}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static SCWList createGenericSCWList(SCWReader scwReader, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype = new GenericSCWListViewBuilder();
		return createSimpleSCWList(scwReader, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a mask {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Mask {@link SCWList} only contains windows with a score of 0 or 1
	 * @param scwReader a {@link SCWReader}
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static SCWList createMaskSCWList(SCWReader scwReader) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype = new MaskListViewBuilder();
		SCWListBuilder builder = new SCWListBuilder(lvBuilderPrototype);
		while (scwReader.readItem()) {
			Chromosome currentChromosome = scwReader.getChromosome();
			builder.addElementToBuild(currentChromosome, scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
		}
		return builder.getSCWList();
	}


	/**
	 * Creates a {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * @param scwReader a {@link SCWReader}
	 * @param scorePrecision precision of the scores of the scores of the genes and exons
	 * @param lvBuilderPrototype a prototype of builders
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	private static SCWList createSimpleSCWList(SCWReader scwReader, SCWListViewBuilder lvBuilderPrototype, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		// create object that will "flattened" pileups of overlapping windows
		PileupFlattener flattenerPrototype = new SimpleSCWPileupFlattener(scoreOperation, lvBuilderPrototype);
		GenomeWideFlattener gwFlattener = new GenomeWideFlattener(flattenerPrototype);
		while (scwReader.readItem()) {
			gwFlattener.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
		}
		return new SimpleSCWList(gwFlattener.getListOfListViews());
	}



	/**
	 * Creates a {@link BinList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * This factory method is called "strand safe" and must be called if both strands are extracted
	 * and the strands are shifted because reads on the 3' strand are shifted toward 5' and can change the order
	 * of reads and cause the file to be no longer sorted.
	 * @param scwReader a {@link SCWReader}
	 * @param binSize size of the bins of the binlist
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return A new {@link BinList}
	 * @throws CloneNotSupportedException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws BinListDifferentWindowSizeException
	 */
	public static BinList createStrandSafeBinList(SCWReader scwReader, int binSize, ScoreOperation scoreOperation) throws InvalidParameterException, InterruptedException, ExecutionException, CloneNotSupportedException, ObjectAlreadyBuiltException, IOException, BinListDifferentWindowSizeException {
		// create object that will "flattened" pileups of overlapping windows
		BinListPileupFlattener flattenerPrototype5 = new BinListPileupFlattener(binSize, scoreOperation);
		BinListPileupFlattener flattenerPrototype3 = new BinListPileupFlattener(binSize, scoreOperation);
		GenomeWideFlattener genomeWideFlattener5 = new GenomeWideFlattener(flattenerPrototype5);
		GenomeWideFlattener genomeWideFlattener3 = new GenomeWideFlattener(flattenerPrototype3);
		while (scwReader.readItem()) {
			if ((scwReader instanceof StrandReader) && (((StrandReader) scwReader).getStrand() == Strand.THREE)){
				genomeWideFlattener3.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
			} else {
				genomeWideFlattener5.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
			}
		}
		BinList binList5 = new BinList(genomeWideFlattener5.getListOfListViews());
		BinList binList3 = new BinList(genomeWideFlattener3.getListOfListViews());
		return (BinList) new BLOTwoLayers(binList5, binList3, ScoreOperation.ADDITION).compute();
	}


	/**
	 * Creates a dense {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Dense {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are consecutive (not separated by windows with a score of 0).
	 * This factory method is called "strand safe" and must be called if both strands are extracted
	 * and the strands are shifted because reads on the 3' strand are shifted toward 5' and can change the order
	 * of reads and cause the file to be no longer sorted.
	 * @param scwReader a {@link SCWReader}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static SCWList createStrandSafeDenseSCWList(SCWReader scwReader, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype = new DenseSCWListViewBuilder();
		return createStrandSafeSimpleSCWList(scwReader, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a generic {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * Generic {@link SCWList} are optimized to minimize the memory usage when most of the windows
	 * are not consecutive (separated by windows with a score of 0).
	 * This factory method is called "strand safe" and must be called if both strands are extracted
	 * and the strands are shifted because reads on the 3' strand are shifted toward 5' and can change the order
	 * of reads and cause the file to be no longer sorted.
	 * @param scwReader a {@link SCWReader}
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 * @throws InvalidChromosomeException
	 */
	public static SCWList createStrandSafeGenericSCWList(SCWReader scwReader, ScoreOperation scoreOperation) throws InterruptedException, ExecutionException, CloneNotSupportedException, InvalidChromosomeException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype = new GenericSCWListViewBuilder();
		return createStrandSafeSimpleSCWList(scwReader, lvBuilderPrototype, scoreOperation);
	}


	/**
	 * Creates a {@link SCWList} from the data retrieved by the specified {@link SCWReader}.
	 * The specified prototype determine the underlying data structure of the {@link SCWList}.
	 * This factory method is called "strand safe" and must be called if both strands are extracted
	 * and the strands are shifted because reads on the 3' strand are shifted toward 5' and can change the order
	 * of reads and cause the file to be no longer sorted.
	 * @param scwReader a {@link SCWReader}
	 * @param scorePrecision precision of the scores of the scores of the genes and exons
	 * @param lvBuilderPrototype a prototype of builders
	 * @param scoreOperation {@link ScoreOperation} to compute the score of windows resulting from the "flattening" of a pileup of overlapping windows
	 * @return a new {@link SCWList}
	 * @throws CloneNotSupportedException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws IOException
	 * @throws ObjectAlreadyBuiltException
	 */
	private static SCWList createStrandSafeSimpleSCWList(SCWReader scwReader, SCWListViewBuilder lvBuilderPrototype5, ScoreOperation scoreOperation) throws CloneNotSupportedException, InvalidParameterException, InterruptedException, ExecutionException, ObjectAlreadyBuiltException, IOException {
		SCWListViewBuilder lvBuilderPrototype3 = lvBuilderPrototype5.clone();
		// create object that will "flattened" pileups of overlapping windows
		PileupFlattener flattenerPrototype5 = new SimpleSCWPileupFlattener(scoreOperation, lvBuilderPrototype5);
		PileupFlattener flattenerPrototype3 = new SimpleSCWPileupFlattener(scoreOperation, lvBuilderPrototype3);
		GenomeWideFlattener gwFlattener5 = new GenomeWideFlattener(flattenerPrototype5);
		GenomeWideFlattener gwFlattener3 = new GenomeWideFlattener(flattenerPrototype3);
		while (scwReader.readItem()) {
			if ((scwReader instanceof StrandReader) && (((StrandReader) scwReader).getStrand() == Strand.THREE)){
				gwFlattener3.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
			} else {
				gwFlattener5.addElementToBuild(scwReader.getChromosome(), scwReader.getStart(), scwReader.getStop(), scwReader.getScore());
			}
		}
		SCWList scwList5 = new SimpleSCWList(gwFlattener5.getListOfListViews());
		SCWList scwList3 = new SimpleSCWList(gwFlattener3.getListOfListViews());
		return new SCWLOTwoLayers(scwList5, scwList3, ScoreOperation.ADDITION).compute();
	}
}
