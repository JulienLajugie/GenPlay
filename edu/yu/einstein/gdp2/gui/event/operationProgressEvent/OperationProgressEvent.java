/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.event.operationProgressEvent;

/**
 * This event is emitted when the progress state of an operation changes. 
 * @author Julien Lajugie
 * @version 0.1
 */
public class OperationProgressEvent {
	
	/**
	 * the operation is starting 
	 */
	public static int STARTING = 0;
	
	/**
	 * the operation is in progress
	 */
	public static int IN_PROGRESS = 1;
	
	/**
	 * the operation was successfully completed 
	 */
	public static int COMPLETE = 2;
	
	/**
	 * the operation was aborted
	 */
	public static int ABORT = 3;
	
	
	private final int 		state;		// state of the progress (STARTING, IN_PROGRESS...)
	private final double 	completion;	// % of completion when the state is IN_PROGRESS
	
	
	/**
	 * Creates an instance if {@link OperationProgressEvent}
	 * @param state state of the progress
	 * @param completion % of completion if the state is in progress
	 */
	public OperationProgressEvent(int state, double completion) {
		this.state = state;
		this.completion = completion;
	}
	
	
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	
	/**
	 * @return the completion
	 */
	public double getCompletion() {
		return completion;
	}
}
