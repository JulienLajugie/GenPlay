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
 * Zooms the {@link GenomeWindow} displayed in the application in
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ZoomInAction extends AbstractAction {

	private static final long serialVersionUID = -8652598240640813151L;	// generated ID

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ZoomInAction";
	
	
	/**
	 * Zooms the {@link GenomeWindow} displayed in the application in
	 */
	public ZoomInAction() {
		super();
        putValue(ACTION_COMMAND_KEY, ACTION_KEY);
        putValue(ACCELERATOR_KEY, ACCELERATOR);
	}
	

	/**
	 * Zooms the {@link GenomeWindow} displayed in the application in
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().getControlPanel().zoomIn();
	}
}
