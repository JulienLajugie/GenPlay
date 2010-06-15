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
 * Decrements the start and the stop positions of the {@link GenomeWindow} displayed in the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAMoveLeft extends AbstractAction {

	private static final long serialVersionUID = -416430771224618219L; // generated ID

	
	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMoveLeft";
	
	
	/**
	 * Creates an instance of {@link PAMoveLeft}
	 */
	public PAMoveLeft() {
		super();
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	

	/**
	 * Decrements the start and the stop positions of the {@link GenomeWindow} displayed in the application
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().getControlPanel().moveLeft();
	}
}
