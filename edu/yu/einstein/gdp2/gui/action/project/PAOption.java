/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;

import yu.einstein.gdp2.gui.mainFrame.MainFrame;


/**
 * Shows the option screen
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAOption extends AbstractAction {

	private static final long serialVersionUID = -7328322178569010171L; // generated ID
	private static final int 		MNEMONIC = KeyEvent.VK_O;	// mnemonic key
	private static final String 	ACTION_NAME = "Option";		// action name
	private final MainFrame 		mainFrame;					// main frame of the application

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAOption";
	
	
	/**
	 * Creates an instance of {@link PAOption}
	 * @param mainFrame {@link MainFrame} of the application
	 */
	public PAOption(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame; 
        putValue(NAME, ACTION_NAME);
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(MNEMONIC_KEY, MNEMONIC);
	}
	
	
	/**
	 * Shows the option screen
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		mainFrame.showOption();
	}
}
