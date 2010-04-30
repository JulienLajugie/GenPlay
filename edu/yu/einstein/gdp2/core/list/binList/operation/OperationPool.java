package yu.einstein.gdp2.core.list.binList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEventsGenerator;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.util.ChromosomeManager;

public final class OperationPool implements OperationProgressEventsGenerator {

	private static OperationPool instance = null; 
	private ExecutorService executor = null;	
	private final ChromosomeManager cm;
	private final long genomeLength;
	private final List<OperationProgressListener> progressListeners;
	

	private OperationPool(ChromosomeManager chromosomeManager) {
		super();
		cm = chromosomeManager;
		long length = 0;
		for (Chromosome currentChromo: chromosomeManager) {
			length += currentChromo.getLength();
		}
		genomeLength = length;
		progressListeners = new ArrayList<OperationProgressListener>();
	}

	
	public static OperationPool getInstance() {
		if (instance == null) {
			instance = new OperationPool(ChromosomeManager.getInstance());
		}
		return instance;
	}
	
	
	public synchronized void notifyDone() {
		notifyAll();
	}
	
	
	public synchronized void stopPool() {
		if ((executor != null) && (!executor.isShutdown()) && (!executor.isTerminated())) {   
			executor.shutdownNow();
			notifyAll();
		}
	}
	

	public synchronized <T> List<T> startPool(Collection<? extends Callable<T>> threads) throws InterruptedException, ExecutionException {
		int nbProcessor = Runtime.getRuntime().availableProcessors();
		executor = Executors.newFixedThreadPool(nbProcessor);
		// notify the listeners that the operation starts
		notifyProgressListeners(OperationProgressEvent.STARTING, 0d);
		// list of futur for the result of the callables		
		List<Future<T>> futures = new ArrayList<Future<T>>();
		// list for the return value of this method
		List<T> results = new ArrayList<T>();
		for (Callable<T> currentCallable: threads) {
			futures.add(executor.submit(currentCallable));
		}
		boolean stillAlive = true;
		while (stillAlive) {
			wait();
			// if the executor is terminated or shut down
			if (executor.isTerminated() || executor.isShutdown()) {
				// we cancel all the futures if there not done
				for (short i = 0; i < futures.size(); i++) {
					if (!futures.get(i).isDone()) {
						futures.get(i).cancel(true);
					}
				}
				// we notify the listeners
				notifyProgressListeners(OperationProgressEvent.ABORT, 100d);				
				return null;
			}
			long done = 0;
			stillAlive = false;
			// compute the completion and check if everything's done 
			for (short i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone()) {
					done += cm.getChromosome(i).getLength();					
				} else {
					stillAlive = true;
				}
			}
			double completion = (done / (double) genomeLength) * 100d;
			int progressState = OperationProgressEvent.IN_PROGRESS;
			notifyProgressListeners(progressState, completion);
		}
		// generate the result list from the future list
		for (int j = 0; j < futures.size(); j++) {
			results.add(futures.get(j).get());
		}
		// notify the listeners that the operation is complete
		notifyProgressListeners(OperationProgressEvent.COMPLETE, 100d);		
		return results;
	}


	/**
	 * Notifies all the listeners that the progression of an operation changed
	 * @param progressState state of the progression
	 * @param completion completion if the state is IN_PROGRESS
	 */
	private void notifyProgressListeners(int progressState, double completion) {
		OperationProgressEvent evt = new OperationProgressEvent(progressState, completion);
		for (OperationProgressListener listener: progressListeners) {
			listener.operationProgressChanged(evt);
		}		
	}


	@Override
	public void addOperationProgressListener(OperationProgressListener operationProgressListener) {
		progressListeners.add(operationProgressListener);
	}


	@Override
	public OperationProgressListener[] getGenomeWindowListeners() {
		OperationProgressListener[] listeners = new OperationProgressListener[progressListeners.size()];
		return progressListeners.toArray(listeners);
	}


	@Override
	public void removeGenomeWindowListener(OperationProgressListener operationProgressListener) {
		progressListeners.remove(operationProgressListener);
	}
}
