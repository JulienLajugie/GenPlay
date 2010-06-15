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

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;


/**
 * Increments the start and the stop positions of the {@link GenomeWindow} displayed in the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAMoveRight extends AbstractAction {

	private static final long serialVersionUID = 4888602850021128872L; // generated ID

	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMoveRight";
	
	
	/**
	 * Creates an instance of {@link PAMoveRight}
	 */
	public PAMoveRight() {
		super();
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	

	/**
	 * Increments the start and the stop positions of the {@link GenomeWindow} displayed in the application
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().getControlPanel().moveRight();
	}
}
