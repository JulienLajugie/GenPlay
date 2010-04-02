/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.writer.binListWriter.ConcatenateBinListWriter;
import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;


/**
 * Concatenates the selected track with other tracks in an output file
 * @author Julien Lajugie
 * @version 0.1
 */
public class ConcatenateAction extends TrackListAction {

	private static final long serialVersionUID = 6381691669271998493L;			// generated ID
	private static final String 	ACTION_NAME = "Concatenate";				// action name
	private static final String 	DESCRIPTION = 
		"Concatenate the selected track with other tracks in an output file";	// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "concatenateBinList";


	/**
	 * Creates an instance of {@link ConcatenateAction}
	 * @param trackList a {@link TrackList}
	 */
	public ConcatenateAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Concatenates the selected track with other tracks in an output file
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final Track[] selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), trackList.getBinListTracks());
		// we want to have at least two tracks
		if (selectedTracks.length > 1) {
			// save dialog
			final String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
			final JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setDialogTitle("Save As");
			jfc.setSelectedFile(new File(".txt"));
			final int returnVal = jfc.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {	
				final File selectedFile = Utils.addExtension(jfc.getSelectedFile(), "txt");
				if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
					// create arrays with the selected BinLists and names
					final BinList[] binListArray = new BinList[selectedTracks.length];
					final String[] nameArray = new String[selectedTracks.length];
					for (int i = 0; i < selectedTracks.length; i++) {
						binListArray[i] = ((BinListTrack)selectedTracks[i]).getBinList();
						nameArray[i] = selectedTracks[i].getName();
					}
					// thread for the action
					new ActionWorker<Void>(trackList, "Concatenating Tracks") {
						@Override
						protected Void doAction() {
							try {
								new ConcatenateBinListWriter(trackList.getChromosomeManager(), binListArray, nameArray, selectedFile).write();
								return null;
							} catch (IOException e) {
								ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks");
								return null;
							} catch (BinListDifferentWindowSizeException e) {
								ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks: different bin sizes");
								return null;
							} 
						}
						@Override
						protected void doAtTheEnd(Void actionResult) {}
					}.execute();
				}
			}
		}		
	}

}