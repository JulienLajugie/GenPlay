/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.allTrack;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the selected track as a PNG image
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ATASaveAsImage extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = -4363481310731795005L; 				// generated ID
	private static final String ACTION_NAME = "Save as Image"; 							// action name
	private static final String DESCRIPTION = "Save the selected track as a PNG image"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_A; 								// mnemonic key


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATASaveAsImage";


	/**
	 * Creates an instance of {@link ATASaveAsImage}
	 */
	public ATASaveAsImage() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Void processAction() throws Exception {
		Track<?> selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			JFileChooser saveFC = new JFileChooser(ConfigurationManager.getInstance().getDefaultDirectory());
			saveFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG file (*.PNG)", "png");
			saveFC.setFileFilter(filter);
			saveFC.setDialogTitle("Save track " + selectedTrack.getName() + " as a PNG image");
			int returnVal = saveFC.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (!Utils.cancelBecauseFileExist(getRootPane(), saveFC.getSelectedFile())) {
					notifyActionStart("Saving Track #" + selectedTrack.getTrackNumber() + " As Image", 1, false);
					selectedTrack.saveAsImage(Utils.addExtension(saveFC.getSelectedFile(), "png"));
				}
			}
		}
		return null;
	}
}
