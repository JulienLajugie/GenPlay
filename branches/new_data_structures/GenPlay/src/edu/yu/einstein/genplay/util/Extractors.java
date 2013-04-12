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
package edu.yu.einstein.genplay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;


/**
 * Tools for extractors.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class Extractors {

	/**
	 * Counts the number of line containing data in the specified
	 * @param file a file
	 * @return the number of line containing data in the specified file
	 */
	public static Integer countDataLines(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (!isHeaderLine(line)) {
					count++;
				}
			}
			return count;
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
			return null;
		} finally {
			// always close the reader before exiting
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
	}


	/**
	 * Convert a string to a double
	 * @param s	the string
	 * @return	the double if the string is valid
	 * @throws DataLineException
	 */
	public static Double getDouble (String s) throws DataLineException {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			throw new DataLineException("The information '" + s + "' does not seem to be a valid number.", DataLineException.SKIP_PROCESS);
		}
	}


	/**
	 * Convert a string to a double
	 * @param s	the string
	 * @param alternative the value to return if the string could not be converted  (can be null)
	 * @return	the double if the string is valid, the alternative otherwise
	 */
	public static Double getDouble (String s, Double alternative) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return alternative;
		}
	}


	/**
	 * Convert a string to an integer
	 * @param s	the string
	 * @return	the integer if the string is valid
	 * @throws DataLineException
	 */
	public static Integer getInt (String s) throws DataLineException {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			throw new DataLineException("The information '" + s + "' does not seem to be a valid number.", DataLineException.SKIP_PROCESS);
		}
	}


	/**
	 * Convert a string to an integer
	 * @param s	the string
	 * @param alternative the value to return if the string could not be converted (can be null)
	 * @return	the integer if the string is valid, the alternative otherwise
	 */
	public static Integer getInt (String s, Integer alternative) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return alternative;
		}
	}


	/**
	 * @param line a line from the data file
	 * @return true if the line is a header line or if the line is empty (could be a blank line in the header)
	 */
	public static boolean isHeaderLine(String line) {
		// if the line starts with chr it's a data line so we skip the other tests
		if ((line.length() >= 3) && (line.substring(0, 3).equalsIgnoreCase("chr"))) {
			return false;
		}
		// empty line
		if (line.length() == 0) {
			return true;
		}
		// comment line
		if (line.charAt(0) == '#') {
			return true;
		}
		// track line
		if ((line.length() >= 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
			return true;
		}
		// browser line
		if ((line.length() >= 7) && (line.substring(0, 7).equalsIgnoreCase("browser"))) {
			return true;
		}
		return false; // not a header line
	}


	/**
	 * @param line line from the data file
	 * @return true if the line is a track info line (line starting with 'track'). False otherwise
	 */
	public static boolean isTrackInfoLine(String line) {
		if ((line.length() > 5) && (line.substring(0, 5).equalsIgnoreCase("track"))) {
			return true;
		}
		return false;
	}


	/**
	 * @param dataFile path to a data file
	 * @return the name of the data if it is specified in the file. Null otherwise
	 */
	public static String retrieveDataName(String dataFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			boolean isHeader = true;
			boolean isTrackInfo = false;
			String line = null;

			while (((line = reader.readLine()) != null) && isHeader) {
				isHeader = isHeaderLine(line);
				isTrackInfo = isTrackInfoLine(line);
				if (isHeader && isTrackInfo) {
					String lineTmp = line.toLowerCase();
					if (lineTmp.contains("name")) {
						int indexStart = lineTmp.indexOf("name") + 4;
						line = line.substring(indexStart);
						line = line.trim();
						if (line.charAt(0) != '=') {
							return null;
						}
						// remove the '=' from the line
						line = line.substring(1);
						line = line.trim();
						if (line.charAt(0) == '\"') {
							reader.close();
							// remove the first "
							line = line.substring(1);
							return Utils.split(line, '"')[0];
						} else {
							line = line.trim();
							return Utils.split(line, ' ')[0].trim();
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
		} finally {
			// always close the reader before exiting
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
		return null;
	}
}