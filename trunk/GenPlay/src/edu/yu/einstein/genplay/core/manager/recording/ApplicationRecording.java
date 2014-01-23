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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * This class manages the saving and loading of the application settings.
 * It does not store the settings information!
 * 
 * @author Nicolas Fourel
 */
public class ApplicationRecording {


	/**
	 * Reads a line from the configuration file and extracts the data
	 * @param line a line from the configuration file
	 */
	private void extractLine(String line) {
		int index = line.indexOf(':');
		// if we find a character ':'
		if (index != -1) {
			ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
			String key = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();
			if ((key != null) && (key.length() > 0) && (value != null) && (value.length() > 0)) {
				if (key.equalsIgnoreCase("DAS Server List file")) {
					projectConfiguration.setDASServerListFile(value);
				} else if (key.equalsIgnoreCase("default directory")) {
					projectConfiguration.setDefaultDirectory(value);
				} else if (key.equalsIgnoreCase("look and feel")) {
					projectConfiguration.setLookAndFeel(value);
				} else if (key.equalsIgnoreCase("track count")) {
					projectConfiguration.setTrackCount(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("track height")) {
					projectConfiguration.setTrackHeight(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("undo count")) {
					projectConfiguration.setUndoCount(Integer.parseInt(value));
				} else if (key.equalsIgnoreCase("reset track")) {
					projectConfiguration.setResetTrack(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("cache track")) {
					projectConfiguration.setCacheTrack(Boolean.parseBoolean(value));
				} else if (key.equalsIgnoreCase("show legend")) {
					projectConfiguration.setLegend(Boolean.parseBoolean(value));
				}
			}
		}
	}


	/**
	 * Reads the configuration from a file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void loadConfigurationFile() throws IOException, FileNotFoundException {
		BufferedReader reader = null;
		try {
			ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
			File dir = new File(projectConfiguration.getTemporaryDirectory());
			dir.mkdirs();
			File configFile = new File(projectConfiguration.getConfigFileAbsolutePath());
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
		}
	}


	/**
	 * Writes the configuration in a file
	 * @throws IOException
	 */
	public void writeConfigurationFile() throws IOException {
		BufferedWriter writer = null;
		try {
			ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
			File dir = new File(projectConfiguration.getTemporaryDirectory());
			dir.mkdirs();
			File configFile = new File(projectConfiguration.getConfigFileAbsolutePath());
			writer = new BufferedWriter(new FileWriter(configFile));
			writer.write("DAS Server List file: " + projectConfiguration.getDASServerListFile());
			writer.newLine();
			writer.write("default directory: " + projectConfiguration.getDefaultDirectory());
			writer.newLine();
			writer.write("look and feel: " + projectConfiguration.getLookAndFeel());
			writer.newLine();
			writer.write("track count: " + projectConfiguration.getTrackCount());
			writer.newLine();
			writer.write("track height: " + projectConfiguration.getTrackHeight());
			writer.newLine();
			writer.write("undo count: " + projectConfiguration.getUndoCount());
			writer.newLine();
			writer.write("reset track: " + projectConfiguration.isResetTrack());
			writer.newLine();
			writer.write("cache track: " + projectConfiguration.isCacheTrack());
			writer.newLine();
			writer.write("show legend: " + projectConfiguration.isLegend());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
