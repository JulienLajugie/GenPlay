/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.mainFrame.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.gui.fileFilter.GenPlayProjectFilter;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a project from a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class LoadProjectAction extends AbstractAction {
	
	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Load a project from a file"; 								// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_L; 		// mnemonic key
	private static final String 	ACTION_NAME = "Load Project";	// action name
	private final 		Component 	parent;							// parent component
	private final 		TrackList	trackList;						// track list where to load the project
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "LoadProjectAction";
	
	
	/**
	 * Creates an instance of {@link LoadProjectAction}
	 * @param parent parent component
	 */
	public LoadProjectAction(Component parent, TrackList trackList) {
		super();
		this.parent = parent;
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
	}
	

	/**
	 * Loads a project from a file
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
		final FileFilter[] fileFilters = {new GenPlayProjectFilter()};
		final File selectedFile = Utils.chooseFileToLoad(parent, "Load Project", defaultDirectory, fileFilters);
		if (selectedFile != null) {
			new ActionWorker<Void>(trackList) {
				@Override
				protected Void doAction() {
					trackList.loadProject(selectedFile);
					return null;
				}
				@Override
				protected void doAtTheEnd(Void result) {}
			}.execute();
		}
	}
}
