package yu.einstein.gdp2.core.list.geneList;

import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.Strand;

public class GeneListOperations {

	/**
	 * before the start position (used for interval extraction)
	 */
	public static final int BEFORE_START = 0;

	/**
	 * after the start position (used for interval extraction)
	 */
	public static final int AFTER_START = 1;

	/**
	 * before the stop position (used for interval extraction)
	 */
	public static final int BEFORE_STOP = 2;

	/**
	 * after the stop position (used for interval extraction)
	 */
	public static final int AFTER_STOP = 3;
	
	
	/**
	 * extract intervals relative to genes
	 * @param startDistance
	 * @param startFrom can take the 4 different values 
	 * @param stopDistance
	 * @param stopFrom
	 * @return
	 */
	public static GeneList extractIntevals(GeneList geneList, int startDistance, int startFrom, int stopDistance, int stopFrom) {
		GeneList resultList = new GeneList(geneList.getChromosomeManager());

		for(short i = 0; i < geneList.size(); i++) {
			int chromoLength = geneList.getChromosomeManager().getChromosome(i).getLength();
			if (geneList.get(i) != null) {
				for(int j = 0; j < geneList.size(i); j++) {
					if (geneList.get(i, j) != null) {
						Gene currentGene = geneList.get(i, j); 
						Gene geneToAdd = new Gene(geneList.get(i, j));
						// search the new start
						int newStart = 0;
						switch (startFrom) {
						case BEFORE_START:
							if (currentGene.getStrand() == Strand.five) {
								newStart = Math.max(0, currentGene.getTxStart() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getTxStop() + startDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.five) {
								newStart = Math.min(chromoLength, currentGene.getTxStart() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getTxStop() - startDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.five) {
								newStart = Math.max(0, currentGene.getTxStop() - startDistance);
							} else {
								newStart = Math.min(chromoLength, currentGene.getTxStart() + startDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.five) {
								newStart = Math.min(chromoLength, currentGene.getTxStop() + startDistance);
							} else {
								newStart = Math.max(0, currentGene.getTxStart() - startDistance);
							}
							break;
						default:
							// invalid argument
							return null;
						}
						// search the new stop
						int newStop = 0;
						switch (stopFrom) {
						case BEFORE_START:
							if (currentGene.getStrand() == Strand.five) {
								newStop = Math.max(0, currentGene.getTxStart() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getTxStop() + stopDistance);
							}
							break;
						case AFTER_START:
							if (currentGene.getStrand() == Strand.five) {
								newStop = Math.min(chromoLength, currentGene.getTxStart() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getTxStop() - stopDistance);
							}
							break;
						case BEFORE_STOP:
							if (currentGene.getStrand() == Strand.five) {
								newStop = Math.max(0, currentGene.getTxStop() - stopDistance);
							} else {
								newStop =  Math.min(chromoLength, currentGene.getTxStart() + stopDistance);
							}
							break;
						case AFTER_STOP:
							if (currentGene.getStrand() == Strand.five) {
								newStop = Math.min(chromoLength, currentGene.getTxStop() + stopDistance);
							} else {
								newStop = Math.max(0, currentGene.getTxStart() - stopDistance);
							}
							break;
						default:
							// invalid argument
							return null;
						}
						geneToAdd.setExonStarts(null);
						geneToAdd.setExonStops(null);
						geneToAdd.setExonScores(null);
						// add the new gene
						if ((newStart < newStop) && (currentGene.getStrand() == Strand.five)) {
							geneToAdd.setTxStart(newStart);
							geneToAdd.setTxStop(newStop);
							resultList.get(i).add(geneToAdd);
						} else if ((newStart > newStop) && (currentGene.getStrand() == Strand.three)) {
							geneToAdd.setTxStart(newStop);
							geneToAdd.setTxStop(newStart);
							resultList.get(i).add(geneToAdd);	
						}
					}
				}
			}
		}
		// we sort the result list
		// the ordre might have changed because on the 3'strand
		// the start of a gene is what was the end before 
		for (List<Gene> chromosomeGeneList : resultList) {
			Collections.sort(chromosomeGeneList);
		}
		return resultList;
	}
	
	
	/**
	 * Indexes the score values of a {@link GeneList}
	 */
	public static void indexScores(GeneList geneList) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for(List<Gene> currentList : geneList) {
			if (currentList != null) {
				for(Gene currentGene : currentList) {
					if ((currentGene != null) && (currentGene.getExonScores() != null)){
						for(double currentScore : currentGene.getExonScores()) {
							min = Math.min(min, currentScore);
							max = Math.max(max, currentScore);
						}
					}
				}
			}
		}
		double minMaxDist = max - min;
		for(List<Gene> currentList : geneList) {
			if (currentList != null) {
				for(Gene currentGene : currentList) {
					if ((currentGene != null) && (currentGene.getExonScores() != null)){
						for(int i = 0; i < currentGene.getExonScores().length; i++) {
							currentGene.getExonScores()[i] = 1000d * (currentGene.getExonScores()[i] - min) / minMaxDist;							
						}
					}
				}
			}
		}		
	}
}
