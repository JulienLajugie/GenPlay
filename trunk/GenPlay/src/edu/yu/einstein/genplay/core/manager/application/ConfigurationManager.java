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
package edu.yu.einstein.genplay.core.manager.application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import edu.yu.einstein.genplay.util.Utils;

/**
 * Class containing the project
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public final class ConfigurationManager {

	private static final String 	DEFAULT_RECENT_PROJECT_FILE_NAME=
			"GenPlayProjects.txt"; 												// the default log file name
	private static final String 	DEFAULT_DAS_SERVER_FILE_NAME 	=
			"DASServerList.xml"; 												// the default log file name
	private final static String 	DEFAULT_DAS_SERVER_PATH 		=
			"edu/yu/einstein/genplay/resource/DAS/DASServerList.xml"; 			// DAS Server List file path
	private static final String 	DEFAULT_DEFAULT_DIRECTORY 		=
			getDefaultGenPlayLibraryPath();										// default directory
	private static final String 	DEFAULT_LOOK_AND_FEEL 			=
			getDefaultLookAndFeel();											// default look and feel
	private static final boolean 	DEFAULT_SHOW_MENU_BAR			=
			getDefaultShowMenuBar();											// if the menu bar should be shown by default
	private static final int 		DEFAULT_TRACK_COUNT 			= 50; 		// default number of track
	private static final int 		DEFAULT_TRACK_HEIGHT 			= 100; 		// default track height
	private static final int 		DEFAULT_UNDO_COUNT 				= 1; 		// default number of undo in memory
	private static final boolean 	DEFAULT_RESET_TRACK 			= true; 	// default value of reset track
	private static final boolean 	DEFAULT_CACHE_TRACK 			= true; 	// default value of cache track
	private static final boolean 	DEFAULT_SHOW_LEGEND 			= true; 	// default value of show legend
	private static final int 		MIN_TRACK_COUNT 				= 1; 		// minimum number of tracks
	private static final int 		MAX_TRACK_COUNT 				= 1024; 	// maximum number of tracks
	private static final int 		MIN_TRACK_HEIGHT 				= 30; 		// minimum height of the tracks
	private static final int 		MAX_TRACK_HEIGHT 				= 2000; 	// maximum height of the tracks
	private static final String 	CONFIG_FILE_NAME 				=
			"GenPlay_config.cfg";												// the config file

	private static ConfigurationManager instance = null;


	/**
	 * @return the default GenPlay library path
	 */
	private static final String getDefaultGenPlayLibraryPath() {
		if (Utils.isWindowsOS()) {
			// dirty trick to get My Documents on windows
			String myDocumentsPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
			return myDocumentsPath + File.separator + "GenPlay Library" + File.separator;
		} else {
			return System.getProperty("user.home") + File.separator + "Documents" + File.separator + "GenPlay Library" + File.separator;
		}
	}


	/**
	 * @return the default look and feel
	 */
	private static String getDefaultLookAndFeel() {
		if (Utils.isMacOS()) {
			return UIManager.getSystemLookAndFeelClassName();
		} else {
			return "javax.swing.plaf.metal.MetalLookAndFeel";
		}
	}


	/**
	 * @return true if the menu bar should be shown by default
	 */
	private static boolean getDefaultShowMenuBar() {
		if (Utils.isMacOS()) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * @return an instance of a {@link ConfigurationManager}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ConfigurationManager getInstance() {
		if (instance == null) {
			synchronized(ConfigurationManager.class) {
				if (instance == null) {
					instance = new ConfigurationManager();
				}
			}
		}
		return instance;
	}


	private String 	defaultDirectory; 				// default directory
	private String 	lookAndFeel; 					// look and feel
	private boolean showMenuBar;					// true if the main menu bar should be shown
	private String 	dasServerListFile;				// DAS Server list
	private int 	trackCount; 					// track count
	private int 	trackHeight; 					// track height
	private int 	undoCount; 						// number of undo in memory
	private boolean resetTrack;						// enable the reset track feature
	private boolean cacheTrack;						// enable the reset track feature
	private boolean legend;							// show legend for multi genome stripes


	/**
	 * Constructor of {@link ConfigurationManager}.
	 */
	protected ConfigurationManager() {
		super();
		restoreDefault();
	}


	/**
	 * Reads a line from the configuration file and extracts the data
	 * @param line a line from the configuration file
	 */
	private void extractLine(String line) {
		int index = line.indexOf(':');
		// if we find a character ':'
		if (index != -1) {
			String key = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();
			if ((key != null) && (key.length() > 0) && (value != null) && (value.length() > 0)) {
				if (key.equalsIgnoreCase("DAS Server List file")) {
					setDASServerListFile(value);
				} else if (key.equalsIgnoreCase("default directory")) {
					setDefaultDirectory(value);
				} else if (key.equalsIgnoreCase("look and feel")) {
					setLookAndFeel(value);
				} else if (key.equalsIgnoreCase("show menu bar")) {
					setShowMenuBar(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("track count")) {
					setTrackCount(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("track height")) {
					setTrackHeight(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("undo count")) {
					setUndoCount(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("reset track")) {
					setResetTrack(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("cache track")) {
					setCacheTrack(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("show legend")) {
					setLegend(Boolean.parseBoolean(value));
				}
			}
		}
	}


	/**
	 * @return the absolute path of the configuration file
	 */
	public String getConfigFileAbsolutePath () {
		return Utils.getConfigurationDirectoryPath() + CONFIG_FILE_NAME;
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
		return Utils.getConfigurationDirectoryPath() + DEFAULT_DAS_SERVER_FILE_NAME;
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
		return Utils.getConfigurationDirectoryPath() + DEFAULT_RECENT_PROJECT_FILE_NAME;
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
	 * @return true if the main menu bar should be shown
	 */
	public final boolean isMenuBarShown() {
		return showMenuBar;
	}


	/**
	 * @return the resetTrack
	 */
	public boolean isResetTrack() {
		return resetTrack;
	}


	/**
	 * Load the configuration from a file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void loadConfiguration() throws IOException, FileNotFoundException {
		BufferedReader reader = null;
		try {
			File dir = new File(Utils.getConfigurationDirectoryPath());
			dir.mkdirs();
			File configFile = new File(getConfigFileAbsolutePath());
			reader = new BufferedReader(new FileReader(configFile));
			// extract data
			String line = null;
			while ((line = reader.readLine()) != null) {
				extractLine(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}


	/**
	 * Restores the default configuration
	 */
	public void restoreDefault() {
		defaultDirectory = DEFAULT_DEFAULT_DIRECTORY;
		dasServerListFile = getDefaultDASServerFileAbsolutePath();
		new File(dasServerListFile).delete();
		lookAndFeel = DEFAULT_LOOK_AND_FEEL;
		showMenuBar = DEFAULT_SHOW_MENU_BAR;
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
	 * @param showMenuBar set to true to show the main menu bar
	 */
	public final void setShowMenuBar(boolean showMenuBar) {
		this.showMenuBar = showMenuBar;
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


	/**
	 * Writes the configuration on the disk
	 * @throws IOException
	 */
	public void writeConfiguration() throws IOException {
		BufferedWriter writer = null;
		try {
			File dir = new File(Utils.getConfigurationDirectoryPath());
			dir.mkdirs();
			File configFile = new File(getConfigFileAbsolutePath());
			writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("DAS Server List file: " + getDASServerListFile());
			writer.newLine();
			writer.write("default directory: " + getDefaultDirectory());
			writer.newLine();
			writer.write("look and feel: " + getLookAndFeel());
			writer.newLine();
			writer.write("show menu bar: " + isMenuBarShown());
			writer.newLine();
			writer.write("track count: " + getTrackCount());
			writer.newLine();
			writer.write("track height: " + getTrackHeight());
			writer.newLine();
			writer.write("undo count: " + getUndoCount());
			writer.newLine();
			writer.write("reset track: " + isResetTrack());
			writer.newLine();
			writer.write("cache track: " + isCacheTrack());
			writer.newLine();
			writer.write("show legend: " + isLegend());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
