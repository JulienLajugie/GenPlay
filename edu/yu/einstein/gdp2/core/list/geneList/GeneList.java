/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList;

import java.awt.FontMetrics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.exception.InvalidChromosomeException;

/**
 * A list of {@link Gene} with tool to rescale it
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneList extends DisplayableListOfLists<Gene, List<List<Gene>>> implements Serializable {

	private static final long serialVersionUID = 1068181566225377150L; 	// generated ID
	private static final int 	MIN_DISTANCE_BETWEEN_2_GENES = 5;	// minimum distance in pixel between two genes 
	private FontMetrics			fontMetrics = null;
	private Gene				lastSearchedGene = null;			// last searched gene
	private String				searchURL = null;					// URL of the gene database
	
	/**
	 * The name of the genes are printed if the horizontal ratio is above this value
	 */
	public  static final double	MIN_X_RATIO_PRINT_NAME = 0.0005d;	
		

	/**
	 * Creates an instance of {@link GeneList}
	 */
	public GeneList() {
		super();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			add(new ArrayList<Gene>());
		}
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
	 */
	public GeneList(ChromosomeListOfLists<String> nameList, ChromosomeListOfLists<Strand> strandList, 
			ChromosomeListOfLists<Integer> startList, ChromosomeListOfLists<Integer> stopList, ChromosomeListOfLists<int[]> exonStartsList, 
			ChromosomeListOfLists<int[]> exonStopsList, ChromosomeListOfLists<double[]> exonScoresList, String searchURL) 
	throws InvalidChromosomeException {
		super();
		if (searchURL != null) {
			this.searchURL = searchURL;
		}
		for(short i = 0; i < nameList.size(); i++) {
			add(new ArrayList<Gene>());
			Chromosome chromo = chromosomeManager.get(i);
			for(int j = 0; j < nameList.size(i); j++) {
				String name = nameList.get(i, j);
				Strand strand = strandList.get(i, j);
				int txStart = startList.get(i, j);
				int txStop = stopList.get(i, j);
				int[] exonStarts = null;
				if (exonStartsList.size(i) > 0) {
					exonStarts = exonStartsList.get(i, j);
				}
				int[] exonStops = null;
				if (exonStopsList.size(i) > 0) {
					exonStops = exonStopsList.get(i, j);
				}
				double[] exonScores = null;
				if ((exonScoresList != null) && (exonScoresList.size(i) > 0)) {
					exonScores = exonScoresList.get(i, j);	
				}			
				add(chromo, new Gene(name, chromo, strand, txStart, txStop, exonStarts, exonStops, exonScores));
			}
		}
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
		} else if (value == list.get(indexStart + middle).getTxStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getTxStart()) {
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
		} else if (value == list.get(indexStart + middle).getTxStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getTxStop()) {
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
						long currentStart = genomePosToAbsoluteScreenPos(currentList.get(i).getTxStart(), fittedXRatio);
						long previousStop;
						// if we don't print the gene names the previous stop is the stop position of the gene + the minimum length between two genes 
						int lastIndex = fittedDataList.get(currentLine).size() - 1;
						if (!isGeneNamePrinted) {
							previousStop = genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getTxStop(), fittedXRatio) + MIN_DISTANCE_BETWEEN_2_GENES;
						} else { // if we print the name the previous stop is the max between the stop of the gene and the end position of the name of the gene (+ MIN_DISTANCE_BETWEEN_2_GENES in both case) 
							long previousNameStop = fontMetrics.stringWidth(fittedDataList.get(currentLine).get(lastIndex).getName()) + genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getTxStart(), fittedXRatio);
							long previousGeneStop = genomePosToAbsoluteScreenPos(fittedDataList.get(currentLine).get(lastIndex).getTxStop(), fittedXRatio);
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
	 * @return the last searched gene
	 */
	public Gene getLastSearchedGene() {
		return lastSearchedGene;
	}


	/**
	 * Returns the first gene of the gene list called <i>name</i>.
	 * Returns null if there is no gene with this name.
	 * @param name name of the gene
	 * @return A gene called <i>name</i>. Return null if not found.
	 */
	public Gene search(String name) {
		if (this.isEmpty()) {
			return null;
		} else {
			// if the searched gene is the same as the previous searched one
			if ((lastSearchedGene != null) && (lastSearchedGene.equals(name))) {
				return lastSearchedGene;
			}
			boolean found = false;
			Gene geneFound = null;
			short i = 0;
			while ((i < chromosomeManager.size()) && (!found)) {
				if (get(i) != null) {
					int j = 0;
					while ((j < size(i)) && (!found)) {
						if (get(i, j).equals(name)) {
							geneFound = get(i, j);
							found = true;
						}
						j++;
					}					
				}
				i++;
			}
			lastSearchedGene = geneFound;
			return geneFound;
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
}
