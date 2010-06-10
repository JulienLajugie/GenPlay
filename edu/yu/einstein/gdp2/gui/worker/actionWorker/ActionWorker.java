/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.actionWorker;

import javax.swing.SwingWorker;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Worker used when an action is launched. 
 * Shows the progress bar and disables the gui.
 * Must be extended to specify the action to execute
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> type of the data returned by the action. Void if none
 */
public abstract class ActionWorker<T> extends SwingWorker<T, Void>  {

	private final TrackList 					trackList;			// track list 


	/**
	 * Creates an instance of {@link ActionWorker}. Shows the progress bar
	 * @param trackList
	 */
	public ActionWorker(TrackList trackList, String actionStartDescription) {
		this.trackList = trackList;
	}


	@Override
	final protected T doInBackground() throws Exception {
		//notifyActionStarted(actionStartDescription);
		return doAction();
	};


	@Override
	final protected void done() {
		try {
//			notifyActionEnded("Operation Done");
			doAtTheEnd(this.get());
		} catch (Exception e) {
//			notifyActionEnded("Operation Aborted");
			ExceptionManager.handleException(trackList.getRootPane(), e, "An unexpected error occurred during the operation");			
		}
	}


	/**
	 * Must be overloaded to specify the action to do
	 * @return the result of the action
	 * @throws Exception
	 */
	protected abstract T doAction() throws Exception;


	/**
	 * Method done at the end of the action 
	 * @param actionResult result returned by the action method
	 */
	protected abstract void doAtTheEnd(T actionResult);




//	/**
//	 * Notifies all the {@link TrackListActionListener} that an action started
//	 * @param actionDescription
//	 */
//	private void notifyActionStarted(String actionDescription) {
//		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
//		for (TrackListActionListener tal: tlalListenerList) {
//			tal.actionStarts(evt);
//		}
//	}


//	/**
//	 * Notifies all the {@link TrackListActionListener} that an action ended
//	 * @param actionDescription
//	 */
//	private void notifyActionEnded(String actionDescription) {
//		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
//		for (TrackListActionListener tal: tlalListenerList) {
//			tal.actionEnds(evt);
//		}
//	}

}
