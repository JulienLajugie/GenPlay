/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the selected track as a JPG image
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SaveAsImageAction extends TrackListAction {

	private static final long serialVersionUID = -4363481310731795005L; 				// generated ID
	private static final String ACTION_NAME = "Save as Image"; 							// action name
	private static final String DESCRIPTION = "Save the selected track as a JPG image"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_A; 								// mnemonic key

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "saveAsImage";


	/**
	 * Creates an instance of {@link SaveAsImageAction}
	 * @param trackList a {@link TrackList}
	 */
	public SaveAsImageAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Saves the selected track as a JPG image
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final Track selectedTrack = trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final JFileChooser saveFC = new JFileChooser(trackList.getConfigurationManager().getDefaultDirectory());
			saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			saveFC.setDialogTitle("Save track " + selectedTrack.getName() + " as a JPG image");
			saveFC.setSelectedFile(new File(".jpg"));
			final int returnVal = saveFC.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (!Utils.cancelBecauseFileExist(getRootPane(), saveFC.getSelectedFile())) {
					// thread for the action
					new ActionWorker<Void>(trackList) {
						@Override
						protected Void doAction() {
							selectedTrack.saveAsImage(Utils.addExtension(saveFC.getSelectedFile(), "jpg"));
							return null;
						}
						@Override
						protected void doAtTheEnd(Void actionResult) {}
					}.execute();
				}
			}
		}
	}
}
