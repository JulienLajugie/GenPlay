/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.allTrack;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Utils;



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
