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
package edu.yu.einstein.genplay.gui.action.project;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.ProjectRecording;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.old.action.TrackListActionWorker;


/**
 * Loads a project from a file
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PAInitMGManager extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Initialize Multi Genome Manager"; 							// tooltip
	private static final String 	ACTION_NAME = "Initialize Multi Genome Manager";	// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAInitMGManager";


	private boolean hasBeenInitialized;


	/**
	 * Creates an instance of {@link PAInitMGManager}
	 */
	public PAInitMGManager() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		hasBeenInitialized = false;
	}


	@Override
	protected Void processAction() throws Exception {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			notifyActionStart("Load Multi Genome information", 1, false);

			ProjectRecording projectRecording = RecordingManager.getInstance().getProjectRecording();
			try {
				projectRecording.initMultiGenomeManager();
				hasBeenInitialized =  true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			hasBeenInitialized =  true;
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Void actionResult) {
		if (latch != null) {
			latch.countDown();
		}
	}


	/**
	 * @return true if the managers have been initialized, false otherwise
	 */
	public boolean hasBeenInitialized () {
		return hasBeenInitialized;
	}


	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

}
