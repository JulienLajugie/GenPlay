/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.ListFactory;


/**
 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOGauss implements BinListOperation<BinList> {

	private final BinList binList;
	private final int sigma;

	
	/**
	 * Creates an instance of {@link BLOGauss}
	 * Applies a gaussian filter on the BinList and returns the result in a new BinList.
	 * @param binList {@link BinList} to gauss
	 * @param sigma parameter of the gaussian filter
	 */
	public BLOGauss(BinList binList, int sigma) {
		this.binList = binList;
		this.sigma = sigma;
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		final int binSize =  binList.getBinSize();
		final int halfWidth = 2 * sigma / binSize;
		// we create an array of coefficients. The index correspond to a distance and for each distance we calculate a coefficient 
		final double[] coefTab = new double[halfWidth + 1];
		for(int i = 0; i <= halfWidth; i++) {
			coefTab[i] = Math.exp(-(Math.pow(((double) (i * binSize)), 2) / (2.0 * Math.pow((double) sigma, 2))));
		}
		// we gauss
		for(short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {			 	
				@Override
				public List<Double> call() throws Exception {
					List<Double> listToAdd = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						listToAdd = ListFactory.createList(precision, currentList.size());
						for(int j = 0; j < currentList.size(); j++) {
							if(currentList.get(j) != 0)  {
								// apply the array of coefficients centered on the current value to gauss
								double SumCoef = 0;
								double SumNormSignalCoef = 0;
								for (int k = -halfWidth; k <= halfWidth; k++) {
									if((j + k >= 0) && ((j + k) < currentList.size()))  {
										int distance = Math.abs(k);
										if(currentList.get(j + k) != 0)  {
											SumCoef += coefTab[distance];
											SumNormSignalCoef += coefTab[distance] * currentList.get(j + k);
										}
									}
								}
								if(SumCoef == 0) {
									listToAdd.set(j, 0d);
								} else {
									listToAdd.set(j, SumNormSignalCoef / SumCoef);
								}
							} else {
								listToAdd.set(j, 0d);
							}
						}
					}
					op.notifyDone();
					return listToAdd;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binSize, precision, result);
			return resultList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getDescription() {
		return "Operation: Gauss, Sigma = " + sigma + "bp";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
}
