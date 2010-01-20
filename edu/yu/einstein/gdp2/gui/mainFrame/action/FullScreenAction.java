/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.mainFrame.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.mainFrame.MainFrame;


/**
 * Toggles the application fullscreen mode on/off
 * @author Julien Lajugie
 * @version 0.1
 */
public final class FullScreenAction extends AbstractAction {

	private static final long serialVersionUID = 3794203118454011414L; // generated ID
//	private static final String 	DESCRIPTION = "Toggle Fullscreen Mode On/Off";				// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_U; 	// mnemonic key
	private static final String 	ACTION_NAME = "Full Screen";// action name
		
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0); 
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "fullscreen";
		
	private final MainFrame mainFrame;	// main frame of the application
	

	/**
	 * Creates an instance of {@link FullScreenAction}
	 * @param mainFrame {@link MainFrame} of the application
	 */
	public FullScreenAction(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
        putValue(NAME, ACTION_NAME);
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
//      putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}

	
	/**
	 * Toggles the main frame to fullscreen mode
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.toggleFullScreenMode();
	}
}
