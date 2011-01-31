/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.fileFilter.GenPlayProjectFilter;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a project from a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class PALoadProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Load a project from a file"; 								// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_L; 		// mnemonic key
	private static final String 	ACTION_NAME = "Load Project";	// action name
	private final 		TrackList	trackList;						// track list where to load the project
	private File 					selectedFile;					// selected file


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PALoadProject";


	/**
	 * Creates an instance of {@link PALoadProject}
	 */
	public PALoadProject(TrackList trackList) {
		super();
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		FileFilter[] fileFilters = {new GenPlayProjectFilter()};
		selectedFile = Utils.chooseFileToLoad(trackList.getRootPane(), "Load Project", defaultDirectory, fileFilters);
		if (selectedFile != null) {
			notifyActionStart("Loading Project", 1, false);
			trackList.loadProject(selectedFile);
			return true;
		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (actionResult) {
			JFrame mainFrame = (JFrame)trackList.getTopLevelAncestor();
			String projectName = Utils.getFileNameWithoutExtension(selectedFile);
			mainFrame.setTitle(projectName + MainFrame.APPLICATION_TITLE);
		}
	}
}
