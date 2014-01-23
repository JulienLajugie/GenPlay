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
package edu.yu.einstein.genplay.core.manager.recording;



/**
 * This class manages the project records.
 * It is organized in three different parts:
 * - Application Records: all about GenPlay configuration
 * - Project Records: save/load the current project
 * - Recent Project Records: basic information about last saved projects
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class RecordingManager {

	private static RecordingManager instance;							// Unique instance of the singleton
	/**
	 * @return the instance of the singleton {@link RecordingManager}.
	 */
	public static RecordingManager getInstance () {
		if (instance == null) {
			instance = new RecordingManager();
		}
		return instance;
	}
	private final ApplicationRecording 		applicationRecording;		// Instance of the application recording manager
	private final ProjectRecording 			projectRecording;			// Instance of the project recording manager


	private final RecentProjectRecording 	recentProjectRecording;		// Instance of the recent project recording


	/**
	 * Constructor of {@link RecordingManager}
	 */
	private RecordingManager () {
		applicationRecording = new ApplicationRecording();
		projectRecording = new ProjectRecording();
		recentProjectRecording = new RecentProjectRecording();
	}


	/**
	 * @return the applicationRecording
	 */
	public ApplicationRecording getApplicationRecording() {
		return applicationRecording;
	}


	/**
	 * @return the projectRecording
	 */
	public ProjectRecording getProjectRecording() {
		return projectRecording;
	}


	/**
	 * @return the recentProjectRecording
	 */
	public RecentProjectRecording getRecentProjectRecording() {
		return recentProjectRecording;
	}

}
