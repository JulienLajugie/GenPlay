/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList.worker.actionWorker;

import java.awt.Container;

import javax.swing.SwingWorker;

import yu.einstein.gdp2.gui.progressBar.ProgressBar;
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
public abstract class ActionWorker<T> extends SwingWorker<T, Void> {

	private final ProgressBar		progressBar;		// a progress bar displayed during the loading
	private final TrackList 		trackList;			// track list 
	private final Container			topAncestor; 		// top container disabled during the operation

	/**
	 * Creates an instance of {@link ActionWorker}. Shows the progress bar
	 * @param trackList
	 */
	public ActionWorker(TrackList trackList) {
		this.trackList = trackList;
		this.progressBar = new ProgressBar(trackList);
		trackList.add(progressBar);
		topAncestor = this.trackList.getTopLevelAncestor();
		topAncestor.setEnabled(false);
	}


	@Override
	final protected T doInBackground() {
		return doAction();		
	};


	@Override
	final protected void done() {
		try {
			this.get();
			doAtTheEnd(this.get());
		} catch (Exception e) {
			ExceptionManager.handleException(trackList.getRootPane(), e, "An error occurred during the operation");
		} finally {
			//trackList.remove(progressBar);
			progressBar.dispose();
			topAncestor.setEnabled(true);
			topAncestor.requestFocus();
		}
	}

	
	/**
	 * Must be overloaded to specify the action to do
	 * @return the result of the action
	 */
	protected abstract T doAction();
	
	
	/**
	 * Method done at the end of the action 
	 * @param actionResult result returned by the action method
	 */
	protected abstract void doAtTheEnd(T actionResult);
}
