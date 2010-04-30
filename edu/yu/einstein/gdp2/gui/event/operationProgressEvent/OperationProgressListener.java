/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.operationProgressEvent;


/**
 * The listener interface for receiving {@link OperationProgressEvent}.
 * The class that is interested in processing a {@link OperationProgressEvent} implements this interface.
 * The listener object created from that class is then registered with a component using the component's addOperationProgressListener method. 
 * A {@link OperationProgressEvent} is generated when the progress state of an operation changes.  
 * @author Julien Lajugie
 * @version 0.1
 */
public interface OperationProgressListener {

	
	/**
	 * Invoked when the progress state of an operation changes
	 * @param evt {@link OperationProgressEvent}
	 */
	public abstract void operationProgressChanged(OperationProgressEvent evt);
}
