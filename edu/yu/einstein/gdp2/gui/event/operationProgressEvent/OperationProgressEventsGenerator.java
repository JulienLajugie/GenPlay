/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.operationProgressEvent;


/**
 * Should be Implemented by objects generating {@link OperationProgressEvent}
 * @author Julien Lajugie
 * @version 0.1
 */
public interface OperationProgressEventsGenerator {
	
	/**
	 * Adds a {@link OperationProgressListener} to the listener list
	 * @param operationProgressListener {@link OperationProgressListener} to add
	 */
	public void addOperationProgressListener(OperationProgressListener operationProgressListener);
	

	/**
	 * @return an array containing all the {@link OperationProgressListener} of the current instance
	 */
	public OperationProgressListener[] getOperationProgressListeners();
	
	
	/**
	 * Removes a {@link OperationProgressListener} from the listener list
	 * @param operationProgressListener {@link OperationProgressListener} to remove
	 */
	public void removeOperationProgressListener(OperationProgressListener operationProgressListener);
}
