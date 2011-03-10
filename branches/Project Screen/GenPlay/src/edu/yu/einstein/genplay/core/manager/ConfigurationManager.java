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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Class containing the project
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class ConfigurationManager implements Serializable {

	private static final long serialVersionUID = 5632320102259442205L; 		// generated ID
	private static ConfigurationManager instance = null; 					// unique instance of the singleton
	private static String TEMP_DIR = System.getProperty("java.io.tmpdir"); 	// java directory for temporary files
	private static String CONFIG_FILE = "GenPlay_config.cfg"; 				// path of the config file
	private static final String DEFAULT_ZOOM_FILE = ""; 					// path of the default zoom config file
	private static final String DEFAULT_CHROMOSOME_FILE = ""; 				// path of the default chromosome config file
	private static final String DEFAULT_LOG_FILE = 
		new File(TEMP_DIR, "GenPlayLog.txt").getAbsolutePath(); 			// path of the default log file
	private final static String DEFAULT_DAS_SERVER_PATH = 
		"edu/yu/einstein/genplay/resource/DASServerList.xml"; 						// DAS Server List file path
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
	private String zoomFile = DEFAULT_ZOOM_FILE; 							// zoom config file
	private String chromosomeFile = DEFAULT_CHROMOSOME_FILE; 				// chromosome config file
	private String logFile = DEFAULT_LOG_FILE; 								// log file
	private String defaultDirectory = DEFAULT_DEFAULT_DIRECTORY; 			// default directory
	private String lookAndFeel = DEFAULT_LOOK_AND_FEEL; 					// look and feel
	private String dasServerListFile = getTempDir() + "DASServerList.xml"; 	// DAS Server list
	private int trackCount = DEFAULT_TRACK_COUNT; 							// track count
	private int trackHeight = DEFAULT_TRACK_HEIGHT; 						// track height
	private int undoCount = DEFAULT_UNDO_COUNT; 							// number of undo in memory

	
	/**
	 * @return an instance of a {@link ConfigurationManager}. Makes sure that
	 * there is only one unique instance as specified in the singleton pattern
	 */
	public static ConfigurationManager getInstance() {
		if (instance == null) {
			synchronized (ConfigurationManager.class) {
				if (instance == null) {
					instance = new ConfigurationManager();
				}
			}
		}
		return instance;
	}

	
	/**
	 * Private constructor of the singleton. Creates an instance of a
	 * {@link ConfigurationManager}.
	 */
	private ConfigurationManager() {
		super();
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
				if (key.equalsIgnoreCase("zoom file")) {
					zoomFile = value;
				} else if (key.equalsIgnoreCase("chromosome file")) {
					chromosomeFile = value;
				} else if (key.equalsIgnoreCase("log file")) {
					logFile = value;
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
				}
			}
		}
	}

	
	/**
	 * @return the chromosomeFile
	 */
	public final String getChromosomeFile() {
		return chromosomeFile;
	}

	
	/**
	 * @return the defaultDirectory
	 */
	public final String getDefaultDirectory() {
		return defaultDirectory;
	}

	
	/**
	 * @return the tempDirectory
	 */
	public final String getTempDir() {
		return TEMP_DIR;
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
		return logFile;
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
	 * @return the zoomFile
	 */
	public final String getZoomFile() {
		return zoomFile;
	}

	
	/**
	 * Reads the configuration from a file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void loadConfigurationFile() throws IOException, FileNotFoundException {
		BufferedReader reader = null;
		try {
			File configFile = new File(TEMP_DIR, CONFIG_FILE);
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
	 * Methods used for the serialization of the singleton object. The
	 * readResolve method is called when ObjectInputStream has read an object
	 * from the stream and is preparing to return it to the caller. See javadocs
	 * for more information
	 * @return the unique instance of the singleton
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return getInstance();
	}

	
	/**
	 * Restores the default configuration
	 */
	public void restoreDefault() {
		zoomFile = DEFAULT_ZOOM_FILE;
		chromosomeFile = DEFAULT_CHROMOSOME_FILE;
		logFile = DEFAULT_LOG_FILE;
		defaultDirectory = DEFAULT_DEFAULT_DIRECTORY;
		new File(dasServerListFile).delete();
		dasServerListFile = getTempDir() + "DASServerList.xml";
		lookAndFeel = DEFAULT_LOOK_AND_FEEL;
		trackCount = DEFAULT_TRACK_COUNT;
		trackHeight = DEFAULT_TRACK_HEIGHT;
		undoCount = DEFAULT_UNDO_COUNT;
	}

	
	/**
	 * @param chromosomeFile the chromosomeFile to set
	 */
	public final void setChromosomeFile(String chromosomeFile) {
		this.chromosomeFile = chromosomeFile;
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
		this.logFile = logFile;
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
	 * @param zoomFile the zoomFile to set
	 */
	public final void setZoomFile(String zoomFile) {
		this.zoomFile = zoomFile;
	}

	
	/**
	 * @param undoCount the undoCount to set
	 */
	public void setUndoCount(int undoCount) {
		this.undoCount = undoCount;
	}

	
	/**
	 * Writes the configuration in a file
	 * @throws IOException
	 */
	public void writeConfigurationFile() throws IOException {
		BufferedWriter writer = null;
		try {
			File configFile = new File(TEMP_DIR, CONFIG_FILE);
			writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("zoom file: " + zoomFile);
			writer.newLine();
			writer.write("chromosome file: " + chromosomeFile);
			writer.newLine();
			writer.write("log file: " + logFile);
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
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
