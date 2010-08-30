/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.operation;

import java.util.concurrent.ExecutionException;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.statusBar.Stoppable;


/**
 * Operation on a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 * @param result type of the operation
 */
public interface Operation<T> extends Stoppable {
	
	
	/**
	 * @return a description of the operation
	 */
	public String getDescription();
	
	
	/**
	 * @return a description of what is done during the process
	 */
	public String getProcessingDescription();
	
	
	/**
	 * Processes the operation
	 * @return the result of the operation
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public T compute() throws Exception;
	
	
	/**
	 * @return the number of steps needed to complete the operation 
	 */
	public int getStepCount();
}
