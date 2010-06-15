/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;


/**
 * Exits the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAExit extends AbstractAction {

	private static final long serialVersionUID = 367623512867873899L;	// generated id
	private static final int 		MNEMONIC = KeyEvent.VK_X; 			// mnemonic key
	private static final String 	ACTION_NAME = "Exit";				// action name
	private final JFrame 			frame; 								// a JFrame


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = 
		KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAExit";


	/**
	 * Creates an instance of {@link PAExit} 
	 * @param frame a {@link JFrame}
	 */
	public PAExit(JFrame frame) {
		super();
		this.frame = frame;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Closes the frame
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// generate a WindowEvent.WINDOW_CLOSING event
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
