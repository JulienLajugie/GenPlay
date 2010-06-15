/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import yu.einstein.gdp2.gui.action.project.PAAbout;
import yu.einstein.gdp2.gui.action.project.PAExit;
import yu.einstein.gdp2.gui.action.project.PAFullScreen;
import yu.einstein.gdp2.gui.action.project.PAHelp;
import yu.einstein.gdp2.gui.action.project.PALoadProject;
import yu.einstein.gdp2.gui.action.project.PAOption;
import yu.einstein.gdp2.gui.action.project.PASaveProject;


/**
 * Main menu of the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MainMenu extends JPopupMenu {

	private static final long serialVersionUID = -8543113416095307670L; // generated ID

	private final JMenuItem jmiSaveProject;	// menu save project
	private final JMenuItem jmiLoadProject;	// menu load project
	private final JMenuItem jmiFullScreen;	// full screen
	private final JMenuItem jmiOption;		// option
	private final JMenuItem jmiHelp;		// help
	private final JMenuItem jmiAbout;		// about
	private final JMenuItem jmiExit;		// exit
	
	
	/**
	 * Creates an instance of {@link MainMenu}
	 * @param actionMap {@link ActionMap} containing the {@link Action} of this menu
	 */
	public MainMenu(ActionMap actionMap) {
		super("Main Menu");		
		
		jmiLoadProject = new JMenuItem(actionMap.get(PALoadProject.ACTION_KEY));
		jmiSaveProject = new JMenuItem(actionMap.get(PASaveProject.ACTION_KEY));
		jmiFullScreen = new JMenuItem(actionMap.get(PAFullScreen.ACTION_KEY));
		jmiOption = new JMenuItem(actionMap.get(PAOption.ACTION_KEY));
		jmiHelp = new JMenuItem(actionMap.get(PAHelp.ACTION_KEY));
		jmiAbout = new JMenuItem(actionMap.get(PAAbout.ACTION_KEY));
		jmiExit = new JMenuItem(actionMap.get(PAExit.ACTION_KEY));
		
		add(jmiLoadProject);
		add(jmiSaveProject);
		addSeparator();
		add(jmiFullScreen);
		add(jmiOption);
		addSeparator();
		add(jmiHelp);
		add(jmiAbout);
		addSeparator();
		add(jmiExit);
	}
}
