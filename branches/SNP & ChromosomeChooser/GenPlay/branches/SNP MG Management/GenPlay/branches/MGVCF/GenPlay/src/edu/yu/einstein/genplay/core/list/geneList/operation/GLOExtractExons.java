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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.enums.Strand;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Extracts exons of each gene of a {@link GeneList}
 * @author Chirag Gorasia
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOExtractExons implements Operation<GeneList> {

	private final GeneList 	geneList;			// input list
	private final int 		exonOption;			// exon option: first, last or all
	private boolean			stopped = false;	// true if the operation must be stopped
	/**
	 * extract the first exon
	 */
	public static final int FIRST_EXON = 0;
	/**
	 * extract the last exon
	 */
	public static final int LAST_EXON = 1;
	/**
	 * Extract all the exons
	 */
	public static final int ALL_EXONS = 2;


	/**
	 * Creates an instance of {@link GLOExtractExons}
	 * @param geneList
	 */
	public GLOExtractExons(GeneList geneList, int exonOption) {
		this.geneList = geneList;		
		this.exonOption = exonOption;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(final List<Gene> currentList: geneList) {
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {					
					if (currentList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; j < currentList.size() && !stopped; j++) {
						Gene currentGene = currentList.get(j); 
						switch (exonOption) {
						case FIRST_EXON:
							resultList.add(extractFirstExon(currentGene));
							break;
						case LAST_EXON:
							resultList.add(extractLastExon(currentGene));
							break;
						case ALL_EXONS:
							resultList.addAll(extractAllExon(currentGene));							
							break;
						default:
							// invalid argument
							throw new IllegalArgumentException("Invalid Choice for Exon");
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return new GeneList(result, geneList.getSearchURL());
		}
	}


	/**
	 * @param inputGene a {@link Gene}
	 * @return a gene representing the first exon of the specified gene
	 */
	private Gene extractFirstExon(Gene inputGene) {
		Gene outputGene = new Gene(inputGene);
		if (outputGene.getStrand() == Strand.FIVE) {
			// new start
			outputGene.setStart(outputGene.getExonStarts()[0]);
			// new stop
			outputGene.setStop(outputGene.getExonStops()[0]);
			// new exon starts
			int[] exonStart = new int[1];
			exonStart[0] = outputGene.getExonStarts()[0];
			outputGene.setExonStarts(exonStart);
			// new exon stops
			int[] exonStop = new int[1];
			exonStop[0] = outputGene.getExonStops()[0];
			outputGene.setExonStops(exonStop);
			// new exon scores
			if (outputGene.getExonScores() != null) {
				double[] exonScore = new double[1];
				exonScore[0] = outputGene.getExonScores()[0];
				outputGene.setExonScores(exonScore);
			}
			// new name
			outputGene.setName(outputGene.getName() + "(1st)");
		} else {
			// new start
			outputGene.setStart(outputGene.getExonStarts()[outputGene.getExonStarts().length - 1]);
			// new stop
			outputGene.setStop(outputGene.getExonStops()[outputGene.getExonStops().length - 1]);
			// new exon starts
			int[] exonStart = new int[1];
			exonStart[0] = outputGene.getExonStarts()[outputGene.getExonStarts().length - 1];
			outputGene.setExonStarts(exonStart);
			// new exon stops
			int[] exonStop = new int[1];
			exonStop[0] = outputGene.getExonStops()[outputGene.getExonStops().length - 1];
			outputGene.setExonStops(exonStop);
			// new exon scores
			if (outputGene.getExonScores() != null) {
				double[] exonScore = new double[1];
				exonScore[0] = outputGene.getExonScores()[outputGene.getExonScores().length - 1];
				outputGene.setExonScores(exonScore);
			}
			// new name
			outputGene.setName(outputGene.getName() + "(1st)");
		}
		return outputGene;
	}


	/**
	 * @param inputGene a {@link Gene}
	 * @return a gene representing the last exon of the specified gene
	 */
	private Gene extractLastExon(Gene inputGene) {
		Gene outputGene = new Gene(inputGene);
		if (outputGene.getStrand() == Strand.FIVE) {
			// new start
			outputGene.setStart(outputGene.getExonStarts()[outputGene.getExonStarts().length - 1]);
			// new stop
			outputGene.setStop(outputGene.getExonStops()[outputGene.getExonStops().length - 1]);
			// new exon starts
			int[] exonStart = new int[1];
			exonStart[0] = outputGene.getExonStarts()[outputGene.getExonStarts().length - 1];
			outputGene.setExonStarts(exonStart);
			// new exon stops
			int[] exonStop = new int[1];
			exonStop[0] = outputGene.getExonStops()[outputGene.getExonStops().length - 1];
			outputGene.setExonStops(exonStop);
			// new exon scores
			if (outputGene.getExonScores() != null) {
				double[] exonScore = new double[1];
				exonScore[0] = outputGene.getExonScores()[outputGene.getExonScores().length - 1];
				outputGene.setExonScores(exonScore);
			}
			// new name
			outputGene.setName(outputGene.getName() + "(last)");
		} else {
			// new start
			outputGene.setStart(outputGene.getExonStarts()[0]);
			// new stop
			outputGene.setStop(outputGene.getExonStops()[0]);
			// new exon starts
			int[] exonStart = new int[1];
			exonStart[0] = outputGene.getExonStarts()[0];
			outputGene.setExonStarts(exonStart);
			// new exon stops
			int[] exonStop = new int[1];
			exonStop[0] = outputGene.getExonStops()[0];			
			outputGene.setExonStops(exonStop);
			// new exon scores
			if (outputGene.getExonScores() != null) {
				double[] exonScore = new double[1];
				exonScore[0] = outputGene.getExonScores()[0];
				outputGene.setExonScores(exonScore);
			}
			// new name
			outputGene.setName(outputGene.getName() + "(last)");
		}
		return outputGene;
	}


	/**
	 * @param inputGene a {@link Gene}
	 * @return a gene list representing each exon of the specified gene
	 */
	private List<Gene> extractAllExon(Gene inputGene) {
		List<Gene> outputGeneList = new ArrayList<Gene>();
		if (inputGene.getStrand() == Strand.FIVE) {								
			for (int i = 0; i < inputGene.getExonStarts().length; i++) {
				Gene outputGene = new Gene(inputGene);
				// new start
				outputGene.setStart(outputGene.getExonStarts()[i]);
				// new stop
				outputGene.setStop(outputGene.getExonStops()[i]);
				// new exon starts
				int[] exonStart = new int[1];
				exonStart[0] = outputGene.getExonStarts()[i];
				outputGene.setExonStarts(exonStart);
				// new exon stops
				int[] exonStop = new int[1];
				exonStop[0] = outputGene.getExonStops()[i];
				outputGene.setExonStops(exonStop);
				// new exon scores
				if ((outputGene.getExonScores() != null) && (outputGene.getExonScores().length > 0)) {
					double[] exonScore = new double[1];
					if (outputGene.getExonScores().length == 1) {
						exonScore[0] = outputGene.getExonScores()[0];
					} else {
						exonScore[0] = outputGene.getExonScores()[i];
					}
					outputGene.setExonScores(exonScore);
				}
				// new name				
				outputGene.setName(outputGene.getName() + "(" + Integer.toString(i + 1) + ")");
				// add new gene
				outputGeneList.add(outputGene);
			}
		} else {
			for (int i = inputGene.getExonStarts().length - 1; i >= 0; i--) {
				Gene outputGene = new Gene(inputGene);
				// new start
				outputGene.setStart(outputGene.getExonStarts()[i]);
				// new stop
				outputGene.setStop(outputGene.getExonStops()[i]);
				// new exon starts
				int[] exonStart = new int[1];
				exonStart[0] = outputGene.getExonStarts()[i];
				outputGene.setExonStarts(exonStart);
				// new exon stops
				int[] exonStop = new int[1];
				exonStop[0] = outputGene.getExonStops()[i];
				outputGene.setExonStops(exonStop);
				// new exon scores
				if ((outputGene.getExonScores() != null) && (outputGene.getExonScores().length > 0)) { 
					double[] exonScore = new double[1];
					if (outputGene.getExonScores().length == 1) {						
						exonScore[0] = outputGene.getExonScores()[0];
					} else {
						exonScore[0] = outputGene.getExonScores()[i];
					}
					outputGene.setExonScores(exonScore);
				}				
				// new name
				outputGene.setName(outputGene.getName() + "(" + Integer.toString(inputGene.getExonStarts().length - i) + ")");
				// add new gene
				outputGeneList.add(outputGene);
			}
		}
		return outputGeneList;
	}


	@Override
	public String getDescription() {
		switch (exonOption) {
		case 0:
			return "Operation: Extract First Exons";
		case 1:
			return "Operation: Extract Last Exons";
		case 2:
			return "Operation: Extract All Exons";
		default:
			return "Operation: Extract Exons";
		}
	}


	@Override
	public String getProcessingDescription() {
		return "Extracting Exons";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
