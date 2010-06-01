package yu.einstein.gdp2.gui.action;

import java.awt.MenuBar;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.list.binList.operation.OperationPool;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.trackList.TrackList;

public abstract class NewTrackListAction<T> extends AbstractAction implements OperationProgressListener {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	private final TrackList trackList; // TrackList	
	private final MenuBar menuBar;
	private final BinListOperation<T> operation;
	private final int totalStepCount;
	private int currentStep = 0;
	
	
	/**
	 * Constructor
	 * @param trackList a {@link TrackList}
	 */
	public NewTrackListAction() {
		this.trackList = MainFrame.getInstance().getTrackList();
		this.menuBar = MainFrame.getInstance().getMenuBar();
		this.operation = setOperation();
		totalStepCount = operation.getStepCount();
	}
	
	
	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return trackList.getRootPane();
	}
	
	protected MenuBar getMenuBar() {
		return menuBar;
	}
	
	
	protected TrackList getTrackList() {
		return trackList;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			// adds itself as a progress listener 
			OperationPool.getInstance().addOperationProgressListener(this);
			T result = operation.compute();
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
			
		}
	}
	
	
	protected abstract BinListOperation<T> setOperation();
	protected abstract void doAtTheEnd(T result);	
}

