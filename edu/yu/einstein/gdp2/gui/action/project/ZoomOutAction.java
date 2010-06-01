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
 * Zooms the {@link GenomeWindow} displayed in the application out
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ZoomOutAction extends AbstractAction {

	private static final long serialVersionUID = -2474270228967497967L;	// generated ID

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ZoomOutAction";
	
	
	/**
	 * Creates an instance of {@link ZoomOutAction}
	 */
	public ZoomOutAction() {
		super();
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	

	/**
	 * Zooms the {@link GenomeWindow} displayed in the application out
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().getControlPanel().zoomOut();
	}
}
