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
package edu.yu.einstein.genplay.core.list.geneList;

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

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * A list of {@link Gene} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneList extends DisplayableListOfLists<Gene, List<List<Gene>>> implements Serializable {

	private static final long serialVersionUID = 1068181566225377150L; 	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	MIN_DISTANCE_BETWEEN_2_GENES = 5;		// minimum distance in pixel between two genes
	private ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome(); // Instance of the Chromosome Manager
	private FontMetrics			fontMetrics = null;						// dimension of the font used to print the name of the genes
	private String				searchURL = null;						// URL of the gene database
	private GeneSearcher		geneSearcher = null;					// object used to search genes
	
	
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
	 * The name of the genes are printed if the horizontal ratio is above this value
	 */
	public  static final double	MIN_X_RATIO_PRINT_NAME = 0.0005d;	
		
	
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
	 */
	public GeneList(Collection<? extends List<Gene>> data, String searchURL) {
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
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public GeneList(final ChromosomeListOfLists<String> nameList, final ChromosomeListOfLists<Strand> strandList, 
			final ChromosomeListOfLists<Integer> startList, final ChromosomeListOfLists<Integer> stopList, final ChromosomeListOfLists<int[]> exonStartsList, 
			final ChromosomeListOfLists<int[]> exonStopsList, final ChromosomeListOfLists<double[]> exonScoresList, String searchURL) 
	throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		if (searchURL != null) {
			this.searchURL = searchURL;
		}
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
	 * @throws InvalidChromosomeException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public GeneList(final ChromosomeListOfLists<String> nameList, final ChromosomeListOfLists<Strand> strandList, final ChromosomeListOfLists<Integer> startList,
			final ChromosomeListOfLists<Integer> stopList, final ChromosomeListOfLists<Integer> UTR5BoundList, final ChromosomeListOfLists<Integer> UTR3BoundList, 
			final ChromosomeListOfLists<int[]> exonStartsList, final ChromosomeListOfLists<int[]> exonStopsList, final ChromosomeListOfLists<double[]> exonScoresList, 
			String searchURL) throws InvalidChromosomeException, InterruptedException, ExecutionException {
		super();
		if (searchURL != null) {
			this.searchURL = searchURL;
		}
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
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value. 
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int findStart(List<Gene> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value.
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position stop equals to value. 
	 * Index of the first gene with a stop position superior to value if nothing found.
	 */
	private int findStop(List<Gene> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Organizes the list of genes by line so two genes don't overlap on the screen
	 */
	protected void fitToScreen() {
		List<Gene> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
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
		return Math.round((double)position * factor);
	}


	@Override
	protected List<List<Gene>> getFittedData(int start, int stop) {
		if (fittedDataList == null) {
			return null;
		}
		List<List<Gene>> resultList = new ArrayList<List<Gene>>();
		// search genes for each line
		for (List<Gene> currentLine : fittedDataList) { 
			// search the start
			int indexStart = findStart(currentLine, start, 0, currentLine.size() - 1);
			// search if the there is a previous stop (stop of the gene or stop of the name of the string)stopping after the start
			if (indexStart > 0) {
				indexStart = indexStart - 1;
			}
			// search the stop
			int indexStop = findStop(currentLine, stop, 0, currentLine.size() - 1);
			if (currentLine.get(indexStart) != null) { 
				// add all the genes found for the current line between index start and index stop to the result list 
				resultList.add(new ArrayList<Gene>());
				for (int i = indexStart; i <= indexStop; i++) {
					resultList.get(resultList.size() - 1).add(currentLine.get(i));
				}
			}
		}
		return resultList;
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
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the {@link FontMetrics}. Used for the generation of fitted data
	 * @param fontMetrics
	 */
	public void setFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
	}


	/**
	 * @return the URL of the gene database
	 */
	public String getSearchURL() {
		return searchURL;
	}
	
	
	/**
	 * Sets the URL of the gene database
	 * @param searchURL
	 */
	public void setSearchURL(String searchURL) {
		this.searchURL = searchURL;		
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
}
