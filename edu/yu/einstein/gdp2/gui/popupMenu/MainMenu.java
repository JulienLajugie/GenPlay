/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import yu.einstein.gdp2.gui.mainFrame.action.AboutAction;
import yu.einstein.gdp2.gui.mainFrame.action.ExitAction;
import yu.einstein.gdp2.gui.mainFrame.action.FullScreenAction;
import yu.einstein.gdp2.gui.mainFrame.action.HelpAction;
import yu.einstein.gdp2.gui.mainFrame.action.LoadProjectAction;
import yu.einstein.gdp2.gui.mainFrame.action.OptionAction;
import yu.einstein.gdp2.gui.mainFrame.action.SaveProjectAction;


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
		
		jmiLoadProject = new JMenuItem(actionMap.get(LoadProjectAction.ACTION_KEY));
		jmiSaveProject = new JMenuItem(actionMap.get(SaveProjectAction.ACTION_KEY));
		jmiFullScreen = new JMenuItem(actionMap.get(FullScreenAction.ACTION_KEY));
		jmiOption = new JMenuItem(actionMap.get(OptionAction.ACTION_KEY));
		jmiHelp = new JMenuItem(actionMap.get(HelpAction.ACTION_KEY));
		jmiAbout = new JMenuItem(actionMap.get(AboutAction.ACTION_KEY));
		jmiExit = new JMenuItem(actionMap.get(ExitAction.ACTION_KEY));
		
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
