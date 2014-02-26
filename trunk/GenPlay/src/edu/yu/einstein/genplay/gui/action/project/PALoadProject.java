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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.fileFilter.GenPlayProjectFilter;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.trackList.TrackListModel;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;
import edu.yu.einstein.genplay.util.FileChooser;

/**
 * Loads a project from a file
 * 
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PALoadProject extends TrackListActionWorker<Track[]> {

	private static final long serialVersionUID = 6498078428524511709L; 		// generated ID
	private static final String DESCRIPTION = "Load a project from a file"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_L; 					// mnemonic key
	private static final String ACTION_NAME = "Load Project"; 				// action name
	private boolean 			skipFileSelection = false; 					// true if the file selection need to be skipped. Default is false

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PALoadProject.class.getName();


	/**
	 * Creates an instance of {@link PALoadProject}
	 */
	public PALoadProject() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected void doAtTheEnd(Track[] actionResult) {
		if (actionResult != null) {
			skipFileSelection = false;

			MainFrame.getInstance().setTitle();
			MainFrame.getInstance().getControlPanel().reinitChromosomePanel();
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				MainFrame.getInstance().getControlPanel().resetGenomeNames(ProjectManager.getInstance().getMultiGenomeProject().getGenomeNames());
			}

			MainFrame.getInstance().setVisible(true);
			MainFrame.getInstance().getTrackListPanel().getModel().setTracks(actionResult);

			TrackListPanel trackListPanel = MainFrame.getInstance().getTrackListPanel();
			Track[] tracks = trackListPanel.getModel().getTracks();
			for (Track currentTrack : tracks) {
				for (Layer<?> layer: currentTrack.getLayers().getLayers()) {
					MGDisplaySettings.getInstance().restoreInformation(layer);
				}
			}

			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				MGDisplaySettings.getInstance().restoreGenomeCoordinate();
			}

			// Finally closes all opened streams
			RecordingManager.getInstance().getProjectRecording().closeStreams();
		}

		if (latch != null) {
			latch.countDown();
		}
	}


	@Override
	protected Track[] processAction() throws Exception {
		boolean hasBeenInitialized = true; // If the file has been selected,
		// managers have been successfully
		// initialized.
		if (!skipFileSelection) {
			FileFilter[] fileFilters = { new GenPlayProjectFilter() };
			File selectedFile = FileChooser.chooseFile(getRootPane(), FileChooser.OPEN_FILE_MODE, "Load Project", fileFilters, true);
			if (selectedFile == null) {
				return null;
			}

			// Initialize the project manager
			PAInitManagers init = new PAInitManagers();
			init.setFile(selectedFile);
			init.actionPerformed(null);

			// Initialize the project manager
			PAInitMGManager initMG = new PAInitMGManager();
			CountDownLatch latch = new CountDownLatch(1);
			initMG.setLatch(latch);
			initMG.actionPerformed(null);
			try {
				latch.await();
			} catch (InterruptedException e) {
				ExceptionManager.getInstance().caughtException(e);
			}

			// Check if everything has been initialized
			if (!init.hasBeenInitialized() || !initMG.hasBeenInitialized()) {
				hasBeenInitialized = false;
			}
			try {
				TrackListModel trackListModel = new TrackListModel();
				MainFrame.getInstance().getTrackListPanel().setModel(trackListModel); // we remove all the track before the loading (better for memory usage)
			} catch (Exception e) {
				// do nothing, dirty trick to avoid getting an awt error
			}
		}
		if (hasBeenInitialized) {
			notifyActionStart("Loading Project", 1, false);
			return RecordingManager.getInstance().getProjectRecording().getTrackList();
		}
		return null;
	}


	/**
	 * @param latch
	 *            the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}


	/**
	 * Sets the user selection of a file to load needs to be skipped
	 * 
	 * @param skipFileSelection
	 *            true if no files need to be selected by the user
	 */
	public void setSkipFileSelection(boolean skipFileSelection) {
		this.skipFileSelection = skipFileSelection;
	}
}
