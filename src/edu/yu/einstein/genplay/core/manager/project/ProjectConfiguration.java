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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private static final String DEFAULT_LOG_FILE_NAME 			= "GenPlayLog.txt"; 	// the default log file name
	private static final String DEFAULT_DAS_SERVER_FILE_NAME 	= "DASServerList.xml"; 	// the default log file name
	private final static String DEFAULT_DAS_SERVER_PATH = 
		"edu/yu/einstein/genplay/resource/DASServerList.xml"; 				// DAS Server List file path
	private static final String DEFAULT_DEFAULT_DIRECTORY = ""; 			// default directory
	private static final String DEFAULT_LOOK_AND_FEEL = 
		"javax.swing.plaf.metal.MetalLookAndFeel";							// default look and feel
	
	private static final int DEFAULT_TRACK_COUNT = 50; 						// default number of track
	private static final int DEFAULT_TRACK_HEIGHT = 100; 					// default track height
	private static final int MIN_TRACK_COUNT = 1; 							// minimum number of tracks
	private static final int MAX_TRACK_COUNT = 1024; 						// maximum number of tracks
	private static final int MIN_TRACK_HEIGHT = 30; 						// minimum height of the tracks
	private static final int MAX_TRACK_HEIGHT = 2000; 						// maximum height of the tracks
	private static final int DEFAULT_UNDO_COUNT = 1; 						// default number of undo in memory
	private static final int PROJECT_NUMBER = 5;							// number of recent project path to save
	
	private static String CONFIG_FILE_NAME = "GenPlay_config.cfg"; 			// the config file
	
	private String logFilePath; 											// log file
	private String defaultDirectory; 										// default directory
	private String lookAndFeel = DEFAULT_LOOK_AND_FEEL; 					// look and feel
	private String dasServerListFile;									 	// DAS Server list
	private int trackCount = DEFAULT_TRACK_COUNT; 							// track count
	private int trackHeight = DEFAULT_TRACK_HEIGHT; 						// track height
	private int undoCount = DEFAULT_UNDO_COUNT; 							// number of undo in memory
	private boolean legend = true;											// show legend for multi genome stripes
	private String[] projects;												// list of the recent project paths
	private String currentProjectPath;										// path to the current project

	
	/**
	 * Constructor of {@link ProjectConfiguration}.
	 */
	protected ProjectConfiguration() {
		super();
		projects = new String[PROJECT_NUMBER];
		restoreDefault();
	}

	
	/**
	 * Restores the default configuration
	 */
	public void restoreDefault() {
		logFilePath = getDefaultLogFileAbsolutePath();
		defaultDirectory = DEFAULT_DEFAULT_DIRECTORY;
		dasServerListFile = getDefaultDASServerFileAbsolutePath();
		new File(dasServerListFile).delete();
		lookAndFeel = DEFAULT_LOOK_AND_FEEL;
		trackCount = DEFAULT_TRACK_COUNT;
		trackHeight = DEFAULT_TRACK_HEIGHT;
		undoCount = DEFAULT_UNDO_COUNT;
		legend = true;
	}
	
	
	/**
	 * Reads the configuration from a file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void loadConfigurationFile() throws IOException, FileNotFoundException {
		BufferedReader reader = null;
		try {
			File dir = new File(getTemporaryDirectory());
			dir.mkdirs();
			File configFile = new File(getConfigFileAbsolutePath());
			//File configFile = new File(WINDOW_TEMP_DIR, CONFIG_FILE);
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
			if ((trackCount < MIN_TRACK_COUNT) || (trackCount > MAX_TRACK_COUNT)) {
				trackCount = DEFAULT_TRACK_COUNT;
			}
			if ((trackHeight < MIN_TRACK_HEIGHT) || (trackHeight > MAX_TRACK_HEIGHT)) {
				trackHeight = DEFAULT_TRACK_HEIGHT;
			}
			if (undoCount < 0) {
				undoCount = DEFAULT_UNDO_COUNT;
			}
		}
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
				if (key.equalsIgnoreCase("log file")) {
					logFilePath = value;
				} else if (key.equalsIgnoreCase("DAS Server List file")) {
					dasServerListFile = value;
				} else if (key.equalsIgnoreCase("default directory")) {
					defaultDirectory = value;
				} else if (key.equalsIgnoreCase("look and feel")) {
					lookAndFeel = value;
				} else if (key.equalsIgnoreCase("track count")) {
					trackCount = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("track height")) {
					trackHeight = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("undo count")) {
					undoCount = Integer.parseInt(value);
				} else if (key.equalsIgnoreCase("show legend")) {
					legend = Boolean.parseBoolean(value);
				} else {
					extractProject(key, value);
				}
			}
		}
	}
	
	
	/**
	 * Writes the configuration in a file
	 * @throws IOException
	 */
	public void writeConfigurationFile() throws IOException {
		BufferedWriter writer = null;
		try {
			//File configFile = new File(WINDOW_TEMP_DIR, CONFIG_FILE);
			File dir = new File(getTemporaryDirectory());
			dir.mkdirs();
			File configFile = new File(getConfigFileAbsolutePath());
			writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("log file: " + logFilePath);
			writer.newLine();
			writer.write("DAS Server List file: " + dasServerListFile);
			writer.newLine();
			writer.write("default directory: " + defaultDirectory);
			writer.newLine();
			writer.write("look and feel: " + lookAndFeel);
			writer.newLine();
			writer.write("track count: " + trackCount);
			writer.newLine();
			writer.write("track height: " + trackHeight);
			writer.newLine();
			writer.write("undo count: " + undoCount);
			writer.newLine();
			writer.write("show legend: " + legend);
			writeProjects (writer);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	
	/**
	 * This method extracts a project from the configuration manager file.
	 * If the file is not found, the value is set to null.
	 * @param key	key from a configuration file line ("project X")
	 * @param value	value from a configuration file line (pathname)
	 */
	private void extractProject (String key, String value) {
		if (key.substring(0, 8).equals("project ")) {
			int number = Integer.parseInt(key.substring(8, 9));
			if (new File(value).exists()) {
				projects[number-1] = value;
			} else {
				projects[number-1] = null;
			}
		}
	}
	
	
	/**
	 * This method writes the projects list content in the configuration file.
	 * It updates the list before writing information.
	 * @param writer	the writer used during the configuration file writing process.
	 */
	private void writeProjects (BufferedWriter writer) {
		updateProjectsList();
		try {
			for (int i = 1; i <= projects.length; i++) {
				if (projects[i-1] != null){
					writer.newLine();
					writer.write("project " + i + ": " + projects[i-1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method updates the project list.
	 * The last pathname is the oldest project.
	 * The first pathname is the newest one.
	 */
	private void updateProjectsList () {
		// first if we want to see if the current project is already in the project list
		int currentProjectIndex = -1;
		int i = 0;
		while ((currentProjectIndex == -1) && (i < PROJECT_NUMBER)) {
			if ((projects[i] != null) && (projects[i].equalsIgnoreCase(currentProjectPath))) {
				currentProjectIndex = i;
			}
			i++;
		}
		if (currentProjectIndex == -1) { // case where the current project is not in the project list
			for (int j = PROJECT_NUMBER - 2; j >= 0; j--) {
				// in this case we shift all the project on position down 
				// and we add the new project at the beginning of the list 
				projects[j + 1] = projects[j];
			}
		} else { // case where the current project is in the project list
			for (int j = currentProjectIndex; j > 0; j--) {
				// in this case we don't add the current project since it's already in the list
				// but we put it in the first position of the list
				projects[j] = projects[j - 1];
			}
		}
		projects[0] = this.currentProjectPath;
	}

	
	/**
	 * @return the defaultDirectory
	 */
	public final String getDefaultDirectory() {
		return defaultDirectory;
	}

	
	/**
	 * @return the absolute path of the configuration file
	 */
	private String getConfigFileAbsolutePath () {
		return getTemporaryDirectory() + CONFIG_FILE_NAME;
	}
	
	
	/**
	 * @return the absolute path of the log file
	 */
	private String getDefaultLogFileAbsolutePath () {
		return getTemporaryDirectory() + DEFAULT_LOG_FILE_NAME;
	}
	
	
	/**
	 * @return the absolute path of the log file
	 */
	private String getDefaultDASServerFileAbsolutePath () {
		return getTemporaryDirectory() + DEFAULT_DAS_SERVER_FILE_NAME;
	}
	
	
	/**
	 * Checks if the user is working under Windows or not (-> Unix).
	 * If the user is using Windows, the temporary directory is the default one (eg: C:\Users\USER\AppData\Local\Temp)
	 * If the user is using Unix platform, the temporary directory is its working directory 
	 * @return the temporary directory path
	 */
	private String getTemporaryDirectory () {
		if (isWindowsPlatform()) {
			return System.getProperty("java.io.tmpdir");
		}
		return System.getProperty("user.home") + "/.genplay/";
	}

	
	/**
	 * @return true if the user is working under Windows, false otherwise (-> Unix)
	 */
	private boolean isWindowsPlatform () {
		String osName = System.getProperty("os.name");
		if (osName.length() > 7 && osName.substring(0, 7).toUpperCase().equals("WINDOWS")) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * @return the dasServerListFile
	 */
	public final String getDASServerListFile() {
		return dasServerListFile;
	}

	
	/**
	 * @return the default das server file
	 */
	public final String getDefaultDasServerListFile() {
		return this.getClass().getClassLoader().getResource(DEFAULT_DAS_SERVER_PATH).toString();
	}

	
	/**
	 * @return the logFile
	 */
	public final String getLogFile() {
		return logFilePath;
	}

	
	/**
	 * @return the lookAndFeel
	 */
	public final String getLookAndFeel() {
		return lookAndFeel;
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
	 * @return the legend
	 */
	public boolean isLegend() {
		return legend;
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
	 * @param logFile the logFile to set
	 */
	public final void setLogFile(String logFile) {
		this.logFilePath = logFile;
	}

	
	/**
	 * @param lookAndFeel the lookAndFeel to set
	 */
	public final void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	
	/**
	 * @param trackCount the trackCount to set
	 */
	public final void setTrackCount(int trackCount) {
		this.trackCount = trackCount;
	}

	
	/**
	 * @param trackHeight the trackHeight to set
	 */
	public final void setTrackHeight(int trackHeight) {
		this.trackHeight = trackHeight;
	}
	
	
	/**
	 * @param undoCount the undoCount to set
	 */
	public void setUndoCount(int undoCount) {
		this.undoCount = undoCount;
	}
	
	
	/**
	 * @param legend the legend to set
	 */
	public void setLegend(boolean legend) {
		this.legend = legend;
	}
	
	
	/**
	 * @param currentProjectPath the currentProjectPath to set
	 */
	public void setCurrentProjectPath(String currentProjectPath) {
		this.currentProjectPath = currentProjectPath;
	}


	/**
	 * @return the projects
	 */
	public String[] getProjects() {
		return projects;
	}
	
}
