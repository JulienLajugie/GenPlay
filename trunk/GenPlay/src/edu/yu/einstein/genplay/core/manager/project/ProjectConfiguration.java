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
package edu.yu.einstein.genplay.core.manager.project;

import java.io.File;
import java.io.Serializable;

/**
 * Class containing the project
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ProjectConfiguration implements Serializable {

	private static final long serialVersionUID = 5632320102259442205L; 		// generated ID

	private static final String DEFAULT_RECENT_PROJECT_FILE_NAME = "GenPlayProjects.txt"; 	// the default log file name
	private static final String DEFAULT_DAS_SERVER_FILE_NAME 	= "DASServerList.xml"; 	// the default log file name
	private final static String DEFAULT_DAS_SERVER_PATH =
			"edu/yu/einstein/genplay/resource/DAS/DASServerList.xml"; 		// DAS Server List file path
	private static final String DEFAULT_DEFAULT_DIRECTORY = ""; 			// default directory
	private static final String DEFAULT_LOOK_AND_FEEL =
			"javax.swing.plaf.metal.MetalLookAndFeel";						// default look and feel

	private static final int DEFAULT_TRACK_COUNT 	= 50; 					// default number of track
	private static final int DEFAULT_TRACK_HEIGHT 	= 100; 					// default track height
	private static final int MIN_TRACK_COUNT 		= 1; 					// minimum number of tracks
	private static final int MAX_TRACK_COUNT 		= 1024; 				// maximum number of tracks
	private static final int MIN_TRACK_HEIGHT 		= 30; 					// minimum height of the tracks
	private static final int MAX_TRACK_HEIGHT 		= 2000; 				// maximum height of the tracks
	private static final int DEFAULT_UNDO_COUNT 	= 1; 					// default number of undo in memory

	private static final boolean DEFAULT_RESET_TRACK 	= true; 			// default value of reset track
	private static final boolean DEFAULT_CACHE_TRACK 	= true; 			// default value of cache track
	private static final boolean DEFAULT_SHOW_LEGEND 	= true; 			// default value of show legend


	private static String CONFIG_FILE_NAME = "GenPlay_config.cfg"; 			// the config file

	private String 	defaultDirectory; 										// default directory
	private String 	lookAndFeel = DEFAULT_LOOK_AND_FEEL; 					// look and feel
	private String 	dasServerListFile;									 	// DAS Server list
	private int 	trackCount = DEFAULT_TRACK_COUNT; 						// track count
	private int 	trackHeight = DEFAULT_TRACK_HEIGHT; 					// track height
	private int 	undoCount = DEFAULT_UNDO_COUNT; 						// number of undo in memory
	private boolean resetTrack = DEFAULT_RESET_TRACK;						// enable the reset track feature
	private boolean cacheTrack = DEFAULT_CACHE_TRACK;						// enable the reset track feature
	private boolean legend = DEFAULT_SHOW_LEGEND;							// show legend for multi genome stripes


	/**
	 * Constructor of {@link ProjectConfiguration}.
	 */
	protected ProjectConfiguration() {
		super();
		restoreDefault();
	}


	/**
	 * @return the absolute path of the configuration file
	 */
	public String getConfigFileAbsolutePath () {
		return getTemporaryDirectory() + CONFIG_FILE_NAME;
	}


	/**
	 * @return the dasServerListFile
	 */
	public final String getDASServerListFile() {
		return dasServerListFile;
	}


	/**
	 * @return the absolute path of the log file
	 */
	private String getDefaultDASServerFileAbsolutePath () {
		return getTemporaryDirectory() + DEFAULT_DAS_SERVER_FILE_NAME;
	}


	/**
	 * @return the default das server file
	 */
	public final String getDefaultDasServerListFile() {
		return this.getClass().getClassLoader().getResource(DEFAULT_DAS_SERVER_PATH).toString();
	}


	/**
	 * @return the defaultDirectory
	 */
	public final String getDefaultDirectory() {
		return defaultDirectory;
	}


	/**
	 * @return the lookAndFeel
	 */
	public final String getLookAndFeel() {
		return lookAndFeel;
	}


	/**
	 * @return the absolute path of the log file
	 */
	public String getRecentProjectsAbsolutePath () {
		return getTemporaryDirectory() + DEFAULT_RECENT_PROJECT_FILE_NAME;
	}


	/**
	 * Checks if the user is working under Windows or not (-> Unix).
	 * If the user is using Windows, the temporary directory is the default one (eg: C:\Users\USER\AppData\Local\Temp)
	 * If the user is using Unix platform, the temporary directory is its working directory
	 * @return the temporary directory path
	 */
	public String getTemporaryDirectory () {
		if (isWindowsPlatform()) {
			return System.getProperty("java.io.tmpdir");
		}
		return System.getProperty("user.home") + "/.genplay/";
	}


	/**
	 * @return the trackCount
	 */
	public final int getTrackCount() {
		return trackCount;
	}


	/**
	 * @return the trackHeight
	 */
	public final int getTrackHeight() {
		return trackHeight;
	}


	/**
	 * @return the undoCount
	 */
	public int getUndoCount() {
		return undoCount;
	}


	/**
	 * @return the cacheTrack
	 */
	public boolean isCacheTrack() {
		return cacheTrack;
	}


	/**
	 * @return the legend
	 */
	public boolean isLegend() {
		return legend;
	}


	/**
	 * @return the resetTrack
	 */
	public boolean isResetTrack() {
		return resetTrack;
	}


	/**
	 * @return true if the user is working under Windows, false otherwise (-> Unix)
	 */
	private boolean isWindowsPlatform () {
		String osName = System.getProperty("os.name");
		if ((osName.length() > 7) && osName.substring(0, 7).toUpperCase().equals("WINDOWS")) {
			return true;
		}
		return false;
	}


	/**
	 * Restores the default configuration
	 */
	public void restoreDefault() {
		defaultDirectory = DEFAULT_DEFAULT_DIRECTORY;
		dasServerListFile = getDefaultDASServerFileAbsolutePath();
		new File(dasServerListFile).delete();
		lookAndFeel = DEFAULT_LOOK_AND_FEEL;
		trackCount = DEFAULT_TRACK_COUNT;
		trackHeight = DEFAULT_TRACK_HEIGHT;
		undoCount = DEFAULT_UNDO_COUNT;
		resetTrack = DEFAULT_RESET_TRACK;
		cacheTrack = DEFAULT_CACHE_TRACK;
		legend = DEFAULT_SHOW_LEGEND;
	}


	/**
	 * @param cacheTrack the cacheTrack to set
	 */
	public void setCacheTrack(boolean cacheTrack) {
		this.cacheTrack = cacheTrack;
	}


	/**
	 * @param dasServerListFile the dasServerListFile to set
	 */
	public final void setDASServerListFile(String dasServerListFile) {
		this.dasServerListFile = dasServerListFile;
	}


	/**
	 * @param defaultDirectory the defaultDirectory to set
	 */
	public final void setDefaultDirectory(String defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}


	/**
	 * @param legend the legend to set
	 */
	public void setLegend(boolean legend) {
		this.legend = legend;
	}


	/**
	 * @param lookAndFeel the lookAndFeel to set
	 */
	public final void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}


	/**
	 * @param resetTrack the resetTrack to set
	 */
	public void setResetTrack(boolean resetTrack) {
		this.resetTrack = resetTrack;
	}


	/**
	 * @param trackCount the trackCount to set
	 */
	public final void setTrackCount(int trackCount) {
		if ((trackCount < MIN_TRACK_COUNT) || (trackCount > MAX_TRACK_COUNT)) {
			this.trackCount = DEFAULT_TRACK_COUNT;
		} else {
			this.trackCount = trackCount;
		}
	}


	/**
	 * @param trackHeight the trackHeight to set
	 */
	public final void setTrackHeight(int trackHeight) {
		if ((trackHeight < MIN_TRACK_HEIGHT) || (trackHeight > MAX_TRACK_HEIGHT)) {
			this.trackHeight = DEFAULT_TRACK_HEIGHT;
		} else {
			this.trackHeight = trackHeight;
		}
	}


	/**
	 * @param undoCount the undoCount to set
	 */
	public void setUndoCount(int undoCount) {
		if (undoCount < 0) {
			this.undoCount = DEFAULT_UNDO_COUNT;
		} else {
			this.undoCount = undoCount;
		}
	}

}
