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
 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLODensity implements BinListOperation<BinList> {

	private final BinList 	binList;	// input BinList
	private final int		halfWidth; 	// half size of the region (in number of bin)


	/**
	 * Computes the density of bin with values on a region of halfWidth * 2 + 1 bins
	 * @param binList input {@link BinList}
	 * @param halfWidth half size of the region (in number of bin)
	 */
	public BLODensity(BinList binList, int halfWidth) {
		this.binList = binList;
		this.halfWidth = halfWidth;
	}


	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		// the result is returned in 32 bits because the result is btw 0 and 1
		final DataPrecision defaultPrecision = DataPrecision.PRECISION_32BIT;
		final int binCount = 2 * halfWidth + 1;

		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(defaultPrecision, currentList.size());
						// We compute the density for each bin
						for (int j = 0; j < currentList.size(); j++) {
							int noneZeroBinCount = 0;
							for (int k = -halfWidth; k <= halfWidth; k++) {
								if((j + k >= 0) && ((j + k) < currentList.size()))  {
									if (currentList.get(j + k) != 0) {
										noneZeroBinCount++;
									}
								}
							}
							resultList.set(j, noneZeroBinCount / (double)binCount);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};

			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binList.getBinSize(), defaultPrecision, result);
			return resultList;
		} else {
			return null;
		}
	}
	

	@Override
	public String getDescription() {
		return "Operation: Density, Region Size = " + (halfWidth * 2 + 1) + " Bins";
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
}
