/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.writer.Writer;
import yu.einstein.gdp2.core.writer.WriterFactory;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.fileFilter.ExtendedFileFilter;
import yu.einstein.gdp2.gui.statusBar.Stoppable;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;

/**
 * Saves the selected {@link Track}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATASave extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 212223991804272305L;	// generated ID
	private static final String 	ACTION_NAME = "Save As";			// action name
	private static final String 	DESCRIPTION = 
		"Save the selected track";								 		// tooltip
	private Writer 					writer;								// object that writes the data

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "saveTrack";


	/**
	 * Creates an instance of {@link ATASave}
	 */
	public ATASave() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getTrackList().getSelectedTrack() != null) {
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setDialogTitle("Save Track");
			Track<?> selectedTrack = getTrackList().getSelectedTrack(); 
			FileFilter[] filters = null;
			if (selectedTrack instanceof BinListTrack) {
				filters = Utils.getWritableBinListFileFilters();
			} else if (selectedTrack instanceof GeneListTrack) {
				filters = Utils.getWritableGeneFileFilters();
			} else if (selectedTrack instanceof SCWListTrack) {
				filters = Utils.getWritableSCWFileFilter();
			} else {
				// case where we don't know how to save the type of the selected track
				return null;
			}		
			for (FileFilter currentFilter: filters) {
				jfc.addChoosableFileFilter(currentFilter);
			}
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
			int returnVal = jfc.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
				File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
				if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
					ChromosomeListOfLists<?> data = (ChromosomeListOfLists<?>) selectedTrack.getData();
					String name = selectedTrack.getName();
					writer = WriterFactory.getWriter(selectedFile, data, name, selectedFilter);
					notifyActionStart("Saving Track #" + selectedTrack.getTrackNumber(), 1, writer instanceof Stoppable);
					writer.write();
				}
			}		}
		return null;
	}
	
	
	@Override
	public void stop() {
		if ((writer != null) && (writer instanceof Stoppable)) {
			((Stoppable)writer).stop();
		}
		super.stop();
	}
}
