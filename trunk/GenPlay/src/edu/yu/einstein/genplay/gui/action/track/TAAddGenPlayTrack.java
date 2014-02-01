/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.IO.extractor.Extractor;
import edu.yu.einstein.genplay.core.IO.extractor.TransferableTrackExtractor;
import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

public class TAAddGenPlayTrack extends TrackListActionWorker<Track> {

	/** Generated ID */
	private static final long serialVersionUID = -402258482159373659L;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddGenPlayTrack.class.getName();

	private static final String ACTION_NAME = "Add GenPlay Track";									// action name
	private static final String DESCRIPTION = "Add a GenPlay Track";								// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_A; 											// mnemonic key

	private final Extractor extractor; 				// extractor to extract the track


	/**
	 * Loads one or multiple layers to a specified track
	 */
	public TAAddGenPlayTrack(Extractor extractor) {
		super();
		this.extractor = extractor;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected void doAtTheEnd(Track actionResult) {
		if (actionResult != null) {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			if (selectedTrack.getLayers().isEmpty()) {
				selectedTrack.setContentAs(actionResult);
			} else {
				Layer<?>[] layers = actionResult.getLayers().getLayers();
				for (Layer<?> currentLayer: layers) {
					currentLayer.setTrack(selectedTrack);
					selectedTrack.getLayers().add(currentLayer);
					selectedTrack.setActiveLayer(currentLayer);
				}
			}
		}
	}


	@Override
	protected Track processAction() throws Exception {
		try {
			TransferableTrackExtractor transferableTrackExtractor = (TransferableTrackExtractor) extractor;
			return transferableTrackExtractor.extract();
		} catch (IncompatibleAssembliesException e) {
			JOptionPane.showMessageDialog(getRootPane(), "The specified file cannot be loaded. "
					+ "\nIt contains a track from a project with different assembly or multigenome parameters.", "Invalid File", JOptionPane.WARNING_MESSAGE, null);
			return null;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getRootPane(), "The specified file cannot be loaded.", "Invalid File", JOptionPane.WARNING_MESSAGE, null);
			return null;
		}
	}
}
