package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.operation.distanceCalculator.DistanceCalculator;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;

public class GLODistanceCalculator implements Operation<long[][]>{
	
	private final GeneList 	geneList1;		// input GeneList
	private final GeneList	geneList2;		// input GeneList
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
	public long[][] compute() throws Exception {
		long[][] result = new long[geneList1.size()][];
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<long[]>> threadList = new ArrayList<Callable<long[]>>();
		for (int i = 0; i < geneList1.size(); i++) {
			final int chromoindex = i;
			Callable<long[]> currentThread = new Callable<long[]>() {	
				@Override				
				public long[] call() throws Exception {
					long[] chromoresult = new long[geneList1.get(chromoindex).size()];					
					if ((geneList1 != null) && (geneList2 != null)) {
						chromoresult = handleCases(geneList1.get(chromoindex), geneList2, chromoindex);						
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return chromoresult;
				}
			};
			threadList.add(currentThread);
		}
		if (op.startPool(threadList) == null) {
			return null;
		}
		
		int i = 0;
		for (long[] currentResult: op.startPool(threadList)) {
			if (currentResult != null) {
				result[i++] = currentResult;				
			}
		}
		return result;
	}
	
	/**
	 * Method to handle all of the 20 cases to compute the distance
	 * @param firstList
	 * @param secondList
	 * @return a double 2-D array containing distances
	 */
	private long[] handleCases(List<Gene> firstList, GeneList secondList, int chrindex) {
		DistanceCalculator dc;
		int k = 0;
		long[] distanceByChromosomes = new long[firstList.size()];
		long[] distanceArray = new long[firstList.size()];
		switch(selectionCase) {
		case POSITIVE_START_START:	//for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.size()];
										for (int j = 0; j < firstList.size(); j++) {
											dc = new DistanceCalculator(secondList, chrindex, 0, 0, 0, firstList.get(j).getTxStart());
											if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}
											//System.out.println("distanceArray[j]" + distanceArray[j]);
										}
										distanceByChromosomes = distanceArray;
									//}
									break;
									
		case POSITIVE_START_MIDDLE:	//for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.size()];
										for (int j = 0; j < firstList.size(); j++) {
											dc = new DistanceCalculator(secondList, chrindex, 0, 0, 1, firstList.get(j).getTxStart());
											if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
										}
										distanceByChromosomes = distanceArray;
									//}
									break;
								
		case POSITIVE_START_STOP:	//for (int i = 0; i < firstList.size(); i++) {
											//long[] distanceArray = new long[firstList.get(i).size()];
											for (int j = 0; j < firstList.size(); j++) {
												dc = new DistanceCalculator(secondList, chrindex, 0, 0, 2, firstList.get(j).getTxStart());
												if (dc.getClosestDistance() >= 0) {
													distanceArray[k++] = dc.getClosestDistance();
												}											
											}
											distanceByChromosomes = distanceArray;
									//}
									break;
								
		case POSITIVE_MIDDLE_START:	//for (int i = 0; i < firstList.size(); i++) {
											//long[] distanceArray = new long[firstList.get(i).size()];
											for (int j = 0; j < firstList.size(); j++) {
												dc = new DistanceCalculator(secondList, chrindex, 0, 1, 0, (int)firstList.get(j).getTxMiddle());
												if (dc.getClosestDistance() >= 0) {
													distanceArray[k++] = dc.getClosestDistance();
												}											
											}
										distanceByChromosomes = distanceArray;
									//}
									break;
								
		case POSITIVE_MIDDLE_MIDDLE: //for (int i = 0; i < firstList.size(); i++) {
											//long[] distanceArray = new long[firstList.get(i).size()];
											 for (int j = 0; j < firstList.size(); j++) {
											 	 dc = new DistanceCalculator(secondList, chrindex, 0, 1, 1, (int)firstList.get(j).getTxMiddle());
											 	 if (dc.getClosestDistance() >= 0) {
												     distanceArray[k++] = dc.getClosestDistance();
												 }											
											 }
										 distanceByChromosomes = distanceArray;
									 //}
									 break;
								 
		case POSITIVE_MIDDLE_STOP: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
										 for (int j = 0; j < firstList.size(); j++) {
										 	 dc = new DistanceCalculator(secondList, chrindex, 0, 1, 2, (int)firstList.get(j).getTxMiddle());
										 	 if (dc.getClosestDistance() >= 0) {
												 distanceArray[k++] = dc.getClosestDistance();
											 }											
										 }
										 distanceByChromosomes = distanceArray;
									 //}
									 break;
								 
		case POSITIVE_STOP_START: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
										  for (int j = 0; j < firstList.size(); j++) {
										  	  dc = new DistanceCalculator(secondList, chrindex, 0, 2, 0, firstList.get(j).getTxStop());
										  	  if (dc.getClosestDistance() >= 0) {
												  distanceArray[k++] = dc.getClosestDistance();
											  }											
										  }
									  distanceByChromosomes = distanceArray;
								    //}
								    break;
							    
		case POSITIVE_STOP_MIDDLE: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
									   for (int j = 0; j < firstList.size(); j++) {
									   	   dc = new DistanceCalculator(secondList, chrindex, 0, 2, 1, firstList.get(j).getTxStop());
										   	if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
									   }
									distanceByChromosomes = distanceArray;
							       //}
							       break;
							     
		case POSITIVE_STOP_STOP: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
									   for (int j = 0; j < firstList.size(); j++) {
									   	   dc = new DistanceCalculator(secondList, chrindex, 0, 2, 2, firstList.get(j).getTxStop());
										   	if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
									   }
								   distanceByChromosomes = distanceArray;
							     //}
							     break;
							     
		case NEGATIVE_START_START: //for (int i = 0; i < firstList.size(); i++) {
											//long[] distanceArray = new long[firstList.get(i).size()];
										   for (int j = 0; j < firstList.size(); j++) {
										   	   dc = new DistanceCalculator(secondList, chrindex, 1, 0, 0, firstList.get(j).getTxStart());
											   	if (dc.getClosestDistance() >= 0) {
													distanceArray[k++] = dc.getClosestDistance();
												}											
										   }
									   distanceByChromosomes = distanceArray;
								   //}
							       break;
							     
		case NEGATIVE_START_MIDDLE: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
										    for (int j = 0; j < firstList.size(); j++) {
										   	    dc = new DistanceCalculator(secondList, chrindex, 1, 0, 1, firstList.get(j).getTxStart());
											   	if (dc.getClosestDistance() >= 0) {
													distanceArray[k++] = dc.getClosestDistance();
												}											
										    }
									    distanceByChromosomes = distanceArray;
							      	//}
							      	break;
							      
		case NEGATIVE_START_STOP: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
								  		for (int j = 0; j < firstList.size(); j++) {
								      		dc = new DistanceCalculator(secondList, chrindex, 1, 0, 2, firstList.get(j).getTxStart());
								      		if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
								  		}
								  	distanceByChromosomes = distanceArray;
								  //} 
							      break;
							    
		case NEGATIVE_MIDDLE_START: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
										    for (int j = 0; j < firstList.size(); j++) {
										        dc = new DistanceCalculator(secondList, chrindex, 1, 1, 0, firstList.get(j).getTxStart());
										        if (dc.getClosestDistance() >= 0) {
													distanceArray[k++] = dc.getClosestDistance();
												}											
										    }
										    distanceByChromosomes = distanceArray;
								  	//} 
							        break;
							        
		case NEGATIVE_MIDDLE_MIDDLE: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
									    for (int j = 0; j < firstList.size(); j++) {
									        dc = new DistanceCalculator(secondList, chrindex, 1, 1, 1, (int)firstList.get(j).getTxMiddle());
									        if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
									    }
									    distanceByChromosomes = distanceArray;
							  	   //} 
								   break;
								  
		case NEGATIVE_MIDDLE_STOP: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
									    for (int j = 0; j < firstList.size(); j++) {
									        dc = new DistanceCalculator(secondList, chrindex, 1, 1, 2, (int)firstList.get(j).getTxMiddle());
									        if (dc.getClosestDistance() >= 0) {
												distanceArray[k++] = dc.getClosestDistance();
											}											
									    }
									    distanceByChromosomes = distanceArray;
							  	   //} 
								   break;
								   
		case NEGATIVE_STOP_START: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
								  		for (int j = 0; j < firstList.size(); j++) {
								      		dc = new DistanceCalculator(secondList, chrindex, 1, 2, 0, firstList.get(j).getTxStop());
								      		if (dc.getClosestDistance() >= 0) {
												distanceArray[j] = dc.getClosestDistance();
											}											
								  		}
								  	distanceByChromosomes = distanceArray;
								  //} 
							      break;
							      
		case NEGATIVE_STOP_MIDDLE: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
								  		for (int j = 0; j < firstList.size(); j++) {
								      		dc = new DistanceCalculator(secondList, chrindex, 1, 2, 1, firstList.get(j).getTxStop());
								      		if (dc.getClosestDistance() >= 0) {
												distanceArray[j] = dc.getClosestDistance();
											}											
								  		}
								  	distanceByChromosomes = distanceArray;
								  //} 
							      break;
							      
		case NEGATIVE_STOP_STOP: //for (int i = 0; i < firstList.size(); i++) {
										//long[] distanceArray = new long[firstList.get(i).size()];
								  		for (int j = 0; j < firstList.size(); j++) {
								      		dc = new DistanceCalculator(secondList, chrindex, 1, 2, 2, firstList.get(j).getTxStop());
								      		if (dc.getClosestDistance() >= 0) {
												distanceArray[j] = dc.getClosestDistance();
											}											
								  		}
								  	distanceByChromosomes = distanceArray;
								  //} 
							      break;
		}	
		return distanceByChromosomes;
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
		return 1;
	}
}