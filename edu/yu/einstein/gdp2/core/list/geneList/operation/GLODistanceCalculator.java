package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.binList.operation.distanceCalculator.DistanceCalculator;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;

public class GLODistanceCalculator implements Operation<Double[][]>{
	
	private final GeneList 	geneList1;		// input BinList
	private final GeneList	geneList2;		// input BinList
	private final int selectionCase;
	
	private static final int POSITIVE_START_START = 1;
	private static final int POSITIVE_START_MIDDLE = 2;
	private static final int POSITIVE_START_STOP = 3;
	
	private static final int POSITIVE_MIDDLE_START = 4;
	private static final int POSITIVE_MIDDLE_MIDDLE = 5;
	private static final int POSITIVE_MIDDLE_STOP = 6;
	
	private static final int POSITIVE_STOP_START = 7;
	private static final int POSITIVE_STOP_MIDDLE = 8;
	private static final int POSITIVE_STOP_STOP = 9;
	
	private static final int NEGATIVE_START_START = 10;
	private static final int NEGATIVE_START_MIDDLE = 11;
	private static final int NEGATIVE_START_STOP = 12;
	
	private static final int NEGATIVE_MIDDLE_START = 13;
	private static final int NEGATIVE_MIDDLE_MIDDLE = 14;
	private static final int NEGATIVE_MIDDLE_STOP = 15;
	
	private static final int NEGATIVE_STOP_START = 16;
	private static final int NEGATIVE_STOP_MIDDLE = 17;
	private static final int NEGATIVE_STOP_STOP = 18;
	
	private static final int BOTH_RELATIVE = 19;
	private static final int BOTH_ABSOLUTE = 20;
	
	public GLODistanceCalculator(GeneList geneList1, GeneList geneList2, int selectionCase) {
		this.geneList1 = geneList1;
		this.geneList2 = geneList2;
		this.selectionCase = selectionCase;
	}
	@Override
	public Double[][] compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (int i = 0; i < geneList1.size(); i++) {
//			final List<Gene> firstList = geneList1.get(i);
//			final List<Gene> secondList = geneList2.get(i);
			final int currentIndex = i;
			Callable<Void> currentThread = new Callable<Void>() {	
				@Override
				public Void call() throws Exception {
					int k = 0;
					if ((geneList1 != null) && (geneList2 != null)) {
						double[][] result = handleCases(geneList1, geneList2, k);
						k++;
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		if (op.startPool(threadList) == null) {
			return null;
		}
		return null;
	}
	
	/**
	 * Method to handle the any of the 20 cases to compute the distance
	 */
	public double[][] handleCases(GeneList firstList, GeneList secondList, int k) {
		DistanceCalculator dc;
		double[] distanceArray = new double[firstList.size() * secondList.size()];
		switch(selectionCase) {
		case POSITIVE_START_START:	for (int i = 0; i < firstList.size(); i++) {
										for (int j = 0; j < firstList.get(i).size(); j++) {
											dc = new DistanceCalculator(secondList, i, 0, 0, 0, firstList.get(i,j).getTxStart());
											distanceArray[k] = dc.getClosestDistance();											
										}
									}
									break;
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Operation: Distance Calculation";
	}

	@Override
	public String getProcessingDescription() {
		return "Calculating Distance";
	}

	@Override
	public int getStepCount() {
		return 0;
	}

}