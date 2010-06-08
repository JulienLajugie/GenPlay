package yu.einstein.gdp2.gui.action;

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.core.list.binList.operation.OperationPool;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.statusBar.StatusBar;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;

public abstract class PooledTrackListAction<T> extends TrackListAction implements OperationProgressListener {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	private final StatusBar statusBar = MainFrame.getInstance().getStatusBar();
	private final int totalStepCount;
	private final String description;
	private ActionWorker<T> worker;	
	private int currentStep = 0;	
	

	public PooledTrackListAction(String description, int stepCount) {
		super();
		totalStepCount = stepCount;
		this.description = description;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			// adds itself as a progress listener 
			OperationPool.getInstance().addOperationProgressListener(this);
			T result = compute();
			doAtTheEnd(result);			
			OperationPool.getInstance().removeGenomeWindowListener(this);			
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "An unexpected exception occured");
		}
		
	}


	@Override
	public void operationProgressChanged(OperationProgressEvent evt) {
		OperationProgressEvent newEvt;
		if (evt.getState() == OperationProgressEvent.STARTING) {
			if (currentStep == 0) {
				// if it's the first step we send a message starting
				newEvt = new OperationProgressEvent(OperationProgressEvent.STARTING, 0);
			} else {
				double progress = currentStep / (double) totalStepCount;
				newEvt = new OperationProgressEvent(OperationProgressEvent.IN_PROGRESS, progress);
			}
			currentStep++;
		} else if (evt.getState() == OperationProgressEvent.IN_PROGRESS) {
			double progress = (currentStep + (evt.getCompletion() / 100)) / (double) totalStepCount;
			newEvt = new OperationProgressEvent(OperationProgressEvent.IN_PROGRESS, progress);
		}
		//MainFrame.getInstance().getStatusBar().setac
	}
	
	
	public void stop() {
		worker.cancel(true);
		OperationPool.getInstance().stopPool();
	}

	
	protected abstract void doAtTheEnd(T result);
	protected abstract T compute();
}

