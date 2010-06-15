/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;
import javax.swing.SwingWorker;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.operationPool.OperationPool;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.statusBar.StatusBar;
import yu.einstein.gdp2.gui.statusBar.Stoppable;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Action that starts a SwingWorker so the GUI doesn't freeze
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionWorker<T> extends AbstractAction implements OperationProgressListener, Stoppable {

	private static final long serialVersionUID = 1383058897700926018L; // generated ID
	private int currentStep = 1;			// current step of the action
	protected SwingWorker<T, Void> worker;	// worker that will process the action
	
	
	/**
	 * @return the {@link JRootPane} of the {@link TrackList}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackList().getRootPane();
	}
	
	
	/**
	 * Shortcut for MainFrame.getInstance().getTrackList()
	 * @return the track list of the project
	 */
	protected TrackList getTrackList() {
		return MainFrame.getInstance().getTrackList();
	}
	
	
	/**
	 * Private inner class that extends SwingWorker<T, Void>.
	 * Processes the action.
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class PooledActionWorker extends SwingWorker<T, Void> {
		
		@Override
		final protected T doInBackground() throws Exception {
			OperationPool.getInstance().addOperationProgressListener(TrackListActionWorker.this);
			getTrackList().actionStarts();			
			return processAction();
		}		
		
		@Override
		final protected void done() {
			try {
				getStatusBar().actionStop("Operation Done");
				doAtTheEnd(this.get());
			} catch (Exception e) {
				if (e.getCause() instanceof InterruptedException) {
					getStatusBar().actionStop("Operation Aborted");
				} else {
					getStatusBar().actionStop("Error");
					ExceptionManager.handleException(getTrackList().getRootPane(), e, "An unexpected error occurred during the operation");
				}
			} finally {
				OperationPool.getInstance().removeOperationProgressListener(TrackListActionWorker.this);
				getTrackList().actionEnds();
			}
		}
	}
	
	
	/**
	 * Public constructor 
	 */
	public TrackListActionWorker() {
		super();
	}
	
	
	/**
	 * @return the status bar of the application
	 */
	private StatusBar getStatusBar() {
		return MainFrame.getInstance().getStatusBar();
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

	
	/**
	 * Notifies that an action starts
	 * Must be called right before the computation starts
	 * @param description description of the action
	 * @param stepCount number of steps needed to complete the action
	 */
	protected void notifyActionStart(String description, int stepCount) {
		currentStep = 1;
		getStatusBar().actionStart(description, stepCount, this);
	}
	
	
	/**
	 * Notifies the status bar that an action ends.
	 */
	protected void notifyActionStop() {
		getStatusBar().actionStop("Operation Done");
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
	 * Method called at the end of the action.
	 * Can be extended to define the action to do at the end.
	 * @param actionResult result returned by the action method
	 */
	protected void doAtTheEnd(T actionResult) {};
}
