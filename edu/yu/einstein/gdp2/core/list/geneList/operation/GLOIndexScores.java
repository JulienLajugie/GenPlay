/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;


/**
 * Indexes the score values of a {@link GeneList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GLOIndexScores implements Operation<GeneList> {

	private final GeneList 	geneList;		// input GeneList


	/**
	 * Indexes the score values of a {@link GeneList}
	 * @param geneList input {@link GeneList}
	 */
	public GLOIndexScores(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
	}


	@Override
	public GeneList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		// compute the distance between the min and the max
		final double min = new GLOMin(geneList, null).compute();
		double max = new GLOMax(geneList, null).compute();
		double distanceMinMax = max - min;
		final double indexFactor = 1000d / distanceMinMax;

		for (int i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);

			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {
					if (currentList == null) {
						return null;
					}
					List<Gene> resultList = new ArrayList<Gene>();
					for (Gene currentGene: currentList) {
						if (currentGene != null) {
							Gene copyCurrentGene = new Gene(currentGene);
							if (copyCurrentGene.getExonScores() != null){
								for(int j = 0; j < copyCurrentGene.getExonScores().length; j++) {
									copyCurrentGene.getExonScores()[j] = (copyCurrentGene.getExonScores()[j] - min) * indexFactor;							
								}
							}
							resultList.add(copyCurrentGene);
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
		return "Operation: Index Scores";
	}


	@Override
	public int getStepCount() {
		// 1 for the min, 1 for the max and 1 for the indexation
		return 3;
	}


	@Override
	public String getProcessingDescription() {
		return "Indexing Scores";
	}
}
