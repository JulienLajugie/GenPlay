/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;


/**
 * Exits the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExitAction extends AbstractAction {

	private static final long serialVersionUID = 367623512867873899L;	// generated id
	
//	private static final String 	DESCRIPTION = "Exit the application"; 	// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_X; 				// mnemonic key
	private static final String 	ACTION_NAME = "Exit";					// action name
	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = 
		KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);		// accelerator key 
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "exit";
	
	private final JFrame frame; // a JFrame
	
	
	/**
	 * Creates an instance of {@link ExitAction} 
	 * @param frame a {@link JFrame}
	 */
	public ExitAction(JFrame frame) {
		super();
		this.frame = frame;
        putValue(NAME, ACTION_NAME);
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
//      putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	
	
	/**
	 * Closes the frame
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	}
}
