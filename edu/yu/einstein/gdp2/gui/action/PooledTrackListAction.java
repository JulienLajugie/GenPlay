package yu.einstein.gdp2.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingWorker;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.statusBar.StatusBar;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


public abstract class PooledTrackListAction<T> extends TrackListAction implements OperationProgressListener, Stoppable {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	private int currentStep = 1;	
	protected SwingWorker<T, Void> worker;
	
	
	private class PooledActionWorker extends SwingWorker<T, Void> {
		@Override
		final protected T doInBackground() throws Exception {
			OperationPool.getInstance().addOperationProgressListener(PooledTrackListAction.this);
			getTrackList().actionStarts();
			return processAction();
		}
		
		
		@Override
		final protected void done() {
			try {
				doAtTheEnd(this.get());
				getStatusBar().actionStop("Operation Done");
			} catch (Exception e) {
				getStatusBar().actionStop("Error");
				ExceptionManager.handleException(getTrackList().getRootPane(), e, "An unexpected error occurred during the operation");
			} finally {
				OperationPool.getInstance().removeOperationProgressListener(PooledTrackListAction.this);
				getTrackList().actionEnds();
			}
		}
	}
	
	
	private StatusBar getStatusBar() {
		return MainFrame.getInstance().getStatusBar();
	}

	public PooledTrackListAction() {
		super();
	}

	
	@Override
	public final void actionPerformed(ActionEvent arg0) {
		worker = new PooledActionWorker();
		worker.execute();
	}


	@Override
	public void operationProgressChanged(OperationProgressEvent evt) {
		StatusBar statusBar = getStatusBar();
		if (evt.getState() == OperationProgressEvent.STARTING) {
			// when a step start
			statusBar.setProgress(currentStep, 0);
		} else if (evt.getState() == OperationProgressEvent.IN_PROGRESS) {
			// when a step is in progress
			statusBar.setProgress(currentStep, (int)evt.getCompletion());
		} else if (evt.getState() == OperationProgressEvent.COMPLETE) {
			// when a step is done
			statusBar.setProgress(currentStep, 100);
			currentStep++;
		}
	}

	
	public void notifyActionStart(String description, int stepCount) {
		currentStep = 1;
		getStatusBar().actionStart(description, stepCount, this);		
	}
	
	
	public void notifyActionStop(String resultStatus) {
		getStatusBar().actionStop(resultStatus);
	}
	
	
	@Override
	public void stop() {
		worker.cancel(true);
		OperationPool.getInstance().stopPool();	
		getStatusBar().actionStop("Operation Aborted");
	}

	
	/**
	 * Specifies the action to process
	 * @return the result of the action
	 * @throws Exception
	 */
	protected abstract T processAction() throws Exception;


	/**
	 * Method called at the end of the action 
	 * @param actionResult result returned by the action method
	 */
	protected abstract void doAtTheEnd(T actionResult);
}
