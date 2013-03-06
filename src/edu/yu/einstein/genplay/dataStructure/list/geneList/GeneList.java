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
package edu.yu.einstein.genplay.dataStructure.list.geneList;

import java.awt.FontMetrics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * A list of {@link Gene} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneList extends DisplayableListOfLists<Gene, List<List<Gene>>> implements Serializable {

	private static final long serialVersionUID = 1068181566225377150L; 	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	MIN_DISTANCE_BETWEEN_2_GENES = 5;		// minimum distance in pixel between two genes
	private ProjectChromosome 	projectChromosome = ProjectManager.getInstance().getProjectChromosome(); // Instance of the Chromosome Manager
	private FontMetrics			fontMetrics = null;						// dimension of the font used to print the name of the genes
	private String				searchURL = null;						// URL of the gene database
	private GeneSearcher		geneSearcher = null;					// object used to search genes
	private GeneScoreType		geneScoreType = null;					// type of the scores of the exons and genes of the gene list (RPKM, max, sum)


	/**
	 * The name of the genes are printed if the horizontal ratio is above this value
	 */
	public  static final double	MIN_X_RATIO_PRINT_NAME = 0.0005d;


	/**
	 * Creates an instance of {@link GeneList}
	 * @param binList a {@link BinList}
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList(BinList binList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();

		final ChromosomeListOfLists<String> nameList;
		final ChromosomeListOfLists<Strand> strandList;
		final ChromosomeListOfLists<Integer> startList;
		final ChromosomeListOfLists<Integer> stopList;
		final ChromosomeListOfLists<int[]> exonStartsList;
		final ChromosomeListOfLists<int[]> exonStopsList;
		final ChromosomeListOfLists<double[]> exonScoresList;

		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		exonStartsList = new ChromosomeArrayListOfLists<int[]>();
		exonStopsList = new ChromosomeArrayListOfLists<int[]>();
		exonScoresList = new ChromosomeArrayListOfLists<double[]>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			strandList.add(new ArrayList<Strand>());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());
		}



		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final int windowData = binList.getBinSize();

		for (short i = 0; i < binList.size(); i++) {
			final List<String> currentNameList = nameList.get(i);
			final List<Strand> currentStrandList = strandList.get(i);
			final List<Integer> currentStartList = startList.get(i);
			final List<Integer> currentStopList = stopList.get(i);
			final List<int[]> currentExonStartsList = exonStartsList.get(i);
			final List<int[]> currentExonStopsList = exonStopsList.get(i);
			final List<double[]> currentExonScoresList = exonScoresList.get(i);
			final String prefixName = projectChromosome.get(i).getName() + ".";

			final List<Double> currentList = binList.get(i);
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if ((currentList != null) && (currentList.size() > 0)) {
						int nameCounter = 0;
						for (int j = 0; j < currentList.size(); j++) {
							double currentScore = currentList.get(j);
							if (currentScore > 0) {
								int start = j * windowData;						// get the current start
								int stop = start + windowData;					// get the current stop

								boolean hasToUpdate = false;										// here, we want to check whether the current window is following the previous one or not.
								int prevIndex = currentStartList.size() - 1;						// get the last index
								if (prevIndex >= 0) {												// if it exists
									int prevStop = currentStopList.get(prevIndex);					// get the last inserted stop
									if (prevStop == start) {										// if the previous window stops where the current one starts
										double prevScore = currentExonScoresList.get(prevIndex)[0];	// we get the previous score
										if (currentScore == prevScore) {							// if scores are the same, both window follow each other and are the same
											hasToUpdate = true;										// an update of the previous window is enough
										}
									}
								}

								if (hasToUpdate) {												// if we only need to update
									currentStopList.set(prevIndex, stop);						// we change the stop of the previous window by the actual one
									int[] exonStops = currentExonStopsList.get(prevIndex);		// we do the same with the score
									exonStops[0] = stop;
									currentExonStopsList.set(prevIndex, exonStops);
								} else {														// if not, we inserted a whole new position
									nameCounter++;
									currentNameList.add(prefixName + nameCounter);
									currentStrandList.add(Strand.FIVE);
									currentStartList.add(start);
									currentStopList.add(stop);

									int[] exonStarts = new int[1];
									int[] exonStops = new int[1];
									double[] exonScores = new double[1];
									exonStarts[0] = start;
									exonStops[0] = stop;
									exonScores[0] = currentScore;
									currentExonStartsList.add(exonStarts);
									currentExonStopsList.add(exonStops);
									currentExonScoresList.add(exonScores);
								}
							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		op.startPool(threadList);


		initialize(nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList, null, null);
	}


	/**
	 * Creates an instance of {@link GeneList}
	 * @param nameList a list of gene names
	 * @param strandList a list of {@link Strand}
	 * @param startList a list of start positions
	 * @param stopList a list of stop positions
	 * @param exonStartsList a list of exon start arrays
	 * @param exonStopsList a list of exon stop arrays
	 * @param exonScoresList a list of exon score arrays
	 * @param searchURL url of the gene database
	 * @param geneScoreType type of the scores of the exons and genes of the gene list (RPKM, max, sum)
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList(final ChromosomeListOfLists<String> nameList, final ChromosomeListOfLists<Strand> strandList,
			final ChromosomeListOfLists<Integer> startList, final ChromosomeListOfLists<Integer> stopList, final ChromosomeListOfLists<int[]> exonStartsList,
			final ChromosomeListOfLists<int[]> exonStopsList, final ChromosomeListOfLists<double[]> exonScoresList, String searchURL, GeneScoreType geneScoreType)
					throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		initialize(nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList, searchURL, geneScoreType);
	}


	/**
	 * Creates an instance of {@link GeneList}
	 * @param nameList a list of gene names
	 * @param strandList a list of {@link Strand}
	 * @param startList a list of start positions
	 * @param stopList a list of stop positions
	 * @param UTR5BoundList 5' UTR bound
	 * @param UTR3BoundList 3' UTR bound
	 * @param exonStartsList a list of exon start arrays
	 * @param exonStopsList a list of exon stop arrays
	 * @param exonScoresList a list of exon score arrays
	 * @param searchURL url of the gene database
	 * @param geneScoreType type of the scores of the exons and genes of the gene list (RPKM, max, sum)
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList(final ChromosomeListOfLists<String> nameList, final ChromosomeListOfLists<Strand> strandList, final ChromosomeListOfLists<Integer> startList,
			final ChromosomeListOfLists<Integer> stopList, final ChromosomeListOfLists<Integer> UTR5BoundList, final ChromosomeListOfLists<Integer> UTR3BoundList,
			final ChromosomeListOfLists<int[]> exonStartsList, final ChromosomeListOfLists<int[]> exonStopsList, final ChromosomeListOfLists<double[]> exonScoresList,
			String searchURL,
			GeneScoreType geneScoreType) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		this.searchURL = searchURL;
		this.geneScoreType = geneScoreType;
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					List<Gene> resultList = new ArrayList<Gene>();
					for(int j = 0; j < nameList.size(currentChromosome); j++) {
						String name = nameList.get(currentChromosome, j);
						Strand strand = strandList.get(currentChromosome, j);
						int start = startList.get(currentChromosome, j);
						int stop = stopList.get(currentChromosome, j);
						int UTR5Bound = UTR5BoundList.get(currentChromosome, j);
						int UTR3Bound = UTR3BoundList.get(currentChromosome, j);
						int[] exonStarts = null;
						if (exonStartsList.size(currentChromosome) > 0) {
							exonStarts = exonStartsList.get(currentChromosome, j);
						}
						int[] exonStops = null;
						if (exonStopsList.size(currentChromosome) > 0) {
							exonStops = exonStopsList.get(currentChromosome, j);
						}
						double[] exonScores = null;
						if ((exonScoresList != null) && (exonScoresList.size(currentChromosome) > 0)) {
							exonScores = exonScoresList.get(currentChromosome, j);
						}
						// we don't add a gene if it is located after the end of a chromosome
						if (stop < currentChromosome.getLength()) {
							resultList.add(new Gene(name, currentChromosome, strand, start, stop, UTR5Bound, UTR3Bound, exonStarts, exonStops, exonScores));
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Gene>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<Gene> currentList: result) {
				add(currentList);
			}
		}
		// sort the gene list
		for (List<Gene> chromosomeGeneList : this) {
			Collections.sort(chromosomeGeneList);
		}
	}


	/**
	 * Creates an instance of {@link GeneList} containing the specified data.
	 * @param data
	 */
	public GeneList(Collection<? extends List<Gene>> data) {
		addAll(data);
		// add the eventual missing chromosomes
		if (size() < projectChromosome.size()) {
			for (int i = size(); i < projectChromosome.size(); i++){
				add(null);
			}
		}

		// sort the data
		for (List<Gene> currentList: this) {
			if (currentList != null) {
				Collections.sort(currentList);
			}
		}
	}


	/**
	 * Creates an instance of {@link GeneList} containing the specified data.
	 * @param data data of the list
	 * @param searchURL URL of the gene data base
	 * @param geneScoreType
	 */
	public GeneList(Collection<? extends List<Gene>> data, String searchURL, GeneScoreType geneScoreType) {
		addAll(data);
		// add the eventual missing chromosomes
		if (size() < projectChromosome.size()) {
			for (int i = size(); i < projectChromosome.size(); i++){
				add(null);
			}
		}
		// sort the data
		for (List<Gene> currentList: this) {
			if (currentList != null) {
				Collections.sort(currentList);
			}
		}
		this.searchURL = searchURL;
		this.geneScoreType = geneScoreType;
	}


	/**
	 * Creates an instance of {@link GeneList}
	 * @param scwList a {@link ScoredChromosomeWindowList}
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public GeneList(ScoredChromosomeWindowList scwList) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();

		final ChromosomeListOfLists<String> nameList;
		final ChromosomeListOfLists<Strand> strandList;
		final ChromosomeListOfLists<Integer> startList;
		final ChromosomeListOfLists<Integer> stopList;
		final ChromosomeListOfLists<int[]> exonStartsList;
		final ChromosomeListOfLists<int[]> exonStopsList;
		final ChromosomeListOfLists<double[]> exonScoresList;

		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		nameList = new ChromosomeArrayListOfLists<String>();
		strandList = new ChromosomeArrayListOfLists<Strand>();
		exonStartsList = new ChromosomeArrayListOfLists<int[]>();
		exonStopsList = new ChromosomeArrayListOfLists<int[]>();
		exonScoresList = new ChromosomeArrayListOfLists<double[]>();
		// initialize the sublists
		for (int i = 0; i < projectChromosome.size(); i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			nameList.add(new ArrayList<String>());
			strandList.add(new ArrayList<Strand>());
			exonStartsList.add(new ArrayList<int[]>());
			exonStopsList.add(new ArrayList<int[]>());
			exonScoresList.add(new ArrayList<double[]>());

			List<ScoredChromosomeWindow> currentList = scwList.get(i);
			String prefixName = projectChromosome.get(i).getName() + ".";
			for (int j = 0; j < currentList.size(); j++) {
				ScoredChromosomeWindow window = currentList.get(j);
				startList.get(i).add(window.getStart());
				stopList.get(i).add(window.getStop());
				nameList.get(i).add(prefixName + (j + 1));
				strandList.get(i).add(Strand.FIVE);

				int[] exonStarts = new int[1];
				int[] exonStops = new int[1];
				double[] exonScores = new double[1];
				exonStarts[0] = window.getStart();
				exonStops[0] = window.getStop();
				exonScores[0] = window.getScore();
				exonStartsList.get(i).add(exonStarts);
				exonStopsList.get(i).add(exonStops);
				exonScoresList.get(i).add(exonScores);
			}
		}

		initialize(nameList, strandList, startList, stopList, exonStartsList, exonStopsList, exonScoresList, null, null);
	}


	/**
	 * Performs a deep clone of the current GeneList
	 * @return a new GeneList
	 */
	public GeneList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((GeneList)ois.readObject());
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
			return null;
		}
	}


	/**
	 * Organizes the list of genes by line so two genes don't overlap on the screen
	 */
	@Override
	protected void fitToScreen() {
		List<Gene> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			fittedDataList = null;
			return;
		}

		if ((currentList == null) || (currentList.isEmpty())) {
			fittedDataList = null;
			return;
		}

		fittedDataList = new ArrayList<List<Gene>>();
		// how many genes have been organized
		int organizedGeneCount = 0;
		// which genes have already been selected and organized
		boolean[] organizedGenes = new boolean[currentList.size()];
		Arrays.fill(organizedGenes, false);
		int currentLine = 0;
		boolean isGeneNamePrinted = (fittedXRatio > MIN_X_RATIO_PRINT_NAME) && (fontMetrics != null);
		// loop until every gene has been organized
		while (organizedGeneCount < currentList.size()) {
			fittedDataList.add(new ArrayList<Gene>());
			// we loop on the gene list
			for (int i = 0; i < currentList.size(); i++) {
				// if the current gene has not been organized yet
				if (!organizedGenes[i]) {
					// if the current line is empty we add the current gene
					if (fittedDataList.get(currentLine).size() == 0) {
						fittedDataList.get(currentLine).add(currentList.get(i));
						organizedGenes[i] = true;
						organizedGeneCount++;
					} else {
						long currentStart = genomePosToAbsoluteScreenPos(currentList.get(i).getStart(), fittedXRatio);
						long previousStop;
						// if we don't print the gene names the previous stop is the stop position of the gene + the minimum length between two genes
						int lastIndex = fittedDataList.get(currentLine).size() - 1;
						if (!isGeneNamePrinted) {
							previousStop = genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getStop(), fittedXRatio) + MIN_DISTANCE_BETWEEN_2_GENES;
						} else { // if we print the name the previous stop is the max between the stop of the gene and the end position of the name of the gene (+ MIN_DISTANCE_BETWEEN_2_GENES in both case)
							long previousNameStop = fontMetrics.stringWidth(fittedDataList.get(currentLine).get(lastIndex).getName()) + genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getStart(), fittedXRatio);
							long previousGeneStop = genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getStop(), fittedXRatio);
							previousStop = (previousNameStop > previousGeneStop) ? previousNameStop : previousGeneStop;
							previousStop += MIN_DISTANCE_BETWEEN_2_GENES;
						}
						// if the current gene won't overlap with the previous one we add it to the current line of the list of organized genes
						if (currentStart > previousStop) {
							fittedDataList.get(currentLine).add(currentList.get(i));
							organizedGenes[i] = true;
							organizedGeneCount++;
						}
					}
				}
			}
			currentLine++;
		}
	}


	/**
	 * @param position a position on the genome
	 * @param factor a ratio between the number of positions to display and the width of the screen
	 * @return a position on the screen
	 */
	private long genomePosToAbsoluteScreenPos(int position, double factor) {
		return Math.round(position * factor);
	}


	@Override
	protected List<List<Gene>> getFittedData(int start, int stop) {
		ChromosomeWindow startGenomeWindow = new SimpleChromosomeWindow(start, start);
		ChromosomeWindow stopGenomeWindow = new SimpleChromosomeWindow(stop, stop);
		if (fittedDataList == null) {
			return null;
		}
		List<List<Gene>> resultList = new ArrayList<List<Gene>>();
		// search genes for each line
		for (List<Gene> currentLine : fittedDataList) {
			// search the start
			int indexStart = Collections.binarySearch(currentLine, startGenomeWindow, new ChromosomeWindowStartComparator());
			if (indexStart < 0) {
				indexStart = -indexStart - 1;
			}
			indexStart = Math.max(0, indexStart - 1);
			// search the stop
			int indexStop = Collections.binarySearch(currentLine, stopGenomeWindow, new ChromosomeWindowStartComparator());
			if (indexStop < 0) {
				indexStop = -indexStop - 1;
			}
			indexStop = Math.min(currentLine.size(), indexStop);
			if (currentLine.get(indexStart) != null) {
				// add all the genes found for the current line between index start and index stop to the result list
				resultList.add(new ArrayList<Gene>());
				for (int i = indexStart; i < indexStop; i++) {
					resultList.get(resultList.size() - 1).add(currentLine.get(i));
				}
			}
		}
		return resultList;
	}


	/**
	 * @return the type of gene and exon scores (base coverage, maximum coverage, RPKM)
	 */
	public GeneScoreType getGeneScoreType() {
		return geneScoreType;
	}


	/**
	 * @return the geneSearcher
	 */
	public GeneSearcher getGeneSearcher() {
		// create a gene searcher if it doesn't exist
		if (geneSearcher == null) {
			geneSearcher = new GeneSearcher(this);
		}
		return geneSearcher;
	}


	/**
	 * @return the URL of the gene database
	 */
	public String getSearchURL() {
		return searchURL;
	}


	/**
	 * @return the list of start positions
	 */
	public ChromosomeListOfLists<Integer> getStartList () {
		ChromosomeListOfLists<Integer> list = new ChromosomeArrayListOfLists<Integer>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			list.add(new IntArrayAsIntegerList());
		}

		for (int i = 0; i < projectChromosome.size(); i++) {
			List<Gene> currentList = this.get(i);
			for (Gene gene: currentList) {
				list.get(i).add(gene.getStart());
			}
		}

		return list;
	}


	/**
	 * @return the list of start positions
	 */
	public ChromosomeListOfLists<Integer> getStopList () {
		ChromosomeListOfLists<Integer> list = new ChromosomeArrayListOfLists<Integer>();
		for (int i = 0; i < projectChromosome.size(); i++) {
			list.add(new IntArrayAsIntegerList());
		}

		for (int i = 0; i < projectChromosome.size(); i++) {
			List<Gene> currentList = this.get(i);
			for (Gene gene: currentList) {
				list.get(i).add(gene.getStop());
			}
		}

		return list;
	}

	/**
	 * Creates an instance of {@link GeneList}
	 * @param nameList a list of gene names
	 * @param strandList a list of {@link Strand}
	 * @param startList a list of start positions
	 * @param stopList a list of stop positions
	 * @param exonStartsList a list of exon start arrays
	 * @param exonStopsList a list of exon stop arrays
	 * @param exonScoresList a list of exon score arrays
	 * @param searchURL url of the gene database
	 * @param geneScoreType type of the scores of the exons and genes of the gene list (RPKM, max, sum)
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private void initialize (final ChromosomeListOfLists<String> nameList, final ChromosomeListOfLists<Strand> strandList,
			final ChromosomeListOfLists<Integer> startList, final ChromosomeListOfLists<Integer> stopList, final ChromosomeListOfLists<int[]> exonStartsList,
			final ChromosomeListOfLists<int[]> exonStopsList, final ChromosomeListOfLists<double[]> exonScoresList, String searchURL, GeneScoreType geneScoreType)
					throws InvalidChromosomeException, InterruptedException, ExecutionException {
		this.searchURL = searchURL;
		this.geneScoreType = geneScoreType;
		// retrieve the instance of the OperationPool
		final OperationPool op = OperationPool.getInstance();
		// list for the threads
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(final Chromosome currentChromosome : projectChromosome) {
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					List<Gene> resultList = new ArrayList<Gene>();
					for(int j = 0; j < nameList.size(currentChromosome); j++) {
						String name = nameList.get(currentChromosome, j);
						Strand strand = strandList.get(currentChromosome, j);
						int txStart = startList.get(currentChromosome, j);
						int txStop = stopList.get(currentChromosome, j);
						int[] exonStarts = null;
						if (exonStartsList.size(currentChromosome) > 0) {
							exonStarts = exonStartsList.get(currentChromosome, j);
						}
						int[] exonStops = null;
						if (exonStopsList.size(currentChromosome) > 0) {
							exonStops = exonStopsList.get(currentChromosome, j);
						}
						double[] exonScores = null;
						if ((exonScoresList != null) && (exonScoresList.size(currentChromosome) > 0)) {
							exonScores = exonScoresList.get(currentChromosome, j);
						}
						// we don't add a gene if it is located after the end of a chromosome
						if (txStop < currentChromosome.getLength()) {
							resultList.add(new Gene(name, currentChromosome, strand, txStart, txStop, exonStarts, exonStops, exonScores));
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Gene>> result = null;
		// starts the pool
		result = op.startPool(threadList);
		// add the chromosome results
		if (result != null) {
			for (List<Gene> currentList: result) {
				add(currentList);
			}
		}
		// sort the gene list
		for (List<Gene> chromosomeGeneList : this) {
			Collections.sort(chromosomeGeneList);
		}
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		projectChromosome = (ProjectChromosome) in.readObject();
		fontMetrics = (FontMetrics) in.readObject();
		searchURL = (String) in.readObject();
		geneSearcher = null;
	}


	/**
	 * Sets the {@link FontMetrics}. Used for the generation of fitted data
	 * @param fontMetrics
	 */
	public void setFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
	}


	/**
	 * Sets the type of the exon and gene scores (base coverage, maximum coverage, RPKM)
	 * @param geneScoreType
	 */
	public void setGeneScoreType(GeneScoreType geneScoreType) {
		this.geneScoreType = geneScoreType;
	}


	/**
	 * Sets the URL of the gene database
	 * @param searchURL
	 */
	public void setSearchURL(String searchURL) {
		this.searchURL = searchURL;
	}


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(projectChromosome);
		out.writeObject(fontMetrics);
		out.writeObject(searchURL);
	}

}
