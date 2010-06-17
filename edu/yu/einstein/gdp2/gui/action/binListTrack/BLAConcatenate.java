/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.writer.binListWriter.ConcatenateBinListWriter;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Concatenates the selected track with other tracks in an output file
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLAConcatenate extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6381691669271998493L;			// generated ID
	private static final String 	ACTION_NAME = "Concatenate";				// action name
	private static final String 	DESCRIPTION = 
		"Concatenate the selected track with other tracks in an output file";	// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAConcatenate";


	/**
	 * Creates an instance of {@link BLAConcatenate}
	 */
	public BLAConcatenate() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(Void actionResult) {} // nothing to do


	@Override
	protected Void processAction() throws Exception {
		Track[] selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getBinListTracks());
		// we want to have at least two tracks
		if ((selectedTracks != null) && (selectedTracks.length > 1)) {
			// save dialog
			String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setDialogTitle("Save As");
			jfc.setSelectedFile(new File(".txt"));
			int returnVal = jfc.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {	
				File selectedFile = Utils.addExtension(jfc.getSelectedFile(), "txt");
				if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
					// create arrays with the selected BinLists and names
					BinList[] binListArray = new BinList[selectedTracks.length];
					String[] nameArray = new String[selectedTracks.length];
					for (int i = 0; i < selectedTracks.length; i++) {
						binListArray[i] = ((BinListTrack)selectedTracks[i]).getBinList();
						nameArray[i] = selectedTracks[i].getName();
					}
					notifyActionStart("Generating File", 1, false);
					new ConcatenateBinListWriter(binListArray, nameArray, selectedFile).write();
				}
			}
		}	
		return null;
	}
}
