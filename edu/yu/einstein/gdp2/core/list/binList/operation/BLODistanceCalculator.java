package yu.einstein.gdp2.core.list.binList.operation;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;

public class BLODistanceCalculator implements Operation<Double[][][]> {

	private final BinList 	binList1;		// input BinList
	private final BinList	binList2;		// input BinList
	
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
	
	public BLODistanceCalculator(BinList binList1, BinList binList2) {
		this.binList1 = binList1;
		this.binList2 = binList2;
	}
	@Override
	public Double[][][] compute() throws Exception {
		
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
