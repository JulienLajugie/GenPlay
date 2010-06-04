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
 * Adds a specified constant to the scores of each bin of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLOIslandFinder implements BinListOperation<BinList> {

	private final BinList 	binList;	// input binlist
	private final double 	read_count_limit;	// number limit of reads to get an eligible windows
	private final int		gap;	//minimum number of windows needed to separate 2 islands
	//private final ArrayList<Integer>	islands_start;	//start position for all islands
	//private final ArrayList<Integer>	islands_stop;	//stop position for all islands
	
	
	/**
	 * Adds a specified constant to the scores of each bin of a {@link BinList}
	 * @param binList input {@link BinList}
	 * @param constant constant to add
	 */
	public BLOIslandFinder(BinList binList, double read_count_limit, int gap) {
		this.binList = binList;
		this.read_count_limit = read_count_limit;
		this.gap = gap;
		//this.islands_start = new ArrayList<Integer>();
		//this.islands_stop = new ArrayList<Integer>();
	}
	
	
	@Override
	public BinList compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
		final DataPrecision precision = binList.getPrecision();
		for (short i = 0; i < binList.size(); i++) {
			final List<Double> currentList = binList.get(i);
			
			Callable<List<Double>> currentThread = new Callable<List<Double>>() {	
				@Override
				public List<Double> call() throws Exception {
					List<Double> resultList = null;
					ArrayList<Integer> islands_start = new ArrayList<Integer>();
					ArrayList<Integer> islands_stop = new ArrayList<Integer>();
					if ((currentList != null) && (currentList.size() != 0)) {
						resultList = ListFactory.createList(precision, currentList.size());
						int j = 0;
						while (j < currentList.size()){
							if (currentList.get(j) >= read_count_limit){
								islands_start.add(j);
								resultList.set(j, currentList.get(j));
								int gap_found = 0;
								int j_tmp = j + 1;
								while (gap_found <= gap){
									if (j_tmp < currentList.size()){
										if (currentList.get(j_tmp) >= read_count_limit){
											resultList.set(j_tmp, currentList.get(j_tmp));
											gap_found = 0;
										}
										else{
											resultList.set(j_tmp, 0.0);
											gap_found++;
										}
										j_tmp++;
									}
									else{
										break;
									}
								}
								islands_stop.add(j_tmp - gap_found - 1);
								j = j_tmp;
							}
							else{
								resultList.set(j, 0.0);
								j++;
							}
						}
					}
					resultList = getListIslandWithConstantValue(precision, currentList.size(), islands_start, islands_stop);
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Double>> result = op.startPool(threadList);
		if (result != null) {
			BinList resultList = new BinList(binList.getBinSize(), precision, result);
			return resultList;
		} else {
			return null;
		}
	}

	
	@Override
	public String getDescription() {
		return "Operation: Island Finder, Read count limit = " + read_count_limit + ", Gap = " + gap;
	}
	
	
	@Override
	public int getStepCount() {
		return BinList.getCreationStepCount(binList.getBinSize()) + 1;
	}
	
	private List<Double> getListIslandWithConstantValue(DataPrecision precision, int size, ArrayList<Integer> islands_start, ArrayList<Integer> islands_stop){
		List<Double> resultList = ListFactory.createList(precision, size);
		int current_pos = 0;
		double value = 0.0;
		System.out.println("Start size: " + islands_start.size());
		System.out.println("Stop size: " + islands_stop.size());
		for (int i = 0; i < size; i++){
			if (current_pos < islands_start.size()){
				if (i >= islands_start.get(current_pos) && i <= islands_stop.get(current_pos)){
					value = 10.0;
					if (i == islands_stop.get(current_pos)){
						current_pos++;
					}
				}
				else{
					value = 0.0;
				}
			}
			else{
				value = 0.0;
			}
			resultList.set(i, value);
		}
		return resultList;
	}
	
}
