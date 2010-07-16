/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.peakFinderDialog;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Defines the common methods of the different peak finder panels
 * @author Julien Lajugie
 * @version 0.1
 */
public interface PeakFinderPanel {
	
	/**
	 * Checks if the input are valid. Notifies the user if not.
	 * @return the Operation with the parameters set by the user. Null if the input are not valid
	 */
	public Operation<BinList[]> validateInput();
	
	
	/**
	 * Saves the input so it can be restored next time the dialog is open  	
	 */
	public void saveInput();
}
