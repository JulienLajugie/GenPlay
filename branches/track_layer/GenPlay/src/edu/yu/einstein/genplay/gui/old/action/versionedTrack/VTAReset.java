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
package edu.yu.einstein.genplay.gui.old.action.versionedTrack;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.old.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.old.track.BinListTrack;
import edu.yu.einstein.genplay.gui.old.track.CurveTrack;



/**
 * Resets the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class VTAReset extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 4801183816800208961L;	// generated ID
	private static final String 	ACTION_NAME = "Reset";				// action name
	private static final String 	DESCRIPTION =
			"Reset the selected track";								 		// tooltip


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "reset";


	/**
	 * Creates an instance of {@link VTAReset}
	 */
	public VTAReset() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getTrackList().getSelectedTrack() instanceof CurveTrack<?>) {
			CurveTrack<?> selectedTrack = (CurveTrack<?>) getTrackList().getSelectedTrack();
			if (selectedTrack != null) {
				notifyActionStart("Reseting Track", 1, false);
				selectedTrack.resetData();
			}
		}
		return null;
	}
}
