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

import yu.einstein.gdp2.core.manager.ExceptionManager;


/**
 * Shows the about dialog window
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAAbout extends AbstractAction {
	
	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ABOUT_URL = 
		"http://www.genplay.net/wiki/index.php/About_GenPlay";			// URL of the about file
	private static final String 	DESCRIPTION = "Show About GenPlay"; // tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_A; 			// mnemonic key
	private static final String 	ACTION_NAME = "About GenPlay";		// action name
	private final 		Component 	parent;								// parent component
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAAbout";
	
	
	/**
	 * Creates an instance of {@link PAAbout}
	 * @param parent parent component
	 */
	public PAAbout(Component parent) {
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
	public void actionPerformed(ActionEvent evt) {
		try {
			if (Desktop.isDesktopSupported()) {
				URI uri = new URI(ABOUT_URL);
				Desktop.getDesktop().browse(uri);
			}
		} catch (Exception e) {
			ExceptionManager.handleException(parent, e, "The about file can't be loaded");
		}
	}
}
