/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.peakFinderDialog;


/**
 * Defines the common methods of the different peak finder panels
 * @author Julien Lajugie
 * @version 0.1
 */
interface PeakFinderPanel {
	
	/**
	 * Checks if the input are valid. Notifies the user if not.
	 * @return true if the input are valid, false otherwise
	 */
	public boolean isInputValid();
	
	
	/**
	 * Saves the input as new default values
	 */
	public void saveInput();
}
