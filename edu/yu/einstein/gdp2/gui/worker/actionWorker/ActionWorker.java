/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.worker.actionWorker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEventsGenerator;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.ExceptionManager;


/**
 * Worker used when an action is launched. 
 * Shows the progress bar and disables the gui.
 * Must be extended to specify the action to execute
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> type of the data returned by the action. Void if none
 */
public abstract class ActionWorker<T> extends SwingWorker<T, Void> implements TrackListActionEventsGenerator {

	private final TrackList 					trackList;			// track list 
	private final List<TrackListActionListener> tlalListenerList;	// list of GenomeWindowListener
	private final String 						actionStartDescription; // description of the action


	/**
	 * Creates an instance of {@link ActionWorker}. Shows the progress bar
	 * @param trackList
	 */
	public ActionWorker(TrackList trackList, String actionStartDescription) {
		this.trackList = trackList;
		this.tlalListenerList = new ArrayList<TrackListActionListener>();
		addTrackListActionListener(trackList);
		this.actionStartDescription = actionStartDescription;
	}


	@Override
	final protected T doInBackground() throws Exception {
		notifyActionStarted(actionStartDescription);
		return doAction();
	};


	@Override
	final protected void done() {
		try {
			notifyActionEnded("Operation Done");
			doAtTheEnd(this.get());
		} catch (Exception e) {
			notifyActionEnded("Operation Aborted");
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


	@Override
	public void addTrackListActionListener(TrackListActionListener trackListActionListener) {
		tlalListenerList.add(trackListActionListener);		
	}


	@Override
	public TrackListActionListener[] getOperationOnTrackListener() {
		TrackListActionListener[] operationOnTrackListeners = new TrackListActionListener[tlalListenerList.size()];
		return tlalListenerList.toArray(operationOnTrackListeners);
	}


	@Override
	public void removeTrackListActionListener(TrackListActionListener trackListActionListener) {
		tlalListenerList.remove(trackListActionListener);		
	}


	/**
	 * Notifies all the {@link TrackListActionListener} that an action started
	 * @param actionDescription
	 */
	private void notifyActionStarted(String actionDescription) {
		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
		for (TrackListActionListener tal: tlalListenerList) {
			tal.actionStarts(evt);
		}
	}


	/**
	 * Notifies all the {@link TrackListActionListener} that an action ended
	 * @param actionDescription
	 */
	private void notifyActionEnded(String actionDescription) {
		TrackListActionEvent evt = new TrackListActionEvent(trackList, actionDescription);
		for (TrackListActionListener tal: tlalListenerList) {
			tal.actionEnds(evt);
		}
	}

}
