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
import javax.swing.JFileChooser;

import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.fileFilter.GenPlayProjectFilter;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the project into a file
 * @author Julien Lajugie
 * @version 0.1
 */
public class SaveProjectAction extends AbstractAction {

	private static final long serialVersionUID = -8503082838697971220L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Save the project into a file"; 							// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_S; 		// mnemonic key
	private static final String 	ACTION_NAME = "Save Project";	// action name
	private final 		Component 	parent;							// parent component
	private final 		TrackList	trackList;						// track list containing the project to save

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SaveProjectAction";


	/**
	 * Creates an instance of {@link SaveProjectAction}
	 * @param parent parent component
	 */
	public SaveProjectAction(Component parent, TrackList trackList) {
		super();
		this.parent = parent;
		this.trackList = trackList;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Saves the project into a file
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final JFileChooser jfc = new JFileChooser(trackList.getConfigurationManager().getDefaultDirectory());
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Save Project");
		jfc.addChoosableFileFilter(new GenPlayProjectFilter());
		jfc.setAcceptAllFileFilterUsed(false);
		final int returnVal = jfc.showSaveDialog(parent);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			final ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			final File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(parent, selectedFile)) {
				new ActionWorker<Void>(trackList) {
					@Override
					protected Void doAction() {
						trackList.saveProject(selectedFile);
						return null;
					}
					@Override
					protected void doAtTheEnd(Void result) {}
				}.execute();
				
			}
		}
	}
}
