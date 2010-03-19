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

import yu.einstein.gdp2.gui.dialog.TextDialog;


/**
 * Shows the about dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class AboutAction extends AbstractAction {
	
	private static final long serialVersionUID = 2102571378866219218L; // generated ID

	private static final String 	ABOUT_URL = 
		"http://129.98.70.139/documents/about.html";							// URL of the help file
	private static final String 	ABOUT_DIALOG_TITLE = "About"; 		// title of the help JDialog
	private static final String 	DESCRIPTION = "Show About GenPlay"; // tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_A; 			// mnemonic key
	private static final String 	ACTION_NAME = "About GenPlay";		// action name
	private final 		Component 	parent;								// parent component
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "about";
	
	
	/**
	 * Creates an instance of {@link AboutAction}
	 * @param parent parent component
	 */
	public AboutAction(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
	}
	

	/**
	 * Shows the about dialog window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			TextDialog.showDialog(parent, ABOUT_URL, ABOUT_DIALOG_TITLE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}