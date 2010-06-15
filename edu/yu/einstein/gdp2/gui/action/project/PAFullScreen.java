/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

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
public final class PAFullScreen extends AbstractAction {

	private static final long serialVersionUID = 3794203118454011414L; 	// generated ID
	private static final int 		MNEMONIC = KeyEvent.VK_U; 			// mnemonic key
	private static final String 	ACTION_NAME = "Full Screen";		// action name
	private final MainFrame 		mainFrame;							// main frame of the application
	
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0); 
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "fullscreen";
	
	

	/**
	 * Creates an instance of {@link PAFullScreen}
	 * @param mainFrame {@link MainFrame} of the application
	 */
	public PAFullScreen(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
        putValue(NAME, ACTION_NAME);
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
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
