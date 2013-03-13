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
package edu.yu.einstein.genplay.gui.action.multiGenome.properties;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;


/**
 * This class updates the tracks display in a multi genome project
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGATrackDisplayRefresh extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;		// generated ID
	private static final String 	DESCRIPTION =
			"Refreshes the track display"; 						// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 			// mnemonic key
	private static		 String 			ACTION_NAME = "Updating tracks";	// action name

	private					MGDisplaySettings 	settings;						// the multi genome settings object shortcut

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Refreshes the track display";


	/**
	 * Creates an instance of {@link MGATrackDisplayRefresh}.
	 */
	public MGATrackDisplayRefresh() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	protected Boolean processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();

		// Checks if the project is multi-genome
		if (projectManager.isMultiGenomeProject()) {

			// Notifies the action
			notifyActionStart(ACTION_NAME, 1, false);

			settings = MGDisplaySettings.getInstance();

			// Update tracks
			List<Layer<?>> layers = MainFrame.getInstance().getTrackListPanel().getModel().getAllLayers();
			for (Layer<?> layer: layers) {
				if (layer instanceof VariantLayer) {
					List<MGFilter> filtersList = settings.getFilterSettings().getMGFiltersForTrack(layer);
					((VariantLayer)layer).setData(filtersList);
				}
			}

			return true;
		}

		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (actionResult) {
			Track[] tracks = MainFrame.getInstance().getTrackListPanel().getModel().getTracks();
			for (Track track: tracks) {
				if (track != null) {
					track.repaint();
				}
			}
		}
		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}