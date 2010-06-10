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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEventsGenerator;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;


/**
 * Pool of threads with tools to start, interrupt and retrieve the result of the execution.
 * Generate progress events and send this events to listeners.  
 * @author Julien Lajugie
 * @version 0.1
 */
public final class OperationPool implements OperationProgressEventsGenerator {

	private static OperationPool 	instance = null;	// unique instance of this singleton class
	private ExecutorService 		executor = null;	// thread executor 
	private final ChromosomeManager cm;					// chromosome manager
	private final long 				genomeLength;		// total length of the genome in base pais
	private final List<OperationProgressListener> progressListeners; // list of progress listeners
	

	/**
	 * Private constructor of the singleton class {@link OperationPool}
	 * @param chromosomeManager a {@link ChromosomeManager}
	 */
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

	
	/**
	 * @return an instance of the singleton class {@link OperationPool}
	 */
	public static OperationPool getInstance() {
		if (instance == null) {
			instance = new OperationPool(ChromosomeManager.getInstance());
		}
		return instance;
	}
	
	
	/**
	 * Notifies the executor that a thread is done
	 */
	public synchronized void notifyDone() {
		notifyAll();
	}
	
	
	/**
	 * Interrupts all the running thread and cancel the execution 
	 */
	public synchronized void stopPool() {
		if ((executor != null) && (!executor.isShutdown()) && (!executor.isTerminated())) {   
			executor.shutdownNow();
			notifyAll();
		}
	}
	
	
	/**
	 * Starts the pool of thread. Waits until the end of the execution and returns the result in a list.
	 * An InterruptedException is thrown if the execution is stopped before the end.
	 * @param <T> type returned by the threads
	 * @param threads a list of {@link Callable}
	 * @return a list of the specified type
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
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
			wait(1000);
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
				throw new InterruptedException();
			}
			long done = 0;
			stillAlive = false;
			// compute the completion and check if everything's done 
			for (short i = 0; i < futures.size(); i++) {
				if (futures.get(i).isDone() || futures.get(i).isCancelled()) {
					done += cm.get(i).getLength();					
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
	public OperationProgressListener[] getOperationProgressListeners() {
		OperationProgressListener[] listeners = new OperationProgressListener[progressListeners.size()];
		return progressListeners.toArray(listeners);
	}


	@Override
	public void removeOperationProgressListener(OperationProgressListener operationProgressListener) {
		progressListeners.remove(operationProgressListener);
	}
}
