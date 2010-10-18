/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Attributes a score to the exons of a GeneList from the scores of a BinList
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOScoreFromBinList implements Operation<GeneList> {
	private final GeneList 					geneList;	// input GeneList
	private final BinList 					binList;	// BinList with the scores
	private final ScoreCalculationMethod 	method;		// method to use to compute the score
	private boolean stopped = false;	// true if the writer needs to be stopped 


	/**
	 * Creates an instance of {@link GLOScoreFromBinList}
	 * @param geneList input GeneList
	 * @param binList BinList with the scores
	 * @param method method to use to compute the score
	 */
	public GLOScoreFromBinList(GeneList geneList, BinList binList, ScoreCalculationMethod method) {
		this.geneList = geneList;
		this.binList = binList;
		this.method = method;
	}


	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		for(int i = 0; i < geneList.size(); i++) {
			final List<Double> currentBinList = binList.get(i);
			final List<Gene> currentGeneList = geneList.get(i);
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {					
					if ((currentGeneList == null) || (currentBinList == null)) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (int j = 0; j < currentGeneList.size() && !stopped; j++) {
						Gene currentGene = currentGeneList.get(j);
						if ((currentGene != null) && (currentGene.getExonStarts() != null) && (currentGene.getExonStarts().length != 0))  {
							double[] scores = new double[currentGene.getExonStarts().length] ;
							for (int k = 0; k < currentGene.getExonStarts().length && !stopped; k++) {
								int start = (int) (currentGene.getExonStarts()[k] / (double) binList.getBinSize());
								int stop = (int) (currentGene.getExonStops()[k] / (double) binList.getBinSize());
								int count = 0; // used for the average
								for (int l = start; l <= stop && !stopped; l++) {
									if ((l < currentBinList.size()) && (currentBinList.get(l) != 0)) {
										switch (method) {
										case AVERAGE:
											scores[k] = (scores[k] * count + currentBinList.get(l)) / (count + 1);
											count++;									
											break;
										case MAXIMUM:
											scores[k] = Math.max(scores[k], currentBinList.get(l));
											break;
										case SUM:
											scores[k] = scores[k] + currentBinList.get(l);
											break;
										}
									}
								}
								System.out.println(scores[k]);
							}
							Gene geneToAdd = new Gene(currentGeneList.get(j));
							geneToAdd.setExonScores(scores);
							resultList.add(geneToAdd);
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


	@Override
	public String getDescription() {
		return "Genes Scored from Fixed Variable Track";
	}


	@Override
	public String getProcessingDescription() {
		return "Scoring the Genes";
	}


	@Override
	public int getStepCount() {
		return 2;
	}


	@Override
	public void stop() {
		stopped = true;
	}	
}
