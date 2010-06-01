/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import yu.einstein.gdp2.gui.dialog.TextDialog;


/**
 * Shows the help dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class HelpAction extends AbstractAction {

	private static final long serialVersionUID = -8315224036423121225L; // generated ID

	private static final String 	HELP_URL = 
		"http://129.98.70.139/documents/help.html";				// URL of the help file
	private static final String 	HELP_DIALOG_TITLE = "Help";	// title of the help JDialog
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
	public static final String ACTION_KEY = "help"; 


	/**
	 * Creates an instance of {@link HelpAction}
	 * @param parent parent component
	 */
	public HelpAction(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
//		putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	

	/**
	 * Shows the help dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TextDialog.showDialog(parent, HELP_URL, HELP_DIALOG_TITLE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
