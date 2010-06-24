/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action;

import yu.einstein.gdp2.core.operation.Operation;


/**
 * Action that starts a {@link Operation} in a thread that can be stopped
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> typed of the value returned by the action
 */
public abstract class TrackListActionOperationWorker<T> extends TrackListActionWorker<T> {

	private static final long serialVersionUID = -1626148358656459751L; // generated ID
	protected Operation<T> operation;// operation to be processed
	
	
	/**
	 * Public constructor 
	 */
	public TrackListActionOperationWorker() {
		super();
	}

	
	@Override
	protected T processAction() throws Exception {
		operation = initializeOperation();
		if (operation != null) {
			notifyActionStart(operation.getProcessingDescription(), operation.getStepCount(), true);
			return operation.compute();
		} else {
			return null;
		}
	}

	
	/**
	 * Initializes the Operation
	 * @return an initialized Operation or null if the user canceled
	 */
	public abstract Operation<T> initializeOperation() throws Exception;
}
