/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.core.manager.ExceptionManager;


/**
 * Shows the help dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAHelp extends AbstractAction {

	private static final long serialVersionUID = -8315224036423121225L; // generated ID
	private static final String 	HELP_URL = 
		"http://www.genplay.net/wiki/index.php/Documentation";	// URL of the help file
	private static final int 		MNEMONIC = KeyEvent.VK_H; 	// mnemonic key
	private static final String 	ACTION_NAME = "Help";		// action name
	private final 		 Component 	parent;						// parent component


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAHelp"; 


	/**
	 * Creates an instance of {@link PAHelp}
	 * @param parent parent component
	 */
	public PAHelp(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Shows the help dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			if (Desktop.isDesktopSupported()) {
				URI uri = new URI(HELP_URL);
				Desktop.getDesktop().browse(uri);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(parent, e, "The help file can't be loaded");
		}
	}
}
