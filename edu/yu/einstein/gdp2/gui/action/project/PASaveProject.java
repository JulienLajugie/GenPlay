/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.project;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.fileFilter.GenPlayProjectFilter;
import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the project into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class PASaveProject extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = -8503082838697971220L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Save the project into a file"; 							// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_S; 		// mnemonic key
	private static final String 	ACTION_NAME = "Save Project";	// action name
	private final 		TrackList	trackList;						// track list containing the project to save
	private File 					selectedFile;					// selected file
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PASaveProject";


	/**
	 * Creates an instance of {@link PASaveProject}
	 */
	public PASaveProject(TrackList trackList) {
		super();
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Void processAction() throws Exception {
		final JFileChooser jfc = new JFileChooser(ConfigurationManager.getInstance().getDefaultDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Project");
		jfc.addChoosableFileFilter(new GenPlayProjectFilter());
		jfc.setAcceptAllFileFilterUsed(false);
		int returnVal = jfc.showSaveDialog(trackList.getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(trackList.getRootPane(), selectedFile)) {
				trackList.saveProject(selectedFile);
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Void actionResult) {
		JFrame mainFrame = (JFrame)trackList.getTopLevelAncestor();
		mainFrame.setTitle(selectedFile.getName() + MainFrame.APPLICATION_TITLE);
	}
}
